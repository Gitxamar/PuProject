package cargill.com.purina.dashboard.View.ProductCatalog

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.FilterOptions.FilterOptions
import cargill.com.purina.dashboard.viewModel.CatalogueFilterViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.CatalogueFilterViewModelFactory
import cargill.com.purina.databinding.FragmentProductCatalogueFilterBinding
import com.google.android.material.chip.Chip
import java.lang.StringBuilder


class ProductCatalogueFilter : Fragment() {
    lateinit var binding: FragmentProductCatalogueFilterBinding
    private lateinit var viewModel: CatalogueFilterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_product_catalogue_filter, container, false)
        binding = FragmentProductCatalogueFilterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    fun init(){
        val factory = CatalogueFilterViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory).get(CatalogueFilterViewModel::class.java)
        binding.lifecycleOwner = this
        binding.catalogueFilterViewModel = viewModel
        binding.searchFilterView.setHintTextColor(getResources().getColor(R.color.white))
        binding.searchFilterView.setTextColor(getResources().getColor(R.color.white))
        //initChips()
        getData()
    }
    fun getData(){
        if(Network.isAvailable(requireContext())){
            viewModel.getData()
            viewModel.filterData.observe(binding.lifecycleOwner!!, Observer {
                Log.i("data comming ",it.toString())
                initChips(it)
            })
        }
    }
    fun SearchView.setHintTextColor(@ColorInt color: Int) {
        findViewById<EditText>(R.id.search_src_text).setHintTextColor(color)
    }
    fun SearchView.setTextColor(@ColorInt color: Int) {
        findViewById<EditText>(R.id.search_src_text).setTextColor(color)
    }
    fun initChips(filterOptions: FilterOptions){
        var subSpecies = filterOptions.subspecies
        var category = filterOptions.subspecies[0].category
        var stage = filterOptions.subspecies[0].category[0].stage

        val inflaterSubSpecies = LayoutInflater.from(this.context)
        for (sub in subSpecies){
            val subSpecies_Chipitem = inflaterSubSpecies.inflate(R.layout.chip_item, null, false) as Chip
            subSpecies_Chipitem.text = sub.name
            binding.subSpeciesChipGroup.addView(subSpecies_Chipitem)
            subSpecies_Chipitem.checkedIcon?.let {
                val wrappedDrawable =
                    DrawableCompat.wrap(it)
                DrawableCompat.setTint(wrappedDrawable, Color.WHITE)
                subSpecies_Chipitem.checkedIcon = wrappedDrawable
            }
        }
        val inflaterCategory = LayoutInflater.from(this.context)
        for (cat in category){
            val category_Chipitem = inflaterCategory.inflate(R.layout.chip_item, null, false) as Chip
            category_Chipitem.text = cat.name
            binding.categoryChipGroup.addView(category_Chipitem)
            category_Chipitem.checkedIcon?.let {
                val wrappedDrawable =
                    DrawableCompat.wrap(it)
                DrawableCompat.setTint(wrappedDrawable, Color.WHITE)
                category_Chipitem.checkedIcon = wrappedDrawable
            }
        }
        val inflaterStage = LayoutInflater.from(this.context)
        for (sta in stage){
            val stage_Chipitem = inflaterStage.inflate(R.layout.chip_item, null, false) as Chip
            stage_Chipitem.text = sta.name
            binding.stageChipGroup.addView(stage_Chipitem)
            stage_Chipitem.checkedIcon?.let {
                val wrappedDrawable =
                    DrawableCompat.wrap(it)
                DrawableCompat.setTint(wrappedDrawable, Color.WHITE)
                stage_Chipitem.checkedIcon = wrappedDrawable
            }
        }

        binding.applyFilterBtn.setOnClickListener{
            val resultSubSpecies : StringBuilder = StringBuilder("")
            for (i in 0 until binding.subSpeciesChipGroup.childCount){
                val subSpeciesChip = binding.subSpeciesChipGroup.getChildAt(i) as Chip
                if(subSpeciesChip.isChecked){
                    resultSubSpecies.append(subSpeciesChip.text).append(",")
                }
            }
            val resultCategory : StringBuilder = StringBuilder("")
            for (i in 0 until binding.categoryChipGroup.childCount){
                val categoryChip = binding.categoryChipGroup.getChildAt(i) as Chip
                if(categoryChip.isChecked){
                    resultCategory.append(categoryChip.text).append(",")
                }
            }
            val resultStage : StringBuilder = StringBuilder("")
            for (i in 0 until binding.stageChipGroup.childCount){
                val stageChip = binding.stageChipGroup.getChildAt(i) as Chip
                if(stageChip.isChecked){
                    resultStage.append(stageChip.text).append(",")
                }
            }
            Toast.makeText(context, resultSubSpecies.toString()+","+resultCategory.toString()+","+resultStage.toString(), Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_productCatalogueFilter_to_productCatalog)
        }
    }
}