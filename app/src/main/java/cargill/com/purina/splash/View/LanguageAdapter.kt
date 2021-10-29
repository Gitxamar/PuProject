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
import cargill.com.purina.utils.Constants

class LanguageAdapter(var context:Context, private val clickListener: (Country)->Unit):
    RecyclerView.Adapter<LanguageViewHolder>()
     {
         lateinit var myPreference: AppPreference
         private val countriesList = ArrayList<Country>()
         var selected_position = 0

         override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
             val layoutInflater = LayoutInflater.from(parent.context)
             val binding : LanguageItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.language_item, parent, false)
             myPreference = AppPreference(context)
             return LanguageViewHolder(binding, context, myPreference)
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
class LanguageViewHolder(val binding: LanguageItemBinding, val ctx: Context, var myPreference: AppPreference): RecyclerView.ViewHolder(binding.root){
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
            binding.cardViewLayout.setBackgroundResource(R.drawable.language_bg_light_grey)
            binding.languageTile.setBackgroundResource(R.drawable.language_bg_light_grey)
            binding.languageTile.isEnabled = false
            binding.languageTile.alpha = 0.4F
        }else{
            binding.cardViewLayout.setBackgroundResource(R.drawable.language_bg_selected_grey)
            binding.languageTile.setBackgroundResource(R.drawable.language_bg_selected_grey)
            binding.languageTile.isEnabled = true
            binding.languageTile.alpha = 1.0F

            if(myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).equals(country.languageCode)){
                binding.cardViewLayout.setBackgroundResource(R.drawable.language_bg_light_grey)
            }

        }

        binding.languageTile.setOnClickListener {
            clickListener(country)
        }
    }
}