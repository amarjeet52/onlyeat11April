package com.codebrew.clikat.module.restaurant_detail.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.databinding.ItemNewMenuCategoryBinding
import com.codebrew.clikat.modal.other.ProductBean
import com.codebrew.clikat.module.restaurant_detail.adapter.MenuCategoryAdapter.MenuCategoryCallback
import com.codebrew.clikat.utils.configurations.Configurations

class NewMenuCategoryListAdapter
    : RecyclerView.Adapter<NewMenuCategoryListAdapter.ViewHolder>() {

    private var list = ArrayList<ProductBean>()
    private var mCallback: MenuCategoryCallback? = null

    fun settingCallback(mCallback: MenuCategoryCallback) {
        this.mCallback = mCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = DataBindingUtil.inflate<ItemNewMenuCategoryBinding>(LayoutInflater.from(parent.context),
                R.layout.item_new_menu_category, parent, false)
        binding.color = Configurations.colors
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.onBind(item)
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(val mView: ItemNewMenuCategoryBinding) : RecyclerView.ViewHolder(mView.root) {

        init {
            mView.tvName.setOnClickListener {
                mCallback?.getMenuCategory(adapterPosition)
            }
        }

        fun onBind(item: ProductBean) {
            mView.tvName.text = item.sub_cat_name
            if(!item.value.isNullOrEmpty())
            {
                mView.tvCount.visibility= View.GONE
                mView.tvCount.text = item.value?.size.toString()
            }else  mView.tvCount.visibility= View.GONE
        }
    }

    fun addList(list: ArrayList<ProductBean>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }


}
