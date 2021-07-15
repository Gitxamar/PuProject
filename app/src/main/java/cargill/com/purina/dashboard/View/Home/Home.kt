package cargill.com.purina.dashboard.View.Home

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
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.databinding.FragmentHomeBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.view.*

class Home : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var myPreference: AppPreference
    private var animalSelected: String = ""
    private var animalSelectedCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myPreference = AppPreference(context)
        animalSelected = myPreference.getStringValue(Constants.USER_ANIMAL).toString()
        animalSelectedCode = myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(animalSelected.isEmpty()){
            binding.userSelected.visibility = View.VISIBLE
            binding.userSelectedAnimal.text = getString(R.string.select_species)
            setAnimalLogo(0)
        }else{
            binding.userSelected.visibility = View.VISIBLE
            binding.userSelectedAnimal.text = getString(R.string.rearing).plus(animalSelected)
            setAnimalLogo(animalSelectedCode.toInt())
        }
        val sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewmodel.selectedItem.observe(viewLifecycleOwner, Observer {
            Log.i("home animal.name", it.name)
            animalSelected = myPreference.getStringValue(Constants.USER_ANIMAL).toString()
            binding.userSelected.visibility = View.VISIBLE
            binding.userSelectedAnimal.text = getString(R.string.rearing).plus(it.name)
            setAnimalLogo(it.order_id)
        })
        binding.root.cardViewProductCatalog.setOnClickListener {
            animalSelected = myPreference.getStringValue(Constants.USER_ANIMAL).toString()
            if(animalSelected.isEmpty()){
                Snackbar.make(binding.root,"Please select the animal in the filter", Snackbar.LENGTH_LONG).show()
            }else{

                if(Network.isAvailable(requireContext())){
                    findNavController().navigate(R.id.action_home_to_productCatalogueFilter)
                }else{
                    findNavController().navigate(R.id.action_home_to_productCatalog)
                }
            }
        }
    }
    fun setAnimalLogo(order_id: Int){
        when (order_id){
            0 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(0, 0,0,0)
            1 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hen, 0,0,0)
            2 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_layer, 0,0,0)
            3 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_duck, 0,0,0)
            4 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_quail, 0,0,0)
            5 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_turkey, 0,0,0)
            6 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rabbit, 0,0,0)
            7 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_swine, 0,0,0)
            8 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cow, 0,0,0)
            9 -> binding.userSelectedAnimal.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sheepgoat, 0,0,0)
        }
    }
}