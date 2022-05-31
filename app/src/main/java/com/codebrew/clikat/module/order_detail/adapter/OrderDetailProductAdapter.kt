package com.codebrew.clikat.module.order_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.data.OrderStatus
import com.codebrew.clikat.data.ReturnStatus
import com.codebrew.clikat.data.VendorAppType
import com.codebrew.clikat.data.constants.AppConstants.Companion.CURRENCY_SYMBOL
import com.codebrew.clikat.data.model.api.Currency
import com.codebrew.clikat.data.model.api.orderDetail.OrderHistory
import com.codebrew.clikat.databinding.ItemOrderDetailProductBinding
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SettingModel
import com.codebrew.clikat.modal.other.SettingModel.DataBean.ScreenFlowBean
import com.codebrew.clikat.module.cart.adapter.VarientItemAdapter
import com.codebrew.clikat.module.order_detail.adapter.OrderDetailProductAdapter.ViewHolder
import com.codebrew.clikat.utils.StaticFunction
import com.codebrew.clikat.utils.StaticFunction.loadImage
import com.codebrew.clikat.utils.StaticFunction.openCustomChrome
import com.codebrew.clikat.utils.Utils
import com.codebrew.clikat.utils.configurations.Configurations
import com.google.android.material.button.MaterialButton

/*
 * Created by cbl80 on 27/4/16.
 */
