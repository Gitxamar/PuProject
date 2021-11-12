package cargill.com.purina.dashboard.View.Home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.util.LocaleData
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.Campaign.Campaign
import cargill.com.purina.dashboard.Model.Campaign.Campaigns
import cargill.com.purina.dashboard.Model.LocateStore.LocationDetails
import cargill.com.purina.dashboard.Model.ProductDetails.Image
import cargill.com.purina.dashboard.Repository.DashboardRepository
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.View.ProductCatalog.ImageViewPagerAdapter
import cargill.com.purina.dashboard.View.RearingAnimals.FragmentRearingAnimals
import cargill.com.purina.dashboard.viewModel.DashboardViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.DashboardViewModelFactory
import cargill.com.purina.databinding.FragmentHomeBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.PermissionCheck
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.io.IOException
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class Home : Fragment(){
    lateinit var binding: FragmentHomeBinding
    lateinit var myPreference: AppPreference
    private var animalSelected: String = ""
    private var animalSelectedCode: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationManager : LocationManager? = null
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var campaigns: Campaigns

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myPreference = AppPreference(context)
        animalSelected = myPreference.getStringValue(Constants.USER_ANIMAL).toString()
        animalSelectedCode = myPreference.getStringValue(Constants.USER_ANIMAL_ORDER).toString()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dao = PurinaDataBase.invoke(requireContext()).dao
        val repository = DashboardRepository(dao, requireContext())
        val factory = DashboardViewModelFactory(repository)
        dashboardViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
        getData()
        if(animalSelected.isEmpty()){
            binding.userSelected.visibility = View.VISIBLE
            binding.userSelectedAnimal.text = getString(R.string.select_species)
            setAnimalLogo(0)
        }else{
            binding.userSelected.visibility = View.VISIBLE
            binding.userSelected.strokeColor = ContextCompat.getColor(requireContext(), R.color.app_primary)
            binding.userSelected.strokeWidth = 2
            binding.userSelectedAnimal.text = getString(R.string.rearing).plus(animalSelected)
            setAnimalLogo(animalSelectedCode.toInt())
        }
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.selectedItem.observe(viewLifecycleOwner, Observer {
            Log.i("home animal.name", it.name)
            animalSelected = myPreference.getStringValue(Constants.USER_ANIMAL).toString()
            binding.userSelected.strokeColor = ContextCompat.getColor(requireContext(), R.color.app_primary)
            binding.userSelected.strokeWidth = 2
            binding.userSelected.visibility = View.VISIBLE
            binding.userSelectedAnimal.text = getString(R.string.rearing).plus(it.name)
            setAnimalLogo(it.order_id)
        })
        binding.root.cardViewProductCatalog.setOnClickListener {
            (requireActivity() as DashboardActivity).closeIfOpen()
            animalSelected = myPreference.getStringValue(Constants.USER_ANIMAL).toString()
            if(animalSelected.isEmpty()){
                Snackbar.make(binding.root,R.string.select_species, Snackbar.LENGTH_LONG).show()
            }else{
                if(Network.isAvailable(requireContext())){
                    findNavController().navigate(R.id.action_home_to_productCatalogueFilter)
                }else{
                    findNavController().navigate(R.id.action_home_to_productCatalog)
                }
            }
        }
        binding.root.cardViewFeed.setOnClickListener {
            (requireActivity() as DashboardActivity).closeIfOpen()
            animalSelected = myPreference.getStringValue(Constants.USER_ANIMAL).toString()
            if(animalSelected.isEmpty()){
                Snackbar.make(binding.root,R.string.select_species, Snackbar.LENGTH_LONG).show()
            }else{
                findNavController().navigate(R.id.action_home_to_fragmentFeedProgramFilter)
            }
        }
        binding.root.cardViewStore.setOnClickListener {
            (requireActivity() as DashboardActivity).closeIfOpen()
            findNavController().navigate(R.id.action_home_to_Locate_Store)
        }
        binding.root.cardViewDiseases.setOnClickListener {
            (requireActivity() as DashboardActivity).closeIfOpen()
            animalSelected = myPreference.getStringValue(Constants.USER_ANIMAL).toString()
            if(animalSelected.isEmpty()){
                Snackbar.make(binding.root,R.string.select_species, Snackbar.LENGTH_LONG).show()
            }else{
                findNavController().navigate(R.id.action_home_to_Disease_List)
            }
        }
        binding.root.userSelected.setOnClickListener {
            (requireActivity() as DashboardActivity).closeIfOpen()
            animalSelected = myPreference.getStringValue(Constants.USER_ANIMAL).toString()
            if(animalSelected.isEmpty()){
                Snackbar.make(binding.root,R.string.select_species, Snackbar.LENGTH_LONG).show()
            }else {
                dashboardViewModel.getArticles(
                    mapOf(
                        Constants.PAGE to "1",
                        Constants.PER_PAGE to "100",
                        Constants.SPECIES_ID to myPreference.getStringValue(Constants.USER_ANIMAL_CODE)
                            .toString(),
                        Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE)
                            .toString()
                    )
                )
                observeArticleData()
            }
        }

        sharedViewModel.locationItem.observe(requireActivity(),{

            if(activity != null && isAdded()){
                binding.root.location.text = it.city
            }

        })

        if(Constants.locationCity.toString().isNotEmpty()){
            binding.root.location.text = Constants.locationCity
        }else{
            binding.root.location.text = ""
        }

        (requireActivity() as DashboardActivity).enableBottomMenu()


        locationManager = requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocationParams()

        (requireActivity() as DashboardActivity).enableBottomMenu()

    }
    fun getData(){
        if(Network.isAvailable(requireContext())){
            dashboardViewModel.campaignCacheData(mapOf(Constants.PAGE to "1", Constants.PER_PAGE to "100", Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()))
        }else{
            dashboardViewModel.getCampaignData(myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString())
        }
        observeData()
    }
    @SuppressLint("NewApi")
    private fun observeData(){
        dashboardViewModel.campaignData().observe(viewLifecycleOwner, Observer {
            Log.i("Campaign data: ", it.toString())
            campaigns = it
            display(it.campaigns)
        })
        dashboardViewModel.offlineCampaignData().observe(viewLifecycleOwner, Observer {
            Log.i("Campaign offline data: ", it.toString())
            campaigns = Campaigns(it,0,0,0,0 )
            val today = LocalDate.now()
            var campaigns: ArrayList<Campaign> = arrayListOf()
            for(cam in it){
                Log.i("1", ChronoUnit.DAYS.between(today, LocalDate.parse(cam.from_date)).toString())
                Log.i("2", ChronoUnit.DAYS.between(today, LocalDate.parse(cam.to_date)).toString())
                if(ChronoUnit.DAYS.between(today, LocalDate.parse(cam.from_date)) < 0 && ChronoUnit.DAYS.between(today, LocalDate.parse(cam.to_date)) > 0){
                    campaigns.add(cam)
                }
            }
            display(campaigns)
        })
    }
    private fun observeArticleData(){
        dashboardViewModel.articles().observe(viewLifecycleOwner, Observer {
            Log.i("articles ", it.toString())
            requireFragmentManager().beginTransaction().add(R.id.fragmentDashboard, FragmentRearingAnimals(it)).addToBackStack(null).commit()
        })
    }
    private fun display(campaigns: List<Campaign>){

        var campaignImages: ArrayList<Image> = arrayListOf()
        for (cam in campaigns){
            campaignImages.add(Image(cam.mode_active,cam.campaign_id, cam.banner_image_url, 0, 0 ))
        }
        if(campaignImages.isNotEmpty()){
            binding.campaignImageContainer.visibility = View.VISIBLE
            binding.campaignImageView.visibility = View.INVISIBLE
            binding.campaignViewPager.adapter = ImageViewPagerAdapter(campaignImages as List<Image>, { images: List<Image>, position:Int ->previewImage(images, position) })
            if(campaignImages.size <= 1){
                binding.campaignTabLayout.visibility = View.GONE
            }else{
                binding.campaignTabLayout.visibility = View.VISIBLE
                binding.campaignTabLayout.let {
                    binding.campaignViewPager?.let { it1 ->
                        TabLayoutMediator(it, it1){ tab, position->
                        }.attach()
                    }
                }
            }
        }else{
            //no data handle
            binding.campaignImageContainer!!.visibility = View.INVISIBLE
            binding.campaignImageView!!.visibility = View.VISIBLE
        }
    }
    private fun previewImage(images: List<Image>, position:Int){
        if(images.isNotEmpty()){
            var campaignImages: ArrayList<Image> = arrayListOf()
            for (cam in campaigns.campaigns){
                campaignImages.add(Image(cam.mode_active,cam.campaign_id, cam.promo_image_url, 0, 0 ))
            }
            val bundle = bundleOf(
                Constants.IMAGES to campaignImages,
                Constants.PRODUCT_ID to 0,
                "position" to position)
            findNavController().navigate(R.id.action_home_to_fragmentImageViewer, bundle)
        }
    }

    fun setAnimalLogo(order_id: Int){
        when (order_id){
            0 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(0, 0,0,0)
            1 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hen, 0,0,0)
            2 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_layer, 0,0,0)
            3 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_duck, 0,0,0)
            4 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_quail, 0,0,0)
            5 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_turkey, 0,0,0)
            6 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rabbit, 0,0,0)
            7 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_swine, 0,0,0)
            8 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cow, 0,0,0)
            9 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sheepgoat, 0,0,0)
        }
    }

    private fun getLocationParams() {
        if (PermissionCheck.accessFineLocation(requireActivity())){
            if(ActivityCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(),arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),Constants.LOCATION_PERMISSION_REQ_CODE)
            }else{
                startLocationManager()
            }
        }
        else {
            setLastKnownLocation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            Constants.LOCATION_PERMISSION_REQ_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locateStoreListener)

                }else{
                    setLastKnownLocation()
                }
        }
    }

    private fun setLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    setLocationName(location.latitude,location.longitude)
                }
            }
    }

    fun setLocationName(lattitude: Double, longitude: Double) {
        var cityName = "Not Found"
        if(activity != null && isAdded()){

            val locale = Locale(myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(), Locale.getDefault().country)
            println("Language ::  "+ Locale.getDefault().language +" ***********Country : "+ (Locale.getDefault().country).toLowerCase())

            val gcd = Geocoder(activity, Locale.getDefault())
            try {
                val addresses = gcd.getFromLocation(lattitude, longitude, 2)
                //val addresses = gcd.getFromLocation(51.719582, 36.188993, 2)
                for (adrs in addresses) {
                    if ((adrs != null) && (adrs.locality.length > 0)) {
                        val city = adrs.locality
                        println("Postal ::  "+ adrs.postalCode)
                        if (city != null && city != "") {
                            cityName = city
                            //binding.root.location.text = cityName
                            Constants.locationCity = cityName
                            sharedViewModel.locationLoaded(LocationDetails(cityName))
                            println("city ::  $cityName")
                            break
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setMessage(R.string.enable_location)
            .setCancelable(false)
            .setPositiveButton(R.string.YES,
                DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
            .setNegativeButton(R.string.NO,
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private val locateStoreListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
          setLocationName(location.latitude ,location.longitude)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {
        }
        override fun onProviderDisabled(provider: String) {
            buildAlertMessageNoGps();
        }
    }

    private fun startLocationManager(){
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
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locateStoreListener)

    }

}