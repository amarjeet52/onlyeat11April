package com.codebrew.clikat.module.roadmap

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.base.BaseFragment
import com.codebrew.clikat.databinding.FragmentRoadMapBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.searchProduct.SearchViewModel
import com.codebrew.clikat.module.splash.SplashActivity
import kotlinx.android.synthetic.main.fragment_road_map.*
import javax.inject.Inject


class RoadMapFragment : BaseFragment<FragmentRoadMapBinding, SearchViewModel>() {

    @Inject
    lateinit var factory: ViewModelProviderFactory

    private lateinit var viewModel: SearchViewModel

    private lateinit var mBinding: FragmentRoadMapBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }



    private fun setClickListeners() {
        ivDelivery?.setOnClickListener {
            goToNextFragment("Delivery")
        }

        ivBookTable?.setOnClickListener {
//            goToNextFragment("Book Table")
            var intent = Intent()
            intent.putExtra("isFromRoadMap",true)
            startActivity(intent)
        }

        ivPickUp.setOnClickListener {
            goToNextFragment("Pick Up")
        }

        ivSignature.setOnClickListener {
//            goToNextFragment("Signature")
            var intent = Intent()
            intent.putExtra("isFromRoadMap",true)
            startActivity(intent)
        }

        ivCraveMania.setOnClickListener {
//            goToNextFragment("Crave Mania")
            var intent = Intent()
            intent.putExtra("isFromRoadMap",true)
            startActivity(intent)
        }
    }

    private fun goToNextFragment(s: String) {
        val bundle = bundleOf("type" to s)
        findNavController().navigate(R.id.action_roadMapFragment_to_mainFragment,bundle)

    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_road_map

    }

    override fun getViewModel(): SearchViewModel {
        viewModel = ViewModelProviders.of(this, factory).get(SearchViewModel::class.java)
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding = viewDataBinding

        setClickListeners()


    }


}