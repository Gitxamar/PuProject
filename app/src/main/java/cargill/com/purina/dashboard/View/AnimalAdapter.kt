package cargill.com.purina.dashboard.View.Home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.R
import cargill.com.purina.databinding.AnimalItemBinding

class AnimalAdapter(private val clickListener: (Animal)->Unit):
    RecyclerView.Adapter<AnimalViewHolder>(){
    private val animalsList = ArrayList<Animal>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : AnimalItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.animal_item, parent, false)
        return AnimalViewHolder(binding)
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

class AnimalViewHolder(val binding: AnimalItemBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(animal: Animal, clickListener: (Animal)->Unit){
        binding.animalName.text = animal.name
        binding.animalContainer.setOnClickListener {
            clickListener(animal)
        }
    }
}