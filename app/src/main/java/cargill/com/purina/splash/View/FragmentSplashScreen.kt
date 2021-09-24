package cargill.com.purina.splash.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.splash.Repository.LanguageRepository
import cargill.com.purina.splash.viewmodel.LanguageViewModel
import cargill.com.purina.splash.viewmodel.LanguageViewModelFactory
import cargill.com.purina.utils.AppPreference
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_splash_screen.*


class FragmentSplashScreen : Fragment() {

    lateinit var myPreference: AppPreference
    lateinit var ctx: Context
    private lateinit var languageViewModel: LanguageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onAttach(context: Context) {
        this.ctx = context
        myPreference = AppPreference(context)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val motionLayout = view.findViewById<MotionLayout>(R.id.splashScreenLayout)

        val dao = PurinaDataBase.invoke(ctx.applicationContext).dao
        val repo = LanguageRepository(dao)
        val factory  = LanguageViewModelFactory(repo,ctx)
        languageViewModel = ViewModelProvider(this, factory).get(LanguageViewModel::class.java)
        if(!myPreference.isLanguageSelected()){
            if(Network.isAvailable(ctx)){
                languageViewModel.getLanguages()
            }
        }
        motionLayout?.addTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if(myPreference.isLanguageSelected()){
                    startActivity(Intent(context, DashboardActivity::class.java))
                    activity?.finish()
                }else{

                    findNavController().navigate(R.id.fragmentOnBoarding)

                }
            }
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })
    }
}