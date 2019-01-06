package ru.kuchanov.scpcore.db.model

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

open class MyNativeBanner(
        @PrimaryKey
        @Index
        var id: Long = 0,
        var imageUrl: String? = "",
        var logoUrl: String? = "",
        var title: String? = "",
        var subTitle: String? = "",
        var ctaButtonText: String? = "",
        var redirectUrl: String? = "",
        var enable: Boolean? = false,
        var authorId: Long? = 0,
        var created: String? = "",
        var updated: String? = "",
        var bannerType: String? = null
) : RealmObject() {

    companion object {
        @JvmField
        val FIELD_ID = "id"
        @JvmField
        val FIELD_IMAGE_URL = "imageUrl"
        @JvmField
        val FIELD_LOGO_URL = "logoUrl"
        @JvmField
        val FIELD_TITLE = "title"
        @JvmField
        val FIELD_SUB_TITLE = "subTitle"
        @JvmField
        val FIELD_CTA_BUTTON_TEXT = "ctaButtonText"
        @JvmField
        val FIELD_REDIRECT_URL = "redirectUrl"
        @JvmField
        val FIELD_ENABLE = "enable"
        @JvmField
        val FIELD_AUTHOR_ID = "authorId"
        @JvmField
        val FIELD_CREATED = "created"
        @JvmField
        val FIELD_UPDATED = "updated"
        @JvmField
        val FIELD_BANNER_TYPE = "bannerType"
    }
}

enum class BannerType {
    QUIZ, ART
}

//[
//    {
//        "id": 5,
//        "imageUrl": "ads/files/5/image",
//        "logoUrl": "ads/files/5/logo",
//        "title": "Книги SCP Foundation уже в продаже!",
//        "subTitle": "Спрашивайте в книжных магазинах своего города или закажите доставку в любой уголок страны",
//        "ctaButtonText": "Подробнее",
//        "redirectUrl": "http://artscp.com/promo?utm_source=ru.kuchanov.scpfoundation&utm_medium=referral&utm_campaign=app-ads&utm_term=1",
//        "bannerType": "ART",
//        "enabled": false,
//        "authorId": 32062,
//        "created": "2019-01-06T17:42:59.341Z",
//        "updated": "2019-01-06T17:42:59.347Z"
//    }
//]