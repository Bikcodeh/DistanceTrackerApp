package com.bikcodeh.distancetrackerapp.ui.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bikcodeh.distancetrackerapp.R
import com.bikcodeh.distancetrackerapp.databinding.FragmentMapsBinding
import com.bikcodeh.distancetrackerapp.model.Result
import com.bikcodeh.distancetrackerapp.services.TrackerService
import com.bikcodeh.distancetrackerapp.ui.maps.MapUtil.calculateDistance
import com.bikcodeh.distancetrackerapp.ui.maps.MapUtil.calculateElapsedTime
import com.bikcodeh.distancetrackerapp.ui.maps.MapUtil.setCameraPosition
import com.bikcodeh.distancetrackerapp.util.Constants.ACTION_SERVICE_START
import com.bikcodeh.distancetrackerapp.util.Constants.ACTION_SERVICE_STOP
import com.bikcodeh.distancetrackerapp.util.Extension.disable
import com.bikcodeh.distancetrackerapp.util.Extension.enable
import com.bikcodeh.distancetrackerapp.util.Extension.hide
import com.bikcodeh.distancetrackerapp.util.Extension.show
import com.bikcodeh.distancetrackerapp.util.Permissions.hasBackgroundLocationPermission
import com.bikcodeh.distancetrackerapp.util.Permissions.requestBackgroundLocationPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private var locationList = mutableListOf<LatLng>()
    private var startTime = 0L
    private var stopTime = 0L

    val started = MutableLiveData<Boolean>(false)
    private var polylineList = mutableListOf<Polyline>()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.tracking = this
        with(binding) {
            btnStar.setOnClickListener {
                onStartButtonClick()
            }

            btnReset.setOnClickListener {
                onResetButtonClicked()
            }

            btnStop.setOnClickListener {
                onStopButtonClick()
            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((requireActivity()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
        map.setOnMyLocationButtonClickListener(this)
        map.uiSettings.apply {
            isZoomControlsEnabled = false
            isZoomGesturesEnabled = false
            isRotateGesturesEnabled = false
            isCompassEnabled = false
            isTiltGesturesEnabled = false
            isScrollGesturesEnabled = false
        }
        observeTrackService()
    }

    private fun observeTrackService() {
        TrackerService.locationList.observe(viewLifecycleOwner) {
            if (it != null) {
                locationList = it
                if (locationList.size > 1) {
                    binding.btnStop.enable()
                }
                drawPolyline()
                followPolyline()
            }
        }
        TrackerService.startTime.observe(viewLifecycleOwner) {
            startTime = it
        }

        TrackerService.stopTime.observe(viewLifecycleOwner) {
            stopTime = it
            if ( stopTime != 0L) {
                showBiggerPicture()
                displayResults()
            }
        }

        TrackerService.started.observe(viewLifecycleOwner) {
            started.value= it
        }
    }

    private fun showBiggerPicture() {
        val bounds = LatLngBounds.Builder()
        for (location in locationList) {
            bounds.include(location)
        }
        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                100
            ),
            2000,
            null
        )
    }

    private fun displayResults() {
        val result = Result(
            calculateDistance(locationList),
            calculateElapsedTime(startTime, stopTime)
        )

        lifecycleScope.launch {
            delay(2500)
            val directions = MapsFragmentDirections.actionMapsFragmentToResultFragment(result)
            findNavController().navigate(directions)
            with(binding) {
                btnStar.apply {
                    hide()
                    enable()
                }
                btnStop.hide()
                btnReset.show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun mapReset() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            val lastKnownLocation = LatLng(
                it.result.latitude,
                it.result.longitude
            )
            for (polyline in polylineList) {
                polyline.remove()
            }
            map.animateCamera(CameraUpdateFactory.newCameraPosition(
                setCameraPosition(lastKnownLocation)
            ))
            locationList.clear()
            binding.btnReset.hide()
            binding.btnStar.show()
        }
    }

    private fun drawPolyline(){
        val polyline = map.addPolyline(
            PolylineOptions().apply {
                width(10f)
                color(Color.BLUE)
                jointType(JointType.ROUND)
                startCap(ButtCap())
                endCap(ButtCap())
                addAll(locationList)
            }
        )
        polylineList.add(polyline)
    }

    private fun followPolyline() {
        if (locationList.isNotEmpty()) {
            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    MapUtil.setCameraPosition(
                        locationList.last()
                    )
                ),
                1000,
                null
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMyLocationButtonClick(): Boolean {
        binding.tvMyLocation.animate().alpha(0f).duration = 1500
        lifecycleScope.launch {
            delay(2500)
            binding.tvMyLocation.hide()
            binding.btnStar.show()
        }
        return false
    }

    private fun onStartButtonClick() {
        if (hasBackgroundLocationPermission(requireContext())) {
            startCountDown()
            binding.btnStar.disable()
            binding.btnStar.hide()
            binding.btnStop.show()
        } else {
            requestBackgroundLocationPermission(this)
        }
    }

    private fun onStopButtonClick() {
        binding.btnStop.hide()
        binding.btnStar.show()
        stopForegroundService()
    }

    private fun onResetButtonClicked() {
        mapReset()
    }

    private fun stopForegroundService() {
        binding.btnStar.disable()
        sendActionCommandToService(ACTION_SERVICE_STOP)
    }

    private fun startCountDown() {
        with(binding) {
            tvTimer.show()
            btnStop.disable()
        }

        val timer: CountDownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val currentSecond = millisUntilFinished / 1000
                if (currentSecond.toString() == "0") {
                    binding.tvTimer.text = getString(R.string.go_label)
                    binding.tvTimer.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                } else {
                    binding.tvTimer.text = currentSecond.toString()
                    binding.tvTimer.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                }
            }

            override fun onFinish() {
                sendActionCommandToService(ACTION_SERVICE_START)
                binding.tvTimer.hide()
            }
        }
        timer.start()
    }

    private fun sendActionCommandToService(action: String) {
        Intent(
            requireContext(),
            TrackerService::class.java
        ).apply {
            this.action = action
            requireContext().startService(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("MissingPermission")
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(requireActivity())
                .build().show()
        } else {
            requestBackgroundLocationPermission(this)
        }
    }
}