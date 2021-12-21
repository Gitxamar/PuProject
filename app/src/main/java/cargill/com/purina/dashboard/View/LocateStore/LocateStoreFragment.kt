package cargill.com.purina.dashboard.View.LocateStore

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.*
import androidx.annotation.LayoutRes
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
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Model.LocateStore.Stores
import cargill.com.purina.dashboard.Repository.LocateStoreRepository
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.viewModel.LocateStoreViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.LocateStoreViewModelFactory
import cargill.com.purina.databinding.FragmentLocateStoresBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import android.view.inputmethod.InputMethodSubtype

import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.*
import okhttp3.internal.toImmutableList


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
  private var pincodeTxt: String = "";
  private var store_txt: Int = 0;
  private lateinit var autoLocationAdapter: AutoLocationAdapter
  private lateinit var builder: AlertDialog.Builder
  private lateinit var ctx: Context
  private var storesListTemp: MutableList<Stores> = mutableListOf()
  private var isDBLoad: Boolean = false
  private var isAutoLocation: Boolean = false
  private var cityTxt: String = "";


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onResume() {
    super.onResume()

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
    ctx = context
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
      (requireActivity() as DashboardActivity).closeIfOpen()
      findNavController().navigate(R.id.action_fragmentLocateStore_to_home)
    }
    _binding.ivlocateStoreMapsEnlarge.setOnClickListener {
      when (IsEnlarged) {
        false -> {
          slideAnimate(true, _binding.rlLocateStoreMaps, 0, 10.0f)
          slideUp(_binding.rlLocateStoreMaps)
          _binding.rlLocateStoreMaps.setPadding(0, 0, 0, 160)
          slideAnimate(false, _binding.rlLocateStoreSearch, 0, 0f)
          slideDown(_binding.rlLocateStoreSearch, 0f)
          IsEnlarged = true
        }
        true -> {
          slideAnimate(true, _binding.rlLocateStoreMaps, 0, 5.0f)
          slideUp(_binding.rlLocateStoreMaps)
          _binding.rlLocateStoreMaps.setPadding(0, 0, 0, 0)
          slideAnimate(false, _binding.rlLocateStoreSearch, 0, 5.0f)
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
          slideAnimate(true, _binding.rlLocateStoreMaps, 0, 5.0f)
          slideUp(_binding.rlLocateStoreMaps)
          slideAnimate(false, _binding.rlLocateStoreSearch, 0, 5.0f)
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
          clearPlottedMaps()

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
          clearPlottedMaps()

        } else if (count >= 3) {
          searchTxt = _binding.etSearchLocations.text.toString()
          //getData()
        }
      }
    })
    _binding.searchLocation.setOnClickListener {
      searchTxt = _binding.etSearchLocations.text.toString()
      if (searchTxt!!.trim().length > 2) {
        _binding.searchLocation.isEnabled = false
        clearPlottedMaps()
        getData(PAGENUMBER)
      } else {
        Snackbar.make(view, R.string.error_search, Snackbar.LENGTH_SHORT).show()
      }
    }

    storeDetailViewModel?.remoteStoreList?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        _binding.searchLocation.isEnabled = true
        Log.i("data commingng", it.body().toString())

        if(it.body()!!.lat_long?.latitude != 0.0f){
          //Update Location of USER if 0.0 in not latitude and longitude
            if(isAutoLocation){
              Constants.locationTemp.latitude = Constants.location.latitude
              Constants.locationTemp.longitude = Constants.location.longitude
            }else{
              Constants.locationTemp.latitude = it.body()!!.lat_long?.latitude!!.toDouble()
              Constants.locationTemp.longitude = it.body()!!.lat_long?.longitude!!.toDouble()
            }

          if (it.body()!!.stores.size != 0) {
            showUpdatedLocation()
            displayData(it.body()!!.stores)
            autoLocationAdapter = AutoLocationAdapter(requireActivity(), android.R.layout.simple_list_item_1, it.body()!!.stores)
            /*_binding.etSearchLocations.setAdapter(autoLocationAdapter)
            _binding.etSearchLocations.threshold = 3*/
            isDBLoad = true
          }else{
            displayRadialSearchAlert()
          }
        }else{
          //Retain Location of USER if 0.0 in latitude and longitude
          Constants.locationTemp.latitude = Constants.location.latitude
          Constants.locationTemp.longitude = Constants.location.longitude

          if (it.body()!!.stores.size != 0) {
            showUpdatedLocation()
            displayData(it.body()!!.stores)
            autoLocationAdapter = AutoLocationAdapter(requireActivity(), android.R.layout.simple_list_item_1, it.body()!!.stores)
            /*_binding.etSearchLocations.setAdapter(autoLocationAdapter)
            _binding.etSearchLocations.threshold = 3*/
            isDBLoad = true
          }else{

            displayRadialSearchAlert()
          }
        }
      } else {
        _binding.searchLocation.isEnabled = true
        displayNodata()
      }
    })

    storeDetailViewModel?.remoteStoreRadial?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        Log.i("data commingng", it.body().toString())
        if(it.body()!!.stores.size > 0){
          if(it.body()!!.lat_long!!.error != null){
            if(it.body()!!.lat_long!!.error.equals("ERROR21")){
              isDBLoad = false
              binding.let { Snackbar.make(it.root, R.string.txtERROR21, Snackbar.LENGTH_LONG).show() }
              Constants.locationTemp.latitude = Constants.location.latitude
              Constants.locationTemp.longitude = Constants.location.longitude
              if(it.body()!!.stores!=null){
                showUpdatedLocation()
                loadDatatoView(it.body()!!.stores)
              }else{
                displayNodata()
              }
            }
          }else if(it.body()!!.lat_long!!.latitude != 0.0f){
            isDBLoad = false
            Constants.locationTemp.latitude = it.body()!!.lat_long!!.latitude!!.toDouble()
            Constants.locationTemp.longitude = it.body()!!.lat_long!!.longitude!!.toDouble()
            showUpdatedLocation()
            loadDatatoView(it.body()!!.stores)
          }
        }else{
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

    _binding.etSearchLocations.setOnItemClickListener { parent, view, position, id ->
      val selectedPoi = parent.adapter.getItem(position) as Stores?
      _binding.etSearchLocations.setText(selectedPoi?.storeName)
      _binding.etSearchLocations.setSelection(selectedPoi?.storeName?.length!!)
      _binding.searchLocation.performClick()
    }

    (requireActivity() as DashboardActivity).closeIfOpen()
    (requireActivity() as DashboardActivity).disableBottomMenu()

  }

  private fun IsExistingCity(latitude: Double, longitude: Double): Boolean {
    val gcd = Geocoder(activity, Locale.getDefault())
    try {
      val addresses = gcd.getFromLocation(latitude, longitude, 1)
      for (adrs in addresses) {
        if ((adrs != null) && (adrs.locality.length > 0)) {
          val city = adrs.locality
          if (city != null && city != "") {
            if(city.equals(cityTxt)){
              return true
            }
          }
        }
      }
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return false
  }

  private fun showUpdatedLocation(){
    if(!isAutoLocation){
      val marker = mMap.addMarker(
        MarkerOptions().position(LatLng(Constants.locationTemp.latitude, Constants.locationTemp.longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(resources.getString(R.string.txtUpdatedLocation))
      )
      mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(Constants.locationTemp.latitude, Constants.locationTemp.longitude)))
      mMap.animateCamera(
        CameraUpdateFactory.newCameraPosition(
          CameraPosition.Builder().target(LatLng(Constants.locationTemp.latitude, Constants.locationTemp.longitude)).zoom(10f).bearing(0f).tilt(0f).build()
        )
      )
      marker.showInfoWindow()
    }else{
      isAutoLocation = false
    }
  }

  private fun loadDatatoView(stores: ArrayList<Stores>) {
    if (stores.size != 0) {
      displayData(stores)
      autoLocationAdapter = AutoLocationAdapter(requireActivity(), android.R.layout.simple_list_item_1, stores)
      //_binding.etSearchLocations.setAdapter(autoLocationAdapter)
      //_binding.etSearchLocations.threshold = 3
    } else {
      displayNodataRadial()
    }
  }

  private fun displayNodataRadial() {
    AlertDialog.Builder(context)
      .setTitle(R.string.no_data_found)
      .setMessage(R.string.txtAlertMsg)
      .setPositiveButton(android.R.string.ok)
      { dialog, which ->
      }
      .show()
  }

  private fun displayRadialSearchAlert() {


    builder = AlertDialog.Builder(requireActivity())
    builder.setTitle(R.string.no_data_found)
    builder.setMessage(R.string.txtAlertRadialMsg)
    builder.setPositiveButton(android.R.string.ok,
      DialogInterface.OnClickListener { dialog, which ->
        isAutoLocation = false
        clearPlottedMaps()
        displayRadialSearchData()
      })
    builder.setNegativeButton(
      android.R.string.cancel, null
    )
    builder.setIcon(android.R.drawable.ic_dialog_alert)
    val alert = builder.create()

    if (!alert.isShowing) {
      alert.show()
    } else {
      alert.dismiss()
    }

  }

  private fun displayNodata() {
    binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }
  }

  private fun initRecyclerView() {
    binding.storeList.layoutManager =
      LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
    adapter = LocateStoreAdapter { store: Stores -> onItemClick(store) }
    binding.storeList.adapter = adapter
    binding.storeList.showShimmer()
    binding.storeList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (!recyclerView.canScrollVertically(1)){
            if(isDBLoad){
              PAGENUMBER++
              getData(PAGENUMBER)
            }
        }
      }

    })

    binding.rvRecentList.layoutManager =
      LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
    adapterRecent = LocateLocalStoreAdapter { storeDetail: StoreDetail -> onItemClick(storeDetail) }
    binding.rvRecentList.adapter = adapterRecent

  }

  private fun getData(pageNo: Int) {
    if (Network.isAvailable(requireActivity())) {
      storeDetailViewModel!!.getRemoteData(
        mapOf(
          Constants.SEARCH_QUERY to searchTxt,
          Constants.LANGUAGE_CODE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE)
            .toString(),
          Constants.PAGE to pageNo.toString(),
          Constants.PER_PAGE to Constants.LOCATE_STORE_COUNT
        )
      )
    } else {
      _binding.searchLocation.isEnabled = true
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

    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      return
    }

    var location: Location? = locationManger.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    if(location == null){
      location = locationManger.getLastKnownLocation(locationManger.getBestProvider(criteria, false)!!)
    }

    else if(location == null){
      //location = locationManger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locateStoreListener)
      //locationManger?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locateStoreListener)
      location = Constants.location
    }

    //val location: Location? = locationManger.getLastKnownLocation(locationManger.getBestProvider(criteria, false)!!)

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
    val cameraPo =
      CameraPosition.Builder().target(currentLatLong).zoom(17f).bearing(0f).tilt(0f).build()
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

    storesListTemp.addAll(stores)

    binding.rlSearchStores?.visibility = View.GONE
    binding.rvStoreList?.visibility = View.VISIBLE
    binding.storeList.hideShimmer()
    adapter.setList(LocateManager.sortListNearBy(ArrayList(storesListTemp)))
    adapter.notifyDataSetChanged()

    //Display Markers when Online and Zoom according to area
    for (item in stores) {
      val marker = mMap.addMarker(
        MarkerOptions().position(LatLng(item.latitude, item.longitude)).title(item.storeName)
      )
      mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(item.latitude, item.longitude)))
      //marker.showInfoWindow()
    }
    if(stores.size>0){
      mMap.animateCamera(
        CameraUpdateFactory.newCameraPosition(
          CameraPosition.Builder().target(LatLng(stores[0].latitude, stores[0].longitude)).zoom(10f)
            .bearing(0f).tilt(0f).build()
        )
      )
    }
  }

  fun clearPlottedMaps(){
    PAGENUMBER = 1
    storesListTemp.clear()
    mMap.clear()
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
    if (activity != null && isAdded()) {

      val locale = Locale(
        myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(),
        Locale.getDefault().country
      )
      println("Language ::  " + Locale.getDefault().language + " -> Country : " + (Locale.getDefault().country).toLowerCase())

      val gcd = Geocoder(activity, Locale.getDefault())
      try {
        val addresses = gcd.getFromLocation(Constants.location.latitude, Constants.location.longitude, 2)
        for (adrs in addresses) {
          if ((adrs != null) && (adrs.locality.length > 0)) {
            val city = adrs.locality
            if (city != null && city != "") {
              pincodeTxt = "," + adrs.postalCode
              _binding.etSearchLocations.setText(city)
              cityTxt = city
              isAutoLocation = true
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

  inner class AutoLocationAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val allPois: List<Stores>
  ) :
    ArrayAdapter<Stores>(context, layoutResource, allPois), Filterable {
    private var mPois: List<Stores> = allPois

    override fun getCount(): Int {
      return mPois.size
    }

    override fun getItem(p0: Int): Stores? {
      return mPois.get(p0)
    }

    override fun getItemId(p0: Int): Long {
      // Or just return p0
      return mPois.get(p0).storeId.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
      val view: TextView = convertView as TextView? ?: LayoutInflater.from(context)
        .inflate(layoutResource, parent, false) as TextView
      view.text = "${mPois[position].storeName}"
      return view
    }

    override fun getFilter(): Filter {
      return object : Filter() {
        override fun publishResults(
          charSequence: CharSequence?,
          filterResults: Filter.FilterResults
        ) {
          mPois = filterResults.values as List<Stores>
          notifyDataSetChanged()
        }

        override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
          val queryString = charSequence?.toString()?.lowercase()

          val filterResults = Filter.FilterResults()
          filterResults.values = if (queryString == null || queryString.isEmpty())
            allPois
          else
            allPois.filter {
              it.storeName.lowercase().contains(queryString) || it.storeDistrict.lowercase()
                .contains(queryString) ||
                      it.storePincode.toString()
                        .contains(queryString) || it.storeVillage.lowercase().contains(queryString)
            }
          return filterResults
        }
      }
    }
  }

  private fun displayRadialSearchData() {
    if (Network.isAvailable(requireActivity())) {
      storeDetailViewModel!!.getRemoteRadialSearchData(
        mapOf(
          Constants.SEARCH_QUERY to searchTxt,
          Constants.LANGUAGE_CODE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE)
            .toString(),
          Constants.PAGE to 1.toString(),
          Constants.PER_PAGE to Constants.LOCATE_STORE_COUNT
        )
      )
    } else {
      displayOfflineData()
    }
  }

  private fun languageKeyBoardAlert(){

    if(!myPreference.isTermsConditionsAccepted()){
      AlertDialog.Builder(requireActivity())
        .setMessage(R.string.txtAlertRadialMsg)
        .setPositiveButton(android.R.string.ok,
          DialogInterface.OnClickListener { dialog, which ->
            myPreference.setStringVal(Constants.IS_LOCATION_LANGUAGE_KEYBOARD,"Accepted")
          })
        .setNegativeButton(android.R.string.cancel,
          DialogInterface.OnClickListener { dialog, which ->
            myPreference.setStringVal(Constants.IS_LOCATION_LANGUAGE_KEYBOARD,"")
          }
        ).show()
    }
  }


}