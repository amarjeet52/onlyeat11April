package com.codebrew.clikat.data.model.api

import com.google.gson.annotations.SerializedName

data class SuggestionResponse(

	@field:SerializedName("data")
	val data: SuggestionData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class SuggestionDataItem(

	@field:SerializedName("added_at")
	val addedAt: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class SuggestionData(

	@field:SerializedName("data")
	val data: ArrayList<SuggestionDataItem>? = null,

	@field:SerializedName("count")
	val count: Int? = null
)
