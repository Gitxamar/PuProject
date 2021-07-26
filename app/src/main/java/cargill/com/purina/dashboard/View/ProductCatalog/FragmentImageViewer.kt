package cargill.com.purina.dashboard.View.ProductCatalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.ProductDetails.Image
import cargill.com.purina.utils.Constants
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_image_viewer.*

class FragmentImageViewer : Fragment() {

  private lateinit var images: List<Image>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_image_viewer, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if(arguments != null) {
      if (requireArguments().containsKey(Constants.IMAGES)) {
        images = arguments?.getParcelableArrayList<Image>(Constants.IMAGES)!!
      }
    }
    viewPager.adapter = ImageViewPagerAdapter(images, {images: List<Image> ->previewImage(images) })
    TabLayoutMediator(tabLayout,viewPager){tab, position ->
    }.attach()
  }
  private fun previewImage(images: List<Image>){
  }
}