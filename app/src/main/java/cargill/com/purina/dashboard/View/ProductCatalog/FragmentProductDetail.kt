package cargill.com.purina.dashboard.View.ProductCatalog

import android.R.attr
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.ProductDetails.Image
import cargill.com.purina.dashboard.Model.ProductDetails.ProductDetail
import cargill.com.purina.dashboard.Repository.ProductCatalogueRepository
import cargill.com.purina.dashboard.viewModel.ProductCatalogueViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.ProductCatalogueViewModelFactory
import cargill.com.purina.databinding.FragmentDetailCatalogueBinding
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.PermissionCheck
import cargill.com.purina.utils.Utils
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File
import cargill.com.purina.Database.Event
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.View.PdfViewActivity
import kotlinx.android.synthetic.main.fragment_detail_catalogue.*
import kotlinx.android.synthetic.main.fragment_detail_catalogue.view.*
import java.text.FieldPosition
import android.R.attr.data
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.core.text.htmlEncode


class FragmentProductDetail(private val product_id:Int) : Fragment(){
    var binding: FragmentDetailCatalogueBinding? = null
    private lateinit var productDetailCatalogueViewModel: ProductCatalogueViewModel
    private val _binding get() = binding!!
    var product:ProductDetail?= null
    var downloadId:Long = 0
    var file:File? = null
    var sharedViewmodel: SharedViewModel? = null
    private var dataLoaded:Boolean = false
    private var progressDialog: ProgressDialog? = null
    private var fileName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailCatalogueBinding.inflate(inflater, container, false)
        val view = binding!!.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
        val repository = ProductCatalogueRepository(dao, PurinaService.getDevInstance(),requireActivity())
        val factory = ProductCatalogueViewModelFactory(repository)

        productDetailCatalogueViewModel = ViewModelProvider(this, factory).get(ProductCatalogueViewModel::class.java)
        binding?.catalogueDetailViewModel = productDetailCatalogueViewModel
        binding?.lifecycleOwner = this

