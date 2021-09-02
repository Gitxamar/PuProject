package cargill.com.purina.dashboard.View.LocateStore

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.databinding.StoreDetailsItemBinding
import com.google.android.gms.maps.model.LatLng

class LocateLocalStoreAdapter(private val clickListener: (StoreDetail) -> Unit) :
  RecyclerView.Adapter<LocateLocalStoreAdapter.LocateStoreViewHolder>() {

  private var storeList = ArrayList<StoreDetail>()
  private var storeListTemp = ArrayList<StoreDetail>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocateStoreViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding: StoreDetailsItemBinding =
      DataBindingUtil.inflate(layoutInflater, R.layout.store_details_item, parent, false)
    return LocateStoreViewHolder(binding, parent.context)
  }

  override fun onBindViewHolder(holder: LocateStoreViewHolder, position: Int) {
    holder.bind(storeListTemp[position], clickListener)
  }

  override fun getItemCount(): Int {
    return storeListTemp.size
  }

  fun setList(stores: ArrayList<StoreDetail>) {
    storeList.clear()
    storeList.addAll(stores)
    storeListTemp = storeList
  }

  class LocateStoreViewHolder(val binding: StoreDetailsItemBinding, val ctx: Context) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(store: StoreDetail, clickListener: (StoreDetail) -> Unit) {

      binding.tvStoreName.text = store.name
      binding.tvStoreNumber.text = store.phone

      var DistanceValue: String
      var Km: String = LocateManager.DistanceinKm(LatLng(store.latitude, store.longitude))

      if (Km > 0.toString()) {
        DistanceValue = "$Km Km away"
      } else {
        var Meter = LocateManager.DistanceinMeters(LatLng(store.latitude, store.longitude))
        DistanceValue = "$Meter meters away"
      }
      binding.tvStoreDistance.text = DistanceValue

      binding.llStoreItemLayout.setOnClickListener {
        clickListener(store)
      }
    }


  }

}