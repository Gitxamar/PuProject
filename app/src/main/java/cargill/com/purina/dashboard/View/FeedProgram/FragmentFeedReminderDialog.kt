package cargill.com.purina.dashboard.View.FeedProgram

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import cargill.com.purina.R

class FragmentFeedReminderDialog : DialogFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  /*override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_feed_reminder_dialog, container, false)
  }*/

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return return activity?.let {
      val builder = AlertDialog.Builder(it)
      // Get the layout inflater
      val inflater = requireActivity().layoutInflater;

      // Inflate and set the layout for the dialog
      // Pass null as the parent view because its going in the dialog layout
      builder.setView(inflater.inflate(R.layout.fragment_feed_reminder_dialog, null))
      // Add action buttons
      builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")
  }
}