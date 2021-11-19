package cargill.com.purina.dashboard.View.LocateStore

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Repository.LocateStoreRepository
import cargill.com.purina.dashboard.viewModel.LocateStoreViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.LocateStoreViewModelFactory
import cargill.com.purina.databinding.FragmentLocateStoreDetailsBinding
import cargill.com.purina.utils.Constants
import coil.load
import coil.request.CachePolicy
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetailsModel
import cargill.com.purina.dashboard.Model.LocateStore.StoreImages
import cargill.com.purina.dashboard.Model.ProductDetails.Image
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.View.ProductCatalog.ImageViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_locate_store_details.view.*
import com.google.android.material.appbar.CollapsingToolbarLayout





class LocateStoreDetailsFragment : Fragment() {
  lateinit var binding: FragmentLocateStoreDetailsBinding
  private lateinit var storeDetailViewModel: LocateStoreViewModel
  private val _binding get() = binding!!
  private var store_id: Int = 0
  var sharedViewmodel: SharedViewModel? = null
  private var dataLoaded: Boolean = false
  var storeLongitude: Double = 0.0
  var storeLatitude: Double = 0.0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentLocateStoreDetailsBinding.inflate(inflater, container, false)
    val view = binding!!.root
    return view
  }

  @SuppressLint("ResourceAsColor")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
    val repository = LocateStoreRepository(dao, PurinaService.getDevInstance(), requireActivity())
    val factory = LocateStoreViewModelFactory(repository)

    storeDetailViewModel = ViewModelProvider(this, factory).get(LocateStoreViewModel::class.java)
    binding.locateStoreDetailsViewModel = storeDetailViewModel
    binding.lifecycleOwner = this
    if (arguments != null) {
      if (requireArguments().containsKey(Constants.STORE_ID)) {
        store_id = arguments?.getInt(Constants.STORE_ID)!!
      }
    }

    binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_left)
    binding.toolbar.setNavigationOnClickListener {
      (requireActivity() as DashboardActivity).closeIfOpen()
      findNavController().navigate(R.id.action_fragmentLocateStore_back)
    }

    binding.tvTitle.setOnClickListener {
      (requireActivity() as DashboardActivity).closeIfOpen()
      findNavController().navigate(R.id.action_fragmentLocateStore_back)
    }

    (requireActivity() as DashboardActivity).disableBottomMenu()

    /*_binding.back.setOnClickListener {
      findNavController().navigate(R.id.action_fragmentLocateStore_back)
    }*/
    _binding.ivStoreDirections.setOnClickListener {

      var latlongVales =
        "google.navigation:q=" + storeLatitude + "," + storeLongitude
      val gmmIntentUri: Uri = Uri.parse(latlongVales)
      val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
      mapIntent.setPackage("com.google.android.apps.maps")
      startActivity(mapIntent)
    }
    _binding.tvStorePhone.setOnClickListener {
      if (_binding.tvStorePhone.text.isNotEmpty()) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("" + _binding.tvStorePhone.text)
        startActivity(intent)
      }
    }

    getData()
    storeDetailViewModel?.remoteStoreDetail?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        Log.i("data commingng", it.body().toString())
        if (it.body() != null) {
          displayOnlineData(it.body()!!.StoreDetail)
        } else {
          displayNodata()
        }
      } else {
        displayNodata()
      }
    })

    (requireActivity() as DashboardActivity).closeIfOpen()
    /*(requireActivity() as DashboardActivity).disableFilterValue = "Yes"
    (requireActivity() as DashboardActivity).disableFilter()*/

  }

  private fun displayOnlineData(storeDetail: StoreDetail) {

    storeLatitude = storeDetail.latitude
    storeLongitude = storeDetail.longitude

    ViewsEnableDisable(storeDetail)

    if(storeDetail.breeding_animals == ""){
      binding.llHeaderBreeding.visibility = View.GONE
      binding.tvBreedingAnimals.visibility = View.GONE
    }else{
      binding.tvBreedingAnimals.text = storeDetail.breeding_animals

    }

    if(storeDetail.is_freedelivery == ""){
      binding.llIsFree.visibility = View.GONE
      binding.tvIsFree.visibility = View.GONE
    }else{
      binding.tvIsFree.text = storeDetail.is_freedelivery
    }

    if(!storeDetail.Store_images.isEmpty()){
      _binding.cvStoreImage.visibility = View.VISIBLE
      //storeDetail.Store_images = listOf(StoreImages(false,storeDetail.id,Constants.DEFAULT_STORE_IMG,0,0))
      _binding.imageViewPager?.adapter = StoreImageViewAdapter(storeDetail.Store_images, {images: List<StoreImages> -> null })

      if(storeDetail.Store_images.size==1){
        _binding.imageTabLayout.visibility = View.GONE
      }else{
        _binding.imageTabLayout?.let {
          _binding.imageViewPager?.let { it1 ->
            TabLayoutMediator(it, it1){ tab, position->
            }.attach()
          }
        }
      }
    }else{
      _binding.imageViewPager.visibility = View.GONE
      _binding.imageTabLayout.visibility = View.GONE
      _binding.cvStoreImage.visibility = View.GONE
    }
  }

  private fun getData() {
    if (Network.isAvailable(requireActivity())) {
      storeDetailViewModel!!.getRemoteStoreDetail(store_id)
    } else {
      var details: StoreDetail = storeDetailViewModel!!.getOfflineStoreDetail(store_id)
      if (details.id != null) {
        displayOfflineData(details)
      } else {
        displayNodata()
      }
    }

    storeDetailViewModel!!.msg.observe(binding.lifecycleOwner!!, Observer {
      Snackbar.make(binding!!.root, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

    })
  }

  private fun displayNodata() {
    binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }
  }

  private fun displayOfflineData(storeDetail: StoreDetail) {
    storeLatitude = storeDetail.latitude
    storeLongitude = storeDetail.longitude

    ViewsEnableDisable(storeDetail)

    if(storeDetail.breeding_animals == ""){
      binding.llHeaderBreeding.visibility = View.GONE
      binding.tvBreedingAnimals.visibility = View.GONE
    }else{
      binding.tvBreedingAnimals.text = storeDetail.breeding_animals

    }

    if(storeDetail.is_freedelivery == ""){
      binding.llIsFree.visibility = View.GONE
      binding.tvIsFree.visibility = View.GONE
    }else{
      binding.tvIsFree.text = storeDetail.is_freedelivery
    }

    if(!storeDetail.Store_images.isEmpty()){
      _binding.cvStoreImage.visibility = View.VISIBLE
      //storeDetail.Store_images = listOf(StoreImages(false,storeDetail.id,Constants.DEFAULT_STORE_IMG,0,0))
      _binding.imageViewPager?.adapter = StoreImageViewAdapter(storeDetail.Store_images, {images: List<StoreImages> -> null })

      if(storeDetail.Store_images.size==1){
        _binding.imageTabLayout.visibility = View.GONE
      }else{
        _binding.imageTabLayout?.let {
          _binding.imageViewPager?.let { it1 ->
            TabLayoutMediator(it, it1){ tab, position->
            }.attach()
          }
        }
      }

    }else{
      _binding.imageViewPager.visibility = View.GONE
      _binding.imageTabLayout.visibility = View.GONE
      _binding.cvStoreImage.visibility = View.GONE

    }
  }

  private fun ViewsEnableDisable(storeDetail: StoreDetail){

    binding.tvStoreName.text = storeDetail.name
    if(storeDetail.address==""){
      binding.llAddressFields.visibility = View.GONE
    }else{
      binding.tvStoreAddress.text = storeDetail.address
    }

    if(storeDetail.district==""){
      binding.llCity.visibility = View.GONE
    }else{
      binding.tvStoreCity.text = storeDetail.district
    }

    if(storeDetail.village==""){
      binding.llRegion.visibility = View.GONE
    }else{
      binding.tvStoreRegion.text = storeDetail.village
    }

    if(storeDetail.pincode==0){
      binding.llPincode.visibility = View.GONE
    }else{
      binding.tvStorePin.text = storeDetail.pincode.toString()
    }

    if(storeDetail.phone==""){
      binding.llPhoneNo.visibility = View.GONE
    }else{
      binding.tvStorePhone.text = storeDetail.phone
    }

    if(storeDetail.dealerName==""){
      binding.llDealer.visibility = View.GONE
    }else{
      binding.llDealer.visibility = View.GONE
      binding.tvStoreDealer.text = storeDetail.dealerName
    }

    if(storeDetail.partnerName==""){
      binding.llPartner.visibility = View.GONE
    }else{
      binding.llPartner.visibility = View.GONE
      binding.tvStorePartner.text = storeDetail.partnerName
    }

    if(storeDetail.workingHours==""){
      binding.llHours.visibility = View.GONE
    }else{
      binding.tvStoreHours.text = storeDetail.workingHours
    }

    if(storeDetail.workingDays==""){
      binding.llDays.visibility = View.GONE
    }else{
      binding.tvStoreDays.text = storeDetail.workingDays
    }

    if(storeDetail.website==""){
      binding.cvStoreWeb.visibility = View.GONE
    }else{
      binding.tvStoreWebsite.text = storeDetail.website
    }

    if((storeDetail.partnerName=="") && (storeDetail.dealerName=="")){
      binding.cvStoreDealer.visibility = View.GONE
    }

    if((storeDetail.workingHours=="") && (storeDetail.workingDays=="")){
      binding.cvStoreClock.visibility = View.GONE
    }

  }

}