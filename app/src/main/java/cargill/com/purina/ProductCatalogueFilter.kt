package cargill.com.purina

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.DrawableCompat
import cargill.com.purina.databinding.FragmentProductCatalogueFilterBinding
import com.google.android.material.chip.Chip
import java.lang.StringBuilder


class ProductCatalogueFilter : Fragment() {
    lateinit var binding: FragmentProductCatalogueFilterBinding


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
        initChips()
    }
    fun initChips(){
        var subSpecies = arrayOf("Species1", "Species2", "Species3" ,"Species4", "Species5", "Species6")
        var category = arrayOf("Category1", "Category2", "Category3" ,"Category4", "Category5", "Category6")
        var stage = arrayOf("Stage1", "Stage2", "Stage3" ,"Stage4", "Stage5", "Stage6")

        val inflaterSubSpecies = LayoutInflater.from(this.context)
        for (sub in subSpecies){
            val subSpecies_Chipitem = inflaterSubSpecies.inflate(R.layout.chip_item, null, false) as Chip
            subSpecies_Chipitem.text = sub
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
            category_Chipitem.text = cat
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
            stage_Chipitem.text = sta
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
        }
    }
}