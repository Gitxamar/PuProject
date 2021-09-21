package cargill.com.purina.dashboard.View.RearingAnimals

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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
import com.google.android.material.snackbar.Snackbar

class FragmentRearingAnimals(private var articles: List<Article>) : Fragment() {
  var binding:FragmentRearingAnimalsBinding? = null
  private val _binding get() = binding!!
  private lateinit var adapter: RearingAnimalAdapter
  private var dataLoaded:Boolean = false
  var sharedViewmodel: SharedViewModel? = null
  private lateinit var dashboardViewModel: DashboardViewModel
  private var animalSelected: String = ""
  lateinit var myPreference: AppPreference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

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
    _binding.articleList.layoutManager = GridLayoutManager(activity?.applicationContext, 1, LinearLayoutManager.HORIZONTAL, false)
    adapter = RearingAnimalAdapter { article: Article ->onItemClick(article)}
    _binding.articleList.adapter = adapter
    _binding.articleList.showShimmer()
    _binding.articleList.addOnScrollListener(object : RecyclerView.OnScrollListener(){

    })
    _binding.back.setOnClickListener {
      requireFragmentManager().popBackStack()
    }
    sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    sharedViewmodel?.selectedItem?.observe(_binding.lifecycleOwner!!, Observer {
      sharedViewmodel!!.navigate("")
      if(dataLoaded){
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
    })
  }
  private fun onItemClick(article: Article){
  //PDF download
  }
  private fun observeArticleData(){
    dashboardViewModel.articles().observe(viewLifecycleOwner, Observer {
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
  }
}