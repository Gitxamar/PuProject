package cargill.com.purina.splash.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import cargill.com.purina.Home.View.DashboardActivity
import cargill.com.purina.splash.Model.Country
import cargill.com.purina.splash.Repository.LanguageRepository
import cargill.com.purina.splash.viewmodel.LanguageViewModel
import cargill.com.purina.splash.viewmodel.LanguageViewModelFactory
import cargill.com.purina.database.PurinaDataBase
import cargill.com.purina.databinding.FragmentOnboardingLanguageBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Localization

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
            startActivity(Intent(context,DashboardActivity::class.java))
        }
        languageViewModel.message.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        })
        if(!myPreference.isLanguageSelected()){
            languageViewModel.saveLanguages()
        }

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
        languageViewModel.countries.observe(viewLifecycleOwner, Observer {
            Log.i("PURINA", it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun changeLanguage(country: Country){
        //Toast.makeText(context, "selected language is ${country.language}", Toast.LENGTH_LONG).show()
        myPreference.setStringVal("my_language", country.languageCode.toString())
        myPreference.setStringVal("my_lang", country.language.toString())
        Localization.localize(ctx, country.languageCode.toString())
        //languageViewModel.updateUserSelection(Country(country.id,country.flag, country.language, country.languageCode, 1))
        activity?.recreate()
    }
}
