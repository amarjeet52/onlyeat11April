package com.codebrew.clikat.module.dialog_adress.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.data.model.api.AddressBean
import com.codebrew.clikat.data.model.api.vehicleDetails.VehicleData
import com.codebrew.clikat.databinding.ListSearchFooterBinding
import com.codebrew.clikat.databinding.ListSearchHeaderBinding
import com.codebrew.clikat.databinding.ListSearchItemBinding
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1
private const val ITEM_VIEW_TYPE_FOOTER = 2

class VehicleDEtailAdapter(internal var vehicle_list:List<VehicleData>, internal var mListener: Callback?)
        : RecyclerView.Adapter<VehicleDEtailAdapter.ViewHolder>(){

    private val adapterScope = CoroutineScope(Dispatchers.Default)


    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.tv_customer_adrs.setText(vehicle_list.get(position).name.toString() ?: "")
            holder.tv_adrs_line_first.setText(vehicle_list.get(position).number_plate.toString()
                    ?: "" + "," + vehicle_list.get(position).color ?: "")
        }catch (e:Exception){}
   holder.imageView4.setOnClickListener{
       mListener?.onDeleteClicked(vehicle_list.get(position).id)
   }
        holder.tv_customer_adrs.setOnClickListener{
            holder.rbSelect.isChecked=true
            mListener?.onItemClicked(position)
        }
        holder.tv_adrs_line_first.setOnClickListener{
            holder.rbSelect.isChecked=true
            mListener?.onItemClicked(position)
        }
        holder.rbSelect.setOnClickListener{
            holder.rbSelect.isChecked=true
            mListener?.onItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return vehicle_list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_vehicle_item, parent, false)

        return ViewHolder(v)
    }

class ViewHolder(viewitem: View) : RecyclerView.ViewHolder(viewitem) {

    var tv_adrs_line_first: TextView
var imageView4: ImageView
    var tv_customer_adrs: TextView
    var rbSelect:RadioButton
    init {
        imageView4 = viewitem.findViewById(R.id.imageView4)
        tv_adrs_line_first = viewitem.findViewById(R.id.tv_adrs_line_first)
        tv_customer_adrs = viewitem.findViewById(R.id.tv_customer_adrs)
        rbSelect = viewitem.findViewById(R.id.rbSelect)
    }
}


interface Callback {
    fun onItemClicked( position:Int)
    fun onDeleteClicked( id:Int)
}
}


