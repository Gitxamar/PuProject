package cargill.com.purina.dashboard.View.LocateStore

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.viewModel.LocateStoreViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.LocateStoreViewModelFactory
import cargill.com.purina.databinding.FragmentLocateStoreOwnerBinding
import cargill.com.purina.utils.Constants
import com.google.android.material.snackbar.Snackbar
import android.os.Looper
import android.util.ArrayMap

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Utils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class LocateStoreOwnerFragment : Fragment(){
  lateinit var binding: FragmentLocateStoreOwnerBinding
  private lateinit var storeDetailViewModel: LocateStoreViewModel
  private val _binding get() = binding!!
  private var store_id: Int = 0
  var sharedViewmodel: SharedViewModel? = null
  var doubleBackToExitPressedOnce = false
  lateinit var myPreference: AppPreference
  private lateinit var ctx: Context

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentLocateStoreOwnerBinding.inflate(inflater, container, false)
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
    binding.locateStoreOwnerViewModel = storeDetailViewModel
    binding.lifecycleOwner = this
    if (arguments != null) {
      if (requireArguments().containsKey(Constants.STORE_ID)) {
        store_id = arguments?.getInt(Constants.STORE_ID)!!
      }
    }

    binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_left)
    binding.toolbar.setNavigationOnClickListener {
      (requireActivity() as DashboardActivity).closeIfOpen()
      goBackFunctionality()
    }

    binding.tvTitle.setOnClickListener {
      (requireActivity() as DashboardActivity).closeIfOpen()
      goBackFunctionality()
    }

    binding.btnEmailOwner.setOnClickListener {
      if((binding.tvStoreEmail.text.trim().length==0) || !(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.tvStoreEmail.text.trim()).matches())){
        Toast.makeText(activity,R.string.txtEmailValid,Toast.LENGTH_SHORT).show()
      }else{
        val jsonParams: MutableMap<String?, Any?> = ArrayMap()
        jsonParams["to"] = "${binding.tvStoreEmail.text}"
        jsonParams["subject"] = "Request to update Store Details"

        val jsonObject = """
          {"Is-Type":"dealer_detail_store",
          "workingHours":"${binding.tvStoreHours.text}",
          "workingDays":"${binding.tvStoreDays.text}",
          "village":"${binding.tvStoreRegion.text}",
          "pincode":"${binding.tvStorePin.text}",
          "district":"${binding.tvStoreCity.text}",
          "address":"${binding.tvStoreAddress.text}",
          "phone":"${binding.tvStorePhone.text}",
          "website":""${binding.tvStoreWebsite.text}"",
          "name":""${binding.tvStoreName.text}"",
          "breeding_animals":""${binding.tvBreedingAnimals.text}"",
          "is_freedelivery":""${binding.tvIsFreeDelievry.text}"",
          "is_vetservice":""${binding.tvIsVet.text}"",
          "email":""${binding.tvStoreEmail.text}""}
         """.trimIndent()


        jsonParams["message"] = "${jsonObject}"
        Log.i("Email Data",JSONObject(jsonParams).toString())
        val body: RequestBody = JSONObject(jsonParams).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        if(checkSendEmail()){
          myPreference.setNewOwnerCounter(1)
          storeDetailViewModel!!.sendEmail(body)
        }
      }
    }

    (requireActivity() as DashboardActivity).disableBottomMenu()

    getData()
    storeDetailViewModel?.remoteEmail?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        Log.i("data commingng", it.body().toString())
        if (it.body() != null) {
          if (it.body()!!.status.equals("Mail sent successfully")){
            Toast.makeText(activity,R.string.txtEmailSuccessMsgOwner,Toast.LENGTH_SHORT).show()
            binding.let { Snackbar.make(it.root, R.string.txtEmailSuccessMsgOwner, Snackbar.LENGTH_LONG).show() }
            val bundle = bundleOf(Constants.STORE_ID to store_id)
            findNavController().navigate(R.id.action_fragmentLocateStore_back, bundle)
          }else{
            binding.let { Snackbar.make(it.root, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show() }
          }
        }else{
          binding.let { Snackbar.make(it.root, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show() }
        }
      } else {
        binding.let { Snackbar.make(it.root, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show() }
      }
    })
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

  private fun displayOnlineData(storeDetail: StoreDetail) {

    ViewsEnableDisable(storeDetail)

    if(storeDetail.breeding_animals == ""){
      binding.tvBreedingAnimals.setText("")
    }else{
      binding.tvBreedingAnimals.setText(storeDetail.breeding_animals)

    }
  }

  private fun displayOfflineData(storeDetail: StoreDetail) {

    ViewsEnableDisable(storeDetail)

    if(storeDetail.breeding_animals == ""){
      binding.tvBreedingAnimals.setText("")
    }else{
      binding.tvBreedingAnimals.setText(storeDetail.breeding_animals)
    }

  }

  private fun displayNodata() {
    binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }
  }

  private fun ViewsEnableDisable(storeDetail: StoreDetail){

    binding.tvStoreName.text = storeDetail.name
    if(storeDetail.address==""){
      binding.tvStoreAddress.setText("")
    }else{
      binding.tvStoreAddress.setText(storeDetail.address)
    }

    if(storeDetail.district==""){
      binding.tvStoreCity.setText("")
    }else{
      binding.tvStoreCity.setText(storeDetail.district)
    }

    if(storeDetail.village==""){
      binding.tvStoreRegion.setText("")
    }else{
      binding.tvStoreRegion.setText(storeDetail.village)
    }

    if(storeDetail.pincode==0){
      binding.tvStorePin.setText("")
    }else{
      binding.tvStorePin.setText(storeDetail.pincode.toString())
    }

    if(storeDetail.phone==""){
      binding.tvStorePhone.setText("")
    }else{
      binding.tvStorePhone.setText(storeDetail.phone)
    }

    binding.cvStoreDealer.visibility = View.GONE

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
      binding.tvStoreHours.setText("")
    }else{
      binding.tvStoreHours.setText(storeDetail.workingHours)
    }

    if(storeDetail.workingDays==""){
      binding.tvStoreDays.setText("")
    }else{
      binding.tvStoreDays.setText(storeDetail.workingDays)
    }

    if(storeDetail.website==""){
      binding.tvStoreWebsite.setText("")
    }else{
      binding.tvStoreWebsite.setText(storeDetail.website)
    }

    if((storeDetail.partnerName=="") && (storeDetail.dealerName=="")){
      binding.cvStoreDealer.visibility = View.GONE
    }

    if((storeDetail.workingHours=="") && (storeDetail.workingDays=="")){
      //binding.cvStoreClock.visibility = View.GONE
    }

    if((storeDetail.breeding_animals.trim().isEmpty()) && (storeDetail.is_freedelivery.trim().isEmpty())){
      binding.llBreedingAnimals.visibility = View.GONE
    }

    if(storeDetail.email==""){
      binding.tvStoreEmail.setText("")
    }else{
      binding.tvStoreEmail.setText(storeDetail.email)
    }

    if(storeDetail.is_vetservice==""){
      binding.tvIsVet.setText("")
    }else{
      binding.tvIsVet.setText(storeDetail.is_vetservice)
    }

  }

  private fun goBackFunctionality(){
    var alertDialog: AlertDialog? = null
    val builder = AlertDialog.Builder(requireActivity())
    //set message for alert dialog
    builder.setMessage(R.string.txtAlertMsgOwnerCancel)
    builder.setIcon(android.R.drawable.ic_dialog_alert)

    //performing positive action
    builder.setPositiveButton(R.string.YES){dialogInterface, which ->
      val bundle = bundleOf(Constants.STORE_ID to store_id)
      findNavController().navigate(R.id.action_fragmentLocateStore_back, bundle)

    }
    //performing negative action
    builder.setNegativeButton("No"){dialogInterface, which ->
      if(alertDialog?.isShowing == true){
        alertDialog!!.dismiss()
        }
    }
    // Create the AlertDialog

    if((alertDialog?.isShowing == true) || (alertDialog!=null)){

    }else{
      alertDialog = builder.create()
      alertDialog!!.setCancelable(false)
      alertDialog!!.show()
    }

  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    ctx = context
    myPreference = AppPreference(context)
  }

  private fun checkSendEmail(): Boolean {
    if (myPreference.getNewOwnerCounter() == 0) {
      return true
    } else if (myPreference.getNewOwnerCounter() == 3) {
      Toast.makeText(ctx, R.string.txtErrEmailDisable, Toast.LENGTH_SHORT).show()
      return false
    } else {
      return true
    }
  }

}