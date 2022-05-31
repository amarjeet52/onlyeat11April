package com.codebrew.clikat.data.model.api

import com.google.gson.annotations.SerializedName

data class VerifyTableResponeModel(

	@field:SerializedName("data")
	val data: List<TableItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class TableItem(

	@field:SerializedName("is_deleted")
	val isDeleted: Int? = null,

	@field:SerializedName("seating_capacity")
	val seatingCapacity: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("branch_id")
	val branchId: Int? = null,

	@field:SerializedName("table_number")
	val tableNumber: Int? = null,

	@field:SerializedName("qr_code")
	val qrCode: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("table_name")
	val tableName: String? = null,

	@field:SerializedName("supplier_id")
	val supplierId: Int? = null
)
