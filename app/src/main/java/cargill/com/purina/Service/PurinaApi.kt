package cargill.com.purina.Service

import cargill.com.purina.dashboard.Model.FeedingProgram.DetailedFeedProgramStages
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedingPrograms
import cargill.com.purina.dashboard.Model.FilterOptions.FilterOptions
import cargill.com.purina.dashboard.Model.Home.Animals
import cargill.com.purina.dashboard.Model.ProductDetails.DetailProduct
import cargill.com.purina.dashboard.Model.Products.ProductCatalogue
import cargill.com.purina.splash.Model.Languages
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface PurinaApi {
    @GET("/language")
    suspend fun getLanguages(): Response<Languages>

    @GET("/species/{id}")
    suspend fun getAnimals(@Path("id") languageCode:String): Response<Animals>

    @GET("/product/filteroptions")
    //https://apipurina.dev.dev-cglcloud.com/product/filteroptions?species_id=1
    suspend fun getFilterOptions(
        @QueryMap query: Map<String, String>
    ): Response<FilterOptions>

    @GET("/product/search")
    //https://apipurina.dev.dev-cglcloud.com/product/search?text=product&lang=en&species_id=1
    // &subspecies_id=2&category_id=2&stage_id=1&page=1&per_page=10
    suspend fun getProducts(
        @QueryMap query: Map<String, String>
    ): Response<ProductCatalogue>

    @GET("/product/id/{id}")
    //https://apipurina.dev.dev-cglcloud.com/product/id/1
    suspend fun getProductDetails(@Path("id") productId:Int):Response<DetailProduct>

    @GET("{path}")
    //https://apipurina.dev.dev-cglcloud.com/aws/download_url/HappyFeedBrouchere.pdf
    suspend fun getProductPDF(@Path("path") path:String):Response<String>


    @GET("/feedprogram")
    //https://apipurina.dev.dev-cglcloud.com/feedprogram?lang_code=en
    suspend fun getFeedPrograms(
        @QueryMap query: Map<String, String>
    ): Response<FeedingPrograms>

    @GET("/feedprogram/id/{id}")
    //https://apipurina.dev.dev-cglcloud.com/feedprogram/id/1
    suspend fun getFeedProgramStageDetails(@Path("id") programId: Int):Response<DetailedFeedProgramStages>
}