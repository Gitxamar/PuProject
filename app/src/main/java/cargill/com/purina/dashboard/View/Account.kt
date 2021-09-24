package cargill.com.purina.dashboard.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cargill.com.purina.databinding.FragmentAccountBinding
import cargill.com.purina.splash.View.OnboardingActivity
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import kotlinx.android.synthetic.main.fragment_account.*
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.dashboard.Model.Home.FAQs
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Repository.DashboardRepository
import cargill.com.purina.dashboard.View.Home.AnimalAdapter
import cargill.com.purina.dashboard.View.Home.FaqAdapter
import cargill.com.purina.dashboard.View.LocateStore.LocateLocalStoreAdapter
import cargill.com.purina.dashboard.View.LocateStore.LocateManager
import cargill.com.purina.dashboard.viewModel.DashboardViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.DashboardViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dashboard_animal_filter.view.*
import kotlinx.android.synthetic.main.fragment_account.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Account.newInstance] factory method to
 * create an instance of this fragment.
 */
class Account : Fragment() {
  lateinit var binding: FragmentAccountBinding
  lateinit var myPreference: AppPreference
  lateinit var ctx: Context
  private lateinit var dashboardViewModel: DashboardViewModel
  private lateinit var faqAdapter: FaqAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    //return inflater.inflate(R.layout.fragment_account, container, false)
    binding = FragmentAccountBinding.inflate(inflater)
    init()
    return binding.root
  }

  private fun init() {
    val dao = PurinaDataBase.invoke(ctx).dao
    val repository = DashboardRepository(dao, ctx)
    val factory = DashboardViewModelFactory(repository)
    dashboardViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
    binding.settingsViewModel = dashboardViewModel
    binding.lifecycleOwner = this
    intiRecyclerView()
  }

  private fun intiRecyclerView() {
    binding.root.rvFaq.layoutManager =
      LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
    faqAdapter = FaqAdapter { storeDetail: FAQs -> onItemClick(storeDetail) }
    binding.root.rvFaq.adapter = faqAdapter
  }

  private fun onItemClick(faqData: FAQs) {

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    myPreference = AppPreference(ctx)
    var lang: String? = myPreference.getStringValue(Constants.USER_LANGUAGE)
    languageChangeText.text = lang
    change.setOnClickListener {
      activity.let {
        val intent = Intent(it, OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
      }
    }
    dashboardViewModel?.faqResponse?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        Log.i("data commingng", it.body().toString())
        if (it.body()!!.FAQs.size != 0) {
          displayData(it.body()!!.FAQs)
        } else {
          displayOffline()
        }
      } else {
        displayOffline()
      }
    })
    fetchBuildVersion()
    binding.rvFaq.visibility = View.GONE
    binding.FAQSpinner.setOnClickListener {
      if(binding.rvFaq.visibility == View.VISIBLE){
        binding.rvFaq.visibility = View.GONE
      }else{
        binding.rvFaq.visibility = View.VISIBLE
        getData()
      }
    }
  }

  private fun displayOffline() {
    var fAQsList: List<FAQs> = dashboardViewModel!!.getOfflineFAQList()
    if (!fAQsList.isEmpty() || fAQsList.size > 0) {
      faqAdapter.setList(fAQsList)
      faqAdapter.notifyDataSetChanged()
    }else{
      binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }
    }

  }

  private fun displayData(faQs: List<FAQs>) {
    faqAdapter.setList(faQs)
    faqAdapter.notifyDataSetChanged()

  }

  private fun fetchBuildVersion() {
    try {
      val pInfo = ctx!!.packageManager.getPackageInfo(ctx!!.packageName, 0)
      binding.tvAppVersion.text = pInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
      e.printStackTrace()
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    this.ctx = context
  }

  private fun getData() {
    if (Network.isAvailable(requireActivity())) {
      dashboardViewModel!!.getFaqViewModel(
        mapOf(
          Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()
        )
      )
    } else {
      displayOffline()
    }
  }

}