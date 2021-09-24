package cargill.com.purina.dashboard.View.Home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.Home.FAQs
import cargill.com.purina.databinding.FaqLayoutItemBinding

class FaqAdapter(private val clickListener: (FAQs) -> Unit) :
  RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

  private var faqList = ArrayList<FAQs>()
  private var faqListTemp = ArrayList<FAQs>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding: FaqLayoutItemBinding =
      DataBindingUtil.inflate(layoutInflater, R.layout.faq_layout_item, parent, false)
    return FaqViewHolder(binding, parent.context)
  }

  override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
    holder.bind(faqListTemp[position], clickListener, position)
  }

  override fun getItemCount(): Int {
    return faqListTemp.size
  }

  fun setList(faqObj: List<FAQs>) {
    faqList.clear()
    faqList.addAll(faqObj)
    faqListTemp = faqList
  }

  class FaqViewHolder(val binding: FaqLayoutItemBinding, val ctx: Context) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(faqObj: FAQs, clickListener: (FAQs) -> Unit, count: Int) {
      val position = count + 1
      binding.tvFaqQuestionNO.text = position.toString()
      binding.tvFaqQuestion.text = faqObj.question
      binding.tvStoreNumber.text = faqObj.answer
    }
  }


}