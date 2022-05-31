package com.codebrew.clikat.module.restaurant_detail.adapter.expandable

import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.extension.loadUserImage
import com.codebrew.clikat.databinding.ItemParentDataBinding
import com.codebrew.clikat.module.restaurant_detail.model.ParentCategoryModel
import com.google.gson.Gson
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder


class ParentDataHolder(val binding: ItemParentDataBinding,private val expandClickListener : ExapandableProdAdapter.ExpandCollapseListener,private val langCode:String) : GroupViewHolder(binding.root) {

    override fun expand() {
        animateExpand()
        expandClickListener.onExpand(adapterPosition)
    }

    override fun collapse() {
        animateCollapse()
    }

    private fun animateExpand() {
        val rotate = RotateAnimation(360f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
    }

    private fun animateCollapse() {
        val rotate = RotateAnimation(180f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
    }

    fun setGenreTitle(gson: Gson, group: ExpandableGroup<*>) {

        val parentCategory=gson.fromJson(group.title,ParentCategoryModel::class.java)

        binding.tvCategory.text=parentCategory.categoryName
        Log.e("long_code",langCode)
        if(langCode.equals("14"))
        {

            binding.imageView14.rotationY=360F
        }else{
            binding.imageView14.rotationY=180F

        }
        binding.ivProduct.loadUserImage(parentCategory.categoryImg)
    }

}