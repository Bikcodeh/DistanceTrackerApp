package com.bikcodeh.distancetrackerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bikcodeh.distancetrackerapp.databinding.FragmentPermissionBinding
import com.bikcodeh.distancetrackerapp.util.Permissions.hasLocationPermission
import com.bikcodeh.distancetrackerapp.util.Permissions.requestLocationPermission
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class PermissionFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)

        binding.btnContinue.setOnClickListener {
            if (hasLocationPermission(requireContext())) {
                findNavController().navigate(R.id.action_permissionFragment_to_mapsFragment)
            } else {
                requestLocationPermission(this)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        findNavController().navigate(R.id.action_permissionFragment_to_mapsFragment)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(requireActivity())
                .build().show()
        } else {
            requestLocationPermission(this)
        }
    }
}