package cargill.com.purina.dashboard.View.Home

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cargill.com.purina.R
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.databinding.FragmentHomeBinding
import cargill.com.purina.utils.AppPreference
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class Home : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var myPreference: AppPreference
    private var animalSelected: String = ""

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
        animalSelected = myPreference.getStringValue("animal_selected").toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(animalSelected.isEmpty()){
            binding.userSelected.visibility = View.GONE
        }else{
            binding.userSelected.visibility = View.VISIBLE
            binding.userSelectedAnimal.text = animalSelected
        }
        val sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewmodel.animalSelected.observe(viewLifecycleOwner, Observer {
            Log.i("home animal.name", it.name)
            binding.userSelected.visibility = View.VISIBLE
            binding.userSelectedAnimal.text = it.name
        })
        binding.root.cardViewProductCatalog.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_productCatalogueFilter)
        }
    }
}