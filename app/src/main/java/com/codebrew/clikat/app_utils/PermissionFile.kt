package com.codebrew.clikat.app_utils

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.codebrew.clikat.R
import com.codebrew.clikat.data.constants.AppConstants
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject


/**
 * Created by Ajit on 05-10-2017.
 */

class PermissionFile @Inject
constructor(private val mContext: Context) {


    private val CAMERA_AND_GALLERY = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private val LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    private val CALL_PHONE = arrayOf(Manifest.permission.CALL_PHONE)

    private val RECORD_AUDIO = arrayOf(Manifest.permission.RECORD_AUDIO)


     fun hasCameraPermissions(ctx: Context): Boolean {
        return EasyPermissions.hasPermissions(ctx, *CAMERA_AND_GALLERY)
    }

    // passing string value Manifest.permission.READ_SMS
    fun hasPermission(ctx: Context, permissionName: String): Boolean {
        return EasyPermissions.hasPermissions(ctx, permissionName)
    }

     fun hasCallPermissions(ctx: Context): Boolean {
        return EasyPermissions.hasPermissions(ctx, *CALL_PHONE)
    }

    fun hasAudioPermission(ctx: Context): Boolean {
        return EasyPermissions.hasPermissions(ctx, *RECORD_AUDIO)
    }

    fun hasLocation(ctx: Context): Boolean {
        return EasyPermissions.hasPermissions(ctx, *LOCATION)
    }

    @AfterPermissionGranted(AppConstants.PERMISSION_REQUEST)
    fun permissionTask(ctx: Context, permissionName: String): Boolean {
        return if (hasPermission(ctx, permissionName)) {
            // Have permission, do the thing!
            true
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    ctx as AppCompatActivity,
                    ctx.getString(R.string.explanation_multiple_request),
                    AppConstants.PERMISSION_REQUEST,
                    permissionName
            )
            false
        }
    }


    @AfterPermissionGranted(AppConstants.CameraGalleryPicker)
    fun cameraAndGalleryTask(mFragment: Fragment): Boolean {
        return if (hasCameraPermissions(mFragment.requireContext())) {
            // Have permissions, do the thing!
            true
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    mFragment,
                    mFragment.getString(R.string.explanation_multiple_request),
                    AppConstants.CameraGalleryPicker,
                    *CAMERA_AND_GALLERY
            )
            false
        }
    }

 @AfterPermissionGranted(AppConstants.CameraGalleryPicker)
    fun cameraAndGalleryTask2(mFragment: Activity): Boolean {
        return if (hasCameraPermissions(mFragment)) {
            // Have permissions, do the thing!
            true
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    mFragment,
                    mFragment.getString(R.string.explanation_multiple_request),
                    AppConstants.CameraGalleryPicker,
                    *CAMERA_AND_GALLERY
            )
            false
        }
    }


    @AfterPermissionGranted(AppConstants.CameraGalleryPicker)
    fun cameraAndGalleryActivity(ctx: Context): Boolean {
        return if (hasCameraPermissions(ctx)) {
            // Have permissions, do the thing!
            true
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    ctx as AppCompatActivity,
                    ctx.getString(R.string.explanation_multiple_request),
                    AppConstants.CameraGalleryPicker,
                    *CAMERA_AND_GALLERY
            )
            false
        }
    }



    @AfterPermissionGranted(AppConstants.REQUEST_CODE_LOCATION)
    fun locationTask(ctx: Context): Boolean {
        return if (hasLocation(ctx)) {
            // Have permissions, do the thing!
            true
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    ctx as AppCompatActivity,
                    ctx.getString(R.string.explanation_multiple_request),
                    AppConstants.REQUEST_CODE_LOCATION,
                    *LOCATION
            )
            false
        }
    }


    @AfterPermissionGranted(AppConstants.REQUEST_CODE_LOCATION)
    fun locationTaskFrag(mFragment: Fragment): Boolean {
        return if (hasLocation(mFragment.requireContext())) {
            // Have permissions, do the thing!
            true
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    mFragment,
                    mFragment.getString(R.string.explanation_multiple_request),
                    AppConstants.REQUEST_CODE_LOCATION,
                    *LOCATION
            )
            false
        }
    }


    @AfterPermissionGranted(AppConstants.REQUEST_CALL)
    fun phoneCallTask(ctx: Context): Boolean {
        return if (hasCallPermissions(ctx)) {
            // Have permissions, do the thing!
            true
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    ctx as AppCompatActivity,
                    ctx.getString(R.string.explanation_multiple_request),
                    AppConstants.REQUEST_CALL,
                    *CALL_PHONE
            )
            false
        }
    }


    @AfterPermissionGranted(AppConstants.REQUEST_AUDIO)
    fun audioCallTask(ctx: Context): Boolean {
        return if (hasAudioPermission(ctx)) {
            // Have permissions, do the thing!
            true
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    ctx as AppCompatActivity,
                    ctx.getString(R.string.explanation_multiple_request),
                    AppConstants.REQUEST_AUDIO,
                    *RECORD_AUDIO
            )
            false
        }
    }


}
