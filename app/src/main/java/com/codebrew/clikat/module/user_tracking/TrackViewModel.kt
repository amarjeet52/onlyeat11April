package com.codebrew.clikat.module.user_tracking

import androidx.lifecycle.ViewModel

class TrackViewModel : ViewModel() {


/*    fun drawPolyLine(sourceLat: Double, sourceLong: Double, destLat: Double, destLong: Double, language: String?) {


        val BASE_URL_for_map = "https://maps.googleapis.com/maps/api/"
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL_for_map).addConverterFactory(GsonConverterFactory.create()).build()
        val api = retrofit.create(RestService::class.java)
        val service = api.getPolYLine(sourceLat.toString() + "," + sourceLong, destLat.toString() + "," + destLong, language)
        service.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val responsePolyline = response.body()?.string()
                        val jsonRootObject = JSONObject(responsePolyline)
                        getView()?.polyLine(jsonRootObject)
                    } catch (e: IOException) {
                        e.printStackTrace()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    val errorModel = getApiError(response.errorBody()?.string())
                    getView()?.handleApiError(errorModel.statusCode, errorModel.msg)
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                getView()?.showLoader(false)
                getView()?.apiFailure()
            }
        })
    }

    fun getRoadPoints(latitude:Double,longitude:Double) {
        //getView()?.showLoader(true)

        val BASE_URL_for_map = "https://maps.googleapis.com/maps/api/"
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL_for_map).addConverterFactory(GsonConverterFactory.create()).build()
        val api = retrofit.create(RestService::class.java)

        val latlng = latitude.toString() + "," + longitude.toString()
        api.getRoadPoints(latlng).enqueue(object : Callback<RoadPoints> {
            override fun onResponse(call: Call<RoadPoints>?,
                                    response: Response<RoadPoints>?) {
                //getView()?.showLoader(false)
                getView()?.snappedPoints((response?.body()?.snappedPoints
                        ?: ArrayList()), trackingModel)
            }

            override fun onFailure(call: Call<RoadPoints>?, t: Throwable?) {
                //getView()?.showLoader(false)
                getView()?.snappedPoints((ArrayList()), trackingModel)
            }

        })
    }*/

}