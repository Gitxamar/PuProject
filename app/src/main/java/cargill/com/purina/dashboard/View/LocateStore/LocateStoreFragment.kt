package cargill.com.purina.dashboard.View.LocateStore

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.LocateStore.LocationDetails
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Model.LocateStore.Stores
import cargill.com.purina.dashboard.Repository.LocateStoreRepository
import cargill.com.purina.dashboard.viewModel.LocateStoreViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.LocateStoreViewModelFactory
import cargill.com.purina.databinding.FragmentLocateStoresBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class LocateStoreFragment : Fragment(), OnMapReadyCallback,
  LocationSource.OnLocationChangedListener {

  private lateinit var mMap: GoogleMap
  lateinit var myPreference: AppPreference
  lateinit var binding: FragmentLocateStoresBinding
  private lateinit var storeDetailViewModel: LocateStoreViewModel
  private lateinit var adapter: LocateStoreAdapter
  private lateinit var adapterRecent: LocateLocalStoreAdapter
  private val _binding get() = binding!!
  private var IsEnlarged: Boolean = false;
  private var IsListEnlarged: Boolean = false;
  private var mapFragment: SupportMapFragment? = null;
  private var PAGENUMBER: Int = 1
  private var searchTxt: String = "";
  private var store_txt: Int = 0;


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentLocateStoresBinding.inflate(inflater, container, false)
    val view = binding!!.root
    return view
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    init()
    initRecyclerView()

    val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
    val repository = LocateStoreRepository(dao, PurinaService.getDevInstance(), requireActivity())
    val factory = LocateStoreViewModelFactory(repository)

    storeDetailViewModel = ViewModelProvider(this, factory).get(LocateStoreViewModel::class.java)
    binding.locateStoreViewModel = storeDetailViewModel
    binding.lifecycleOwner = this

    _binding.back.setOnClickListener {
      findNavController().navigate(R.id.action_fragmentLocateStore_to_home)
    }
    _binding.ivlocateStoreMapsEnlarge.setOnClickListener {
      when (IsEnlarged) {
        false -> {
          slideAnimate(true, _binding.rlLocateStoreMaps, 0, 10.0f)
          slideUp(_binding.rlLocateStoreMaps)
          slideAnimate(false, _binding.rlLocateStoreSearch, 0, 0f)
          slideDown(_binding.rlLocateStoreSearch, 0f)
          IsEnlarged = true
        }
        true -> {
          slideAnimate(true, _binding.rlLocateStoreMaps, 0, 6.0f)
          slideUp(_binding.rlLocateStoreMaps)
          slideAnimate(false, _binding.rlLocateStoreSearch, 0, 4.0f)
          slideDown(_binding.rlLocateStoreSearch, 0f)
          IsEnlarged = false
        }
      }
    }
    _binding.ivlocateListEnlarge.setOnClickListener {
      when (IsListEnlarged) {
        false -> {
          slideAnimate(true, _binding.rlLocateStoreSearch, 0, 10.0f)
          slideUp(_binding.rlLocateStoreSearch)
          slideAnimate(false, _binding.rlLocateStoreMaps, 0, 0f)
          slideDown(_binding.rlLocateStoreMaps, 0f)
          IsListEnlarged = true
        }
        true -> {
          slideAnimate(true, _binding.rlLocateStoreMaps, 0, 6.0f)
          slideUp(_binding.rlLocateStoreMaps)
          slideAnimate(false, _binding.rlLocateStoreSearch, 0, 4.0f)
          slideDown(_binding.rlLocateStoreSearch, 0f)
          IsListEnlarged = false
        }
      }
    }
    _binding.etSearchLocations.addTextChangedListener()
    _binding.etSearchLocations.addTextChangedListener(object : TextWatcher {

      override fun afterTextChanged(s: Editable) {}

      override fun beforeTextChanged(
        s: CharSequence, start: Int,
        count: Int, after: Int
      ) {
        if (count == 0) {
          binding.rlSearchStores?.visibility = View.VISIBLE
          binding.rvStoreList?.visibility = View.GONE
          displayOfflineData()
        }

      }

      override fun onTextChanged(
        s: CharSequence, start: Int,
        before: Int, count: Int
      ) {
        if (count == 0) {
          binding.rlSearchStores?.visibility = View.VISIBLE
          binding.rvStoreList?.visibility = View.GONE
        }
      }
    })
    _binding.searchLocation.setOnClickListener {
      searchTxt = _binding.etSearchLocations.text.toString()
      if (searchTxt!!.trim().length > 0) {
        getData()
      } else {
        Snackbar.make(view, R.string.error_search, Snackbar.LENGTH_SHORT).show()
      }
    }

    storeDetailViewModel?.remoteStoreList?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        Log.i("data commingng", it.body().toString())
        if (it.body()!!.stores.size != 0) {
          displayData(it.body()!!.stores)
        } else {
          displayNodata()
        }
      } else {
        displayNodata()
      }
    })

    storeDetailViewModel!!.msg.observe(binding.lifecycleOwner!!, Observer {
      Snackbar.make(binding!!.root, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
      displayNodata()
    })

  }

  private fun displayNodata() {
    binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }
  }

  private fun initRecyclerView() {
    binding.storeList.layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
    adapter = LocateStoreAdapter { store: Stores -> onItemClick(store) }
    binding.storeList.adapter = adapter
    binding.storeList.showShimmer()

    binding.rvRecentList.layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
    adapterRecent = LocateLocalStoreAdapter { storeDetail: StoreDetail -> onItemClick(storeDetail) }
    binding.rvRecentList.adapter = adapterRecent

  }

  private fun getData() {
    if (Network.isAvailable(requireActivity())) {
      storeDetailViewModel!!.getRemoteData(
        mapOf(
          Constants.SEARCH_QUERY to searchTxt,
          Constants.LANGUAGE_CODE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE)
            .toString(),
          Constants.PAGE to PAGENUMBER.toString(),
          Constants.PER_PAGE to 10.toString()
        )
      )
    } else {
      displayOfflineData()
    }
  }

  private fun init() {
    mapFragment =
      childFragmentManager.findFragmentById((R.id.locateStoreMaps)) as SupportMapFragment
    mapFragment!!.getMapAsync(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    showCurrentLocation()
    displayOfflineData()
  }

  private fun showCurrentLocation() {
    val locationManger = activity?.getSystemService(LOCATION_SERVICE) as LocationManager
    val criteria = Criteria()

    if (ActivityCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return
    }

    val location: Location? =
      locationManger.getLastKnownLocation(locationManger.getBestProvider(criteria, false)!!)
    if (location != null) {
      mMap.isMyLocationEnabled = true
      Constants.location.longitude = location.longitude
      Constants.location.latitude = location.latitude
      mapsLocate(LatLng(location.latitude, location.longitude), false)

      getAddressFromLocation()

    } else {
      setLastLocation()
    }

  }

  private fun setLastLocation() {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    if (ActivityCompat.checkSelfPermission(
        requireActivity(),
        Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        requireActivity(),
        Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
      if (location != null) {
        mMap.isMyLocationEnabled = true
        Constants.location.longitude = location.longitude
        Constants.location.latitude = location.latitude
        mapsLocate(LatLng(location.latitude, location.longitude), false)
      } else {
        setLastLocation()
      }
    }
  }

  override fun onLocationChanged(location: Location) {
    if (location != null) {
      
      Constants.location.longitude = location.longitude
      Constants.location.latitude = location.latitude
      mapsLocate(LatLng(location.latitude, location.longitude), true)
      
      
      
    } else {
      setLastLocation()
    }
  }

  private fun mapsLocate(currentLatLong: LatLng, isMarker: Boolean) {

    if (isMarker) {
      mMap.clear()
      val marker = MarkerOptions().position(currentLatLong)
      mMap.addMarker(marker)
    }
    //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLong))
    val cameraPo = CameraPosition.Builder().target(currentLatLong).zoom(17f).bearing(0f).tilt(0f).build()
    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPo))

  }

  private fun slideUp(view: View) {
    view.visibility = View.VISIBLE
    val animate = TranslateAnimation(
      0f,  // fromXDelta
      0f,  // toXDelta
      view.height.toFloat(),  // fromYDelta
      0f
    ) // toYDelta
    animate.duration = 600
    animate.fillAfter = true
    view.startAnimation(animate)
  }

  private fun slideDown(view: View, height: Float) {
    val animate = TranslateAnimation(
      0f,  // fromXDelta
      0f,  // toXDelta
      0f,  // fromYDelta
      height
    ) // toYDelta
    animate.duration = 600
    animate.fillAfter = true
    view.startAnimation(animate)
  }

  private fun slideAnimate(upDown: Boolean, view: View, height: Int, width: Float) {
    val result = LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      height,
      width
    )
    when (upDown) {
      true -> {
        view.setLayoutParams(result)
        slideUp(view)
      }
      false -> {
        view.setLayoutParams(result)
        slideDown(view, height.toFloat())
      }
    }

  }

  private fun onItemClick(store: Stores) {
    //navigate to store details screen
    if (store != null) {
      val bundle = bundleOf(Constants.STORE_ID to store.storeId)
      if (Network.isAvailable(requireContext())) {
        findNavController().navigate(R.id.action_locate_Store_details, bundle)
      } else {
        binding.let {
          findNavController().navigate(R.id.action_locate_Store_details, bundle)
        }
      }
    }
  }

  private fun onItemClick(store: StoreDetail) {
    //navigate to store details screen
    if (store != null) {
      val bundle = bundleOf(Constants.STORE_ID to store.id)
      if (Network.isAvailable(requireContext())) {
        findNavController().navigate(R.id.action_locate_Store_details, bundle)
      } else {
        binding.let {
          findNavController().navigate(R.id.action_locate_Store_details, bundle)
        }
      }
    }
  }

  private fun displayData(stores: ArrayList<Stores>) {

    binding.rlSearchStores?.visibility = View.GONE
    binding.rvStoreList?.visibility = View.VISIBLE
    binding.storeList.hideShimmer()
    adapter.setList(LocateManager.sortListNearBy(stores))
    adapter.notifyDataSetChanged()

    mMap.clear()
    //Display Markers when Online and Zoom according to area
    for (item in stores) {
      val marker = mMap.addMarker(MarkerOptions().position(LatLng(item.latitude, item.longitude)).title(item.storeName))
      mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(item.latitude, item.longitude)))
      mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder().target(LatLng(item.latitude, item.longitude)).zoom(10f).bearing(0f).tilt(0f).build()))
      marker.showInfoWindow()
    }
  }

  private fun displayOfflineData() {

    var storesList: List<StoreDetail> = storeDetailViewModel!!.getOfflineStoreList()
    if (!storesList.isEmpty() || storesList.size > 0) {
      binding.rvRecentList.hideShimmer()
      adapterRecent.setList(LocateManager.sortListNearByOffline(ArrayList(storesList)))
      adapterRecent.notifyDataSetChanged()
    }
  }


  private fun getAddressFromLocation() {
    var cityName = "Not Found"
    if(activity != null && isAdded()){

      val locale = Locale(myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(), Locale.getDefault().country)
      println("Language ::  "+ Locale.getDefault().language +" -> Country : "+ (Locale.getDefault().country).toLowerCase())

      val gcd = Geocoder(activity, Locale.getDefault())
      try {
        val addresses = gcd.getFromLocation(Constants.location.latitude, Constants.location.longitude, 2)
        for (adrs in addresses) {
          if ((adrs != null) && (adrs.locality.length > 0)) {
            val city = adrs.locality
            if (city != null && city != "") {
              _binding.etSearchLocations.setText(city)
              _binding.searchLocation.performClick()
              break
            }
          }
        }
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }

}