package cargill.com.purina.dashboard.View.IdentifyDiseases

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.IdentifyDisease.SymptomsList
import cargill.com.purina.databinding.DiseaseSymptomsItemBinding

class DiseaseSymptomsAdapter : RecyclerView.Adapter<DiseaseSymptomsAdapter.DiseaseSymptomsViewHolder>() {

  private var symptomsListTemp = ArrayList<SymptomsList>()

  class DiseaseSymptomsViewHolder(val binding: DiseaseSymptomsItemBinding, val ctx: Context) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(symptoms: SymptomsList) {

      binding.tvSympomsName.text = symptoms.name
      binding.tvSymptomsDetails.text = symptoms.description
    }

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseSymptomsViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding: DiseaseSymptomsItemBinding =
      DataBindingUtil.inflate(layoutInflater, R.layout.disease_symptoms__item, parent, false)
    return DiseaseSymptomsViewHolder(binding, parent.context)
  }

  override fun onBindViewHolder(holder: DiseaseSymptomsViewHolder, position: Int) {
    holder.bind(symptomsListTemp[position])
  }

  override fun getItemCount(): Int {
    return symptomsListTemp.size
  }

  fun setList(stores: ArrayList<SymptomsList>) {
    symptomsListTemp.clear()
    symptomsListTemp.addAll(stores)
  }

}