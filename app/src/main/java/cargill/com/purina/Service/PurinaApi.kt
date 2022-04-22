package cargill.com.purina.Service

import cargill.com.purina.dashboard.Model.Articles.Article
import cargill.com.purina.dashboard.Model.Articles.Articles
import cargill.com.purina.dashboard.Model.Campaign.Campaigns
import cargill.com.purina.dashboard.Model.FeedingProgram.DetailedFeedProgramStages
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedingPrograms
import cargill.com.purina.dashboard.Model.FilterOptions.FilterOptions
import cargill.com.purina.dashboard.Model.Home.Animals
import cargill.com.purina.dashboard.Model.Home.FaqResponse
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseaseDetailResponse
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseaseListResponse
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseaseResponse
import cargill.com.purina.dashboard.Model.IdentifyDisease.SymptomsResponse
import cargill.com.purina.dashboard.Model.LocateStore.EmailResponse
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetailsResponse
import cargill.com.purina.dashboard.Model.LocateStore.StoreResponse
import cargill.com.purina.dashboard.Model.ProductDetails.DetailProduct
import cargill.com.purina.dashboard.Model.Products.ProductCatalogue
import cargill.com.purina.splash.Model.Languages
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PurinaApi {
  @GET("v2/language")
  suspend fun getLanguages(): Response<Languages>

  @GET("v2/species/{id}")
  suspend fun getAnimals(@Path("id") languageCode: String): Response<Animals>

  @GET("v2/product/filteroptions")
  //https://apipurina.dev.dev-cglcloud.com/product/filteroptions?species_id=1
  suspend fun getFilterOptions(
    @QueryMap query: Map<String, String>
  ): Response<FilterOptions>

  @GET("v2/product/search")
  //https://apipurina.dev.dev-cglcloud.com/product/search?text=product&lang=en&species_id=1
  // &subspecies_id=2&category_id=2&stage_id=1&page=1&per_page=10
  suspend fun getProducts(
    @QueryMap query: Map<String, String>
  ): Response<ProductCatalogue>

  @GET("v2/product/id/{id}")
  //https://apipurina.dev.dev-cglcloud.com/product/id/1
  suspend fun getProductDetails(@Path("id") productId: Int): Response<DetailProduct>

  @GET("v2{path}")
  //https://apipurina.dev.dev-cglcloud.com/aws/download_url/HappyFeedBrouchere.pdf
  suspend fun getProductPDF(@Path("path") path: String): Response<String>


  @GET("v2/feedprogram")
  //https://apipurina.dev.dev-cglcloud.com/feedprogram?lang_code=en
  suspend fun getFeedPrograms(
    @QueryMap query: Map<String, String>
  ): Response<FeedingPrograms>

  @GET("v2/feedprogram/id/{id}")
  //https://apipurina.dev.dev-cglcloud.com/feedprogram/id/1
  suspend fun getFeedProgramStageDetails(@Path("id") programId: Int): Response<DetailedFeedProgramStages>

  /*Store Location Api Starts Here*/
  @GET("v2/store/app")
  //"https://apipurina.dev.dev-cglcloud.com/store?search=Bangalore&lang_code=ru&page=1&per_page=10"
  suspend fun getStoreList(@QueryMap query: Map<String, String>): Response<StoreResponse>

  @GET("v2/store/radial")
  //https://apipurina.dev.dev-cglcloud.com/store/radial?search=Bangalore%2C572102&lang_code=en
  suspend fun getRadialStoreList(@QueryMap query: Map<String, String>): Response<StoreResponse>

  @GET("v2/store/{id}")
  //https://apipurina.dev.dev-cglcloud.com/store/id/1
  suspend fun getStoreDetails(@Path("id") storeId: Int): Response<StoreDetailsResponse>

  /*Email*/
  @POST("send/mail")
  //https://api-dev.dev.dev-cglcloud.com/api/purinarussia/purinaserver/send/mail
  suspend fun postEmail(@Body body: RequestBody) : Response<EmailResponse>

  /*Store Location Api End Here*/

  /*Identify Disease Api Starts Here*/
  @GET("v2/diseases/app")
  //https://apipurina.dev.dev-cglcloud.com/diseases?page=1&per_page=100&text=arun&species_id=9&symptoms_id=78&lang=en
  suspend fun getDiseaseList(@QueryMap query: Map<String, String>): Response<DiseaseListResponse>

  @GET("v2/diseases/id/{id}")
  //https://apipurina.dev.dev-cglcloud.com/diseases/id/38
  suspend fun getDiseaseDetails(@Path("id") diseaseId: Int): Response<DiseaseDetailResponse>

  @GET("v2/symptoms/filterSymptoms")
  //https://apipurina.dev.dev-cglcloud.com/symptoms/filterSymptoms?lang=en
  suspend fun getAllSymptoms(@QueryMap query: Map<String, String>): Response<SymptomsResponse>

  @GET("v2/symptoms")
  //https://apipurina.dev.dev-cglcloud.com/symptoms?lang=en&symptoms_id=52,53
  suspend fun getDigitVetDetails(@QueryMap query: Map<String, String>): Response<DiseaseResponse>

  @GET("v2/diseases/filteroptions")
  //https://apipurina.dev.dev-cglcloud.com/diseases/filteroptions?lang=en&species_id=1
  suspend fun getFilteredSymptoms(@QueryMap query: Map<String, String>): Response<SymptomsResponse>

  /*Identify Disease Api Ends Here*/

  /*FAQ Api Starts Here*/

  @GET("v2/faq/app")
  //https://apipurina.dev.dev-cglcloud.com/faq?lang=en
  suspend fun getFAQ(@QueryMap query: Map<String, String>): Response<FaqResponse>

  /*FAQ Api Ends Here*/

  /*Product Campaign */
  @GET("v2/campaigns/app")
  //https://apipurina.dev.dev-cglcloud.com/campaigns/app?page=1&per_page=100&lang=en
  suspend fun getCampaignData(@QueryMap query: Map<String, String>): Response<Campaigns>

  /*Articles*/
  @GET("v2/articles/app")
  //https://apipurina.dev.dev-cglcloud.com/articles/app?page=1&per_page=100&species_id=1&lang=en
  suspend fun getArticles(@QueryMap query: Map<String, String>): Response<Articles>




}