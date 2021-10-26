package cargill.com.purina.splash.View

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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
        if(country.languageCode.equals("ru")){
            binding.flag.setImageResource(R.drawable.ic_russian)
        }else if(country.languageCode.equals("en")){
            binding.flag.setImageResource(R.drawable.ic_english)
        }else if(country.languageCode.equals("it")){
            binding.flag.setImageResource(R.drawable.ic_italian)
        }else if(country.languageCode.equals("hu")){
            binding.flag.setImageResource(R.drawable.ic_hungarian)
        }else if(country.languageCode.equals("pl")){
            binding.flag.setImageResource(R.drawable.ic_polish)
        }else if(country.languageCode.equals("ro")){
            binding.flag.setImageResource(R.drawable.ic_romana)
        }
        if(country.status == -1){
            binding.cardViewLayout.setCardBackgroundColor(ContextCompat.getColor(ctx,R.color.app_light_gray2))
            binding.languageTile.setBackgroundColor(ContextCompat.getColor(ctx,R.color.app_light_gray2))
            binding.languageTile.isEnabled = false
            binding.languageTile.alpha = 0.4F
        }else{
            binding.cardViewLayout.setCardBackgroundColor(ContextCompat.getColor(ctx,R.color.app_light_gray3))
            binding.languageTile.setBackgroundColor(ContextCompat.getColor(ctx,R.color.app_light_gray3))
            binding.languageTile.isEnabled = true
            binding.languageTile.alpha = 1.0F
        }
        binding.languageTile.setOnClickListener {
            clickListener(country)
        }
    }
}