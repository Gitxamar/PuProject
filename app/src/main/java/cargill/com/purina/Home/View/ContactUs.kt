package cargill.com.purina.Home.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cargill.com.purina.R
import cargill.com.purina.databinding.FragmentContactUsBinding
import cargill.com.purina.databinding.FragmentNotificationsBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactUs.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactUs : Fragment() {
    lateinit var binding: FragmentContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_contact_us, container, false)
        binding = FragmentContactUsBinding.inflate(inflater)
        return binding.root
    }
}