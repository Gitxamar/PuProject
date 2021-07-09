package cargill.com.purina.dashboard.View.ProductCatalog

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.Products.Product
import cargill.com.purina.dashboard.Repository.ProductCatalogueRepository
import cargill.com.purina.dashboard.viewModel.ProductCatalogueViewModel
import cargill.com.purina.dashboard.viewModel.ProductCatalogueViewModelFactory
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.databinding.FragmentProductCatalogBinding
import kotlinx.android.synthetic.main.fragment_contact_us.view.*
import kotlinx.android.synthetic.main.fragment_product_catalog.view.*

class ProductCatalog : Fragment() {
    lateinit var binding: FragmentProductCatalogBinding
    private lateinit var productCatalogueViewModel: ProductCatalogueViewModel
    private lateinit var adapter:ProductCatalogueAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.appbar_menu,menu)
        val item = menu?.findItem(R.id.search)
        val searchQuery=item?.actionView as SearchView
        super.onCreateOptionsMenu(menu, inflater)
    }*/
    private fun init(){
        val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
        val repository = ProductCatalogueRepository(dao, PurinaService.getDevInstance(),requireActivity())
        val factory = ProductCatalogueViewModelFactory(repository)
        productCatalogueViewModel = ViewModelProvider(this, factory).get(ProductCatalogueViewModel::class.java)
        binding.catalogueViewModel = productCatalogueViewModel
        binding.lifecycleOwner = this
        //binding.searchFilterView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.product_catalog) + "</font>"));
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
        initRecyclerView()
        val sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewmodel.animalSelected.observe(viewLifecycleOwner, Observer {
            //send species data to get new data
            Log.i("animal",it.toString())
            getData()
        })
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
        getData()
    }
    private fun getData(){
        if(Network.isAvailable(requireActivity())){
            //text=product&lang=en&species_id=1&subspecies_id=2&category_id=2&stage_id=1&page=1&per_page=10
            productCatalogueViewModel.getRemoteData(mapOf("text" to "product"))
            productCatalogueViewModel.remotedata.observe(binding.lifecycleOwner!!, Observer {
                if(it.isSuccessful){
                    Log.i("data commingng",it.body().toString())
                    displayData(it.body()!!.product)
                }else{
                    displayNodata()
                }
            })
        }else{
            productCatalogueViewModel.offlinedata.observe(binding.lifecycleOwner!!, Observer {
                if(!it.isEmpty()){
                    displayData(ArrayList(it))
                }else{
                    displayNodata()
                }
            })
        }
    }
    private fun onItemClick(product:Product){
        //navigate to product details screen
    }

    private fun displayData(products:ArrayList<Product>){
        binding.productsList.hideShimmer()
        binding.sad.visibility = View.GONE
        binding.root.error_textview.visibility = View.GONE
        adapter.setList(products)
        adapter.notifyDataSetChanged()
    }
    private fun displayNodata(){
        binding.sad.visibility = View.VISIBLE
        binding.root.error_textview.visibility = View.VISIBLE
        binding.productsList.hideShimmer()
    }
}