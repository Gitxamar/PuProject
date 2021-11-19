package cargill.com.purina.splash.View

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.Home.OnBoardingItem
import cargill.com.purina.dashboard.View.Home.OnboardingAdapter
import cargill.com.purina.databinding.FragmentRegisterBinding
import cargill.com.purina.splash.Repository.LanguageRepository
import cargill.com.purina.splash.viewmodel.LanguageViewModel
import cargill.com.purina.splash.viewmodel.LanguageViewModelFactory
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
  private lateinit var languageViewModel: LanguageViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentRegisterBinding.inflate(inflater, container, false)

    val dao = PurinaDataBase.invoke(ctx.applicationContext).dao
    val repo = LanguageRepository(dao)
    val factory  = LanguageViewModelFactory(repo,ctx)
    languageViewModel = ViewModelProvider(this, factory).get(LanguageViewModel::class.java)
    binding!!.langViewModel = languageViewModel
    binding!!.lifecycleOwner = this

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
        languageScreen()
      }
    }

    binding.buttonOnBoardingActionPrevious.setOnClickListener {

      /*if (binding.onboardingViewPager.getCurrentItem() - 1 < onboardingAdapter!!.getItemCount()) {
        binding.onboardingViewPager.setCurrentItem(binding.onboardingViewPager.getCurrentItem() - 1);
      } else {
      }*/

      languageScreen()

    }

    binding.onboardingViewPager.registerOnPageChangeCallback(object :
      ViewPager2.OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        setCurrentOnboardingIndicators(position)
      }
    })

    /*val currentNightMode = Configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    when (currentNightMode) {
      Configuration.UI_MODE_NIGHT_NO -> {} // Night mode is not active, we're using the light theme
      Configuration.UI_MODE_NIGHT_YES -> {} // Night mode is active, we're using dark theme
    }*/

  }

  private fun languageScreen() {
    if (Network.isAvailable(ctx)) {
      activity.let {
        val intent = Intent(it, OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity?.finish()
      }
    } else {
      languageViewModel.countries.observe(viewLifecycleOwner, {
        Log.i("PURINA", it.toString())
        if(!it.isEmpty()){
          val intent = Intent(activity, OnboardingActivity::class.java)
          intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
          startActivity(intent)
          activity?.finish()
        }else{
          Snackbar.make(layoutOnBoarding, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
            .setAction("Settings") {
              startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }.show()
        }
      })
    }
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
    lateinit var skip: String
    lateinit var getStarted: String
    if (Locale.getDefault().getLanguage().equals("ru")) {
      next = Constants.txtNext
      skip = Constants.txtSkip
      getStarted = Constants.txtGetStarted
    } else {
      next = resources.getText(R.string.next) as String
      skip = resources.getText(R.string.skip) as String
      getStarted = resources.getText(R.string.getStarted) as String
    }

    if (index == 0) {
      binding.buttonOnBoardingAction.text = getStarted
      binding.buttonOnBoardingActionPrevious.visibility = View.GONE
    } else if (index == onboardingAdapter.getItemCount() - 1) {
      binding.buttonOnBoardingAction.text = next
      binding.buttonOnBoardingActionPrevious.visibility = View.VISIBLE
      binding.buttonOnBoardingActionPrevious.text = skip
    } else {
      binding.buttonOnBoardingAction.text = next
      binding.buttonOnBoardingActionPrevious.visibility = View.VISIBLE
      binding.buttonOnBoardingActionPrevious.text = skip
    }
  }

}