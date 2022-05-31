package com.codebrew.clikat.app_utils

import android.animation.ObjectAnimator
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.view.View
import android.widget.TextView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.network.HostSelectionInterceptor
import com.facebook.FacebookSdk
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type
import java.util.*


class CommonUtils {
    companion object {

        fun initProgressDialog(context: Context): ProgressDialog {
            val progressDialog = ProgressDialog(context)
            progressDialog.show()
            if (progressDialog.window != null) {
                progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            progressDialog.setContentView(R.layout.progress_dialog)
            progressDialog.isIndeterminate = true
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            return progressDialog
        }

        fun convrtReqBdy(param: String): RequestBody {
            return RequestBody.create("text/plain".toMediaTypeOrNull(), param)
        }


        fun isNetworkConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

        fun setBaseUrl(baseUrl:String,retrofit: Retrofit)
        {
            val field = Retrofit::class.java.getDeclaredField("baseUrl")
            field.isAccessible = true
            val newHttpUrl = baseUrl.toHttpUrlOrNull()
            field.set(retrofit, newHttpUrl)
        }

        fun checkAppDBKey(dBKey: String,interceptor: HostSelectionInterceptor) {

            if (dBKey.isNotEmpty()) {
                if (interceptor.secret_key == null || interceptor.secret_key
                        !== dBKey) {
                    interceptor.secret_key = dBKey
                }
            }
        }


         fun getaddress(mContext: Context,latitude: Double?, longitude: Double?): String {

             val addresses: List<Address>
            var address: Address
            var addressData = ""
             val geocoder = Geocoder(mContext, DateTimeUtils.timeLocale)

            try {
                addresses = geocoder.getFromLocation(latitude?:0.0, longitude?:0.0, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                for (i in addresses.indices) {
                    address = addresses[i]
                    if (address.getAddressLine(0) != null) {
                        addressData = address.getAddressLine(0)
                        break
                    }
                }

                return addressData
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return addressData


        }

        fun areDrawablesIdentical(drawableA: Drawable, drawableB: Drawable): Boolean {
            val stateA = drawableA.constantState
            val stateB = drawableB.constantState
            // If the constant state is identical, they are using the same drawable resource.
            // However, the opposite is not necessarily true.
            return stateA != null && stateB != null && stateA == stateB
        }


        fun getBitmap(drawable: Drawable): Bitmap {
            val result: Bitmap
            if (drawable is BitmapDrawable) {
                result = drawable.bitmap
            } else {
                var width = drawable.intrinsicWidth
                var height = drawable.intrinsicHeight
                // Some drawables have no intrinsic width - e.g. solid colours.
                if (width <= 0) {
                    width = 1
                }
                if (height <= 0) {
                    height = 1
                }

                result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(result)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
            return result
        }



        fun changebaseUrl(retrofit: Retrofit,baseUrl: String) {
            val field = Retrofit::class.java.getDeclaredField("baseUrl")
            field.isAccessible = true
            val newHttpUrl = baseUrl.toHttpUrlOrNull()
            field.set(retrofit, newHttpUrl)
        }


         fun expandTextView(tv: TextView?) {
            val animation: ObjectAnimator = ObjectAnimator.ofInt(tv, "maxLines", tv?.lineCount ?:0)
            animation.setDuration(200).start()
        }


        fun <T> deserializeList(json: String?, type: Type?): List<T>? {
            val gson = Gson()
            return gson.fromJson(json, type)
        }

        fun <T> List<T>.toArrayList(): ArrayList<T>{
            return ArrayList(this)
        }

        fun <P, R> CoroutineScope.executeAsyncTask(
                onPreExecute: () -> Unit,
                doInBackground: suspend (suspend (P) -> Unit) -> R,
                onPostExecute: (R) -> Unit,
                onProgressUpdate: (P) -> Unit
        ) = launch {
            onPreExecute()

            val result = withContext(Dispatchers.IO) {
                doInBackground {
                    withContext(Dispatchers.Main) { onProgressUpdate(it) }
                }
            }
            onPostExecute(result)
        }

        fun View.hide() {
            visibility = View.GONE
        }

        fun View.visible() {
            visibility = View.VISIBLE
        }


    }
}
