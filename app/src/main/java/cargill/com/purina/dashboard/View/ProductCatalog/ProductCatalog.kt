package cargill.com.purina.dashboard.View.ProductCatalog

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.Products.Product
import cargill.com.purina.dashboard.Repository.ProductCatalogueRepository
import cargill.com.purina.dashboard.viewModel.ProductCatalogueViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.ProductCatalogueViewModelFactory
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.databinding.FragmentProductCatalogBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlin.collections.ArrayList

class ProductCatalog : Fragment() {
    lateinit var binding: FragmentProductCatalogBinding
    private lateinit var productCatalogueViewModel: ProductCatalogueViewModel
    private lateinit var adapter:ProductCatalogueAdapter
    lateinit var myPreference: AppPreference
    var sharedViewmodel: SharedViewModel? = null
    private var PAGENUMBER:Int = 1
    private var searchQuery:String = ""
    private var subSpecies_id:String = ""
    private var category_id:String = ""
    private var stage_id:String = ""
    private var dataLoaded:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductCatalogBinding.inflate(inflater)

        return binding.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myPreference = AppPreference(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null){
            if(requireArguments().containsKey(Constants.SEARCH_QUERY_TEXT)){
                searchQuery = arguments?.getString(Constants.SEARCH_QUERY_TEXT).toString()
            }
            if(requireArguments().containsKey(Constants.SUBSPECIES_ID)){
                subSpecies_id = arguments?.getString(Constants.SUBSPECIES_ID).toString()
            }
            if(requireArguments().containsKey(Constants.CATEGORY_ID)){
                category_id = arguments?.getString(Constants.CATEGORY_ID).toString()
            }
            if(requireArguments().containsKey(Constants.STAGE_ID)){
                stage_id = arguments?.getString(Constants.STAGE_ID).toString()
            }

        }
        init()
        sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewmodel?.selectedItem?.observe(binding.lifecycleOwner!!, Observer {
            if(dataLoaded){
                if(Network.isAvailable(requireContext())){
                    findNavController().navigate(R.id.action_productCatalog_to_productCatalogueFilter)
                }else{
                    getData()
                }
            }
        })
    }
    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(Network.isAvailable(requireContext())){
                getData()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        requireActivity().registerReceiver(broadCastReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(broadCastReceiver);
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
    private fun init(){
        val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
        val repository = ProductCatalogueRepository(dao, PurinaService.getDevInstance(),requireActivity())
        val factory = ProductCatalogueViewModelFactory(repository)
        productCatalogueViewModel = ViewModelProvider(this, factory).get(ProductCatalogueViewModel::class.java)
        binding.catalogueViewModel = productCatalogueViewModel
        binding.lifecycleOwner = this
        binding.searchFilterView.setHintTextColor(getResources().getColor(R.color.white))
        binding.searchFilterView.setTextColor(getResources().getColor(R.color.white))
        binding.searchFilterView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_productCatalog_to_productCatalogueFilter)
        }
        productCatalogueViewModel.remotedata.observe(binding.lifecycleOwner!!, Observer {
            if(it.isSuccessful){
                Log.i("data commingng",it.body().toString())
                if(it.body()!!.product.size != 0){
                    displayData(it.body()!!.product)
                }else{
                    displayNodata()
                }
            }else{
                displayNodata()
            }
        })
        initRecyclerView()
    }
    fun SearchView.setHintTextColor(@ColorInt color: Int) {
        findViewById<EditText>(R.id.search_src_text).setHintTextColor(color)
    }
    fun SearchView.setTextColor(@ColorInt color: Int) {
        findViewById<EditText>(R.id.search_src_text).setTextColor(color)
    }
    private fun initRecyclerView(){
        binding.productsList.layoutManager = GridLayoutManager(activity?.applicationContext, 2, LinearLayoutManager.VERTICAL, false)
        adapter = ProductCatalogueAdapter { product: Product ->onItemClick(product)}
        binding.productsList.adapter = adapter
        binding.productsList.showShimmer()
        binding.productsList.addOnScrollListener(object : RecyclerView.OnScrollListener(){

        })
        getData()
    }
    private fun getData(){
        if(Network.isAvailable(requireActivity())){
            //text=product&lang=en&species_id=1&subspecies_id=2&category_id=2&stage_id=1&page=1&per_page=10
            productCatalogueViewModel.getRemoteData(mapOf(
                Constants.SEARCH_TEXT to searchQuery,
                Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(),
                Constants.SPECIES_ID to myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString(),
                Constants.SUBSPECIES_ID to subSpecies_id,
                Constants.CATEGORY_ID to category_id,
                Constants.STAGE_ID to stage_id,
                Constants.PAGE to PAGENUMBER.toString(),
                Constants.PER_PAGE to 10.toString()))
        }else{
            Snackbar.make(binding.root,R.string.working_offline, Snackbar.LENGTH_LONG).show()
           var products:List<Product> = productCatalogueViewModel.getOfflineData(myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(), myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString())
            if(!products.isEmpty() || products.size >0){
                displayData(ArrayList(products))
            }else{
                displayNodata()
            }
        }
        productCatalogueViewModel.msg.observe(binding.lifecycleOwner!!, Observer {
            Snackbar.make(binding.root,R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
            displayNodata()
        })
    }
    private fun onItemClick(product:Product){
        //navigate to product details screen
        if(product != null){
            val bundle = bundleOf(
                Constants.PRODUCT_ID to product.product_id)
            if(Network.isAvailable(requireContext())){
                productCatalogueViewModel.getRemoteProductDetail(product.product_id)
                productCatalogueViewModel.remoteProductDetail.observe(binding.lifecycleOwner!!, Observer {
                    if(it.isSuccessful){
                            findNavController().navigate(R.id.action_productCatalog_to_fragmentProductDetail, bundle)
                    }else{
                        Snackbar.make(binding.root,R.string.no_data_found, Snackbar.LENGTH_LONG).show()
                    }
                })
            }else{
                Snackbar.make(binding.root,R.string.working_offline, Snackbar.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_productCatalog_to_fragmentProductDetail, bundle)
            }
        }
    }

    private fun displayData(products:ArrayList<Product>){
        dataLoaded = true
        binding.productsList.hideShimmer()
        binding.sad.visibility = View.GONE
        binding.errorTextview.visibility = View.GONE
        adapter.setList(products)
        adapter.notifyDataSetChanged()
    }
    private fun displayNodata(){
        dataLoaded = true
        binding.sad.visibility = View.VISIBLE
        binding.errorTextview.visibility = View.VISIBLE
        binding.productsList.hideShimmer()
    }
}