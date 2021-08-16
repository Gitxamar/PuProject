package cargill.com.purina.dashboard.View.FeedProgram

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import cargill.com.purina.R
import kotlinx.android.synthetic.main.fragment_feed_reminder_dialog.*

class FragmentFeedReminderDialog : DialogFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let {
      val builder = AlertDialog.Builder(it)
      val inflater = requireActivity().layoutInflater;
      builder.setView(inflater.inflate(R.layout.fragment_feed_reminder_dialog, null))
      builder.create()
    } ?: throw IllegalStateException("Activity cannot be null")
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    toBuy_Check.setOnClickListener {
      if(toBuy_Check.isChecked){
        toBuy_Check.isChecked = false
        toBuy_Check.setCheckMarkDrawable(R.drawable.ic_outline_check_box)
      }else{
        toBuy_Check.isChecked = true
        toBuy_Check.setCheckMarkDrawable(R.drawable.ic_checkbox_default)
      }
    }
  }
}