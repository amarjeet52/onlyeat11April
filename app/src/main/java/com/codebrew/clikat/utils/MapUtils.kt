package com.codebrew.clikat.utils

import android.content.Context
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.util.*


object MapUtils {


    fun bearingBetweenLocations(sourceLatLng: LatLng, destinationLatLng: LatLng): Float {
        val source = Location("source")
        source.latitude = sourceLatLng.latitude
        source.longitude = sourceLatLng.longitude

        val destination = Location("destination")
        destination.latitude = destinationLatLng.latitude
        destination.longitude = destinationLatLng.longitude
        return source.bearingTo(destination)
    }

    fun calculateBearing(sourceLatLng: LatLng?, destinationLatLng: LatLng?): Float {

        val PI = 3.14159
        val lat1 = sourceLatLng?.latitude ?: 0.0 * PI / 180
        val long1 = sourceLatLng?.longitude ?: 0.0 * PI / 180
        val lat2 = destinationLatLng?.latitude ?: 0.0 * PI / 180
        val long2 = destinationLatLng?.longitude ?: 0.0 * PI / 180

        val dLon = long2 - long1

        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon))

        var brng = Math.atan2(y, x)

        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360

        return brng.toFloat()
    }

    fun getBearing(start: LatLng, end: LatLng): Float? {
        val absoluteLatitude = Math.abs(start.latitude - end.latitude)
        val absoluteLongitude = Math.abs(start.longitude - end.longitude)

        if (start.latitude < end.latitude && start.longitude < end.longitude)
            return Math.toDegrees(Math.atan(absoluteLongitude / absoluteLatitude)).toFloat()
        else if (start.latitude >= end.latitude && start.longitude < end.longitude)
            return (90 - Math.toDegrees(Math.atan(absoluteLongitude / absoluteLatitude)) + 90).toFloat()
        else if (start.latitude >= end.latitude && start.longitude >= end.longitude)
            return (Math.toDegrees(Math.atan(absoluteLongitude / absoluteLatitude)) + 180).toFloat()
        else if (start.latitude < end.latitude && start.longitude >= end.longitude)
            return (90 - Math.toDegrees(Math.atan(absoluteLongitude / absoluteLatitude)) + 270).toFloat()
        return -1f
    }

    fun interpolateLatLng(fraction: Float, start: LatLng, end: LatLng): LatLng {
        val updatedLatitude = (end.latitude - start.latitude) * fraction + start.latitude
        var deltaLongitude = end.longitude - start.longitude

        // Take the shortest path across the 180th meridian.
        if (Math.abs(deltaLongitude) > 180) {
            deltaLongitude -= Math.signum(deltaLongitude) * 360
        }

        val updatedLongitude = deltaLongitude * fraction + start.longitude
        return LatLng(updatedLatitude, updatedLongitude)
    }

    fun getDistanceBetweenTwoPoints(latLng1: LatLng, latLng2: LatLng): Float {
        val distance = FloatArray(2)

        Location.distanceBetween(latLng1.latitude, latLng1.longitude,
                latLng2.latitude, latLng2.longitude, distance)
        //Timber.d("Distance Calculated : ${distance[0]}")

        return distance[0]
    }


}