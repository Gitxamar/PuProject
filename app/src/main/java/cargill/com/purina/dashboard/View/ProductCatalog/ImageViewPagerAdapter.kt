package cargill.com.purina.dashboard.View.ProductCatalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.ProductDetails.Image
import cargill.com.purina.utils.Constants
import coil.load
import coil.request.CachePolicy
import kotlinx.android.synthetic.main.viewpager_item.view.*

class ImageViewPagerAdapter(images:List<Image>): RecyclerView.Adapter<ImageViewPagerAdapter.ViewHolder>() {

  private var imagesList:List<Image> = images

  class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      LayoutInflater
      .from(parent.context)
      .inflate(R.layout.viewpager_item,parent,false))
  }

  override fun getItemCount(): Int = imagesList.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.view.imageView.load(Constants.DEV_BASE_URL+imagesList[position].image_url){
      memoryCachePolicy(CachePolicy.ENABLED)
      diskCachePolicy(CachePolicy.READ_ONLY)
    }
  }
}