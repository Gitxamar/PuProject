package cargill.com.purina.dashboard.View.Home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cargill.com.purina.R
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.databinding.BottomSheetTermsAndConditionsBinding
import cargill.com.purina.databinding.FragmentOnboardingLanguageBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants


class TermsAndConditionsBottomSheet : Fragment() {

  var binding: BottomSheetTermsAndConditionsBinding? = null
  lateinit var myPreference: AppPreference

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    binding = BottomSheetTermsAndConditionsBinding.inflate(inflater, container, false)
    return binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpViews()
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }

  private fun setUpViews() {

    if (myPreference.isTermsConditionsAccepted()) {
      binding?.llCheckBox?.visibility = View.GONE
      binding?.btnDismiss?.visibility = View.VISIBLE

    } else {
      binding?.cbTerms?.setOnCheckedChangeListener { buttonView, isChecked ->
        if (binding!!.cbTerms.isChecked) {
          myPreference.setStringVal(Constants.USER_TERMS_ACCEPTED, "Accepted")
          movetoNextScreen()
        }
      }
    }

    binding?.btnDismiss?.setOnClickListener {
      movetoNextScreen()
    }
  }

  private fun movetoNextScreen() {
    when(Constants.TERMS_VALUE){
      "OnBoarding" -> {
        startActivity(Intent(context, DashboardActivity::class.java))
      }
      "LanguageScreen"->{
        //findNavController().navigate(R.id.account)
        startActivity(Intent(context, DashboardActivity::class.java))
      }
      "ProdOnBoarding"->{
        startActivity(Intent(context, DashboardActivity::class.java).putExtra("IsProd","TRUE"))
      }
      ""->{
        Constants.TERMS_VALUE = "OnBoarding"
        startActivity(Intent(context, DashboardActivity::class.java))
      }
    }
  }

}