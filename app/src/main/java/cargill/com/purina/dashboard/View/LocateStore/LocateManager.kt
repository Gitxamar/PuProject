package cargill.com.purina.dashboard.View.LocateStore

import android.util.Log
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Model.LocateStore.Stores
import cargill.com.purina.utils.Constants
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat


object LocateManager {

  fun DistanceinKm(EndP: LatLng): String {
    val Radius = 6371 // radius of earth in Km
    val lat1 = Constants.location.latitude
    val lat2 = EndP.latitude
    val lon1 = Constants.location.longitude
    val lon2 = EndP.longitude
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + (Math.cos(Math.toRadians(lat1))
            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
            * Math.sin(dLon / 2)))
    val c = 2 * Math.asin(Math.sqrt(a))
    val valueResult = Radius * c
    val km = valueResult / 1
    val newFormat = DecimalFormat("####")
    val kmInDec: Int = Integer.valueOf(newFormat.format(km))
    val meter = valueResult % 1000
    val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
    Log.i(
      "Radius Value", "" + valueResult + "   KM  " + kmInDec
              + " Meter   " + meterInDec
    )
    Log.i(
      "Radius Value", "   KM  " + kmInDec

    )
    //return Radius * c
    return kmInDec.toString()
  }

  fun DistanceinMeters(EndP: LatLng): String {
    val Radius = 6371 // radius of earth in Km
    val lat1 = Constants.location.latitude
    val lat2 = EndP.latitude
    val lon1 = Constants.location.longitude
    val lon2 = EndP.longitude
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = (Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + (Math.cos(Math.toRadians(lat1))
            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
            * Math.sin(dLon / 2)))
    val c = 2 * Math.asin(Math.sqrt(a))
    val valueResult = Radius * c
    val km = valueResult / 1
    val newFormat = DecimalFormat("####")
    val kmInDec: Int = Integer.valueOf(newFormat.format(km))
    val meter = valueResult % 1000
    val meterInDec: Int = Integer.valueOf(newFormat.format(meter))
    Log.i(
      "Radius Value", "" + valueResult + "   KM  " + kmInDec
              + " Meter   " + meterInDec
    )
    Log.i(
      "Radius Value", "   KM  " + kmInDec

    )
    //return Radius * c
    return meterInDec.toString()
  }

  fun sortListNearBy(stores: ArrayList<Stores>): ArrayList<Stores>{

    var tempList : ArrayList<Stores> = arrayListOf()
    for(item in stores){
      item.distanceBy = LocateManager.DistanceinKm(LatLng(item.latitude, item.longitude)).toInt()
      tempList.add(item)
    }
    tempList.sortBy { Stores -> Stores.distanceBy }
    return tempList
  }

  fun sortListNearByOffline(stores: ArrayList<StoreDetail>): ArrayList<StoreDetail>{

    var tempList : ArrayList<StoreDetail> = arrayListOf()
    for(item in stores){
      item.distanceBy = LocateManager.DistanceinKm(LatLng(item.latitude, item.longitude)).toInt()
      tempList.add(item)
    }
    tempList.sortBy { Stores -> Stores.distanceBy }
    return tempList
  }

}