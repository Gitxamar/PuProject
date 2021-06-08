package cargill.com.purina.Splash.View

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import cargill.com.purina.R
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Localization
import kotlinx.android.synthetic.main.fragment_onboarding_language.*

class OnboardingLanguage : Fragment(){

    lateinit var myPreference: AppPreference
    lateinit var ctx:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_language, container, false)
    }

    override fun onAttach(context: Context) {
        ctx = context
        myPreference = AppPreference(context)
        super.onAttach(context)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layout_russian.setOnClickListener {
            Toast.makeText(ctx,"Russian", Toast.LENGTH_SHORT).show()
            loadLanguage("ru") }

        layout_english.setOnClickListener {
            Toast.makeText(ctx,"English", Toast.LENGTH_SHORT).show()
            loadLanguage("en")
        }
        layout_hungarian.setOnClickListener {
            Toast.makeText(ctx,"Hungarian", Toast.LENGTH_SHORT).show()
            loadLanguage("hu")
        }
        layout_italian.setOnClickListener {
            Toast.makeText(ctx,"Italian", Toast.LENGTH_SHORT).show()
            loadLanguage("it")
        }
        layout_polish.setOnClickListener {
            Toast.makeText(ctx,"Polish", Toast.LENGTH_SHORT).show()
            loadLanguage("pl")
        }
    }
    private fun loadLanguage(lang: String) {
        myPreference.setStringVal("my_language", lang)
        Localization.localize(ctx, lang!!)
        activity?.recreate()
    }
}
