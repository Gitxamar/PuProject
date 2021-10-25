package cargill.com.purina.splash.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.splash.Model.Country
import cargill.com.purina.splash.Repository.LanguageRepository
import cargill.com.purina.splash.viewmodel.LanguageViewModel
import cargill.com.purina.splash.viewmodel.LanguageViewModelFactory
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.databinding.FragmentOnboardingLanguageBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.PermissionCheck
import com.google.android.material.snackbar.Snackbar

class FragmentOnboardingLanguage : Fragment(){

    lateinit var myPreference: AppPreference
    lateinit var ctx:Context
    private var languageBinding: FragmentOnboardingLanguageBinding? = null
    private lateinit var languageViewModel: LanguageViewModel
    private lateinit var adapter: LanguageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        languageBinding = FragmentOnboardingLanguageBinding.inflate(inflater, container, false)
        val dao = PurinaDataBase.invoke(ctx.applicationContext).dao
        val repo = LanguageRepository(dao)
        val factory  = LanguageViewModelFactory(repo,ctx)
        PermissionCheck.accessFineLocation(ctx)

        languageViewModel = ViewModelProvider(this, factory).get(LanguageViewModel::class.java)
        languageBinding!!.langViewModel = languageViewModel
        languageBinding!!.lifecycleOwner = this

        if(myPreference.isLanguageSelected())
            //languageBinding?.instruction?.visibility = View.GONE

        languageBinding?.nextButton?.setOnClickListener{
            if(myPreference.isLanguageSelected()) {
                startActivity(Intent(context, DashboardActivity::class.java))
            }else{
                Snackbar.make(languageBinding!!.languageLayout,getString(R.string.please_select_language), Snackbar.LENGTH_LONG).show()
            }
        }
        languageViewModel.message.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        })
        return languageBinding?.root
    }

    override fun onAttach(context: Context) {
        ctx = context
        myPreference = AppPreference(context)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniRecyclerView()
    }

    override fun onDestroyView() {
        languageBinding = null
        super.onDestroyView()
    }

    private fun iniRecyclerView(){
        languageBinding?.languageList?.layoutManager = GridLayoutManager(activity?.applicationContext, 3, LinearLayoutManager.VERTICAL, false)
        languageBinding?.languageList?.setHasFixedSize(true)
        adapter = LanguageAdapter(ctx,{languageSelected: Country ->changeLanguage(languageSelected)})
        languageBinding?.languageList?.adapter = adapter
        displayLanguages()
    }
    private fun displayLanguages(){
        languageViewModel.countries.observe(viewLifecycleOwner, {
            Log.i("PURINA", it.toString())
            if(!it.isEmpty()){
                adapter.setList(it)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun changeLanguage(country: Country){
        languageViewModel.updateUserSelection(myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(), Country(country.id,country.flag, country.language, country.languageCode, 1))
        myPreference.setStringVal(Constants.USER_LANGUAGE_CODE, country.languageCode.toString())
        myPreference.setStringVal(Constants.USER_LANGUAGE, country.language.toString())
        myPreference.setStringVal(Constants.USER_ANIMAL, "")
        activity?.recreate()
    }
}
