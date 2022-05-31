package com.codebrew.clikat.module.supplier_detail.supplier_tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.codebrew.clikat.R
import com.codebrew.clikat.adapters.ReviewsAdapter
import com.codebrew.clikat.app_utils.extension.loadPlaceHolder
import com.codebrew.clikat.data.model.api.supplier_detail.DataSupplierDetail
import com.codebrew.clikat.data.model.api.supplier_detail.ReviewList
import com.codebrew.clikat.databinding.FragmentTabReviewBinding
import com.codebrew.clikat.modal.ExampleCommon
import com.codebrew.clikat.module.login.LoginActivity
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.retrofit.RestClient
import com.codebrew.clikat.utils.*
import com.codebrew.clikat.utils.Dialogs.RatingDialog
import com.codebrew.clikat.utils.configurations.Configurations
import com.codebrew.clikat.utils.customviews.ClikatTextView
import kotlinx.android.synthetic.main.nothing_found.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private const val ARG_PARAM1 = "supplier_data"

class TabReviews : Fragment() {


    private var recyclerView: RecyclerView? = null


    private var reviewsAdapter: ReviewsAdapter? = null

    private var reviewList: MutableList<ReviewList>? = null


    private var param1: DataSupplierDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val binding = DataBindingUtil.inflate<FragmentTabReviewBinding>(inflater, R.layout.fragment_tab_review, container, false)
        binding.color = Configurations.colors
        binding.string = Configurations.strings

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rvParent_home)
        val noData = view.findViewById<View>(R.id.noData)

        val tv_total_rating = view.findViewById<TextView>(R.id.tv_total_rating)
        val rate_supplier_tv = view.findViewById<ClikatTextView>(R.id.rate_supplier_tv)
        val tv_total_reviews = view.findViewById<ClikatTextView>(R.id.tv_total_reviews)

        setData()

        //        ((MainActivity) getActivity()).setSupplierImage(false, "", 0, "", 0, 0);

        // tvDesc.setText(Html.fromHtml(data.getReviews()));

        if (param1?.reviewList != null) {

            reviewList?.clear()

            reviewList?.addAll(param1?.reviewList ?: emptyList())

            reviewsAdapter?.notifyDataSetChanged()
        }


        rate_supplier_tv.setOnClickListener { v ->

            val pojoLoginData = StaticFunction.isLoginProperly(activity)

            if (pojoLoginData.data != null) {

                param1?.myReview?.userImage = pojoLoginData.data.user_image
                param1?.myReview?.comment = ""
                param1?.myReview?.firstname = pojoLoginData.data.firstname
                param1?.myReview?.rating = 0f

                val dialog = RatingDialog(activity, true, { comment, rating -> supplierRatingApi(rating, comment) }, param1?.myReview)

                dialog.show()

            } else {
                startActivityForResult(Intent(activity, LoginActivity::class.java), DataNames.REQUEST_HOME_SCREEN)
            }

        }



        assert(param1?.reviewList != null)
        if (param1?.reviewList?.size == 0) {
            recyclerView!!.visibility = View.GONE
            noData.visibility = View.VISIBLE

            tv_total_rating.visibility = View.GONE
            tv_total_reviews.visibility = View.GONE
        } else {
            recyclerView!!.visibility = View.VISIBLE
            noData.visibility = View.GONE

            tv_total_rating.visibility = View.VISIBLE
            tv_total_reviews.visibility = View.VISIBLE

            tv_total_rating.text = (param1?.rating?:0).toString()
            tv_total_reviews.text = getString(R.string.reviews_text_, param1?.totalReviews?.toFloat() ?: 0.0f)

        }


    }

    private fun setData() {

        reviewList = ArrayList()

        recyclerView!!.layoutManager = LinearLayoutManager(activity)

        val itemDecoration: ItemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        recyclerView?.addItemDecoration(itemDecoration)

        reviewsAdapter = ReviewsAdapter(activity, reviewList)

        recyclerView!!.adapter = reviewsAdapter


    }

    private fun supplierRatingApi(rating: Float, comment: String) {
        val barDialog = ProgressBarDialog(activity)
        barDialog.show()
        val hashMap = HashMap<String, String>()
        hashMap["accessToken"] = StaticFunction.getAccesstoken(activity)
        //  hashMap["supplierId"] = "" + (activity as MainActivity).supplierId
        hashMap["rating"] = "" + rating
        if (comment != "")
            hashMap["comment"] = comment

        val call = RestClient.getModalApiService(activity).supplierRating(hashMap)

        call.enqueue(object : Callback<ExampleCommon> {
            override fun onResponse(call: Call<ExampleCommon>, response: Response<ExampleCommon>) {
                barDialog.dismiss()

                val success = response.body()

                if (response.code() == 200 && success!!.status == ClikatConstants.STATUS_SUCCESS) {

                    param1?.myReview?.comment = comment
                    param1?.myReview?.rating = rating

                    val myReview = ReviewList(param1?.myReview?.comment, param1?.myReview?.rating!!, param1?.myReview?.firstname, param1?.myReview?.userImage)
                    reviewList!!.add(myReview)
                    reviewsAdapter?.notifyDataSetChanged()

                    GeneralFunctions.showSnackBar(view, success.message, activity)

                } else {
                    GeneralFunctions.showSnackBar(view, success!!.message, activity)
                }

            }

            override fun onFailure(call: Call<ExampleCommon>, t: Throwable) {
                barDialog.dismiss()
            }
        })
    }


    companion object {

        @JvmStatic
        fun newInstance(supplierDetail: DataSupplierDetail?) =
                TabReviews().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_PARAM1, supplierDetail)
                    }
                }
    }


}
