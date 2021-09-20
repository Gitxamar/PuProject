package cargill.com.purina.Service

import cargill.com.purina.dashboard.Model.Campaign.Campaigns
import cargill.com.purina.dashboard.Model.FeedingProgram.DetailedFeedProgramStages
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedingPrograms
import cargill.com.purina.dashboard.Model.FilterOptions.FilterOptions
import cargill.com.purina.dashboard.Model.Home.Animals
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseaseDetailResponse
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseaseListResponse
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseaseResponse
import cargill.com.purina.dashboard.Model.IdentifyDisease.SymptomsResponse
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetailsResponse
import cargill.com.purina.dashboard.Model.LocateStore.StoreResponse
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
  suspend fun getAnimals(@Path("id") languageCode: String): Response<Animals>

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
  suspend fun getProductDetails(@Path("id") productId: Int): Response<DetailProduct>

  @GET("{path}")
  //https://apipurina.dev.dev-cglcloud.com/aws/download_url/HappyFeedBrouchere.pdf
  suspend fun getProductPDF(@Path("path") path: String): Response<String>


  @GET("/feedprogram")
  //https://apipurina.dev.dev-cglcloud.com/feedprogram?lang_code=en
  suspend fun getFeedPrograms(
    @QueryMap query: Map<String, String>
  ): Response<FeedingPrograms>

  @GET("/feedprogram/id/{id}")
  //https://apipurina.dev.dev-cglcloud.com/feedprogram/id/1
  suspend fun getFeedProgramStageDetails(@Path("id") programId: Int): Response<DetailedFeedProgramStages>

  /*Store Location Api Starts Here*/
  @GET("/store/app")
  //"https://apipurina.dev.dev-cglcloud.com/store?search=Bangalore&lang_code=ru&page=1&per_page=10"
  suspend fun getStoreList(@QueryMap query: Map<String, String>): Response<StoreResponse>

  @GET("/store/{id}")
  //https://apipurina.dev.dev-cglcloud.com/store/id/1
  suspend fun getStoreDetails(@Path("id") storeId: Int): Response<StoreDetailsResponse>
  /*Store Location Api End Here*/

  /*Identify Disease Api Starts Here*/
  @GET("/diseases/app")
  //https://apipurina.dev.dev-cglcloud.com/diseases?page=1&per_page=100&text=arun&species_id=9&symptoms_id=78&lang=en
  suspend fun getDiseaseList(@QueryMap query: Map<String, String>): Response<DiseaseListResponse>

  @GET("/diseases/id/{id}")
  //https://apipurina.dev.dev-cglcloud.com/diseases/id/38
  suspend fun getDiseaseDetails(@Path("id") diseaseId: Int): Response<DiseaseDetailResponse>

  @GET("/symptoms/filterSymptoms")
  //https://apipurina.dev.dev-cglcloud.com/symptoms/filterSymptoms?lang=en
  suspend fun getAllSymptoms(@QueryMap query: Map<String, String>): Response<SymptomsResponse>

  @GET("/symptoms")
  //https://apipurina.dev.dev-cglcloud.com/symptoms?lang=en&symptoms_id=52,53
  suspend fun getDigitVetDetails(@QueryMap query: Map<String, String>): Response<DiseaseResponse>


  /*Identify Disease Api Ends Here*/

  /*Product Campaign */
  @GET("/campaigns/app")
  //https://apipurina.dev.dev-cglcloud.com/campaigns/app?page=1&per_page=100&lang=en
  suspend fun getCampaignData(@QueryMap query: Map<String, String>): Response<Campaigns>


}