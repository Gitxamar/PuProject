package cargill.com.purina.dashboard.View.LocateStore

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
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetailsModel
import cargill.com.purina.dashboard.Model.LocateStore.StoreImages
import cargill.com.purina.dashboard.Model.ProductDetails.Image
import cargill.com.purina.dashboard.View.ProductCatalog.ImageViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


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

    _binding.back.setOnClickListener {
      findNavController().navigate(R.id.action_fragmentLocateStore_back)
    }
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

  }

  private fun displayOnlineData(storeDetail: StoreDetail) {

    storeLatitude = storeDetail.latitude
    storeLongitude = storeDetail.longitude

    binding.tvStoreName.text = storeDetail.name
    binding.tvStoreAddress.text = storeDetail.address
    binding.tvStoreCity.text = storeDetail.district
    binding.tvStoreRegion.text = storeDetail.village
    binding.tvStorePin.text = storeDetail.pincode.toString()
    binding.tvStorePhone.text = storeDetail.phone
    binding.tvStoreDealer.text = storeDetail.dealerName
    binding.tvStorePartner.text = storeDetail.partnerName
    binding.tvStoreHours.text = storeDetail.workingHours
    binding.tvStoreDays.text = storeDetail.workingDays
    binding.tvStoreWebsite.text = storeDetail.website
    if(storeDetail.breeding_animals == null){
      binding.tvBreedingAnimals.visibility = View.GONE
      binding.tvIsFree.visibility = View.GONE
    }else{
      binding.tvBreedingAnimals.text = storeDetail.breeding_animals
      binding.tvIsFree.text = storeDetail.is_freedelivery
    }


    if(storeDetail.Store_images.isEmpty()){
      storeDetail.Store_images = listOf(StoreImages(false,storeDetail.id,Constants.DEFAULT_STORE_IMG,0,0))
    }

    _binding.imageViewPager?.adapter = StoreImageViewAdapter(storeDetail.Store_images, {images: List<StoreImages> -> null })
    _binding.imageTabLayout?.let {
      _binding.imageViewPager?.let { it1 ->
        TabLayoutMediator(it, it1){ tab, position->
        }.attach()
      }
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

    binding.tvStoreName.text = storeDetail.name
    binding.tvStoreAddress.text = storeDetail.address
    binding.tvStoreCity.text = storeDetail.district
    binding.tvStoreRegion.text = storeDetail.village
    binding.tvStorePin.text = storeDetail.pincode.toString()
    binding.tvStorePhone.text = storeDetail.phone
    binding.tvStoreDealer.text = storeDetail.dealerName
    binding.tvStorePartner.text = storeDetail.partnerName
    binding.tvStoreHours.text = storeDetail.workingHours
    binding.tvStoreDays.text = storeDetail.workingDays
    binding.tvStoreWebsite.text = storeDetail.website

    if(storeDetail.breeding_animals == null){
      binding.tvBreedingAnimals.visibility = View.GONE
      binding.tvIsFree.visibility = View.GONE
    }else{
      binding.tvBreedingAnimals.text = storeDetail.breeding_animals
      binding.tvIsFree.text = storeDetail.is_freedelivery
    }

    _binding.imageViewPager?.adapter = StoreImageViewAdapter(storeDetail.Store_images, {images: List<StoreImages> -> null })
    _binding.imageTabLayout?.let {
        _binding.imageViewPager?.let { it1 ->
          TabLayoutMediator(it, it1){ tab, position->
          }.attach()
        }
      }
  }

}