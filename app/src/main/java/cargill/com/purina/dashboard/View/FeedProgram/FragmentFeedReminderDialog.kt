package cargill.com.purina.dashboard.View.FeedProgram

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import cargill.com.purina.R
import android.os.Build
import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import cargill.com.purina.utils.PermissionCheck
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.util.*


class FragmentFeedReminderDialog : DialogFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let {
      val builder = AlertDialog.Builder(it)
      val inflater = requireActivity().layoutInflater;
      val view:View = inflater.inflate(R.layout.fragment_feed_reminder_dialog, null)
      PermissionCheck.readAndWriteCalender(requireContext())
      view.findViewById<MaterialButton>(R.id.create).setOnClickListener {
        if(PermissionCheck.readAndWriteCalender(requireContext())){
          createReminder()
        }
      }
      builder.setView(view)
      builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
  }
  fun createReminder(){
    val calID: Long = 3
    val startMillis: Long = Calendar.getInstance().run {
      set(2021, 7, 17, 14, 30)
      timeInMillis
    }
    val endMillis: Long = Calendar.getInstance().run {
      set(2021, 7, 17, 15, 0)
      timeInMillis
    }
    val values = ContentValues().apply {
      put(CalendarContract.Events.DTSTART, startMillis)
      put(CalendarContract.Events.DTEND, endMillis)
      put(CalendarContract.Events.TITLE, "Purina")
      put(CalendarContract.Events.DESCRIPTION, "Test")
      put(CalendarContract.Events.CALENDAR_ID, calID)
      put(CalendarContract.Events.EVENT_TIMEZONE, "India")
    }
    val uri: Uri? = requireActivity().contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    val eventID: Long = uri!!.lastPathSegment!!.toLong()
    val reminderValues = ContentValues().apply {
      put(CalendarContract.Reminders.MINUTES, 15)
      put(CalendarContract.Reminders.EVENT_ID, eventID)
      put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
    }
    val reminderUri: Uri? = requireActivity().contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
    Toast.makeText(context, "Events created", Toast.LENGTH_LONG).show()
  }
}