package cargill.com.purina.dashboard.View

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
import cargill.com.purina.dashboard.viewModel.viewModelFactory.DashboardViewModelFactory
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.databinding.ActivityDashboardBinding
import cargill.com.purina.utils.AppPreference
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.dashboard_animal_filter.*
import kotlinx.android.synthetic.main.dashboard_animal_filter.view.*
import android.content.IntentFilter
import cargill.com.purina.utils.Constants


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
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else{
                adapter.notifyDataSetChanged()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            }
        }
    }
    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(Network.isAvailable(this@DashboardActivity))
            dashboardViewModel.getData(languageCode)
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(broadCastReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadCastReceiver);
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
        languageCode = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()
    }


    fun displayAnimalsFilterData(){
        if(!myPreference.isAnimalSelected()){
            if(Network.isAvailable(this))
            dashboardViewModel.getData(languageCode)
        }
        dashboardViewModel.animals.observe(this, Observer {
            Log.i("dashboard", it.toString())
            if(!it.isEmpty()){
                dashboardBottomFab.isEnabled = true
                adapter.setList(it)
                adapter.notifyDataSetChanged()
            }else{
                dashboardBottomFab.isEnabled = false
            }
        })
        dashboardViewModel.selectedAnimal.observe(this, Observer {
            if(it != null){
                sharedViewModel.animalSelected(it)
                setAnimalLogo(it)
            }
        })
    }
    private fun changeAnimal(animal: Animal){
        Log.i("dashboard animal.name", animal.name)
        sharedViewModel.animalSelected(animal)
        setAnimalLogo(animal)
        dashboardViewModel.updateUserSelection(myPreference.getStringValue(Constants.USER_ANIMAL).toString(), Animal(animal.order_id, animal.id, animal.language_code, animal.name, 1))
        myPreference.setStringVal(Constants.USER_ANIMAL, animal.name)
        myPreference.setStringVal(Constants.USER_ANIMAL_CODE, animal.id.toString())
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun setAnimalLogo(animal: Animal){
        when (animal.order_id){
            1 -> dashboardBottomFab.setImageResource(R.drawable.ic_hen)
            2 -> dashboardBottomFab.setImageResource(R.drawable.ic_layer)
            3 -> dashboardBottomFab.setImageResource(R.drawable.ic_duck)
            4 -> dashboardBottomFab.setImageResource(R.drawable.ic_quail)
            5 -> dashboardBottomFab.setImageResource(R.drawable.ic_turkey)
            6 -> dashboardBottomFab.setImageResource(R.drawable.ic_rabbit)
            7 -> dashboardBottomFab.setImageResource(R.drawable.ic_swine)
            8 -> dashboardBottomFab.setImageResource(R.drawable.ic_cow)
            9 -> dashboardBottomFab.setImageResource(R.drawable.ic_sheepgoat)
        }
    }
}
