package com.codebrew.clikat.app_utils

/**
 * ChatImageData capturing related utility methods
 */


import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Bitmap.Config
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log

import com.codebrew.clikat.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageUtility @Inject
constructor(private val context: Context) {


    /**
     * Display SampleContract dialog to select between Camera and Gallery to pick your image
     *
     * @param cameraRequestCode  request code for camera use
     * @param galleryRequestCode request code gallery use
     */

    /**
     * Compresses the image
     *
     * @param filePath : Location of image file
     * @return compressed image file path
     */
    fun compressImage(filePath: String): String {
        var scaledBitmap: Bitmap? = null

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bmp: Bitmap? = BitmapFactory.decodeFile(filePath, options)

        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        val maxHeight = 1024.0f
        val maxWidth = 812.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inDither = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)

        try {
            bmp = BitmapFactory.decodeFile(filePath, options)
        } catch (exception: Exception) {
            exception.printStackTrace()

        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }

        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

        val canvas = Canvas(scaledBitmap!!)
        // canvas.matrix = scaleMatrix
        canvas.drawBitmap(bmp!!, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(
                Paint.FILTER_BITMAP_FLAG))

        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath)

            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            Log.e("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.e("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.e("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.e("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height,
                    matrix, true
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var out: FileOutputStream? = null
        val filename = filename(this).absolutePath
        try {
            out = FileOutputStream(filename)
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            bmp.recycle()
            bmp = null
            scaledBitmap?.recycle()
        }
        return filename

    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    //get file path from its URI
    fun getRealPathFromURI(context: Activity, contentUri: Uri): String {
        // can post image
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.managedQuery(contentUri, proj, null, null, null)// Which
        // columns
        // to
        // return
        // WHERE clause; which rows to return (all rows)
        // WHERE clause selection arguments (none)
        // Order-by clause (ascending by name)
        val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    // load image into image view using Picasso


    /**
     * Compress image and convert to multipart
     *
     * @param filePath path of the file to be converted
     * @return multipart image for the path supplied
     */


    /**
     * get request body image
     */
    fun getRequestBodyImage(file: File): RequestBody {
        return RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
    }

    /**
     * convert image to base 64 string
     *
     * @param filePath path of image
     * @return base 64 string
     */
    fun getBase64Image(filePath: String): String {
        var filePath = filePath
        filePath = getCompressedImage(filePath)
        val bm = BitmapFactory.decodeFile(filePath)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos) //bm is the bitmap object
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    /**
     * get compressed image
     */
    private fun getCompressedImage(filePath: String): String {
        val newFilePath: String
        val file_size = Integer.parseInt((File(filePath).length() shr 10).toString())
        if (file_size >= 120) {
            newFilePath = compressImage(filePath)
        } else {
            newFilePath = filePath
        }
        return filePath
    }

    fun hasPermissionInManifest(context: Context, permissionName: String): Boolean {
        val packageName = context.packageName
        try {
            val packageInfo = context.packageManager
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val declaredPermisisons = packageInfo.requestedPermissions
            if (declaredPermisisons != null && declaredPermisisons.size > 0) {
                for (p in declaredPermisisons) {
                    if (p == permissionName) {
                        return true
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {

        }

        return false
    }

    fun checksize(uri: Uri, context: Context): Boolean {
        val file = File(uri.path!!)
        var size: Long = 0

        try {
            /*// Get the number of bytes in the file
                   long sizeInBytes = file.length();*/
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor!!.moveToFirst()
            size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //transform in MB
        val sizeInMb = size / (1024 * 1024)
        return sizeInMb < 12
    }

    companion object {

        fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round(height.toFloat() / reqHeight.toFloat())
                } else {
                    inSampleSize = Math.round(width.toFloat() / reqWidth.toFloat())
                }
            }
            return inSampleSize
        }


        //get file name
        fun filename(imageUtility: ImageUtility): File {
            return File(file(imageUtility))
        }

        fun file(imageUtility: ImageUtility): String {

            lateinit var currentPhotoPath: String

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss",DateTimeUtils.timeLocale).format(Date())
            val storageDir: File = imageUtility.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
             File.createTempFile(
                    "JPEG_${timeStamp}_", /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }

            return currentPhotoPath
        }
    }
}