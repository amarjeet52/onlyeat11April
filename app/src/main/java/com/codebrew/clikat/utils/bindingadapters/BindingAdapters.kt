package com.codebrew.clikat.utils.bindingadapters

import android.R
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.codebrew.clikat.BuildConfig
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.app_utils.extension.loadUserImage
import com.codebrew.clikat.modal.other.VariantValuesBean
import com.codebrew.clikat.utils.StaticFunction.changeBorderColor
import com.codebrew.clikat.utils.StaticFunction.changeCategoryColor
import com.codebrew.clikat.utils.StaticFunction.changeGradientColor
import com.codebrew.clikat.utils.StaticFunction.changeLocationStroke
import com.codebrew.clikat.utils.StaticFunction.changeStrokeColor
import com.codebrew.clikat.utils.StaticFunction.isValidColorHex
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.StaticFunction.pxToDp
import com.codebrew.clikat.utils.StaticFunction.varientColor
import com.codebrew.clikat.utils.configurations.ColorConfig
import com.codebrew.clikat.utils.configurations.Configurations
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class BindingAdapters {

    companion object {

        @JvmStatic
        @BindingAdapter(value = ["background", "viewstroke", "viewborder", "gradient"], requireAll = false)
        fun setBackgroundAdapter(view: View, background: String?, viewstroke: String?, viewborder: String?, gradient: String?) {
            if (background != null) view.setBackgroundColor(Color.parseColor(background))
            if (viewstroke != null) {
                view.background = changeLocationStroke(viewstroke, "")
            }
            if (viewborder != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.foreground = changeStrokeColor(viewborder)
                }
            }
            if (gradient != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.foreground = changeCategoryColor()
                }
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["hint", "background", "stroke", "hintcolr", "textcolor"], requireAll = false)
        fun EditText.setEdit(hint: String?, stroke: String?, background: String?, hintcolr: String?, textcolor: String?) {


            if (hint?.isNullOrBlank() == false) {
                setHint(hint)
            }

            if (hintcolr?.isNullOrBlank() == false) {
                setHintTextColor(Color.parseColor(hintcolr))
            }

            if (textcolor?.isNullOrBlank() == false) {
                setTextColor(Color.parseColor(textcolor))
            }


            if (stroke != null && background != null) {
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["background", "textcolor", "hintcolor", "whitetext", "textstroke", "gradient"], requireAll = false)
        fun setTextColor(view: TextView, background: String?, textcolor: String?, hintcolor: String?, whitetext: Boolean, textstroke: String?, gradient: String?) {
            if (textcolor != null) view.setTextColor(Color.parseColor(textcolor))
            if (background != null) view.setBackgroundColor(Color.parseColor(background))
            if (hintcolor != null) view.setHintTextColor(Color.parseColor(hintcolor))
            if (textstroke != null) view.background = changeLocationStroke(textstroke, "")
            if (whitetext) {
                view.setTextColor(Color.parseColor(Configurations.colors.appBackground))
            }
            if (gradient != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.foreground = changeGradientColor()
                }
            }
        }

        @JvmStatic
        @BindingAdapter("background")
        fun setBackgroundAdapter(view: CardView, color: String?) {
            if (color != null) view.setCardBackgroundColor(Color.parseColor(color))
        }

        @JvmStatic
        @BindingAdapter(value = ["customSrc", "userImage", "drawable"], requireAll = false)
        fun ImageView.setImageCustomSrc(src: String?, userImage: String?, drawable: Int?) {
            when {
                src != null -> {
                    loadImage(src, this, false)
                }
                userImage != null -> {
                    loadUserImage(userImage)
                }
                else -> {
                    Glide.with(this.context).load(drawable).into(this)
                }
            }

        }

        @JvmStatic
        @BindingAdapter(value = ["imageTint", "borderColor"], requireAll = false)
        fun CircleImageView.setCircularImageSrc(imageTint: String?, borderColor: String?) {
            if (imageTint != null) {
                if (isValidColorHex(imageTint)) {
                    val cf: ColorFilter = PorterDuffColorFilter(Color.parseColor(imageTint), PorterDuff.Mode.MULTIPLY)
                    colorFilter = cf
                }
            }

            if (borderColor != null) {
                setBorderColor(Color.parseColor(borderColor))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["src", "background", "changeDrawableColor", "viewborder", "tint"], requireAll = false)
        fun setImageSrc(view: ImageView, src: String?, background: String?, changeDrawableColor: Boolean, viewborder: String?, tint: String?) {
            if (changeDrawableColor && view.background != null && background != null) {
                val shapeDrawable = view.background as GradientDrawable
                shapeDrawable.setColor(Color.parseColor(background))
            } else {
                if (background != null) view.setBackgroundColor(Color.parseColor(background))
            }
            if (viewborder != null) {
                view.background = changeLocationStroke(viewborder, "")
            }
            if (tint != null) {
                if (isValidColorHex(tint)) {
                    view.setColorFilter(Color.parseColor(tint), PorterDuff.Mode.SRC_IN);
                }
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["text", "background", "strokeColor", "changeDrawableColor"], requireAll = false)
        fun changeBackgroundDrawable(view: TextView, text: String?, customBgColor: String?, strokeColor: String?, changeDrawableColor: Boolean) {
            view.text = text
            if (changeDrawableColor && view.background != null && customBgColor != null) {
                val shapeDrawable = view.background as GradientDrawable
                shapeDrawable.setColor(Color.parseColor(customBgColor))
                if (strokeColor != null && !strokeColor.isEmpty()) {
                    shapeDrawable.setStroke(pxToDp(1f, view.context).toInt(), Color.parseColor(strokeColor))
                }
            } else {
                if (customBgColor != null) view.setBackgroundColor(Color.parseColor(customBgColor))
            }

            /*   if(tint!=null)
               {
                   for (drawable in view.getCompoundDrawables()) {
                       drawable?.colorFilter = PorterDuffColorFilter( Color.parseColor(tint), PorterDuff.Mode.SRC_IN)
                   }
               }*/
        }


        @JvmStatic
        @BindingAdapter(value = ["textcolor", "checkedDrawable", "unCheckedDrawable"], requireAll = false)
        fun setImageSrc(radioButton: RadioButton, textcolor: String?, checkedDrawable: String?, unCheckedDrawable: String?) {
            radioButton.setTextColor(Color.parseColor(textcolor))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioButton.buttonTintList = ColorStateList.valueOf(Color.parseColor(checkedDrawable))
            }
            radioButton.highlightColor = Color.parseColor(unCheckedDrawable)
        }

        @JvmStatic
        @BindingAdapter(value = ["textcolor", "checkColor", "uncheckColor"], requireAll = false)
        fun setCheckBox(checkBox: CheckBox, textcolor: String?, checkColor: String?, uncheckColor: String?) {
            checkBox.setTextColor(Color.parseColor(textcolor))
            val colorStateList = ColorStateList(arrayOf(intArrayOf(-R.attr.state_checked), intArrayOf(R.attr.state_checked)), intArrayOf(
                    Color.parseColor(uncheckColor),
                    Color.parseColor(checkColor)
            ))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                checkBox.buttonTintList = colorStateList
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["activatedColor", "colorforeground", "thumbnormal"], requireAll = false)
        fun SwitchMaterial.setSwitch(activatedColor: String?, colorforeground: String?, thumbnormal: String?) {
            val states = arrayOf(intArrayOf(-R.attr.state_checked), intArrayOf(R.attr.state_checked))

            if (activatedColor != null && colorforeground != null) {
                val thumbColors = intArrayOf(
                        Color.parseColor(activatedColor),
                        Color.parseColor(colorforeground))

                DrawableCompat.setTintList(DrawableCompat.wrap(thumbDrawable), ColorStateList(states, thumbColors))
            }

            if (thumbnormal != null && colorforeground != null) {
                val trackColors = intArrayOf(
                        Color.parseColor(thumbnormal),
                        Color.parseColor(colorforeground))

                DrawableCompat.setTintList(DrawableCompat.wrap(trackDrawable), ColorStateList(states, trackColors))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["thumbcolor", "textcolor"], requireAll = false)
        fun CrystalRangeSeekbar.setSeekbar(thumbcolor: String?, textcolor: String?) {
            if (thumbcolor != null) {
                val primaryColor = Color.parseColor(thumbcolor)
                setBarHighlightColor(primaryColor)
                setBarColor(Color.parseColor(Configurations.colors.textSubhead))
                setLeftThumbColor(primaryColor)
                setRightThumbColor(primaryColor)
                setLeftThumbHighlightColor(primaryColor)
                setRightThumbHighlightColor(primaryColor)
            }
        }


        @JvmStatic
        @BindingAdapter(value = ["progessBackground"], requireAll = false)
        fun ContentLoadingProgressBar.setProgessBar(progessBackground: String?) {
            if (progessBackground != null && this.background != null) {
                progressTintList = ColorStateList.valueOf(Color.parseColor(progessBackground))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["boxStroke", "textColor"], requireAll = false)
        fun TextInputLayout.setEdit(boxStroke: String?, textColor: String?) {
            if (boxStroke != null) {
                boxStrokeColor = Color.parseColor(boxStroke)
            }

            if (textColor != null) {
                val colorStateList = ColorStateList(
                        arrayOf(
                                intArrayOf(-R.attr.state_checked), intArrayOf(
                                R.attr.state_enabled
                        )
                        ), intArrayOf(
                        Color.parseColor(textColor),
                        Color.GRAY
                )
                )

                hintTextColor = colorStateList
            }
        }


        @JvmStatic
        @BindingAdapter(value = ["background", "textcolor", "butonstroke"], requireAll = false)
        fun setButton(button: Button, background: String?, textcolor: String?, butonstroke: String?) {
            if (background != null) button.setBackgroundColor(Color.parseColor(background))
            if (textcolor != null) button.setTextColor(Color.parseColor(textcolor))
            if (butonstroke != null) button.background = changeBorderColor(butonstroke, "", GradientDrawable.RECTANGLE)
        }

        @JvmStatic
        @BindingAdapter(value = ["tabSelectedText", "tabTextColr", "tabIndicateColr", "background"], requireAll = false)
        fun TabLayout.setTabLayout(tabSelectedText: String?, tabTextColr: String?, tabIndicateColr: String?, background: String?) {


            if (tabIndicateColr != null) setSelectedTabIndicatorColor(Color.parseColor(tabIndicateColr))
            if (tabSelectedText != null) setTabTextColors(Color.parseColor(tabTextColr), Color.parseColor(tabSelectedText))

            if (background != null) setBackgroundColor(Color.parseColor(background))
        }

        @JvmStatic
        @BindingAdapter(value = ["textcolor", "background"], requireAll = false)
        fun MaterialButton.setMaterialButton(textColor: String?, background: String?) {
            setTextColor(Color.parseColor(textColor))
            setBackgroundColor(Color.parseColor(background))
        }

        @JvmStatic
        @BindingAdapter(value = ["ratecolor", "deliveryTime", "openStatus", "isHunger", "isOpenStatus"], requireAll = false)
        fun TextView.setRtaingColor(rating: Double, deliveryTime: Int, openStatus: Boolean, isHunger: Boolean, isOpenStatus: Boolean) {


            if (deliveryTime == 0) {
                val color: String = when (String.format(DateTimeUtils.timeLocale, "%.0f", rating)) {
                    "1" -> "#c68f9a"
                    "2" -> "#FABC0E"
                    "3" -> "#CDD814"
                    "4", "5" -> "#63AA27"
                    else -> "#8A959B"
                }

                if (BuildConfig.CLIENT_CODE == "craveqatar123_0896")
                    background = changeLocationStroke(Configurations.colors.textAppTitle?:"#357EB6", "")
                else
                    background = changeLocationStroke(color, "")
            }

            if (isOpenStatus) {
                when (openStatus) {
                    true -> {
                        text = ""//this.context.getString(com.codebrew.clikat.R.string.open_)
                        if (!isHunger) {
                           // setTextColor(ContextCompat.getColor(this.context, com.codebrew.clikat.R.color.username4))
                              }
                        setBackgroundColor(Color.TRANSPARENT)
                    }
                    else -> {
                       text = this.context.getString(com.codebrew.clikat.R.string.close)
                        if (!isHunger) {
                            setTextColor(ContextCompat.getColor(this.context, com.codebrew.clikat.R.color.blueColor))
                          setBackgroundResource(com.codebrew.clikat.R.drawable.button_bg_yellow)
                           // setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.getDrawable(this.context, com.codebrew.clikat.R.drawable.ic_status_offline), null, null, null)
                        }
                    }
                }

               // setBackgroundColor(Color.TRANSPARENT)
            }


            if (deliveryTime > 0) {
                val calendar = Calendar.getInstance()

                calendar.clear()
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.MINUTE] = 0
                calendar[Calendar.DAY_OF_MONTH] = 1

                calendar.add(Calendar.MINUTE, deliveryTime)

                text = String.format("%s %s %s", if (calendar[Calendar.DAY_OF_MONTH] - 1 > 0) context.getString(com.codebrew.clikat.R.string.day_tag, calendar[Calendar.DAY_OF_MONTH] - 1)
                else "", if (calendar[Calendar.HOUR_OF_DAY] > 0) context.getString(com.codebrew.clikat.R.string.hour_tag, calendar[Calendar.HOUR_OF_DAY]) else "", if (calendar[Calendar.MINUTE] > 0)
                    context.getString(com.codebrew.clikat.R.string.minute_tag, calendar[Calendar.MINUTE]) else "").trim()

            }
        }


        @JvmStatic
        @BindingAdapter(value = ["background"], requireAll = false)
        fun TextView.setBackgroundAdapter(background: Boolean?) {
            if (background == true) {
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.parseColor("#01AFFF"))
            } else {
                setTextColor(Color.BLACK)
                setBackgroundColor(Color.WHITE)
            }
        }


        @JvmStatic
        @BindingAdapter(value = ["varient"], requireAll = false)
        fun TextView.setVarientItem(varient: VariantValuesBean?) {

            if (varient?.variant_type == 1 && varient.variant_name != "Size") {
                background = varientColor(varient.variant_value
                        ?: "", Configurations.colors.primaryColor
                        ?: "", GradientDrawable.RADIAL_GRADIENT)
            } else {
                text = varient?.variant_value
            }

        }


        @JvmStatic
        @BindingAdapter(value = ["deActivatedColor", "activatedColor", "background"], requireAll = false)
        fun BottomNavigationView.setAdapter(deActivatedColor: String?, activatedColor: String?, background: String?) {
            if (deActivatedColor != null && activatedColor != null) {
                val states = arrayOf(intArrayOf(-R.attr.state_checked), intArrayOf(R.attr.state_checked), intArrayOf())

                // Fill in color corresponding to state defined in state
                val colors = intArrayOf(
                        Color.parseColor(deActivatedColor),
                        Color.parseColor(activatedColor),
                        Color.parseColor(deActivatedColor))

                val navigationViewColorStateList = ColorStateList(states, colors)

                itemIconTintList = navigationViewColorStateList
                itemTextColor = navigationViewColorStateList
            }

            if (background != null) {
                setBackgroundColor(Color.parseColor(background))
            }
        }


        @JvmStatic
        @BindingAdapter(value = ["background", "textcolor"], requireAll = false)
        fun FloatingActionButton.setAdapter(background: String?, textcolor: String?) {
            if (background != null && textcolor != null) {

                setBackgroundColor(Color.parseColor(background))
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["background", "textcolor"], requireAll = false)
        fun Toolbar.setToolbar(background: String?, textcolor: String?) {
            setBackgroundColor(Color.parseColor(background))
            setTitleTextColor(Color.parseColor(textcolor))
        }

        /* @JvmStatic
         @BindingAdapter(value = ["android:layout_width", "android:layout_height", "isMatchParent"], requireAll = false)
         fun View.setLayoutHeightWidth(isMatchParent: Boolean,height:Int, width:Int) {

             val params: ViewGroup.LayoutParams = layoutParams
             if (isMatchParent) {
                 params.width = if (isMatchParent) MATCH_PARENT else WRAP_CONTENT
             } else {
                 layoutParams.height = height
                 layoutParams.width = width
             }
             layoutParams = params
         }*/

    }
}

