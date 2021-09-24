package cargill.com.purina.dashboard.View.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.Home.OnBoardingItem


class OnboardingAdapter() :  RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

  private val onBoardingList = ArrayList<OnBoardingItem>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
    return OnboardingViewHolder(
      LayoutInflater.from(parent.context).inflate(
        R.layout.item_container_boarding, parent, false
      )
    )
  }

  fun setList(data: Array<OnBoardingItem>) {
    onBoardingList.clear()
    onBoardingList.addAll(data)
  }

  override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
    holder.setOnBoardingData(onBoardingList[position])
  }

  override fun getItemCount(): Int {
    return onBoardingList.size
  }

  class OnboardingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textTitle: TextView
    private val textDescription: TextView
    private val imageOnboarding: ImageView
    fun setOnBoardingData(onBoardingItem: OnBoardingItem) {
      textTitle.text = onBoardingItem.title
      textDescription.text = onBoardingItem.description
      imageOnboarding.setImageResource(onBoardingItem.image)
    }

    init {
      textTitle = itemView.findViewById(R.id.textTitle)
      textDescription = itemView.findViewById(R.id.textDescription)
      imageOnboarding = itemView.findViewById(R.id.imageOnboarding)
    }
  }
}