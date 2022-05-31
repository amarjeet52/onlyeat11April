package com.codebrew.clikat.module.rating

import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.codebrew.clikat.BR
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.base.BaseActivity
import com.codebrew.clikat.base.BaseInterface
import com.codebrew.clikat.data.DataManager
import com.codebrew.clikat.data.constants.PrefenceConstants
import com.codebrew.clikat.data.preferences.PreferenceHelper
import com.codebrew.clikat.databinding.LayoutRatingFragBinding
import com.codebrew.clikat.di.ViewModelProviderFactory
import com.codebrew.clikat.module.order_detail.rate_product.RateViewModel

import com.codebrew.clikat.module.questions.main.QuestionsViewModel
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector

import javax.inject.Inject


class RatingFragment : BaseActivity<LayoutRatingFragBinding, RateViewModel>(), RatingBar.OnRatingBarChangeListener {
    private var mBinding: LayoutRatingFragBinding? = null
    var order_id=""
    var name=""
    var image=""
    var rate: Int=0
    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var appUtils: AppUtils
    @Inject
    lateinit var dataManager: DataManager
    private var mViewModel: RateViewModel? = null
    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=DataBindingUtil.setContentView(this,R.layout.layout_rating_frag)
        try{

            name=intent.getStringExtra("name")?: ""
            image=intent.getStringExtra("image")?: ""
            order_id=intent.getStringExtra("orderId")?: ""
            mBinding!!.tvCafeName.text=name!!
            Picasso.get().load(image).into(mBinding!!.ivCup)
        }catch (e:Exception)
        {

        }
        rateProductObserver()
        mBinding!!.rlSkip.setOnClickListener {
            finish()
        }
        mBinding!!. rvRating.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {

                var i=progress
                val stepSize = 25
                i = i / stepSize * stepSize
               // Toast.makeText(applicationContext,""+ i+" ", Toast.LENGTH_SHORT).show()

                seekBar.progress = i
                if(i==0)
                {
                    rate=1
                    mBinding!!.rlRateCard.setBackgroundResource(R.drawable.red_rate_gradient)
                    mBinding!!.rlBottom.setBackgroundResource(R.drawable.red_round_button)

                    mBinding!!.tvStatus1.setBackgroundResource(R.drawable.awful_text)
                    mBinding!!.ivSmiley.setBackgroundResource(R.drawable.awful_emoji)
                    mBinding!!.tvStatus.setTextColor(ContextCompat.getColor(this@RatingFragment, R.color.brown_dark))
                    mBinding!!.tvStatus.text = "Awful"
                }else if(i==25)
                {
                    rate=2
                    mBinding!!.rlRateCard.setBackgroundResource(R.drawable.brown_rate_gradient)
                    mBinding!!.rlBottom.setBackgroundResource(R.drawable.dark_brown)

                    mBinding!!.tvStatus1.setBackgroundResource(R.drawable.bad_text)
                    mBinding!!.ivSmiley.setBackgroundResource(R.drawable.bad_emoji)
                    mBinding!!.tvStatus.setTextColor(ContextCompat.getColor(this@RatingFragment, R.color.yellow))
                    mBinding!!.tvStatus.text = "Bad"
                }else if(i==50){
                    rate=3
                    mBinding!!.rlRateCard.setBackgroundResource(R.drawable.orange_rate_gradient)
                    mBinding!!.rlBottom.setBackgroundResource(R.drawable.dark_brown)

                    mBinding!!.tvStatus1.setBackgroundResource(R.drawable.okay_text)
                    mBinding!!.ivSmiley.setBackgroundResource(R.drawable.okay_emoji)
                    mBinding!!.tvStatus.setTextColor(ContextCompat.getColor(this@RatingFragment, R.color.yellow))
                    mBinding!!.tvStatus.text = "Okay"
                }else if(i==75){
                    rate=4
                    mBinding!!.rlRateCard.setBackgroundResource(R.drawable.yellow_rate_gradient)
                    mBinding!!.rlBottom.setBackgroundResource(R.drawable.dark_brown)

                    mBinding!!.tvStatus1.setBackgroundResource(R.drawable.good_text)
                    mBinding!!.ivSmiley.setBackgroundResource(R.drawable.good_emoji)
                    mBinding!!.tvStatus.setTextColor(ContextCompat.getColor(this@RatingFragment, R.color.yellow))
                    mBinding!!.tvStatus.text = "Good"
                }
                else if(i==100){
                    rate=5
                    mBinding!!.ivSmiley.setBackgroundResource(R.drawable.great_emoji)
                    mBinding!!.rlRateCard.setBackgroundResource(R.drawable.green_rate_gradient)
                    mBinding!!.rlBottom.setBackgroundResource(R.drawable.dark_green)

                    mBinding!!.tvStatus1.setBackgroundResource(R.drawable.great_text)
                    mBinding!!.tvStatus.setTextColor(ContextCompat.getColor(this@RatingFragment, R.color.green_rate))
                    mBinding!!.tvStatus.text = "Excelent"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //Toast.makeText(applicationContext, "seekbar touch started!", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //Toast.makeText(applicationContext, "seekbar touch stopped!", Toast.LENGTH_SHORT).show()
            }
        })


        mBinding!!.tvSubmit.setOnClickListener {
            mBinding!!.progressBar.visibility= View.VISIBLE
            val hashMap = hashMapOf<String, String>("accessToken" to dataManager.getKeyValue(PrefenceConstants.ACCESS_TOKEN, PrefenceConstants.TYPE_STRING).toString(),
                    "orderId" to order_id, "rating_type" to  mBinding!!.tvStatus.text.toString(),
                    "rating_text" to mBinding!!.etText.text.toString(),
                    "rating_number" to rate.toString())

            mViewModel?.RateOrderApi(hashMap)
        }
    }
    private fun rateProductObserver() {

        val catObserver = Observer<String> { resource ->
            mBinding!!.progressBar.visibility= View.GONE
finish()

        }
        viewModel.rateOrderData.observe(this, catObserver)
    }


    override fun onRatingChanged(p0: RatingBar?, p1: Float, p2: Boolean) {
         rate = p1.toInt();
        if (rate == 5) {
            mBinding!!.rlRateCard.setBackgroundResource(R.drawable.green_rate_gradient)
            mBinding!!.rlBottom.setBackgroundResource(R.drawable.dark_green)

            mBinding!!.tvStatus1.setBackgroundResource(R.drawable.great_text)
            mBinding!!.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.green_rate))
            mBinding!!.tvStatus.text = "Excelent"
        } else if (rate == 4) {
            mBinding!!.rlRateCard.setBackgroundResource(R.drawable.yellow_rate_gradient)
            mBinding!!.rlBottom.setBackgroundResource(R.drawable.dark_brown)

            mBinding!!.tvStatus1.setBackgroundResource(R.drawable.good_text)
            mBinding!!.tvStatus.setTextColor(ContextCompat.getColor(this@RatingFragment, R.color.yellow))
            mBinding!!.tvStatus.text = "Good"
        } else if (rate == 3) {
            mBinding!!.rlRateCard.setBackgroundResource(R.drawable.orange_rate_gradient)
            mBinding!!.rlBottom.setBackgroundResource(R.drawable.dark_brown)

            mBinding!!.tvStatus1.setBackgroundResource(R.drawable.okay_text)
            mBinding!!.tvStatus.setTextColor(ContextCompat.getColor(this@RatingFragment, R.color.yellow))
            mBinding!!.tvStatus.text = "Okay"
        } else if (rate == 2) {
            mBinding!!.rlRateCard.setBackgroundResource(R.drawable.brown_rate_gradient)
            mBinding!!.rlBottom.setBackgroundResource(R.drawable.dark_brown)

            mBinding!!.tvStatus1.setBackgroundResource(R.drawable.bad_text)
            mBinding!!.tvStatus.setTextColor(ContextCompat.getColor(this@RatingFragment, R.color.yellow))
            mBinding!!.tvStatus.text = "Bad"
        } else {
            mBinding!!.rlRateCard.setBackgroundResource(R.drawable.red_rate_gradient)
            mBinding!!.rlBottom.setBackgroundResource(R.drawable.red_round_button)

            mBinding!!.tvStatus1.setBackgroundResource(R.drawable.awful_text)
            mBinding!!.tvStatus.setTextColor(ContextCompat.getColor(this@RatingFragment, R.color.red))
            mBinding!!.tvStatus.text = "Awful"
        }
    }

    override fun getBindingVariable(): Int {
   return  BR.viewModel
    }

    override fun getLayoutId(): Int {

        return R.layout.layout_rating_frag
    }

    override fun getViewModel(): RateViewModel {
        mViewModel = ViewModelProviders.of(this, factory).get(RateViewModel::class.java)
        return mViewModel as RateViewModel
    }


}