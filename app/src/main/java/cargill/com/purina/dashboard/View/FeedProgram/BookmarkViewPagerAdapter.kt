package cargill.com.purina.dashboard.View.FeedProgram

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgram
import kotlinx.android.synthetic.main.bookmark_viewpager_item.view.*

class BookmarkViewPagerAdapter(programs:List<FeedProgram>, private val clickListener: (FeedProgram)->Unit): RecyclerView.Adapter<BookmarkViewPagerAdapter.ViewHolder>() {
  private var programs:List<FeedProgram> = programs

  class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      LayoutInflater
        .from(parent.context)
        .inflate(R.layout.bookmark_viewpager_item, parent, false)
    )
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.view.programName.text = programs[position].program_name
    holder.view.enter_animals_Data.text = programs[position].numberOfAnimals.toString()
  }

  override fun getItemCount(): Int = programs.size
}