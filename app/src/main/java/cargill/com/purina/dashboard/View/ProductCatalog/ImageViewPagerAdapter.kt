package cargill.com.purina.dashboard.View.ProductCatalog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.ProductDetails.Image
import cargill.com.purina.splash.Model.Country
import cargill.com.purina.utils.Constants
import coil.load
import coil.request.CachePolicy
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.viewpager_item.view.*

class ImageViewPagerAdapter(images:List<Image>, private val clickListener: (List<Image>, Int)->Unit): RecyclerView.Adapter<ImageViewPagerAdapter.ViewHolder>() {

  private var imagesList:List<Image> = images
  private lateinit var ctx: Context

  class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    ctx = parent.context
    return ViewHolder(
      LayoutInflater
      .from(parent.context)
      .inflate(R.layout.viewpager_item,parent,false))
  }

  override fun getItemCount(): Int = imagesList.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    Glide
      .with(ctx)
      .load(Constants.DEV_BASE_URL+"v2"+imagesList[position].image_url)
      .into(holder.view.imageView);

   /* holder.view.imageView.load(Constants.DEV_BASE_URL+imagesList[position].image_url){
      memoryCachePolicy(CachePolicy.ENABLED)
      diskCachePolicy(CachePolicy.READ_ONLY)
    }*/
    holder.view.imageView.setOnClickListener {
      clickListener(imagesList, position)
    }
  }
}