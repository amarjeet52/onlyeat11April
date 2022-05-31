package com.codebrew.clikat.module.crave_mania.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.DateTimeUtils
import com.codebrew.clikat.base.EmptyListListener
import com.codebrew.clikat.module.crave_mania.FdArray
import com.codebrew.clikat.module.crave_mania.SectionDataModel
import com.codebrew.clikat.module.crave_mania.SupplierDetailX
import com.codebrew.clikat.module.home_screen.adapter.BranchAdapter

class CraveManiaAllItemAdapter(internal var activity: Activity,
                               internal var supplierList: MutableList<SupplierDetailX>,
                               private val listener: EmptyListListener,
                               internal val lang_code: String)
    : RecyclerView.Adapter<CraveManiaAllItemAdapter.ViewHolder>(), Filterable {
    private lateinit var item_list: MutableList<SupplierDetailX>
    private var mCallback: CraveManiaAllItemAdapter.CraveListCallback? = null
    override fun onCreateViewHolder(viewgroup: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(viewgroup.getContext())
                .inflate(R.layout.item_supplier_crave_new_layout, viewgroup, false)

        return ViewHolder(v)
    }

    fun settingCallback(mCallback: CraveManiaAllItemAdapter.CraveListCallback?) {
        this.mCallback = mCallback
    }

    init {
        item_list = supplierList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (lang_code.equals("14")) {
            holder.tvName.setText(item_list.get(position).branch_name)
        } else {
            holder.tvName.setText(item_list.get(position).branch_name_arabic)
        }
        if (item_list.get(position).supplier_image != null || item_list.get(position).supplier_image.toString().equals("")) {
            var url = item_list.get(position).supplier_image.toString().replace(" ", "%20")
            Glide.with(activity).load(url).into(holder.sdvImage)
        } else {

        }
        if (item_list.get(position).rating == null) {

        } else {
            holder.ratingBar.rating = item_list.get(position).rating?.toFloat()
                    ?: 0f
        }
        holder.viewLayout.setOnClickListener {
            mCallback!!.onCraveListDetail(item_list, position)
        }
        holder.tv_supplier_inf.visibility = View.GONE
        if (item_list.get(position).is_free_delivery == 1) {

            holder.iv_car.visibility = View.VISIBLE
        } else {

            holder.iv_car.visibility = View.GONE
        }
        if(item_list.get(position).is_on_discount==1)
        {
            holder.iv_percentage.visibility = View.VISIBLE
        }else{
            holder.iv_percentage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return item_list.size
    }

    class ViewHolder(viewitem: View) : RecyclerView.ViewHolder(viewitem) {

        var tvName: TextView
        var tvSupplierloc: TextView
        var sdvImage: ImageView
        var iv_car: TextView
        var iv_percentage: ImageView
        var tv_pre_order: TextView
        var ratingBar: RatingBar
        var rl_close: RelativeLayout
        var rl_close_e: RelativeLayout
        var viewLayout: ConstraintLayout
        var tv_supplier_inf: TextView

        init {
            ratingBar = viewitem.findViewById(R.id.ratingBar)
            tv_supplier_inf = viewitem.findViewById(R.id.tv_supplier_inf)
            iv_car = viewitem.findViewById(R.id.iv_car)
            iv_percentage = viewitem.findViewById(R.id.iv_percentage)
            viewLayout = viewitem.findViewById(R.id.viewLayout)
            tv_pre_order = viewitem.findViewById(R.id.tv_pre_order)
            rl_close = viewitem.findViewById(R.id.rl_close)
            rl_close_e = viewitem.findViewById(R.id.rl_close_e)
            tvName = viewitem.findViewById(R.id.tvName)
            tvSupplierloc = viewitem.findViewById(R.id.tvSupplierloc)
            sdvImage = viewitem.findViewById(R.id.sdvImage)
            tv_pre_order.visibility = View.GONE
            rl_close.visibility = View.GONE
            rl_close_e.visibility = View.GONE
            //   ratingBar = viewitem.findViewById(R.id.ratingBar)
        }
    }

    interface CraveListCallback {
        fun onCraveListDetail(item_list: List<SupplierDetailX>, pos: Int)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    item_list = supplierList
                } else {
                    val filteredList = mutableListOf<SupplierDetailX>()
                    for (supplierBean in supplierList) {
                        if (supplierBean.supplier_branch_name?.toLowerCase(DateTimeUtils.timeLocale)?.contains(charSequence) == true) {
                            filteredList.add(supplierBean)
                        }
                    }
                    item_list = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = item_list
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                item_list = (filterResults.values as? MutableList<SupplierDetailX>)
                        ?: mutableListOf()

                listener.onEmptyList(item_list?.count() ?: 0)
                notifyDataSetChanged()
            }
        }
    }

}