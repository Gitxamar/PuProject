package cargill.com.purina.dashboard.View.RearingAnimals

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import kotlinx.android.synthetic.main.fragment_detail_catalogue.view.*

class RearingAnimalAdapter(private val clickListener: (Article,Int)->Unit): RecyclerView.Adapter<RearingAnimalViewHolder>() {
  private var articlesList = ArrayList<Article>()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RearingAnimalViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding : RearingAnimalItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.rearing_animal_item,parent,false)
    return RearingAnimalViewHolder(binding, parent.context)
  }

  override fun onBindViewHolder(holder: RearingAnimalViewHolder, position: Int) {
    holder.bind(articlesList[position],clickListener )
  }

  override fun getItemCount(): Int {
    return articlesList.size
  }
  fun setList(articles: ArrayList<Article>){
    articlesList.clear()
    articlesList.addAll(articles)
    Log.i("List", articlesList.toString())
    notifyDataSetChanged()
  }
}
class RearingAnimalViewHolder(val binding: RearingAnimalItemBinding, val ctx: Context): RecyclerView.ViewHolder(binding.root){
  fun bind(article: Article, clickListener: (Article, Int) -> Unit){
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
    binding.animalContainer.setOnClickListener {
      clickListener(article,position)
    }
  }
}