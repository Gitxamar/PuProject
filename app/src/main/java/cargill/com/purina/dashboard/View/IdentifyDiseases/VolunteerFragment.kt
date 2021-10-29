package cargill.com.purina.dashboard.View.IdentifyDiseases

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cargill.com.purina.R
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.viewModel.IdentifyDiseaseViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.databinding.FragmentDigitalVetDetailsBinding
import cargill.com.purina.databinding.FragmentVolunteerBinding
import cargill.com.purina.utils.AppPreference

class VolunteerFragment : Fragment() {
  lateinit var myPreference: AppPreference
  lateinit var binding: FragmentVolunteerBinding
  var sharedViewmodel: SharedViewModel? = null
  private lateinit var identifyDiseaseViewModel: IdentifyDiseaseViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentVolunteerBinding.inflate(inflater)
    return binding.root
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.back.setOnClickListener {
      (requireActivity() as DashboardActivity).closeIfOpen()
      findNavController().navigate(R.id.action_fragment_volunteer_back)
    }
    (requireActivity() as DashboardActivity).disableBottomMenu()
  }

}