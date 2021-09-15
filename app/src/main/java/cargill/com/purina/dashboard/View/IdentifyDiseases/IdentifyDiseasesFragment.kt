package cargill.com.purina.dashboard.View.IdentifyDiseases

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.databinding.FragmentIdentifyDiseasesBinding
import cargill.com.purina.utils.AppPreference

class IdentifyDiseasesFragment : Fragment() {
  lateinit var myPreference: AppPreference
  lateinit var binding: FragmentIdentifyDiseasesBinding
  var sharedViewmodel: SharedViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentIdentifyDiseasesBinding.inflate(inflater)

    return binding.root
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.back.setOnClickListener {
      findNavController().navigate(R.id.action_fragmentIdentifyDisease_to_home)
    }
    binding.cvEncyclopedia.setOnClickListener {
      findNavController().navigate(R.id.action_disease_dictionary)
    }
    binding.cvDigitalVet.setOnClickListener {
      findNavController().navigate(R.id.action_digital_vet)
    }
    binding.cvVolunteer.setOnClickListener {
      findNavController().navigate(R.id.action_volunteer)
    }
  }

}