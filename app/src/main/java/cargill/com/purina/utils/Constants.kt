package cargill.com.purina.utils

import android.location.Location

class Constants {
    companion object{
        const val DEBUG_API_KEY = "AIzaSyCJhCfsZ1Hu2TgqUMeDrQKnfcpXounMCp0"
        const val DEV_BASE_URL = "https://apipurina.dev.dev-cglcloud.com"
        const val STAGE_BASE_URL = "https://apipurina.stage.cglcloud.in"
        const val USER_LANGUAGE_CODE:String = "my_language"
        const val USER_LANGUAGE:String = "my_lang"
        const val USER_ANIMAL:String = "animal_selected"
        const val USER_ANIMAL_CODE:String = "species_code"
        const val SEARCH_QUERY_TEXT = "seachQuery"
        const val SEARCH_QUERY = "search"
        const val SEARCH_TEXT = "text"
        const val LANGUAGE = "lang"
        const val LANGUAGE_CODE = "lang_code"
        const val SPECIES_ID = "species_id"
        const val SUBSPECIES_ID = "subspecies_id"
        const val CATEGORY_ID = "category_id"
        const val STAGE_ID = "stage_id"
        const val PAGE = "page"
        const val PER_PAGE = "per_page"
        const val PRODUCT_ID = "product_id"
        const val IMAGES = "images"
        const val PROGRAM_ID:String = "program_id"
        const val PROGRAM_NAME:String = "program_name"
        const val NUMBER_ANIMALS:String = "animals"
        const val FEED_PROGRAM_STAGE = "stage"
        const val MIME_TYPE_PDF = "application/pdf"

        /*Store Location Constants Starts here*/
        const val LOCATION_PERMISSION_REQ_CODE = 1000
        const val LOCATION_LOADED:String = "location"
        const val STORE_ID = "store_id"
        var STORE_TEXT = "text"
        var location: Location = Location("")
        var locationCity: String? = null
        const val DEFAULT_STORE_IMG = "/aws/download/DefaultImgPurina210827.png"
        /*Store Location Constants Ends here*/

    }
}