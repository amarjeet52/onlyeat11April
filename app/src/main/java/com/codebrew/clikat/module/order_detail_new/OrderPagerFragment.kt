package com.codebrew.clikat.module.order_detail_new

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.utils.OnMapAndViewReadyListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

/**
 * A simple [Fragment] subclass.
 * Use the [OrderPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderPagerFragment : Fragment(), OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    private lateinit var map: GoogleMap
    private var mParam1: OrderHistory? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "orderDetail"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment OrderPagerFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(orderHistory: OrderHistory?): OrderPagerFragment {
            val fragment = OrderPagerFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, orderHistory)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
    }
}