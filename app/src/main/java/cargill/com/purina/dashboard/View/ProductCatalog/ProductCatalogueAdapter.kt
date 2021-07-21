package cargill.com.purina.dashboard.View.ProductCatalog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.dashboard.Model.Products.Product
import cargill.com.purina.databinding.ProductCatalogItemBinding
import cargill.com.purina.utils.Constants
import coil.load
import coil.request.CachePolicy

class ProductCatalogueAdapter(private val clickListener: (Product)->Unit): RecyclerView.Adapter<ProductCatalogueViewHolder>(),Filterable{

    private var productList = ArrayList<Product>()
    private var productFilterList = ArrayList<Product>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCatalogueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding : ProductCatalogItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.product_catalog_item, parent, false)
        return ProductCatalogueViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ProductCatalogueViewHolder, position: Int) {
        holder.bind(productFilterList[position],clickListener)
    }

    override fun getItemCount(): Int {
        return productFilterList.size
    }
    fun setList(products: ArrayList<Product>){
        productList.clear()
        productList.addAll(products)
        productFilterList = productList
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    productFilterList = productList as ArrayList<Product>
                } else {
                    val resultList = ArrayList<Product>()
                    for (row in productList) {
                        if (row.product_name.toLowerCase().contains(constraint.toString().toLowerCase()) || row.recipe_code.toLowerCase().contains(constraint.toString().toLowerCase()) ) {
                            resultList.add(row)
                        }
                    }
                    productFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = productFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productFilterList = results?.values as ArrayList<Product>
                notifyDataSetChanged()
            }
        }
    }
}
class ProductCatalogueViewHolder(val binding: ProductCatalogItemBinding, val ctx:Context): RecyclerView.ViewHolder(binding.root){
    fun bind(product: Product, clickListener: (Product)->Unit){
        binding.productName.text = product.product_name
        binding.recipeCode.text = ctx.getString(R.string.recipe_code).plus(product.recipe_code)
        if(Network.isAvailable(ctx)){
            binding.productImage.load(Constants.DEV_BASE_URL+product.image_url){
                placeholder(R.drawable.ic_image_not_supported)
                crossfade(true)
                crossfade(100)
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.READ_ONLY)
            }
        }else{
            //binding.productImage.setImageResource(R.drawable.ic_image_not_supported)
            binding.productImage.load(Constants.DEV_BASE_URL+product.image_url){
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.READ_ONLY)
            }
        }

        binding.productTitle.setOnClickListener {
            clickListener(product)
        }
    }
}