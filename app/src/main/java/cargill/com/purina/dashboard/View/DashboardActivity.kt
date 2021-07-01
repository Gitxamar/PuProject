package cargill.com.purina.dashboard.View

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.dashboard.Repository.DashboardRepository
import cargill.com.purina.dashboard.View.Home.AnimalAdapter
import cargill.com.purina.dashboard.viewModel.DashboardViewModel
import cargill.com.purina.dashboard.viewModel.DashboardViewModelFactory
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.databinding.ActivityDashboardBinding
import cargill.com.purina.utils.AppPreference
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dashboard_animal_filter.*
import kotlinx.android.synthetic.main.dashboard_animal_filter.view.*

class DashboardActivity : AppCompatActivity() {
    lateinit var navController:NavController
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var myPreference: AppPreference
    private var languageCode: String = ""
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var adapter: AnimalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_dashboard)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_dashboard)
        init()
        bottomSheetBehavior = BottomSheetBehavior.from(dashboardAnimalFilter)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        dashboardBottomFab.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
    private fun init(){
        val dao = PurinaDataBase.invoke(applicationContext).dao
        val repository = DashboardRepository(dao, applicationContext)
        val factory = DashboardViewModelFactory(repository)
        dashboardViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        binding.dashViewModel = dashboardViewModel
        binding.lifecycleOwner = this
        dashboardBottomAppBar.background = ContextCompat.getDrawable(applicationContext, R.drawable.top_rounded_corner)
        dashboardBottomNavView.background = null
        dashboardBottomNavView.menu.findItem(R.id.placeholder).isEnabled = false
        navController = findNavController(R.id.fragmentDashboard)
        dashboardBottomNavView.setupWithNavController(navController)
        intiRecyclerView()
    }
    fun intiRecyclerView(){
        binding.root.animals_list.layoutManager = LinearLayoutManager(this)
        adapter = AnimalAdapter({animalSelected: Animal ->changeAnimal(animalSelected)})
        binding.root.animals_list.adapter = adapter
        displayAnimalsFilterData()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        myPreference = AppPreference(newBase!!)
        languageCode = myPreference.getStringValue("my_language").toString()
    }

    fun displayAnimalsFilterData(){
        if(!myPreference.isAnimalSelected()){
            dashboardViewModel.getData(languageCode)
        }
        dashboardViewModel.animals.observe(this, Observer {
            Log.i("dashboard", it.toString())
            if(!it.isEmpty()){
                adapter.setList(it)
                adapter.notifyDataSetChanged()
            }
        })
    }
    private fun changeAnimal(animal: Animal){
        Log.i("dashboard animal.name", animal.name)
        sharedViewModel.animalSelected(animal)
        myPreference.setStringVal("animal_selected", animal.name)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}