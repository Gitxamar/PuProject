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
import cargill.com.purina.Service.Network
import cargill.com.purina.databinding.FragmentOnboardingLanguageBinding
import cargill.com.purina.utils.AppPreference
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_onboarding_language.*
import kotlinx.android.synthetic.main.fragment_splash_screen.*

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
        languageViewModel = ViewModelProvider(this, factory).get(LanguageViewModel::class.java)
        languageBinding!!.langViewModel = languageViewModel
        languageBinding!!.lifecycleOwner = this
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
        // Implementation moved to the splash screen
        /*if(!myPreference.isLanguageSelected()){
            if(Network.isAvailable(ctx)){

                languageViewModel.getLanguages()
            }
        }*/
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
        languageViewModel.selectedLanguage.observe(viewLifecycleOwner, {
            //Log.i("selectedLanguage", it.toString())
            if(it == null){
                languageViewModel.updateUserSelection(Country(country.id,country.flag, country.language, country.languageCode, 1), Country(country.id,country.flag, country.language, country.languageCode, 1))
            }else{
                languageViewModel.updateUserSelection(Country(it.id,it.flag, it.language, it.languageCode, 0), Country(country.id,country.flag, country.language, country.languageCode, 1))
            }
        })
        myPreference.setStringVal("my_language", country.languageCode.toString())
        myPreference.setStringVal("my_lang", country.language.toString())
        activity?.recreate()
    }
}
