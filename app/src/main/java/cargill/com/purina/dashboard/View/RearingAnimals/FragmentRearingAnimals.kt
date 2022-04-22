package cargill.com.purina.dashboard.View.RearingAnimals

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.Articles.Article
import cargill.com.purina.dashboard.Repository.DashboardRepository
import cargill.com.purina.dashboard.viewModel.DashboardViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.DashboardViewModelFactory
import cargill.com.purina.databinding.FragmentRearingAnimalsBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.PermissionCheck
import com.google.android.material.snackbar.Snackbar
import java.io.File
import android.database.Cursor
import android.app.ProgressDialog
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.View.PdfViewActivity
import cargill.com.purina.utils.Utils


class FragmentRearingAnimals(private var articles: List<Article>) : Fragment(),UpdateProgress{
  var binding:FragmentRearingAnimalsBinding? = null
  private val _binding get() = binding!!
  private lateinit var adapter: RearingAnimalAdapter
  private var dataLoaded:Boolean = false
  var sharedViewmodel: SharedViewModel? = null
  private lateinit var dashboardViewModel: DashboardViewModel
  private var animalSelected: String = ""
  lateinit var myPreference: AppPreference
  var downloadId:Long = 0
  var file: File? = null
  var userClickedPosition : Int = 0
  private var progressDialog:ProgressDialog? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentRearingAnimalsBinding.inflate(inflater, container, false)
    return binding!!.root
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val dao = PurinaDataBase.invoke(requireContext()).dao
    val repository = DashboardRepository(dao, requireContext())
    val factory = DashboardViewModelFactory(repository)
    dashboardViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
    _binding.rearingAnimalViewModel = dashboardViewModel
    _binding.lifecycleOwner = this
    init()
    if(articles.isNotEmpty()){
      displayData(articles)
    }else{
      displayNodata()
    }
  }

  private fun init(){
    _binding.articleList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    PermissionCheck.readAndWriteExternalStorage(requireContext())
    adapter = RearingAnimalAdapter { article: Article, position: Int ->
      onItemClick(
        article,
        position
      )
    }
    _binding.articleList.adapter = adapter
    _binding.articleList.showShimmer()
    _binding.articleList.addOnScrollListener(object : RecyclerView.OnScrollListener(){

    })
    _binding.back.setOnClickListener {
      (requireActivity() as DashboardActivity).closeIfOpen()
      requireFragmentManager().popBackStack()
    }
    _binding.refresh.setOnRefreshListener {
      getArticles()
      _binding.refresh.isRefreshing = true
    }
    sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    sharedViewmodel?.selectedItem?.observe(_binding.lifecycleOwner!!, Observer {
      sharedViewmodel!!.navigate("")
      if(dataLoaded){
        getArticles()
      }
    })
  }
  private fun getArticles(){
    animalSelected = myPreference.getStringValue(Constants.USER_ANIMAL).toString()
    if(animalSelected.isEmpty()){
      Snackbar.make(_binding.root, R.string.select_species, Snackbar.LENGTH_LONG).show()
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
  val br= object : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
      var id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
      if(id == downloadId){
        launchPDF()
      }
    }
  }
  override fun onResume() {
    super.onResume()
    requireActivity().registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
  }
  override fun onDestroyView() {
    super.onDestroyView()
    requireActivity().unregisterReceiver(br)
    binding = null
  }
  private fun onItemClick(article: Article,position: Int){
    userClickedPosition = position
    if(article!!.pdf_link.isEmpty() || article!!.pdf_link == ""){
      Snackbar.make(_binding.root,R.string.no_file_path, Snackbar.LENGTH_LONG).show()
      return
    }
    file = File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
      article.article_name.plus("_"+Utils.getFileName(article.pdf_link))
    )
    if(PermissionCheck.readAndWriteExternalStorage(requireContext())){
      progressDialog = ProgressDialog(requireContext())
      if(!file!!.exists()){
        if(Network.isAvailable(requireContext())){
          dashboardViewModel.getProductPDF(article!!.pdf_link)
          dashboardViewModel.pathWithToken.observe(_binding.lifecycleOwner!!, Observer {
            Log.i("path", it.body().toString())
            var request = DownloadManager.Request(
              Uri.parse(it.body().toString())
            ).setTitle(article.article_name.plus("_"+Utils.getFileName(article.pdf_link)))
              .setDescription(article.species_name)
              .setAllowedOverRoaming(true)
              .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
              .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, article.article_name.plus("_"+Utils.getFileName(article.pdf_link)))
              .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
              .setAllowedOverMetered(true)
              .setMimeType(Constants.MIME_TYPE_PDF)
            downloadId = (requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
            val q = DownloadManager.Query()
            q.setFilterById(downloadId)
            val cursor: Cursor = (requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).query(q)
            cursor.moveToFirst()
            val bytes_downloaded =
              cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            cursor.close()
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.setTitle(getString(R.string.file_downloading))
            progressDialog!!.setMessage(getString(R.string.please_wait))
            progressDialog!!.show()
          })
        }else{
          Snackbar.make(binding!!.root,R.string.no_File_no_internet, Snackbar.LENGTH_LONG).show()
        }
      }else{
        Log.i("file Path", file!!.absolutePath)
        launchPDF()
      }
    }else{
      PermissionCheck.readAndWriteExternalStorage(requireContext())
    }
  }
  fun launchPDF(){
    if(progressDialog!!.isShowing && progressDialog != null){
      progressDialog!!.dismiss()
    }

    /*val uri:Uri = FileProvider.getUriForFile(requireContext(),"cargill.com.purina"+".provider",file!!)
    val i: Intent = Intent(Intent.ACTION_VIEW)
    i.setDataAndType(uri, Constants.MIME_TYPE_PDF)
    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_GRANT_READ_URI_PERMISSION
    startActivity(i)*/
    activity.let {
      val intent = Intent(it, PdfViewActivity::class.java)
      Log.i("filepath", file.toString())
      intent.putExtra("filePath",file!!.absolutePath.toString())
      intent.putExtra("header",getString(R.string.rearing_animal))
      startActivity(intent)
    }
  }
  private fun observeArticleData(){

    dashboardViewModel.articles().observe(viewLifecycleOwner, Observer {
      _binding.refresh.isRefreshing = false
      Log.i("articles ", it.toString())
      articles = it
      if(articles.isNotEmpty()){
        displayData(articles)
      }else{
        displayNodata()
      }
    })
  }
  private fun displayData(article: List<Article>){
    dataLoaded = true
    _binding.articleList.hideShimmer()
    _binding.articleList.visibility =View.VISIBLE
    _binding.nodata.visibility = View.GONE
    adapter.setList(article as ArrayList<Article>)
    adapter.notifyDataSetChanged()
  }
  private fun displayNodata(){
    dataLoaded = true
    _binding.nodata.visibility = View.VISIBLE
    _binding.articleList.visibility =View.GONE
    _binding.articleList.hideShimmer()
    _binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }
  }

  override fun stop() {
    adapter.notifyItemChanged(userClickedPosition)
  }
}
interface UpdateProgress{
  fun stop()
}