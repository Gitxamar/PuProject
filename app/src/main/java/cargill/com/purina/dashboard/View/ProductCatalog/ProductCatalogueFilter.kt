package cargill.com.purina.dashboard.View.ProductCatalog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.FilterOptions.Category
import cargill.com.purina.dashboard.Model.FilterOptions.FilterOptions
import cargill.com.purina.dashboard.Model.FilterOptions.Stage
import cargill.com.purina.dashboard.Model.FilterOptions.Subspecy
import cargill.com.purina.dashboard.viewModel.CatalogueFilterViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.CatalogueFilterViewModelFactory
import cargill.com.purina.databinding.FragmentProductCatalogueFilterBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder


class ProductCatalogueFilter : Fragment() {
    lateinit var binding: FragmentProductCatalogueFilterBinding
    private lateinit var viewModel: CatalogueFilterViewModel
    lateinit var myPreference: AppPreference
    lateinit var subSpecies:List<Subspecy>
    private var dataLoaded:Boolean = false
    var sharedViewmodel: SharedViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductCatalogueFilterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        viewModel.filterData.observe(viewLifecycleOwner, Observer {
            Log.i("data comming ",it.toString())
            subSpecies = emptyList()
            dataLoaded = true
            if(it.subspecies.isNotEmpty()){
                initChips(it)
            }else{
                displayNodata()
            }
        })
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myPreference = AppPreference(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
    fun init(){
        val factory = CatalogueFilterViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(CatalogueFilterViewModel::class.java)
        binding.lifecycleOwner = this
        binding.catalogueFilterViewModel = viewModel
        binding.searchFilterView.setHintTextColor(getResources().getColor(R.color.white))
        binding.searchFilterView.setTextColor(getResources().getColor(R.color.white))
        binding.searchFilterView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(Network.isAvailable(requireContext())){
                    val bundle = bundleOf(Constants.SEARCH_QUERY_TEXT to query)
                    findNavController().navigate(R.id.action_productCatalogueFilter_to_productCatalog, bundle)
                }else{
                    Snackbar.make(binding.root,R.string.no_internet, Snackbar.LENGTH_LONG).show()
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_productCatalogueFilter_to_home)
        }
        sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewmodel?.selectedItem?.observe(viewLifecycleOwner, Observer {
            sharedViewmodel!!.navigate("")
            if(dataLoaded){
                getfilterData()
            }
        })
        binding.applyFilterBtn.setOnClickListener{
            val resultSubSpecies : StringBuilder = StringBuilder("")
            for (i in 0 until binding.subSpeciesChipGroup.childCount){
                val subSpeciesChip = binding.subSpeciesChipGroup.getChildAt(i) as Chip
                if(subSpeciesChip.isChecked){
                    resultSubSpecies.append(subSpeciesChip.tag).append(",")
                }
            }
            val resultCategory : StringBuilder = StringBuilder("")
            for (i in 0 until binding.categoryChipGroup.childCount){
                val categoryChip = binding.categoryChipGroup.getChildAt(i) as Chip
                if(categoryChip.isChecked){
                    resultCategory.append(categoryChip.tag).append(",")
                }
            }
            val resultStage : StringBuilder = StringBuilder("")
            for (i in 0 until binding.stageChipGroup.childCount){
                val stageChip = binding.stageChipGroup.getChildAt(i) as Chip
                if(stageChip.isChecked){
                    resultStage.append(stageChip.tag).append(",")
                }
            }
            val bundle = bundleOf(
                Constants.SUBSPECIES_ID to if(resultSubSpecies.toString().isEmpty()) "" else resultSubSpecies.toString().substring(0, resultSubSpecies.toString().lastIndexOf(",")),
                Constants.CATEGORY_ID to if(resultCategory.toString().isEmpty()) "" else resultCategory.toString().substring(0, resultCategory.toString().lastIndexOf(",")),
                Constants.STAGE_ID to if(resultStage.toString().isEmpty()) "" else resultStage.toString().substring(0, resultStage.toString().lastIndexOf(",")))

            findNavController().navigate(R.id.action_productCatalogueFilter_to_productCatalog, bundle)
        }
        getfilterData()
    }
    fun getfilterData(){
        if(Network.isAvailable(requireContext())){
            viewModel.getfilterData(myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(),myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString())
        }else{
            Snackbar.make(binding.root,R.string.no_internet, Snackbar.LENGTH_LONG).show()
        }
    }
    fun SearchView.setHintTextColor(@ColorInt color: Int) {
        findViewById<EditText>(R.id.search_src_text).setHintTextColor(color)
    }
    fun SearchView.setTextColor(@ColorInt color: Int) {
        findViewById<EditText>(R.id.search_src_text).setTextColor(color)
    }

