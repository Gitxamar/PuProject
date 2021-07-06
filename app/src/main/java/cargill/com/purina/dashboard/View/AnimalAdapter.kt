package cargill.com.purina.dashboard.View.Home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.R
import cargill.com.purina.databinding.AnimalItemBinding
import cargill.com.purina.utils.AppPreference

class AnimalAdapter(private val clickListener: (Animal)->Unit):
    RecyclerView.Adapter<AnimalViewHolder>(){

    private val animalsList = ArrayList<Animal>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)


        val binding : AnimalItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.animal_item, parent, false)
        return AnimalViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        holder.bind(animalsList[position],clickListener)
    }

    override fun getItemCount(): Int {
        return animalsList.size
    }
    fun setList(animals: List<Animal>){
        animalsList.clear()
        animalsList.addAll(animals)
    }
}

class AnimalViewHolder(val binding: AnimalItemBinding, var context: Context): RecyclerView.ViewHolder(binding.root){
    fun bind(animal: Animal, clickListener: (Animal)->Unit){
        binding.animalName.text = animal.name
        lateinit var myPreference: AppPreference
        myPreference = AppPreference(context)
        var animalSelected:String = myPreference.getStringValue("animal_selected").toString()
        binding.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        binding.animalName.setTextColor(ContextCompat.getColor(context, R.color.black))

        when (animal.order_id){
            1 -> if (animalSelected == animal.name) binding.animaleLogo?.setImageResource(R.drawable.ic_hen_selected) else binding.animaleLogo?.setImageResource(R.drawable.ic_hen)
            2 -> if (animalSelected == animal.name) binding.animaleLogo?.setImageResource(R.drawable.ic_layer_selected) else binding.animaleLogo?.setImageResource(R.drawable.ic_layer)
            3 -> if (animalSelected == animal.name) binding.animaleLogo?.setImageResource(R.drawable.ic_duck_selected) else binding.animaleLogo?.setImageResource(R.drawable.ic_duck)
            4 -> if (animalSelected == animal.name) binding.animaleLogo?.setImageResource(R.drawable.ic_quail_selected) else binding.animaleLogo?.setImageResource(R.drawable.ic_quail)
            5 -> if (animalSelected == animal.name) binding.animaleLogo?.setImageResource(R.drawable.ic_turkey_selected) else binding.animaleLogo?.setImageResource(R.drawable.ic_turkey)
            6 -> if (animalSelected == animal.name) binding.animaleLogo?.setImageResource(R.drawable.ic_rabbit_selected) else binding.animaleLogo?.setImageResource(R.drawable.ic_rabbit)
            7 -> if (animalSelected == animal.name) binding.animaleLogo?.setImageResource(R.drawable.ic_swine_selected) else binding.animaleLogo?.setImageResource(R.drawable.ic_swine)
            8 -> if (animalSelected == animal.name) binding.animaleLogo?.setImageResource(R.drawable.ic_cow_selected) else binding.animaleLogo?.setImageResource(R.drawable.ic_cow)
            9 -> if (animalSelected == animal.name) binding.animaleLogo?.setImageResource(R.drawable.ic_sheepgoat_selected) else binding.animaleLogo?.setImageResource(R.drawable.ic_sheepgoat)
        }
        if(animalSelected == animal.name){
            binding.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.app_primary))
            binding.animalName.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
        binding.animalContainer.setOnClickListener {
            clickListener(animal)
        }
    }
}