package com.codebrew.clikat.modal.other

class AddtoCartModel {
    /**
     * status : 200
     * message : Success
     * cartdata : {"cartId":155,"freeProduct":{},"min_order":0}
     */
    var status = 0
    var message: String? = null
    var data: CartdataBean? = null

    class CartdataBean {
        /**
         * cartId : 155
         * freeProduct : {}
         * min_order : 0
         */
        var cartId: String? = null
        var freeProduct: FreeProductBean? = null
        var min_order = 0

        class FreeProductBean
    }
}