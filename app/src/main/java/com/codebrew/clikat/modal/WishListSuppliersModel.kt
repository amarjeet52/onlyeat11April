package com.codebrew.clikat.modal

import com.codebrew.clikat.modal.other.SupplierDataBean



data class WishListSuppliersModel(
        /**
         * status : 200
         * message : Success
         * data : [{"min_order":0,"onOffComm":1,"delivery_prior_time":30,"delivery_min_time":0,"delivery_max_time":0,"urgent_delivery_time":30,"total_reviews":0,"rating":0,"supplier_branch_id":1,"name":"Fashion Planet","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/13347012_1032394443514805_8217217467431659662_n-2mEetEk.png","id":15,"status":0,"start_time":"15:10:00","end_time":"15:10:00","payment_method":0,"commission_package":0},{"min_order":0,"onOffComm":1,"delivery_prior_time":1470,"delivery_min_time":120,"delivery_max_time":1440,"urgent_delivery_time":30,"total_reviews":7,"rating":5,"supplier_branch_id":2,"name":"Metro Shoes Ltd","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/metroaNrdb6.jpg","id":16,"status":0,"start_time":"00:00:00","end_time":"23:55:00","payment_method":0,"commission_package":0},{"min_order":0,"onOffComm":1,"delivery_prior_time":1530,"delivery_min_time":15,"delivery_max_time":15,"urgent_delivery_time":60,"total_reviews":14,"rating":4.2,"supplier_branch_id":3,"name":"Kapsoons","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/150600301972Xq30.jpg","id":17,"status":1,"start_time":"12:46:00","end_time":"12:46:00","payment_method":0,"commission_package":0},{"min_order":0,"onOffComm":1,"delivery_prior_time":1470,"delivery_min_time":420,"delivery_max_time":300,"urgent_delivery_time":30,"total_reviews":0,"rating":0,"supplier_branch_id":4,"name":"Intas Pharmaceuticals Pvt Ltd","ic_splash":"http://45.232.252.46:8082/clikat-buckettest/productN0RbDs.gif","id":18,"status":1,"start_time":"00:00:00","end_time":"23:55:00","payment_method":0,"commission_package":0}]
         */
        var status: Int = 0,
        var message: String? = null,
        var data: SuppliersModel? = null
)

data class SuppliersModel(
    val favourites: MutableList<SupplierDataBean>
)