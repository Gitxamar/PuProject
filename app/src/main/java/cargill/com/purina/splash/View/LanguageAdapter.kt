package cargill.com.purina.splash.View

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.splash.Model.Country
import cargill.com.purina.databinding.LanguageItemBinding
import cargill.com.purina.utils.AppPreference

class LanguageAdapter(var context:Context, private val clickListener: (Country)->Unit):
    RecyclerView.Adapter<LanguageViewHolder>()
     {
         lateinit var myPreference: AppPreference
         private val countriesList = ArrayList<Country>()

         override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
             val layoutInflater = LayoutInflater.from(parent.context)
             val binding : LanguageItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.language_item, parent, false)
             myPreference = AppPreference(context)
             return LanguageViewHolder(binding, context)
         }

         @RequiresApi(Build.VERSION_CODES.M)
         override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
             holder.bind(countriesList[position],clickListener)
         }

         override fun getItemCount(): Int {
             return countriesList.size
         }
         fun setList(countries: List<Country>){
             countriesList.clear()
             countriesList.addAll(countries)
         }
}
class LanguageViewHolder(val binding: LanguageItemBinding, val ctx: Context): RecyclerView.ViewHolder(binding.root){
    fun bind(country: Country, clickListener: (Country)->Unit){
        binding.language.text = country.language
        if(country.language.equals(ctx.getString(R.string.language_russia))){
            binding.flag.setImageResource(R.drawable.ic_russian)
        }else if(country.language.equals(ctx.getString(R.string.language_english))){
            binding.flag.setImageResource(R.drawable.ic_english)
        }else if(country.language.equals(ctx.getString(R.string.language_italia))){
            binding.flag.setImageResource(R.drawable.ic_italian)
        }else if(country.language.equals(ctx.getString(R.string.language_hungaria))){
            binding.flag.setImageResource(R.drawable.ic_hungarian)
        }else if(country.language.equals(ctx.getString(R.string.language_polish))){
            binding.flag.setImageResource(R.drawable.ic_polish)
        }else if(country.language.equals(ctx.getString(R.string.language_roman))){
            binding.flag.setImageResource(R.drawable.ic_romana)
        }
        if(country.status == -1){
            binding.cardViewLayout.setCardBackgroundColor(ContextCompat.getColor(ctx,R.color.app_light_gray2))
            binding.languageTile.setBackgroundColor(ContextCompat.getColor(ctx,R.color.app_light_gray2))
            binding.languageTile.isEnabled = false
            binding.languageTile.alpha = 0.4F
        }else{
            binding.cardViewLayout.setCardBackgroundColor(ContextCompat.getColor(ctx,R.color.app_light_gray))
            binding.languageTile.setBackgroundColor(ContextCompat.getColor(ctx,R.color.app_light_gray))
            binding.languageTile.isEnabled = true
            binding.languageTile.alpha = 1.0F
        }
        binding.languageTile.setOnClickListener {
            clickListener(country)
        }
    }
}