        getData()
        sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewmodel?.navigateToDetails?.observe(_binding.lifecycleOwner!!, Observer {
            sharedViewmodel!!.navigateToDetails.value?.getContentIfNotHandled()?.let { it1 ->
                if(it1 == "navigate"){
                    if(dataLoaded){
                        sharedViewmodel!!.navigate("")
                        if(Network.isAvailable(requireContext())){
                            requireFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragmentDashboard, ProductCatalogueFilter()).addToBackStack(null).commit()
                        }else{
                            requireFragmentManager().popBackStack()
                        }
                    }
                }
            }
        })
        _binding.productPdf.setOnClickListener {
            progressDialog = ProgressDialog(requireContext())
            if(product!!.pdf_link.isEmpty() || product!!.pdf_link == ""){
                Snackbar.make(_binding.root,R.string.no_file_path, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(PermissionCheck.readAndWriteExternalStorage(requireContext())){
                Log.i("is exists", file.toString())
                if(!file!!.exists()){
                    if(Network.isAvailable(requireContext())){
                        _binding.productPdf.animate().apply {
                            duration = 1000
                            rotationYBy(360f)
                        }
                        productDetailCatalogueViewModel.getProductPDF(product!!.pdf_link)
                        productDetailCatalogueViewModel.pathWithToken.observe(_binding.lifecycleOwner!!, Observer {
                            Log.i("path", it.body().toString())
                            Log.i("File name when yet download", fileName)
                            requireActivity().registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                            var request = DownloadManager.Request(
                                Uri.parse(it.body().toString())
                            ).setTitle(fileName)
                                .setDescription(product!!.recipe_code)
                                .setAllowedOverRoaming(true)
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName)
                                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                                .setAllowedOverMetered(true)
                                .setMimeType(Constants.MIME_TYPE_PDF)
                            downloadId = (requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
                            progressDialog!!.setCanceledOnTouchOutside(false)
                            progressDialog!!.setTitle(getString(R.string.file_downloading))
                            progressDialog!!.setMessage(getString(R.string.please_wait))
                            progressDialog!!.show()
                        })
                    }else{
                        Snackbar.make(_binding.root,R.string.no_File_no_internet, Snackbar.LENGTH_LONG).show()
                    }
                }else{
                    Log.i("file Path", file!!.absolutePath)
                    launchPDF()
                }
            }else{
                PermissionCheck.readAndWriteExternalStorage(requireContext())
            }
        }
        _binding.back.setOnClickListener {
            requireFragmentManager().popBackStack()
            (requireActivity() as DashboardActivity).closeIfOpen()
        }
        _binding.knowMoreWeb.setOnClickListener {
        if(product!!.read_more.isNotEmpty())
            openWebPage(product!!.read_more)
        }
    }
    val br= object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            var id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if(id == downloadId){
                launchPDF()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    fun getData(){
        if(Network.isAvailable(requireContext())){
            productDetailCatalogueViewModel!!.getRemoteProductDetail(product_id)
            productDetailCatalogueViewModel!!.remoteProductDetail.observe(binding?.lifecycleOwner!!, Observer {
                if(it.isSuccessful){
                    product = it.body()!!.ProductDetail
                    PermissionCheck.readAndWriteExternalStorage(requireContext())
                    file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        product?.name.plus("_"+Utils.getFileName(product!!.pdf_link))
                    )
                    fileName = file?.nameWithoutExtension.toString()
                    Log.i("File before download", fileName)
                    if(!file!!.exists()){
                        if(!Network.isAvailable(requireContext())){
                            _binding.productPdf.alpha = 0.5f
                            _binding.productPdf.isClickable = false
                        }
                    }
                    _binding.scrollContainer.visibility = View.VISIBLE
                    _binding.productPdf.visibility = View.VISIBLE
                    _binding.sad.visibility = View.GONE
                    loadData(it.body()!!.ProductDetail)
                }else{
                    dataLoaded = false
                    Snackbar.make(_binding.root,R.string.no_data_found, Snackbar.LENGTH_LONG).show()
                    _binding.scrollContainer.visibility = View.GONE
                    _binding.productPdf.visibility = View.GONE
                    _binding.sad.visibility = View.VISIBLE
                }
            })
        }else{
            product = productDetailCatalogueViewModel.getCacheProductDetail(product_id)
            Snackbar.make(binding!!.root,R.string.working_offline, Snackbar.LENGTH_LONG).show()
            if(product != null){
                file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    product?.name//.plus("_"+Utils.getFileName(product!!.pdf_link))
                )
                _binding.scrollContainer.visibility = View.VISIBLE
                _binding.productPdf.visibility = View.VISIBLE
                _binding.sad.visibility = View.GONE
                loadData(product!!)
            }else{
                dataLoaded = false
                Snackbar.make(_binding.root,R.string.no_data_found, Snackbar.LENGTH_LONG).show()
                _binding.scrollContainer.visibility = View.GONE
                _binding.productPdf.visibility = View.GONE
                _binding.sad.visibility = View.VISIBLE
            }
        }
    }
    fun launchPDF(){
        if(progressDialog!!.isShowing && progressDialog != null){
            progressDialog!!.dismiss()
        }
        /*val uri:Uri = FileProvider.getUriForFile(requireContext(),"cargill.com.purina"+".provider",file!!)
        val i:Intent = Intent(Intent.ACTION_VIEW)
        i.setDataAndType(uri, Constants.MIME_TYPE_PDF)
        i.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if(br.isOrderedBroadcast){
            requireActivity().unregisterReceiver(br)
        }
        startActivity(i)*/
        activity.let {
            val intent = Intent(it, PdfViewActivity::class.java)
            Log.i("filepath", file.toString())
            intent.putExtra("filePath",file!!.absolutePath.toString())
            intent.putExtra("header",getString(R.string.product_catalog_header))
            startActivity(intent)
        }
    }
    private fun loadData(product: ProductDetail){
        loadImageViewPager()
        _binding.catalogueName.text = product.name
        _binding.recipeCode.text = getString(R.string.recipe_code).plus(" : ").plus(product.recipe_code)
        dataLoaded = true
    }
    fun openWebPage(url: String?) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }
    private fun previewImage(images: List<Image>, position:Int){
        if(images.isNotEmpty()){
            val bundle = bundleOf(
                Constants.IMAGES to images,
                Constants.PRODUCT_ID to product_id,
                "position" to position)
            val mFrag = FragmentImageViewer()
            mFrag.arguments = bundle
            requireFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentDashboard, mFrag).addToBackStack(null).commit()

        }
    }
    private fun loadImageViewPager(){
        if(product!!.images.isNotEmpty()){
            _binding.catalogueImageContainer.visibility = View.VISIBLE
            _binding.imageViewPager?.adapter = ImageViewPagerAdapter(product!!.images, {images: List<Image>, postion:Int ->previewImage(images, postion) })
            if(product!!.images.size <= 1){
                _binding.imageTabLayout.visibility = View.GONE
            }else{
                _binding.imageTabLayout.visibility = View.VISIBLE
                _binding.imageTabLayout?.let {
                    _binding.imageViewPager?.let { it1 ->
                        TabLayoutMediator(it, it1){ tab, position->
                        }.attach()
                    }
                }
            }
        }else{
            _binding.catalogueImageContainer.visibility = View.GONE
        }
        loadPackagesData()
    }
    private fun loadPackagesData(){
        if(product!!.pkg_type.isNotEmpty()){
            _binding.kgCard.visibility = View.VISIBLE
            val pkgTypes:List<String> = product!!.pkg_type.split(",").map { it -> it.trim() }
            val inflaterSubSpecies = LayoutInflater.from(this.context)
            pkgTypes.forEach {
                val pkgTypeChips = inflaterSubSpecies.inflate(R.layout.chip_item, null, false) as Chip
                pkgTypeChips.text = it.plus(getString(R.string.kg))
                pkgTypeChips.tag = it
                pkgTypeChips.isCheckable = false
                _binding.kgChipGroup.addView(pkgTypeChips)
            }
        }else{
            _binding.kgCard.visibility = View.GONE
        }
        loadProductVideo()
    }
    private fun loadProductVideo(){
        if(product!!.video_link.isNotEmpty() && Network.isAvailable(requireContext()) && URLUtil.isValidUrl(product!!.video_link)){
            _binding.youtube.visibility = View.VISIBLE
            _binding.youtubeView.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return false
                }
            })
            val ws: WebSettings = _binding.youtubeView.settings
            _binding.youtubeView.webChromeClient = WebChromeClient()
            ws.javaScriptEnabled = true
            val videoId = Utils.getYouTubeVideoIdFromUrl(product!!.video_link)
            /*var width  = _binding.youtubeView.measuredWidth
            width = (width.div(2) * .80).toInt()
            var height  = _binding.youtubeView.measuredHeight
            height = (width.div(2) * .99).toInt()*/
            var width  = "100%"
            var height  = "100%"
            val videoStr =
                "<html><body><br><iframe width=\"$width\" height=\"$height\" src=\"https://www.youtube.com/embed/$videoId?rel=0&amp;modestbranding=1&iv_load_policy=3&showinfo=0\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>";
            _binding.youtubeView.loadData(videoStr, "text/html", "utf-8")
        }else{
            _binding.youtube.visibility = View.GONE
        }
        loadDescription()
    }
    private fun loadDescription(){
        if(product!!.product_details.isNotEmpty()){
            _binding.descriptionCard.visibility = View.VISIBLE
            _binding.expandableDescription.text = Html.fromHtml(product!!.product_details)
        }else{
            _binding.descriptionCard.visibility = View.GONE
        }
        loadBenefits()
    }
    private fun loadBenefits(){
        if(product!!.benefits.isNotEmpty()){
            _binding.BenefitsCard.visibility = View.VISIBLE
            _binding.expandableBenefits.loadData(Html.fromHtml(product!!.benefits).toString(), "text/html", "utf-8")
        }else{
         _binding.BenefitsCard.visibility = View.GONE
        }
        loadIngredients()
    }
    private fun loadIngredients(){
        if(product!!.ingredients.isNotEmpty()){
            _binding.ingredientsCard.visibility = View.VISIBLE
            _binding.expandableIngredients.loadData(Html.fromHtml(product!!.ingredients).toString(), "text/html", "utf-8")
        }else{
           _binding.ingredientsCard.visibility = View.GONE
        }
        loadMixingInstructions()
    }
    private fun loadMixingInstructions(){
        if(product!!.mixing_instructions.isNotEmpty()){
            _binding.mixingInstructionsCard.visibility = View.VISIBLE
            _binding.expandableMixingInstructions.loadData(Html.fromHtml(product!!.mixing_instructions).toString(), "text/html", "utf-8")
        }else{
         _binding.mixingInstructionsCard.visibility = View.GONE
        }
        loadNutritionalData()
    }
    private fun loadNutritionalData(){
        if(product!!.nutritional_data.isNotEmpty()){
            _binding.nutritionalDataCard.visibility = View.VISIBLE
            _binding.expandableNutritionalData.loadData(Html.fromHtml(product!!.nutritional_data).toString(), "text/html", "utf-8")
        }else{
            _binding.nutritionalDataCard.visibility = View.GONE
        }
        loadFeedingInstructions()
    }
    private fun loadFeedingInstructions(){
        if(product!!.feeding_instructions.isNotEmpty()){
            _binding.feedingInstructionsCard.visibility = View.VISIBLE
            _binding.expandableFeedingInstructions.loadData(Html.fromHtml(product!!.feeding_instructions).toString(), "text/html", "utf-8")
        }else{
            _binding.feedingInstructionsCard.visibility = View.GONE
        }
        loadRecommendation()
    }
    private fun loadRecommendation(){
        if(product!!.recommendation_for_slaughter.isNotEmpty()){
            _binding.recommendationCard.visibility =View.VISIBLE
            _binding.expandableRecommendation.loadData(Html.fromHtml(product!!.recommendation_for_slaughter).toString(), "text/html", "utf-8")
        }else{
            _binding.recommendationCard.visibility =View.GONE
        }
        loadForm()
    }
    private fun loadForm(){
        if(product!!.form.isNotEmpty()){
            _binding.formLoyout.visibility = View.VISIBLE
            _binding?.formData?.text = Html.fromHtml(product!!.form)
        }else{
            _binding.formLoyout.visibility = View.GONE
        }
        loadValidity()
    }
    private fun loadValidity(){
        if(product!!.validity.isNotEmpty()){
            _binding.validityLoyout.visibility = View.VISIBLE
            _binding?.validityData?.text = Html.fromHtml(product!!.validity)
        }else{
            _binding.validityLoyout.visibility = View.GONE
        }
        loadSubBrand()
    }
    private fun loadSubBrand(){
        if(product!!.sub_brand.isNotEmpty()){
            _binding.subBrandLoyout.visibility = View.VISIBLE
            _binding?.subBrandData?.text = product!!.sub_brand
        }else{
            _binding.subBrandLoyout.visibility = View.GONE
        }
        loadKnowMoreWeb()
    }
    private fun loadKnowMoreWeb(){
        if(product!!.read_more.isNotEmpty() && URLUtil.isValidUrl(product!!.read_more)){
            _binding.knowMoreWeb.visibility = View.VISIBLE
            val param = _binding.knowMoreWeb.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,6,0,350)
            _binding.knowMoreWeb.layoutParams = param

        }else{
            _binding.knowMoreWeb.visibility = View.GONE
            val param = _binding.knowmoreCard.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,6,0,350)
            _binding.knowmoreCard.layoutParams = param
        }
    }
}