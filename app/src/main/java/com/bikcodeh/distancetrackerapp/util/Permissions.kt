package com.bikcodeh.distancetrackerapp.util

import android.Manifest
import android.content.Context
import androidx.fragment.app.Fragment
import com.bikcodeh.distancetrackerapp.R
import com.bikcodeh.distancetrackerapp.util.Constants.PERMISSION_LOCATION_REQUEST_CODE
import pub.devrel.easypermissions.EasyPermissions

object Permissions {

    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )


    fun requestLocationPermission(fragment: Fragment) {
        fragment.context?.getString(R.string.permission_required_description)?.let {
            EasyPermissions.requestPermissions(
                fragment,
                it,
                PERMISSION_LOCATION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
}