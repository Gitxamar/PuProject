package cargill.com.purina.splash.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.fragment.findNavController
import cargill.com.purina.Home.View.DashboardActivity
import cargill.com.purina.R
import cargill.com.purina.utils.AppPreference
import kotlinx.android.synthetic.main.activity_dashboard.*

class FragmentSplashScreen : Fragment() {

    lateinit var myPreference: AppPreference
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
        myPreference = AppPreference(context)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val motionLayout = view.findViewById<MotionLayout>(R.id.splashScreenLayout)
        motionLayout?.addTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }
            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if(myPreference.isLanguageSelected()){
                    startActivity(Intent(context, DashboardActivity::class.java))
                }else{
                    findNavController().navigate(
                        R.id.action_splashScreen_to_onboardingLanguage)
                }
            }
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }
        })
    }
}