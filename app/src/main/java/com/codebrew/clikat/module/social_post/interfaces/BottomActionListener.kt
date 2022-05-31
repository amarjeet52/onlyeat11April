package com.codebrew.clikat.module.social_post.interfaces

import com.codebrew.clikat.data.model.api.CommentBean
import com.codebrew.clikat.modal.other.ProductDataBean
import com.codebrew.clikat.modal.other.SupplierDataBean

interface BottomActionListener {
    fun onSupplierSelect(suppplier: SupplierDataBean){}
    fun onProductSelect(product: ProductDataBean){}
    fun onUpdateComment(product: CommentBean?){}
}