package cargill.com.purina.dashboard.View.FeedProgram

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import cargill.com.purina.R
import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.text.Editable
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import androidx.annotation.RequiresApi
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedprogramRow
import cargill.com.purina.utils.PermissionCheck
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.log

class FragmentFeedReminderDialog(private val stages:List<FeedprogramRow>) : DialogFragment() {
  private var startingFeedDate:String = ""
  private var toBuy:Boolean = false
  private var changeFeed:Boolean = false
  private var age:Int = 0
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }
  @SuppressLint("NewApi")
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let {
      val builder = AlertDialog.Builder(it)
      val inflater = requireActivity().layoutInflater;
      val view:View = inflater.inflate(R.layout.fragment_feed_reminder_dialog, null)
      PermissionCheck.readAndWriteCalender(requireContext())
      val feedingStartDate = view.findViewById<TextInputEditText>(R.id.feedingStartEditText)
      startingFeedDate = LocalDate.now().toString()
      Log.i("Date now", startingFeedDate)
      feedingStartDate.text = Editable.Factory.getInstance().newEditable(startingFeedDate)
      val ageOfAnimal = view.findViewById<TextInputEditText>(R.id.ageEditText)
      val defaultAge = 0
      ageOfAnimal.text = Editable.Factory.getInstance().newEditable(defaultAge.toString())

      view.findViewById<MaterialButton>(R.id.create).setOnClickListener {
        if(PermissionCheck.readAndWriteCalender(requireContext())){
          toBuy = view.findViewById<MaterialCheckBox>(R.id.toBuy_Check).isChecked
          changeFeed = view.findViewById<MaterialCheckBox>(R.id.changeFeed_check).isChecked
          age = ageOfAnimal.toString().toInt()
          if(age > 0){
            var feedStartDate = LocalDate.parse(startingFeedDate).minusDays(age.toLong())
            val today = LocalDate.now()
            for(stage in stages){
              feedStartDate = feedStartDate.plusDays(stage.age_days.toLong())
              Log.i("NumberDay", ChronoUnit.DAYS.between(today, feedStartDate).toString())
              if(ChronoUnit.DAYS.between(today, feedStartDate) > 0){
                /*Reminder for to buy*/
                val  reminderToBuy = if(toBuy) feedStartDate.minusDays(3) else feedStartDate
                createReminder(reminderToBuy)
                /*Reminder for change feed*/
                val reminderToChangeFeed = if(changeFeed) feedStartDate.minusDays(1) else feedStartDate
                createReminder(reminderToChangeFeed)
              }
            }
          }else{
            Snackbar.make(view,"Please enter the Age of the species", Snackbar.LENGTH_LONG).show()
          }
        }
      }
      view.findViewById<CalendarView>(R.id.calenderView).setOnDateChangeListener { v, year, month, dayOfMonth ->
        val calendar = Calendar.getInstance()
        calendar[year, month] = dayOfMonth
        startingFeedDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        Log.i("Date", startingFeedDate)
        feedingStartDate.text = Editable.Factory.getInstance().newEditable(startingFeedDate)
      }
      builder.setView(view)
      builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")
  }
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
  }

  @RequiresApi(Build.VERSION_CODES.O)
  fun createReminder(reminderDate: LocalDate){
    val calID: Long = 3

    val startMillis: Long = Calendar.getInstance().run {
      set(reminderDate.year, reminderDate.monthValue, reminderDate.dayOfMonth, 18, 10)
      timeInMillis
    }
    val endMillis: Long = Calendar.getInstance().run {
      set(reminderDate.year, reminderDate.monthValue, reminderDate.dayOfMonth, 18, 40)
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