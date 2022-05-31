package com.codebrew.clikat.data.model.others

data class CreatePostInput(var heading: String?=null, var description: String?=null,
                           var supplier_id: Int?=null, var product_id: Int?=null, var branch_id: Int?=null,
                           var user_id :Int?=null, var post_images:List<String>?=null,var id:Int?=null)