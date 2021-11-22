package cargill.com.purina.dashboard.View.ProductCatalog

import android.R.*
import android.annotation.SuppressLint
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
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.View.FeedProgram.FragmentDetailFeedProgram
import cargill.com.purina.dashboard.viewModel.ProductCatalogueViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.ProductCatalogueViewModelFactory
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.databinding.FragmentProductCatalogBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.Utils
import com.google.android.material.snackbar.Snackbar
import java.util.function.Predicate
import kotlin.collections.ArrayList
import android.widget.ScrollView
import okhttp3.internal.notifyAll
import android.os.Parcelable
import android.widget.LinearLayout
import android.view.View
import android.R.attr.duration

import android.R.id.message
import android.graphics.Rect
import android.view.Gravity

import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.snackbar.BaseTransientBottomBar


class ProductCatalog : Fragment() {
    var binding: FragmentProductCatalogBinding?= null
    private var productCatalogueViewModel: ProductCatalogueViewModel? = null
    private lateinit var adapter:ProductCatalogueAdapter
    lateinit var myPreference: AppPreference
    var sharedViewmodel: SharedViewModel? = null
    private var PAGENUMBER:Int = 1
    private var searchQuery:String = ""
    private var subSpecies_id:String = ""
    private var category_id:String = ""
    private var stage_id:String = ""
    private var dataLoaded:Boolean = false
    private var isLoading =false
    private var stopPagination =false
    private var recyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductCatalogBinding.inflate(inflater)

        return binding!!.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myPreference = AppPreference(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        requireActivity().registerReceiver(broadCastReceiver, filter)
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
        sharedViewmodel?.selectedItem?.observe(binding?.lifecycleOwner!!, Observer {
            sharedViewmodel!!.navigate("")
            if(dataLoaded){
                if(Network.isAvailable(requireContext())){
                    findNavController().navigate(R.id.action_productCatalog_to_productCatalogueFilter)
                }else{
                    adapter.clear()
                    getData()
                }
            }
        })
    }
    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(Network.isAvailable(requireContext())){
                if(dataLoaded)
                getData()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        productCatalogueViewModel = null
    }

    override fun onDestroyView() {
        requireActivity().unregisterReceiver(broadCastReceiver);
        super.onDestroyView()
        binding = null
    }

    @SuppressLint("NewApi")
    private fun init(){
        val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
        val repository = ProductCatalogueRepository(dao, PurinaService.getDevInstance(),requireActivity())
        val factory = ProductCatalogueViewModelFactory(repository)
        productCatalogueViewModel = ViewModelProvider(this, factory).get(ProductCatalogueViewModel::class.java)
        binding?.catalogueViewModel = productCatalogueViewModel
        binding?.lifecycleOwner = this
        binding?.searchFilterView?.setHintTextColor(resources.getColor(R.color.white))
        binding?.searchFilterView?.setTextColor(resources.getColor(R.color.white))
        binding?.searchFilterView?.setOnSearchClickListener {
            (requireActivity() as DashboardActivity).closeIfOpen()
        }
        binding?.searchFilterView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
        binding?.back?.setOnClickListener {
            Utils.hideSoftKeyBoard(requireContext(), binding!!.root)
            (requireActivity() as DashboardActivity).closeIfOpen()
            if(Network.isAvailable(requireContext())){
                findNavController().navigate(R.id.action_productCatalog_to_productCatalogueFilter)
            }else{
                findNavController().navigate(R.id.action_productCatalog_to_home)
            }
        }
        binding?.refresh?.setOnRefreshListener {
            binding?.refresh!!.isRefreshing = true
            PAGENUMBER = 1
            adapter.clear()
            getData()
        }
        productCatalogueViewModel?.remotedata?.observe(binding?.lifecycleOwner!!, Observer {
            binding?.refresh?.isRefreshing = false
            if(it.isSuccessful){
                Log.i("data commingng",it.body().toString())
                if (it.body()!!.next == 0){
                    stopPagination = true
                }
                it.body()!!.product.removeIf { filter -> !filter.mode_active }
                var products = it.body()!!.product
                if(products.size != 0){
                    displayData(products)
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
        binding?.productsList?.layoutManager = GridLayoutManager(activity?.applicationContext, 2, LinearLayoutManager.VERTICAL, false)
        binding?.productsList?.addItemDecoration(EqualSpaceItemDecoration(2))
        adapter = ProductCatalogueAdapter { product: Product ->onItemClick(product)}
        binding?.productsList?.adapter = adapter
        binding?.productsList?.showShimmer()
        binding?.productsList?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
                val visibleItemCount = (binding?.productsList?.layoutManager as GridLayoutManager).childCount
                val pastVisibleItem = (binding?.productsList?.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition()
                val total = adapter.itemCount
                if (!isLoading && !stopPagination){
                    if((visibleItemCount+pastVisibleItem) >= total){
                        PAGENUMBER++
                        getData()
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        getData()
    }
    @SuppressLint("NewApi")
    private fun getData(){
        isLoading = true
        if(Network.isAvailable(requireActivity())){
            productCatalogueViewModel!!.getRemoteData(mapOf(
                Constants.SEARCH_TEXT to searchQuery,
                Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(),
                Constants.SPECIES_ID to myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString(),
                Constants.SUBSPECIES_ID to subSpecies_id,
                Constants.CATEGORY_ID to category_id,
                Constants.STAGE_ID to stage_id,
                Constants.PAGE to PAGENUMBER.toString(),
                Constants.PER_PAGE to 10.toString()))
        }else{
            Snackbar.make(binding!!.root,R.string.working_offline, Snackbar.LENGTH_LONG).show()
           var products:List<Product> = productCatalogueViewModel!!.getOfflineData(myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(), myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString())
            stopPagination = true // there is no pagination in offline
            adapter.clear()
            ArrayList(products).removeIf { filter -> !filter.mode_active }
            var arrayProducts = ArrayList(products)
            if(!products.isEmpty() || products.size >0){
                binding?.refresh?.isRefreshing = false
                displayData(arrayProducts)
            }else{
                displayNodata()
            }
        }
        productCatalogueViewModel!!.msg.observe(binding?.lifecycleOwner!!, Observer {
            Snackbar.make(binding!!.root,R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
            displayNodata()
        })
    }
    private fun onItemClick(product:Product){
        (requireActivity() as DashboardActivity).closeIfOpen()
        Utils.hideSoftKeyBoard(requireContext(), binding!!.root)
        requireFragmentManager()
            .beginTransaction()
            .add(R.id.fragmentDashboard, FragmentProductDetail(product.product_id)).addToBackStack(null).commit()
    }

    private fun displayData(products:ArrayList<Product>){
        dataLoaded = true
        binding?.productsList?.hideShimmer()
        binding?.sad?.visibility = View.GONE
        adapter.setList(products)
        adapter.notifyDataSetChanged()
        binding?.productsList?.layoutManager?.onRestoreInstanceState(recyclerViewState)
        isLoading = false
    }
    private fun displayNodata(){
        dataLoaded = true
        binding?.productsList?.hideShimmer()
        binding?.sad?.visibility = View.VISIBLE
        Snackbar.make(binding!!.root,R.string.no_data_found, Snackbar.LENGTH_LONG).show()
        binding?.refresh?.isRefreshing = false
        isLoading = false
    }
}

class EqualSpaceItemDecoration(private val mSpaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = mSpaceHeight
        outRect.top = mSpaceHeight
        outRect.left = mSpaceHeight
        outRect.right = mSpaceHeight
    }
}
