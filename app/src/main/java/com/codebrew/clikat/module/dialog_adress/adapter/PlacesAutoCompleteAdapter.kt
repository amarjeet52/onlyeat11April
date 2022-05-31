package com.codebrew.clikat.module.dialog_adress.adapter

import android.content.Context
import android.graphics.Typeface
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.setSafeOnClickListener
import com.codebrew.clikat.data.constants.AppConstants.Companion.USER_LATLNG
import com.codebrew.clikat.databinding.ListSearchPlaceBinding
import com.codebrew.clikat.modal.other.SettingModel.DataBean.SettingData
import com.codebrew.clikat.module.dialog_adress.adapter.PlacesAutoCompleteAdapter.PredictionHolder
import com.codebrew.clikat.utils.Utils.getRectangularBound
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class PlacesAutoCompleteAdapter(private val mContext: Context?, private val mSettingData: SettingData?) : RecyclerView.Adapter<PredictionHolder>(), Filterable {
    private val placesClient: PlacesClient?
    private var mResultList: ArrayList<PlaceAutocomplete>? = ArrayList()
    private val STYLE_BOLD: CharacterStyle
    private val STYLE_NORMAL: CharacterStyle
    private var clickListener: ClickListener? = null
    fun setClickListener(clickListener: ClickListener?) {
        this.clickListener = clickListener
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val results = FilterResults()
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getPredictions(constraint)
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList
                        results.count = mResultList!!.size
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged()
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        }
    }

    private fun getPredictions(constraint: CharSequence): ArrayList<PlaceAutocomplete> {
        val resultList = ArrayList<PlaceAutocomplete>()

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()

        //https://gist.github.com/graydon/11198540
        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request = createAutoPred(token, constraint)
        val autocompletePredictions = placesClient?.findAutocompletePredictions(request)

        // This method should have been called off the main UI thread. Block and wait for at most
        // 60s for a result from the API.
        try {

            autocompletePredictions?.let {
                Tasks.await(it, 60, TimeUnit.SECONDS)
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }
        return if (autocompletePredictions?.isSuccessful==true) {
            val findAutocompletePredictionsResponse = autocompletePredictions?.result
            if (findAutocompletePredictionsResponse != null) for (prediction in findAutocompletePredictionsResponse.autocompletePredictions) {
                Log.i(TAG, prediction.placeId)
                resultList.add(PlaceAutocomplete(prediction.placeId, prediction.getPrimaryText(STYLE_NORMAL).toString(), prediction.getFullText(STYLE_BOLD).toString()))
            }
            resultList
        } else {
            resultList
        }
    }

    private fun createAutoPred(token: AutocompleteSessionToken, constraint: CharSequence): FindAutocompletePredictionsRequest {
        val request: FindAutocompletePredictionsRequest
        request = if (mSettingData?.search_user_locale != null && mSettingData.search_user_locale == "1") {
            FindAutocompletePredictionsRequest.builder()
                    .setLocationBias(getRectangularBound())
                    .setCountry("ZM")
                    .setOrigin(USER_LATLNG) //.setTypeFilter(TypeFilter.GEOCODE)
                    .setSessionToken(token)
                    .setQuery(constraint.toString())
                    .build()
        } else {
            FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
                    //.setLocationBias(bounds)
                    .setLocationBias(getRectangularBound())
                    .setOrigin(USER_LATLNG)
                    .setCountry("QA")
                    .setSessionToken(token)
                    .setQuery(constraint.toString())
                    .build()
        }
        return request
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PredictionHolder {
        val binding: ListSearchPlaceBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context), R.layout.list_search_place, viewGroup, false)
        binding.color = Configurations.colors
        return PredictionHolder(binding.root)
    }

    override fun onBindViewHolder(mPredictionHolder: PredictionHolder, i: Int) {
        mPredictionHolder.address.text = mResultList!![i].address
        mPredictionHolder.area.text = mResultList!![i].area
    }

    override fun getItemCount(): Int {
        return mResultList!!.size
    }

    fun getItem(position: Int): PlaceAutocomplete {
        return mResultList!![position]
    }

    interface ClickListener {
        fun click(place: Place?)
    }

    inner class PredictionHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val address: TextView
        val area: TextView
        private val mRow: ConstraintLayout
        override fun onClick(v: View) {
            if (v.id == R.id.place_item_view) {
                if (adapterPosition != -1 && !mResultList!!.isEmpty()) {
                    val item = mResultList!![adapterPosition]
                    val placeId = item.placeId.toString()
                    val placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
                    val request = FetchPlaceRequest.builder(placeId, placeFields).build()
                    placesClient?.fetchPlace(request)?.addOnSuccessListener { response: FetchPlaceResponse ->
                        val place = response.place
                        clickListener!!.click(place)
                    }?.addOnFailureListener { exception ->
                        if (exception is ApiException) {
                            Toast.makeText(mContext, exception.message + "", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        init {
            area = itemView.findViewById(R.id.place_area)
            address = itemView.findViewById(R.id.place_address)
            mRow = itemView.findViewById(R.id.place_item_view)
            itemView.setOnClickListener(this)
        }
    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    inner class PlaceAutocomplete internal constructor(var placeId: CharSequence, var area: CharSequence, var address: CharSequence) {
        override fun toString(): String {
            return area.toString()
        }
    }

    companion object {
        private const val TAG = "PlacesAutoAdapter"
    }

    init {
        STYLE_BOLD = StyleSpan(Typeface.BOLD)
        STYLE_NORMAL = StyleSpan(Typeface.NORMAL)
        placesClient = mContext?.let { Places.createClient(it) }
    }
}