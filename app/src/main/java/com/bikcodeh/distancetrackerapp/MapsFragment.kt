package com.bikcodeh.distancetrackerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bikcodeh.distancetrackerapp.databinding.FragmentMapsBinding
import com.bikcodeh.distancetrackerapp.util.Extension.hide
import com.bikcodeh.distancetrackerapp.util.Extension.show
import com.bikcodeh.distancetrackerapp.util.Permissions.hasBackgroundLocationPermission
import com.bikcodeh.distancetrackerapp.util.Permissions.requestBackgroundLocationPermission
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        with(binding) {
            btnStar.setOnClickListener {
                onStartButtonClick()
            }

            btnReset.setOnClickListener {

            }

            btnStop.setOnClickListener {

            }
        }
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
        return true
    }

    private fun onStartButtonClick() {
        if (hasBackgroundLocationPermission(requireContext())) {
            Toast.makeText(context, "already enabled", Toast.LENGTH_SHORT).show()
        } else {
            requestBackgroundLocationPermission(this)
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

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //findNavController().navigate(R.id.action_permissionFragment_to_mapsFragment)
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