class OrderDetailProductAdapter(private val mContext: Context?=null,
                                private val list: List<ProductDataBean?>,
                                private val screenFlowBean: ScreenFlowBean?,
                                private val settingsData: SettingModel.DataBean.SettingData?,
                                private val mCallback: OnReturnClicked?=null,
                                private val status: Double?, private val appUtil: AppUtils,
                                private val orderDetail: OrderHistory?, val parentPosition: Int?=null,val selectedCurrency:Currency?=null) : Adapter<ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder { //        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order_detail_product, parent, false);
        val binding: ItemOrderDetailProductBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.item_order_detail_product, parent, false)
        binding.color = Configurations.colors
        binding.drawables = Configurations.drawables
        binding.strings = appUtil.loadAppConfig(0).strings
        binding.isWeightVisible = settingsData?.is_product_weight == "1"
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mProductDataBean = list[position]

        holder.updateItem(mProductDataBean)
        holder.tvProductName.text = mProductDataBean?.name
        holder.tvProductDesc.text = Utils.getHtmlData(mProductDataBean?.product_desc ?: "")


        holder.tvRecipe.setOnClickListener {
            if (!mProductDataBean?.recipe_pdf.isNullOrEmpty()) {
                mContext?.let { it1 -> openCustomChrome(it1, mProductDataBean?.recipe_pdf ?: "") }
            }
        }
        holder.tvReturnProduct.setOnClickListener {
            if (mProductDataBean?.return_data.isNullOrEmpty()) {
                mCallback?.onReturnProductClicked(mProductDataBean)
            }
        }
        holder.iv_increment.setOnClickListener {
            mCallback?.onIncrementClicked(mProductDataBean, position, parentPosition?:0)
        }
        holder.iv_decrement.setOnClickListener {
            mCallback?.onDecrementClicked(mProductDataBean, position, parentPosition?:0)
        }

        holder.tvViewReceipt.setOnClickListener {
            if(!mProductDataBean?.product_upload_reciept.isNullOrEmpty())
                mContext?.let { it1 -> openCustomChrome(it1,mProductDataBean?.product_upload_reciept?:"") }
        }

        if (screenFlowBean?.is_single_vendor == VendorAppType.Multiple.appType) {
            holder.tvSupplierName.visibility = View.VISIBLE

            holder.tvSupplierName.text = if (mProductDataBean?.supplier_branch_id ?: 0 > 0 && mProductDataBean?.branch_name != null) {
                mContext?.getString(R.string.supplier_tag, mProductDataBean.branch_name)
            } else {
                mContext?.getString(R.string.supplier_tag, mProductDataBean?.supplier_name)
            }
        } else {
            holder.tvSupplierName.visibility = View.GONE
        }
        if (mProductDataBean?.prod_variants != null) {
            holder.rvVarientlist.visibility = View.VISIBLE
            val adapter = VarientItemAdapter()
            holder.rvVarientlist.adapter = adapter
            adapter.submitItemList(mProductDataBean.prod_variants?.toMutableList())
        } else {
            holder.rvVarientlist.visibility = View.GONE
        }
        // loadImage(mProductDataBean?.image_path.toString(), holder.sdvProduct, false)
        if (mProductDataBean?.image_path.toString().isEmpty())
            holder.sdvProduct.visibility = View.GONE
        else {
            holder.sdvProduct.visibility = View.VISIBLE
            loadImage(mProductDataBean?.image_path.toString(), holder.sdvProduct, false)
        }
        if (list.size == 1) {
            holder.divider.visibility = View.GONE
        } else {
            holder.divider.visibility = View.VISIBLE
        }
        holder.addonName.visibility = if (mProductDataBean?.adds_on!!.isNotEmpty()) View.VISIBLE else View.GONE
        holder.addonName.text = mContext?.getString(R.string.addon_name_tag, mProductDataBean.add_on_name)
        val quant:Float = if (mProductDataBean.prod_quantity == 0f) mProductDataBean.quantity
                ?: 0f else mProductDataBean.prod_quantity ?: 0f
        holder.tvProductcode.text = mContext?.getString(R.string.qunatity) + " " + Utils.getDecimalPointValue(settingsData, quant.toFloat())

        holder.tvQuantity.text = Utils.getDecimalPointValue(settingsData, quant.toFloat())
        holder.tvPrice.text = mContext?.getString(R.string.currency_tag, CURRENCY_SYMBOL, Utils.getPriceFormat(mProductDataBean.fixed_price?.toFloatOrNull()
                ?: 0.0f,settingsData,selectedCurrency))

        holder.tvProductDesc.setOnClickListener {
            mCallback?.onClickProdDescListener(mProductDataBean.product_desc.toString())
        }


        holder.tvRecipe.visibility =
                if (settingsData?.product_pdf_upload != null && settingsData.product_pdf_upload == "1" && !mProductDataBean.recipe_pdf.isNullOrEmpty())
                    View.VISIBLE else View.GONE


        if (settingsData?.is_return_request != null && settingsData.is_return_request == "1"
                && status == OrderStatus.Delivered.orderStatus)
            holder.tvReturnProduct.visibility = View.VISIBLE
        else
            holder.tvReturnProduct.visibility = View.GONE


        if ((status == OrderStatus.Delivered.orderStatus || status == OrderStatus.Rating_Given.orderStatus)
                && mProductDataBean.is_rated == 0 && settingsData?.is_product_rating == "1") {
            holder.rateProduct.visibility = View.VISIBLE
            holder.rateProduct.text = mContext?.getString(R.string.rate_food_item, (appUtil.loadAppConfig(0).strings)?.product)
        }


        holder.clQuantity.visibility = if (orderDetail?.editOrder == true) View.VISIBLE else View.GONE
        holder.rateProduct.setOnClickListener {
            mCallback?.onRateProd(mProductDataBean)
        }

        if (!mProductDataBean.return_data.isNullOrEmpty()) {
            val status = mProductDataBean.return_data?.firstOrNull()?.status
            holder.tvReturnProduct.text = when (status) {
                ReturnStatus.Return_requested.returnStatus -> {
                    mContext?.getString(R.string.return_requested)
                }
                ReturnStatus.Agent_on_the_way.returnStatus -> {
                    mContext?.getString(R.string.agent_is_on_the_way)
                }
                ReturnStatus.Product_picked.returnStatus -> {
                    mContext?.getString(R.string.product_picked_up)
                }
                ReturnStatus.returned.returnStatus -> {
                    mContext?.getString(R.string.returned)
                }
                else -> ""
            }

        } else mContext?.getString(R.string.return_product)

        holder.groupOutNetwork.visibility=if(!mProductDataBean.product_owner_name.isNullOrEmpty()) View.VISIBLE else View.GONE
        holder.tvOwnerName.text=mContext?.getString(R.string.owner_name,mProductDataBean.product_owner_name)
        holder.tvReferenceId.text=mContext?.getString(R.string.reference_id_show,mProductDataBean.product_reference_id)
        holder.tvDimensions.text=mContext?.getString(R.string.dimensions_show,mProductDataBean.product_dimensions)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(private  val binding: ItemOrderDetailProductBinding) : RecyclerView.ViewHolder(binding.root) {
        internal var tvProductName: TextView
        internal var tvSupplierName: TextView
        internal var tvProductDesc: TextView
        internal var tvProductcode: TextView
        internal var tvPrice: TextView
        internal var addonName: TextView
        internal var sdvProduct: ImageView
        internal var divider: View
        internal var tvRecipe: TextView
        internal var rvVarientlist: RecyclerView
        internal var tvReturnProduct: Button
        internal var rateProduct: Button
        internal var tvQuantity: TextView
        internal var clQuantity: ConstraintLayout
        internal var iv_decrement: ImageView
        internal var iv_increment: ImageView
        internal var groupOutNetwork:Group
        internal var tvDimensions: TextView
        internal var tvReferenceId: TextView
        internal var tvOwnerName: TextView
        internal var tvViewReceipt:TextView
        init {
            tvProductName = itemView.findViewById(R.id.tvProductName)
            tvProductDesc = itemView.findViewById(R.id.tvProductDesc)
            tvSupplierName = itemView.findViewById(R.id.tvSupplierName)
            tvProductcode = itemView.findViewById(R.id.tvProductcode)
            tvPrice = itemView.findViewById(R.id.tv_total_prod)
            addonName = itemView.findViewById(R.id.tvAddonName)
            sdvProduct = itemView.findViewById(R.id.sdvProduct)
            rvVarientlist = itemView.findViewById(R.id.rv_varient_list)
            divider = itemView.findViewById(R.id.divider)
            tvRecipe = itemView.findViewById(R.id.tvRecepie)
            tvReturnProduct = itemView.findViewById(R.id.tvReturnProduct)
            rateProduct = itemView.findViewById(R.id.btnRateProd)
            tvQuantity = itemView.findViewById(R.id.tvQuantity)
            clQuantity = itemView.findViewById(R.id.clQuantity)
            iv_increment = itemView.findViewById(R.id.iv_increment)
            iv_decrement = itemView.findViewById(R.id.iv_decrement)
            groupOutNetwork=itemView.findViewById(R.id.groupOutNetwork)
            tvDimensions=itemView.findViewById(R.id.tvDimensions)
            tvOwnerName=itemView.findViewById(R.id.tvOwnerName)
            tvReferenceId=itemView.findViewById(R.id.tvReferenceId)
            tvViewReceipt=itemView.findViewById(R.id.tvViewReceipt)
        }

        fun updateItem(productItem: ProductDataBean?) {
            binding.productItem=productItem
        }
    }


    interface OnReturnClicked {
        fun onReturnProductClicked(item: ProductDataBean?)
        fun onRateProd(item: ProductDataBean?)
        fun onClickProdDescListener(productDesc: String)
        fun onIncrementClicked(item: ProductDataBean?, adapterPosition: Int, parentPosition: Int)
        fun onDecrementClicked(item: ProductDataBean?, adapterPosition: Int, parentPosition: Int)
    }
}