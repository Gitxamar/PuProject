package cargill.com.purina.dashboard.View.ProductCatalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.ProductDetails.Image
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.utils.Constants
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_image_viewer.*

class FragmentImageViewer : Fragment() {

  private lateinit var images: List<Image>
  var sharedViewmodel: SharedViewModel? = null
  private var dataLoaded:Boolean = false
  private var product_id:Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_image_viewer, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if(arguments != null) {
      if (requireArguments().containsKey(Constants.IMAGES)) {
        images = arguments?.get(Constants.IMAGES)!! as List<Image>
      }
      if(requireArguments().containsKey(Constants.PRODUCT_ID)){
        product_id = arguments?.getInt(Constants.PRODUCT_ID)!!
      }
    }
    viewPager.adapter = ImageViewPagerAdapter(images, {images: List<Image> ->previewImage(images) })
    TabLayoutMediator(tabLayout,viewPager){tab, position ->
      dataLoaded = true
    }.attach()

    sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    sharedViewmodel?.navigateToDetails?.observe(viewLifecycleOwner, Observer {
      sharedViewmodel!!.navigateToDetails.value?.getContentIfNotHandled()?.let { it1 ->
        if(it1.equals("navigate")){
          if(dataLoaded){
            sharedViewmodel!!.navigate("")
            if(Network.isAvailable(requireContext())){
              findNavController().navigate(R.id.action_fragmentImageViewer_to_productCatalogueFilter)
            }else{
              findNavController().navigate(R.id.action_fragmentImageViewer_to_productCatalog)
            }
          }
        }
      }
    })
    back.setOnClickListener {
      if(product_id == 0){
        val bundle = bundleOf(
          Constants.PRODUCT_ID to product_id)
        findNavController().navigate(R.id.action_fragmentImageViewer_to_home, bundle)
      }else{
        requireFragmentManager().popBackStack()
      }
   }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    sharedViewmodel = null
  }
  private fun previewImage(images: List<Image>){
  }
}