package com.bikcodeh.distancetrackerapp.util

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.bikcodeh.distancetrackerapp.R
import com.bikcodeh.distancetrackerapp.util.Constants.PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE
import com.bikcodeh.distancetrackerapp.util.Constants.PERMISSION_LOCATION_REQUEST_CODE
import com.vmadalin.easypermissions.EasyPermissions

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

    fun hasBackgroundLocationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
        return true
    }

    fun requestBackgroundLocationPermission(fragment: Fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            fragment.context?.getString(R.string.background_location_permission_description)?.let {
                EasyPermissions.requestPermissions(
                    fragment,
                    it,
                    PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
    }

}