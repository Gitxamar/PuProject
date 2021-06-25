package cargill.com.purina.Home.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cargill.com.purina.R
import cargill.com.purina.databinding.FragmentAccountBinding
import cargill.com.purina.splash.View.OnboardingActivity
import cargill.com.purina.utils.AppPreference
import kotlinx.android.synthetic.main.fragment_account.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Account.newInstance] factory method to
 * create an instance of this fragment.
 */
class Account : Fragment() {
    lateinit var binding: FragmentAccountBinding
    lateinit var myPreference: AppPreference
    lateinit var ctx:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_account, container, false)
        binding = FragmentAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myPreference = AppPreference(ctx)
        var lang: String? = myPreference.getStringValue("my_lang")
        languageChangeText.text = lang
        change.setOnClickListener {
            activity.let {
                val intent = Intent(it, OnboardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.ctx = context
    }
}