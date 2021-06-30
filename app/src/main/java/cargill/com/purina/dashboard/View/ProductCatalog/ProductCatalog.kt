package cargill.com.purina.dashboard.View.ProductCatalog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import cargill.com.purina.databinding.FragmentProductCatalogBinding

class ProductCatalog : Fragment() {
    lateinit var binding: FragmentProductCatalogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_product_catalog, container, false)
        binding = FragmentProductCatalogBinding.inflate(inflater)
        return binding.root
    }
}