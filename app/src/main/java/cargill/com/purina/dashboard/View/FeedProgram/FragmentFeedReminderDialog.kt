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
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedprogramRow
import cargill.com.purina.dashboard.Model.FeedingProgram.Reminder
import cargill.com.purina.utils.PermissionCheck
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

class FragmentFeedReminderDialog(private val stages:List<FeedprogramRow>) : DialogFragment() {
  private var startingFeedDate:String = ""
  private var toBuy:Boolean = false
  private var changeFeed:Boolean = false
  private var age:Int = 0
  private lateinit var buyReminder:Reminder
  private lateinit var feedChangeReminders:Reminder
  private var buyReminderList:ArrayList<Reminder> = arrayListOf()
  private var feedChangeRemindersList:ArrayList<Reminder> =arrayListOf()
  var alertDialog: MaterialAlertDialogBuilder? = null
  var builder: MaterialAlertDialogBuilder? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }
  @SuppressLint("NewApi")
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let {
      builder = MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_rounded)
      val inflater = requireActivity().layoutInflater;
      val reminderDialog:View = inflater.inflate(R.layout.fragment_feed_reminder_dialog, null)
      dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      PermissionCheck.readAndWriteCalender(requireContext())
      val feedingStartDate = reminderDialog.findViewById<TextInputEditText>(R.id.feedingStartEditText)
      startingFeedDate = LocalDate.now().toString()
      Log.i("Date now", startingFeedDate)
      feedingStartDate.text = Editable.Factory.getInstance().newEditable(LocalDate.now().format(
        DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString())
      val ageOfAnimal = reminderDialog.findViewById<TextInputEditText>(R.id.ageEditText)
      val defaultAge = 0
      ageOfAnimal.text = Editable.Factory.getInstance().newEditable(defaultAge.toString())
      /*reminderDialog.findViewById<MaterialButton>(R.id.create).isEnabled = false*/

      reminderDialog.findViewById<MaterialButton>(R.id.create).setOnClickListener {
        /*reminderDialog.findViewById<MaterialButton>(R.id.create).isEnabled = true*/
        alertDialog = null
        buyReminderList.clear()
        feedChangeRemindersList.clear()
        if(PermissionCheck.readAndWriteCalender(requireContext())){
          toBuy = reminderDialog.findViewById<MaterialCheckBox>(R.id.toBuy_Check).isChecked
          changeFeed = reminderDialog.findViewById<MaterialCheckBox>(R.id.changeFeed_check).isChecked
          age = reminderDialog.findViewById<TextInputEditText>(R.id.ageEditText).text.toString().toInt()
          val c = stages[stages.size-1].age_days.toString()
          Log.i("Stages.age", c)
          if (age.equals("")) 0 else age
          if(age > 0 && age < stages[stages.size-1].age_days){
            var feedStartDate = LocalDate.parse(startingFeedDate).minusDays(age.toLong())
            Log.i("Feed Started On", feedStartDate.toString())
            val today = LocalDate.now()
            for(stage in stages){
              feedStartDate = feedStartDate.plusDays(stage.age_days.toLong())
              Log.i("Stage Days",stage.age_days.toString())
              Log.i("Stage number added", ChronoUnit.DAYS.between(today, feedStartDate).toString())
              if(ChronoUnit.DAYS.between(today, feedStartDate) > 0){

                /*Reminder for to buy*/
                val  reminderToBuy = if(toBuy) feedStartDate.minusDays(3) else feedStartDate
                Log.i("Reminder date to buy",reminderToBuy.toString())
                createReminder(stage.stage_no.toString(),reminderToBuy, true)

                /*Reminder for change feed*/
                val reminderToChangeFeed = if(changeFeed) feedStartDate.minusDays(1) else feedStartDate
                Log.i("Reminder date to change",reminderToChangeFeed.toString())
                createReminder(stage.stage_no.toString(), reminderToChangeFeed, false)
              }
            }
            if(buyReminderList.isNotEmpty() && feedChangeRemindersList.isNotEmpty()){
              showSuccessAlert()
              //reminderDialog.findViewById<MaterialButton>(R.id.create).isEnabled = true
            }else{
              //reminders not created
              Snackbar.make(reminderDialog,getString(R.string.please_enter_age), Snackbar.LENGTH_LONG).show()
            }
          }else{
            if(age <= 0){
              Snackbar.make(reminderDialog,getString(R.string.please_enter_positive_values), Snackbar.LENGTH_LONG).show()
            } else if(age > stages[stages.size-1].age_days){
              Snackbar.make(reminderDialog,getString(R.string.age_greater), Snackbar.LENGTH_LONG).show()
            }
          }
        }
      }
      reminderDialog.findViewById<CalendarView>(R.id.calenderView).setOnDateChangeListener { v, year, month, dayOfMonth ->
        val calendar = Calendar.getInstance()
        calendar[year, month] = dayOfMonth
        startingFeedDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        Log.i("Date", startingFeedDate)
        feedingStartDate.text = Editable.Factory.getInstance().newEditable(SimpleDateFormat("dd-MM-yyyy").format(calendar.time))
      }
      reminderDialog.findViewById<ImageView>(R.id.cancel).setOnClickListener {
        this.dismiss()
      }
      builder!!.setView(reminderDialog)
      builder!!.create()
    } ?: throw IllegalStateException("Activity cannot be null")
  }
  private fun showSuccessAlert() {
    alertDialog = MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog_rounded)
    val customLayout: View = layoutInflater.inflate(R.layout.success_feed_program_reminder, null)
    alertDialog!!.setView(customLayout)
    val toBuyList = customLayout.findViewById<ListView>(R.id.tobuyList)
    val toChangeFeedList = customLayout.findViewById<ListView>(R.id.toChangeFeedList)
    toBuyList.adapter = ReminderAdapter(
      requireActivity(),
      buyReminderList
    )

    toChangeFeedList.adapter = ReminderAdapter(requireActivity(),
      feedChangeRemindersList
    )
    alertDialog!!.setPositiveButton(
      "OK"
    ) { dialog, which ->
    }
    val alert = alertDialog!!.create()
    alertDialog = null
    if(!alert.isShowing)
      alert.show()
    this.dismiss()
  }
  @RequiresApi(Build.VERSION_CODES.O)
  fun createReminder(stageName:String , reminderDate: LocalDate, isBuy:Boolean){
    val calID: Long = 3
    var description:String
    val startMillis: Long = Calendar.getInstance().run {
      set(reminderDate.year, reminderDate.monthValue.minus(1), reminderDate.dayOfMonth, 18, 10)
      timeInMillis
    }
    Log.i("startMillis",startMillis.toString())
    val endMillis: Long = Calendar.getInstance().run {
      set(reminderDate.year, reminderDate.monthValue.minus(1), reminderDate.dayOfMonth, 18, 40)
      timeInMillis
    }
    Log.i("endMillis",endMillis.toString())
    if(isBuy){
      description = getString(R.string.stage).plus(stageName).plus(" : ").plus(getString(R.string.reminder_buy_description))
    }else{
      description = getString(R.string.stage).plus(stageName).plus(" : ").plus(getString(R.string.reminder_change_description))
    }
    val values = ContentValues().apply {
      put(CalendarContract.Events.DTSTART, startMillis)
      put(CalendarContract.Events.DTEND, endMillis)
      put(CalendarContract.Events.TITLE, getString(R.string.app_name))
      put(CalendarContract.Events.DESCRIPTION, description)
      put(CalendarContract.Events.CALENDAR_ID, calID)
      put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
    }

    val uri: Uri? = requireActivity().contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    val eventID: Long = uri!!.lastPathSegment!!.toLong()
    val reminderValues = ContentValues().apply {
      put(CalendarContract.Reminders.MINUTES, 15)
      put(CalendarContract.Reminders.EVENT_ID, eventID)
      put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
    }
    val reminderUri: Uri? = requireActivity().contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
    Toast.makeText(context, getString(R.string.events_created), Toast.LENGTH_SHORT).show()
    if(isBuy){
      buyReminder = Reminder(getString(R.string.stage).plus(stageName).plus(":"), reminderDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString(), reminderUri.toString())
      buyReminderList.add(buyReminder)
    }else{
      feedChangeReminders = Reminder(getString(R.string.stage).plus(stageName).plus(":"), reminderDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString(), reminderUri.toString())
      feedChangeRemindersList.add(feedChangeReminders)
    }
  }
}