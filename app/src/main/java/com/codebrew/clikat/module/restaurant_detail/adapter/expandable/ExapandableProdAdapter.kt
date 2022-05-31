package com.codebrew.clikat.module.restaurant_detail.adapter.expandable

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.databinding.ItemParentDataBinding
import com.codebrew.clikat.databinding.ItemSupplierProductBinding
import com.codebrew.clikat.modal.other.ProductBean
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.module.restaurant_detail.RestaurantDetailFrag
import com.codebrew.clikat.module.restaurant_detail.adapter.ProdListAdapter
import com.codebrew.clikat.module.restaurant_detail.model.Genre
import com.codebrew.clikat.utils.configurations.ColorConfig
import com.codebrew.clikat.utils.configurations.TextConfig
import com.google.gson.Gson
//import com.stripe.android.model.AccountParams.BusinessTypeParams.*
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition.GROUP
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*
import kotlin.collections.ArrayList


class ExapandableProdAdapter(
        private val mCallback: ProdListAdapter.ProdCallback,
        private val isOpen: Boolean,
        private var groups_list: List<ExpandableGroup<*>>?,
        private val clientData: SettingModel.DataBean.SettingData? = null,
        val selectedCurrency: Currency?,
        private val textConfig: TextConfig?,
        private val colorConfig: ColorConfig,
        private val gson: Gson,
        private val mFrag: RestaurantDetailFrag,
private val langCode:String) :
        ExpandableRecyclerViewAdapter<ParentDataHolder, ChildProdListHolder>(groups_list) {


    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): ParentDataHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemParentDataBinding.inflate(layoutInflater, parent, false)
        return ParentDataHolder(binding, mFrag,langCode)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): ChildProdListHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSupplierProductBinding.inflate(layoutInflater, parent, false)
        return ChildProdListHolder(binding)
    }

    override fun onBindChildViewHolder(
            holder: ChildProdListHolder, flatPosition: Int,
            group: ExpandableGroup<*>, childIndex: Int
    ) {
        val artist = (group as Genre).items[childIndex]
        holder.setArtistName(artist, isOpen, clientData, mCallback, selectedCurrency = selectedCurrency, colorConfig = colorConfig, textConfig = textConfig)
    }

    override fun onBindGroupViewHolder(
            holder: ParentDataHolder, flatPosition: Int,
            group: ExpandableGroup<*>
    ) {
        holder.setGenreTitle(gson, group)
    }

    fun getAdapterList(): List<ExpandableGroup<*>> {
        return groups
    }


    interface ExpandCollapseListener {
        fun onExpand(position: Int)
    }

    fun onCollapse() {
        for (i in itemCount - 1 downTo 0) {
            if (i <= itemCount) {
                if (getItemViewType(i) == GROUP && isGroupExpanded(i)) {
                    toggleGroup(i)
                }
            }
        }
    }

    fun filter(text: String?) {
        val temp = mutableListOf<ExpandableGroup<*>>()

        if (text?.isNotEmpty() == true) {
            for (group in mFrag.getFilterList()) {

                val updatedGroup= group to group

                val list = (updatedGroup.second.items as MutableList<ProductDataBean>).filter { it.name?.toLowerCase(Locale.ENGLISH)?.contains(text.toString()) == true }

                if (list.isNotEmpty()) {
                    try {
                        updatedGroup.second.items.clear()
                        (updatedGroup.second.items as MutableList<ProductDataBean>).addAll(list.toList())
                        temp.add(updatedGroup.second)
                    }catch (e:Exception){}
                }
            }
        } else {
            temp.addAll(mFrag.getFilterList().toList())
        }
        expandableList.groups = temp.toList()
        notifyDataSetChanged()
    }


    fun getGroupPositionWhenExpand(group: ExpandableGroup<*>?): Int {
        return expandableList.getFlattenedGroupIndex(group)
    }

    fun getGroupPositionWhenCollapse(group: ExpandableGroup<*>?): Int {
        val listPosition: ExpandableListPosition =
            expandableList.getUnflattenedPosition(expandableList.getFlattenedGroupIndex(group))
        var previousGroupPosition = listPosition.groupPos - 1
        if (previousGroupPosition <= 0) {
            previousGroupPosition = 0
        }
        return expandableList.getFlattenedGroupIndex(previousGroupPosition)
    }

    fun getGroupItemRealPosition(indexGroup: Int) : Int {
        var group = 0
        for (i in 0..itemCount) {
            if (getItemViewType(i) == GROUP) {
                group++
            }
            if (group > indexGroup) {
                return i
            }
        }
        return -1
    }


}
