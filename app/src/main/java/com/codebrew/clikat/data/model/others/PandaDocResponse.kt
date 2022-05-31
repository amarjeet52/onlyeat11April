package com.codebrew.clikat.data.model.others

import com.google.gson.annotations.SerializedName

data class PandaDocResponse(

	@field:SerializedName("images")
	val images: List<ImagesItem?>? = null,

	@field:SerializedName("metadata")
	val metadata: Metadata? = null,

	@field:SerializedName("template_uuid")
	var templateUuid: String? = null,

	@field:SerializedName("recipients")
	var recipients: ArrayList<RecipientsItem>? = null,

	@field:SerializedName("folder_uuid")
	val folderUuid: String? = null,

	@field:SerializedName("name")
	var name: String? = null,

	@field:SerializedName("pricing_tables")
	val pricingTables: List<PricingTablesItem?>? = null,

	@field:SerializedName("tokens")
	val tokens: List<TokensItem?>? = null,

	@field:SerializedName("fields")
	val fields: Fields? = null,

	@field:SerializedName("tags")
	val tags: List<String?>? = null
)

data class RowsItem(

	@field:SerializedName("data")
	val data: PandaDocData? = null,

	@field:SerializedName("custom_fields")
	val customFields: CustomFields? = null,

	@field:SerializedName("options")
	val options: Options? = null
)

data class PricingTablesItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("options")
	val options: Options? = null,

	@field:SerializedName("sections")
	val sections: List<SectionsItem?>? = null
)

data class PandaDocData(

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("qty")
	val qty: Int? = null,

	@field:SerializedName("tax_first")
	val taxFirst: TaxFirst? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null
)

data class Like(

	@field:SerializedName("value")
	val value: Boolean? = null
)

data class Fields(

	@field:SerializedName("Like")
	val like: Like? = null,

	@field:SerializedName("Delivery")
	val delivery: Delivery? = null,

	@field:SerializedName("Favorite.Color")
	val favoriteColor: FavoriteColor? = null,

	@field:SerializedName("Date")
	val date: Date? = null
)

data class ImagesItem(

	@field:SerializedName("urls")
	val urls: List<String?>? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class CustomFields(

	@field:SerializedName("Fluffiness")
	val fluffiness: String? = null
)

data class TaxFirst(

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("value")
	val value: Double? = null
)

data class FavoriteColor(

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)

data class SectionsItem(

	@field:SerializedName("default")
	val jsonMemberDefault: Boolean? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("rows")
	val rows: List<RowsItem?>? = null
)

data class TokensItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)

data class Discount(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("is_global")
	val isGlobal: Boolean? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("value")
	val value: Double? = null
)

data class Date(

	@field:SerializedName("value")
	val value: String? = null
)

data class RecipientsItem(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class Options(

	@field:SerializedName("qty_editable")
	val qtyEditable: Boolean? = null,

	@field:SerializedName("optional_selected")
	val optionalSelected: Boolean? = null,

	@field:SerializedName("optional")
	val optional: Boolean? = null
)

data class Metadata(

	@field:SerializedName("opp_id")
	val oppId: String? = null,

	@field:SerializedName("my_favorite_pet")
	val myFavoritePet: String? = null
)

data class Delivery(

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)
