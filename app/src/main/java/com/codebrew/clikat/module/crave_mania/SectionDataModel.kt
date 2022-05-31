package com.codebrew.clikat.module.crave_mania

class SectionDataModel {

    private var headerTitle: String? = null
    private var allItemsInSection: List<BannerData>? = null
private var image:Int?=null

    fun SectionDataModel() {}
    fun SectionDataModel(headerTitle: String?, allItemsInSection: List<BannerData>?) {
        this.headerTitle = headerTitle
        this.allItemsInSection = allItemsInSection
    }
    fun getImage(): Int? {
        return image
    }

    fun setImage(image: Int?) {
        this.image = image
    }

    fun getHeaderTitle(): String? {
        return headerTitle
    }

    fun setHeaderTitle(headerTitle: String?) {
        this.headerTitle = headerTitle
    }

    fun getAllItemsInSection(): List<BannerData>? {
        return allItemsInSection
    }

    fun setAllItemsInSection(allItemsInSection: List<BannerData>?) {
        this.allItemsInSection = allItemsInSection
    }
}