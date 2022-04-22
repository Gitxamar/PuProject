package cargill.com.purina.dashboard.View

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.Home.FAQs
import cargill.com.purina.dashboard.Repository.DashboardRepository
import cargill.com.purina.dashboard.View.Home.FaqAdapter
import cargill.com.purina.dashboard.viewModel.DashboardViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.DashboardViewModelFactory
import cargill.com.purina.databinding.FragmentAccountBinding
import cargill.com.purina.splash.View.OnboardingActivity
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dashboard_animal_filter.view.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import java.io.File


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
    var assetsPath: String = getLanguagePathRaw(lang!!)
    //Log.i("Afterlanguage:::::",assetsPath)
    //viewOrDisableOpenPdf(assetsPath)

    if (Constants.IS_PROD) {
      change.visibility = View.GONE
    }

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
      if (binding.rvFaq.visibility == View.VISIBLE) {
        binding.rvFaq.visibility = View.GONE
      } else {
        binding.rvFaq.visibility = View.VISIBLE
        getData()
      }
    }

    helpManualHeader.setOnClickListener {
      activity.let {
        val intent = Intent(it, PdfViewActivity::class.java)
        intent.putExtra("absolutePath", assetsPath)
        startActivity(intent)
      }
    }

    helpUsage.setOnClickListener {
      activity.let {
        val intent = Intent(it, PdfViewActivity::class.java)
        intent.putExtra("absolutePath", assetsPath)
        startActivity(intent)
      }
    }

    btnTerms.setOnClickListener {
      Constants.IS_TERMS = true
      Constants.TERMS_VALUE = "LanguageScreen"
      activity.let {
        val intent = Intent(it, OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
      }
    }

    btnShareApp.setOnClickListener {
      val sendIntent = Intent()
      sendIntent.action = Intent.ACTION_SEND
      sendIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=cargill.com.purina")
      sendIntent.type = "text/plain"
      val shareIntent = Intent.createChooser(sendIntent, null)
      startActivity(shareIntent)
    }


  }

  private fun viewOrDisableOpenPdf(assetsPath: String) {

    val path: Uri = Uri.parse(assetsPath)
    val absoluteFolderpath: String = path.toString()
    val directory = File(absoluteFolderpath)
    if (directory.isDirectory) {
      val files = directory.listFiles()
      for (i in 0 until files.size) {
        Log.i("Files", "FileName:" + files[i].name)
        binding.helpManualCard.visibility = View.VISIBLE
      }
    } else {
      binding.helpManualCard.visibility = View.GONE
    }

  }

  private fun displayOffline() {
    var fAQsList: List<FAQs> = dashboardViewModel!!.getOfflineFAQList()
    if (!fAQsList.isEmpty() || fAQsList.size > 0) {
      faqAdapter.setList(fAQsList)
      faqAdapter.notifyDataSetChanged()
    } else {
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

  private fun getLanguagePathRaw(lang: String): String {

    if (lang == "English") {
      return "language/en/" + Constants.txtEnglighPDF
    } else if (lang == "Русский") {
      return "language/ru/" + Constants.txtRussianPDF
    } else if (lang == "Magyar") {
      return "language/hu-rHU/" + Constants.txtMagyarPDF
    } else if (lang == "Polskie") {
      return "language/pl-rPL/" + Constants.txtPolskiePDF
    } else if (lang == "Italiana") {
      return "language/it-rIT/" + Constants.txtItalianaPDF
    } else if (lang == "Română") {
      return "language/ro/" + Constants.txtRomanaPDF
    } else {
      return "language/en/" + Constants.txtEnglighPDF
    }
  }

}