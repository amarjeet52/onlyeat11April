package com.codebrew.clikat.module.home_screen.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemSortByBinding
import com.codebrew.clikat.databinding.ItemSupplierViewBinding
import com.codebrew.clikat.modal.other.CategoryWiseSuppliers
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SortByData
import com.codebrew.clikat.modal.other.SupplierDataBean
import com.codebrew.clikat.module.home_screen.listeners.OnSortByListenerClicked
import com.codebrew.clikat.utils.configurations.Configurations

class CategoryWiseSuppliersAdapter internal constructor(private val list: List<CategoryWiseSuppliers>,
                                                       private val mScreenType: Int,
                                                       private val clientInform: SettingModel.DataBean.SettingData?,
                                                       private val parentPos: Int,
                                                       private val tableBooked: Boolean)
    : RecyclerView.Adapter<CategoryWiseSuppliersAdapter.ViewHolder>() {

    private var mCallback: OnSortByListenerClicked? = null

    fun settingCallback(mCallback: OnSortByListenerClicked) {
        this.mCallback = mCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = DataBindingUtil.inflate<ItemSupplierViewBinding>(LayoutInflater.from(parent.context),
                R.layout.item_supplier_view, parent, false)
        binding.color = Configurations.colors
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.onBind(item)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val mView: ItemSupplierViewBinding) : RecyclerView.ViewHolder(mView.root) {

        init {
            itemView.setOnClickListener {
                mCallback?.onItemSelected(adapterPosition)
            }
        }

        fun onBind(item: CategoryWiseSuppliers) {
            mView.tvTitle.text = item.category_name
            val listAdapter=NearBySuppliersAdapter(item.suppliers ?: listOf(), mScreenType, clientInform, parentPos, tableBooked = tableBooked,appUtils = null,deliveryType = null)
            mView.rvRecomdSupplier.apply {
                layoutManager=LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = listAdapter
            }
        }
    }




}
