package cargill.com.purina.dashboard.View.FeedProgram

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.FeedingProgram.Reminder

class ReminderAdapter(private val context: Activity, private val reminders: ArrayList<Reminder>) : ArrayAdapter<Reminder>(context, R.layout.reminders, reminders) {
  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val inflater:LayoutInflater = LayoutInflater.from(context)
    val view:View = inflater.inflate(R.layout.reminders, null)
    val stageName:TextView = view.findViewById(R.id.stageName)
    val date:TextView = view.findViewById(R.id.date)
    stageName.text = reminders[position].stage
    date.text = reminders[position].date
    return view
  }
}