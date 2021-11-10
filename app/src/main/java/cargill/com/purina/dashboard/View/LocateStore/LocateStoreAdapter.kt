package cargill.com.purina.dashboard.View.LocateStore

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.LocateStore.Stores
import cargill.com.purina.databinding.StoreDetailsItemBinding
import com.google.android.gms.maps.model.LatLng

class LocateStoreAdapter(private val clickListener: (Stores) -> Unit) :
  RecyclerView.Adapter<LocateStoreAdapter.LocateStoreViewHolder>() {

  private var storeList = ArrayList<Stores>()
  private var storeListTemp = ArrayList<Stores>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocateStoreViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding: StoreDetailsItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.store_details_item, parent, false)
    return LocateStoreViewHolder(binding, parent.context)
  }

  override fun onBindViewHolder(holder: LocateStoreViewHolder, position: Int) {
    holder.bind(storeListTemp[position], clickListener)
  }

  override fun getItemCount(): Int {
    return storeListTemp.size
  }

  fun setList(stores: ArrayList<Stores>) {
    storeList.clear()
    storeList.addAll(stores)
    storeListTemp = storeList
  }

  class LocateStoreViewHolder(val binding: StoreDetailsItemBinding, val ctx: Context) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(store: Stores, clickListener: (Stores) -> Unit) {

      binding.tvStoreName.text = store.storeName
      binding.tvStoreNumber.text = store.phone

      var DistanceValue: String
      var Km: String = LocateManager.DistanceinKm(LatLng(store.latitude, store.longitude))

      if (Km > 0.toString()) {
        DistanceValue = "$Km "+ctx.getString(R.string.store_distance_kms)
      } else {
        var Meter = LocateManager.DistanceinMeters(LatLng(store.latitude, store.longitude))
        DistanceValue = "$Meter "+ctx.getString(R.string.store_distance_meters)
      }

      binding.tvStoreDistance.text = DistanceValue

      binding.llStoreItemLayout.setOnClickListener {
        clickListener(store)
      }
    }

  }

}