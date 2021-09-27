package cargill.com.purina.splash.View

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.Home.OnBoardingItem
import cargill.com.purina.dashboard.View.Home.OnboardingAdapter
import cargill.com.purina.databinding.FragmentRegisterBinding
import cargill.com.purina.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_register.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OnBoardingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OnBoardingFragment : Fragment() {
  // TODO: Rename and change types of parameters
  lateinit var onboardingAdapter: OnboardingAdapter

  lateinit var binding: FragmentRegisterBinding
  lateinit var ctx: Context
  lateinit var dataTemp: Array<OnBoardingItem>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    /* arguments?.let {
         param1 = it.getString(ARG_PARAM1)
         param2 = it.getString(ARG_PARAM2)
     }*/
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentRegisterBinding.inflate(inflater, container, false)
    val view = binding!!.root
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    onboardingAdapter = OnboardingAdapter()
    setOnboardingItem()

    setOnboadingIndicator();
    setCurrentOnboardingIndicators(0);

    binding.buttonOnBoardingAction.setOnClickListener {

      if (binding.onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter!!.getItemCount()) {
        binding.onboardingViewPager.setCurrentItem(binding.onboardingViewPager.getCurrentItem() + 1);
      } else {
        if (Network.isAvailable(ctx)) {
          activity.let {
            val intent = Intent(it, OnboardingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            activity?.finish()
          }
        } else {
          Snackbar.make(layoutOnBoarding, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
            .setAction("Settings") {
              startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }.show()
        }
      }
    }

    binding.buttonOnBoardingActionPrevious.setOnClickListener {

      if (binding.onboardingViewPager.getCurrentItem() - 1 < onboardingAdapter!!.getItemCount()) {
        binding.onboardingViewPager.setCurrentItem(binding.onboardingViewPager.getCurrentItem() - 1);
      } else {

      }
    }

    binding.onboardingViewPager.registerOnPageChangeCallback(object :
      ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        setCurrentOnboardingIndicators(position)
      }
    })

  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    ctx = context
  }

  private fun setOnboardingItem() {

    if (Locale.getDefault().getLanguage().equals("ru")) {
      dataTemp = Constants.OnBoardingListRussian
    } else {
      dataTemp = Constants.OnBoardingListEnglish
    }

    onboardingAdapter.setList(dataTemp)
    onboardingAdapter.notifyDataSetChanged()
    binding.onboardingViewPager.adapter = onboardingAdapter

  }

  private fun setOnboadingIndicator() {
    val indicators: Array<ImageView?> = arrayOfNulls<ImageView>(onboardingAdapter.itemCount)
    val layoutParams = LinearLayout.LayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    layoutParams.setMargins(8, 0, 8, 0)
    for (i in indicators.indices) {
      indicators[i] = ImageView(ctx.getApplicationContext())
      indicators[i]!!.setImageDrawable(
        ContextCompat.getDrawable(
          ctx,
          R.drawable.indicator_inactive
        )
      )
      indicators[i]!!.setLayoutParams(layoutParams)
      binding.layoutOnboardingIndicators.addView(indicators[i])
    }
  }

  private fun setCurrentOnboardingIndicators(index: Int) {

    var childCount: Int = binding.layoutOnboardingIndicators.getChildCount()
    for (item in 0 until childCount) {
      var imageView = binding.layoutOnboardingIndicators.getChildAt(item) as ImageView
      if (item == index) {
        imageView.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.indicator_active));
      } else {
        imageView.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.indicator_inactive));
      }
    }

    lateinit var next: String
    lateinit var back: String
    lateinit var getStarted: String
    if (Locale.getDefault().getLanguage().equals("ru")) {
      next = Constants.txtNext
      back = Constants.txtBack
      getStarted = Constants.txtGetStarted
    } else {
      next = resources.getText(R.string.next) as String
      back = resources.getText(R.string.back) as String
      getStarted = resources.getText(R.string.getStarted) as String
    }

    if (index == 0) {
      binding.buttonOnBoardingAction.text = getStarted
      binding.buttonOnBoardingActionPrevious.visibility = View.GONE
    } else if (index == onboardingAdapter.getItemCount() - 1) {
      binding.buttonOnBoardingAction.text = next
      binding.buttonOnBoardingActionPrevious.visibility = View.VISIBLE
      binding.buttonOnBoardingActionPrevious.text = back
    } else {
      binding.buttonOnBoardingAction.text = next
      binding.buttonOnBoardingActionPrevious.visibility = View.VISIBLE
      binding.buttonOnBoardingActionPrevious.text = back
    }
  }

}