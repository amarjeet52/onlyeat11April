package com.codebrew.clikat.data.model.others

class FilterInputModel (
        var languageId: String? = null,
        var subCategoryId: MutableList<Int> ?= mutableListOf(),
        var low_to_high: String? = null,
        var is_popularity:Int?=null,
        var is_availability: String? = null,
        var is_discount: String? = null,
        var product_name: String? = null,
        var max_price_range: String? = null,
        var min_price_range: String? = null,
        var latitude: String? = null,
        var longitude: String? = null,
        var sub_category_array: ArrayList<Int>? = null,
        var need_agent:Int ?=null,
        var categoryId: Int?=null,
        var offset:Int ?=null,
        var limit:Int ?=null,
        var variant_ids: MutableList<Int>?= mutableListOf(),
        var supplier_ids: MutableList<String> ?= mutableListOf(),
        var brand_ids: MutableList<Int> ?= mutableListOf()
)