    fun initChips(filterOptions: FilterOptions){
        subSpecies = filterOptions.subspecies
        binding.sad.visibility = View.GONE
        binding.errorTextview.visibility = View.GONE
        binding.subSpeciesChipGroup.removeAllViewsInLayout()
        binding.subSpeciesCard.visibility = View.INVISIBLE
        binding.categoryCard.visibility = View.INVISIBLE
        binding.stageCard.visibility = View.INVISIBLE

        val inflaterSubSpecies = LayoutInflater.from(this.context)
        for (sub in subSpecies){
            val subSpecies_Chipitem = inflaterSubSpecies.inflate(R.layout.chip_item, null, false) as Chip
            subSpecies_Chipitem.text = sub.name
            subSpecies_Chipitem.tag = sub.subspecies_id
            binding.subSpeciesCard.visibility = View.VISIBLE
            binding.subSpeciesChipGroup.addView(subSpecies_Chipitem)
            subSpecies_Chipitem.checkedIcon?.let {
                val wrappedDrawable =
                    DrawableCompat.wrap(it)
                DrawableCompat.setTint(wrappedDrawable, Color.WHITE)
                subSpecies_Chipitem.checkedIcon = wrappedDrawable
            }
            if(subSpecies.size == 1){
                subSpecies_Chipitem.isChecked = true
                displayCategoryChips(filterOptions.subspecies[0].category)
            }
            subSpecies_Chipitem.setOnCheckedChangeListener { _, _ ->
                for (i in 0 until binding.subSpeciesChipGroup.childCount){
                val subSpeciesChip = binding.subSpeciesChipGroup.getChildAt(i) as Chip
                    if(subSpeciesChip.isChecked){
                        var id:Int = subSpeciesChip.tag.toString().toInt()
                        var cat = filterOptions.subspecies.find { it.subspecies_id.equals(id)}
                        cat?.category?.let { displayCategoryChips(it) }
                    }else{
                        binding.categoryCard.visibility = View.GONE
                        binding.categoryChipGroup.removeAllViewsInLayout()
                        binding.stageChipGroup.removeAllViewsInLayout()
                        binding.stageCard.visibility = View.GONE
                    }
                }
            }
        }
    }
    private fun displayCategoryChips(category: List<Category>){
        binding.categoryChipGroup.removeAllViewsInLayout()
        for (i in 0 until binding.subSpeciesChipGroup.childCount){
            val subSpeciesChip = binding.subSpeciesChipGroup.getChildAt(i) as Chip
            if(subSpeciesChip.isChecked){
                binding.categoryCard.visibility = View.VISIBLE
                val inflaterCategory = LayoutInflater.from(this.context)
                for (cat in category) {
                    val category_Chipitem = inflaterCategory.inflate(R.layout.chip_item, null, false) as Chip
                    category_Chipitem.text = cat.name
                    category_Chipitem.tag = cat.category_id
                    binding.categoryChipGroup.addView(category_Chipitem)
                    category_Chipitem.checkedIcon?.let {
                        val wrappedDrawable =
                            DrawableCompat.wrap(it)
                        DrawableCompat.setTint(wrappedDrawable, Color.WHITE)
                        category_Chipitem.checkedIcon = wrappedDrawable
                    }
                    category_Chipitem.setOnCheckedChangeListener { _, _ ->
                        binding.stageChipGroup.removeAllViewsInLayout()
                        for (j in 0 until binding.categoryChipGroup.childCount) {
                            val categoryChip = binding.categoryChipGroup.getChildAt(j) as Chip
                            if (categoryChip.isChecked) {
                                var id:Int = categoryChip.tag.toString().toInt()
                                var stage = category.find { it.category_id.equals(id) }
                                stage?.stage?.let { displayStageChips(it) }
                            }else{
                                binding.stageChipGroup.removeAllViewsInLayout()
                                binding.stageCard.visibility = View.GONE
                            }
                        }
                    }
                }
            }else{
                binding.categoryChipGroup.removeAllViewsInLayout()
                binding.categoryCard.visibility = View.GONE
                binding.stageChipGroup.removeAllViewsInLayout()
                binding.stageCard.visibility = View.GONE
            }
        }
    }
    private fun displayStageChips(stage: List<Stage>){
        binding.stageChipGroup.removeAllViewsInLayout()
        for (i in 0 until binding.subSpeciesChipGroup.childCount) {
            for (j in 0 until binding.categoryChipGroup.childCount) {
                val categoryChip = binding.categoryChipGroup.getChildAt(j) as Chip
                if (categoryChip.isChecked) {
                    binding.stageCard.visibility = View.VISIBLE
                    val inflaterStage = LayoutInflater.from(this.context)
                    for (sta in stage) {
                        val stage_Chipitem = inflaterStage.inflate(
                            R.layout.chip_item,
                            null,
                            false
                        ) as Chip
                        stage_Chipitem.text = sta.name
                        stage_Chipitem.tag = sta.stage_id
                        binding.stageChipGroup.addView(stage_Chipitem)
                        stage_Chipitem.checkedIcon?.let {
                            val wrappedDrawable =
                                DrawableCompat.wrap(it)
                            DrawableCompat.setTint(wrappedDrawable, Color.WHITE)
                            stage_Chipitem.checkedIcon = wrappedDrawable
                        }
                    }
                } else {
                    binding.stageChipGroup.removeAllViewsInLayout()
                    binding.stageCard.visibility = View.GONE
                }
            }
        }
    }
    private fun displayNodata(){
        dataLoaded = true
        binding.sad.visibility = View.VISIBLE
        binding.errorTextview.visibility = View.VISIBLE
        binding.subSpeciesChipGroup.removeAllViewsInLayout()
        binding.subSpeciesCard.visibility = View.INVISIBLE
        binding.categoryCard.visibility = View.INVISIBLE
        binding.stageCard.visibility = View.INVISIBLE
    }
}