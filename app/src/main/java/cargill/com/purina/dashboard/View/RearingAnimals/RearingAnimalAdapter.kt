package cargill.com.purina.dashboard.View.RearingAnimals

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.Articles.Article
import cargill.com.purina.databinding.RearingAnimalItemBinding
import cargill.com.purina.utils.Constants
import coil.load
import coil.request.CachePolicy

class RearingAnimalAdapter(private val clickListener: (Article)->Unit): RecyclerView.Adapter<RearingAnimalViewHolder>() {
  private var articlesList = ArrayList<Article>()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RearingAnimalViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding : RearingAnimalItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.rearing_animal_item,parent,false)
    return RearingAnimalViewHolder(binding, parent.context)
  }

  override fun onBindViewHolder(holder: RearingAnimalViewHolder, position: Int) {
    holder.bind(articlesList[position],clickListener)
  }

  override fun getItemCount(): Int {
    return articlesList.size
  }
  fun setList(articles: ArrayList<Article>){
    articlesList.addAll(articles)
    notifyDataSetChanged()
  }
}
class RearingAnimalViewHolder(val binding: RearingAnimalItemBinding, val ctx: Context): RecyclerView.ViewHolder(binding.root){
  fun bind(article: Article, clickListener: (Article)->Unit){
    binding.articleData.text = article.article_name
    binding.speciesData.text = article.species_name
    if(Network.isAvailable(ctx)){
      binding.animalImage.load(Constants.DEV_BASE_URL+article.thumbnail_url){
        placeholder(R.drawable.ic_image_not_supported)
        crossfade(true)
        crossfade(100)
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.READ_ONLY)
      }
    }else{
      //binding.productImage.setImageResource(R.drawable.ic_image_not_supported)
      binding.animalImage.load(Constants.DEV_BASE_URL+article.thumbnail_url){
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.READ_ONLY)
      }
    }
  }
}