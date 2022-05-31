package com.codebrew.clikat.module.rental.rental_checkout

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.extension.onSnackbar
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.data.constants.AppConstants
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.model.others.HomeRentalParam
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.FragmentRentalCheckoutBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.modal.other.PlaceOrderInput
import com.codebrew.clikat.preferences.DataNames
import com.codebrew.clikat.preferences.Prefs
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_rental_checkout.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val ARG_PARAM1 = "rentalParam"

class RentalCheckoutFrag : BottomSheetDialogFragment(), CheckoutNavigator {
    private var homeRentalModel: HomeRentalParam? = null


    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils


    @Inject
    lateinit var prefHelper: PreferenceHelper

    private var mViewModel: CheckoutViewModel? = null

    private var mBinding: FragmentRentalCheckoutBinding? = null

    var baseActivity: BaseActivity<*, *>? = null
        private set

    val isNetworkConnected: Boolean
        get() = baseActivity != null && baseActivity?.isNetworkConnected == true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*, *>) {
            this.baseActivity = context
            context.onFragmentAttached()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel?.navigator = this

        arguments?.let {
            homeRentalModel = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rental_checkout, container, false)
        AndroidSupportInjection.inject(this)
        mViewModel = ViewModelProviders.of(this, factory).get(CheckoutViewModel::class.java)
        mBinding?.viewModel = mViewModel

        mViewModel?.navigator = this

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding?.color = Configurations.colors

        tv_pickup_adrs.text = homeRentalModel?.from_address
        tv_pickup_date_time.text = appUtils.convertDateOneToAnother(homeRentalModel?.booking_from_date
                ?: "", "yyyy-MM-dd HH:mm:ss", "EEEE dd, yyyy - hh:mm aa")
        tv_total.text = getString(R.string.currency_tag, AppConstants.CURRENCY_SYMBOL, homeRentalModel?.totalAmt)

        val placeOrderInput = PlaceOrderInput()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", DateTimeUtils.timeLocale)
        placeOrderInput.accessToken = prefHelper.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString()
        placeOrderInput.offset = SimpleDateFormat("ZZZZZ", DateTimeUtils.timeLocale).format(System.currentTimeMillis())
        placeOrderInput.cartId = Prefs.with(activity).getString(DataNames.CART_ID, "0")
        placeOrderInput.paymentType = DataNames.PAYMENT_CASH
        placeOrderInput.languageId = Prefs.with(activity).getInt(DataNames.SELECTED_LANGUAGE, 14)

        val calendar = Calendar.getInstance()
        placeOrderInput.booking_date_time = dateFormat.format(calendar.time)
        placeOrderInput.duration = 0
        placeOrderInput.from_address = homeRentalModel?.from_address
        placeOrderInput.to_address = homeRentalModel?.to_address
        placeOrderInput.booking_from_date = homeRentalModel?.booking_from_date
        placeOrderInput.booking_to_date = homeRentalModel?.booking_to_date
        placeOrderInput.from_latitude = homeRentalModel?.from_latitude
        placeOrderInput.to_latitude = homeRentalModel?.to_latitude
        placeOrderInput.from_longitude = homeRentalModel?.from_longitude
        placeOrderInput.to_longitude = homeRentalModel?.to_longitude

        btn_place_order.setOnClickListener {
            if (isNetworkConnected) {
                mViewModel?.generateOrder(placeOrderInput)
            }
        }
    }

    override fun onOrderPlaced() {
        NavHostFragment.findNavController(this).navigate(R.id.action_rentalCheckoutFrag_to_mainFragment)
    }

    override fun onErrorOccur(message: String) {
        mBinding?.root?.onSnackbar(message)
    }

    override fun onSessionExpire() {
        baseActivity?.openActivityOnTokenExpire()
    }
}
