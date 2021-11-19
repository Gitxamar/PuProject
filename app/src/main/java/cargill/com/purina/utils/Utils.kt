package cargill.com.purina.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.io.File
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class Utils {
  companion object{
    fun getYouTubeVideoIdFromUrl(inurl: String): String?{
      if (inurl.toLowerCase().contains("youtu.be")){
        return inurl.substring(inurl.lastIndexOf("/") +1)
      }
      val pattern = "(?<=watch\\?v=|/videos/embed\\/)[^#\\&\\?]*"
      val compiledPattern = Pattern.compile(pattern)
      val matcher = compiledPattern.matcher(inurl)
      return if (matcher.find()){
        matcher.group()
      }else null
    }
    fun roundOffDecimal(number: Double): Double? {
      val df = DecimalFormat("#.##")
      df.roundingMode = RoundingMode.CEILING
      return df.format(number).toDouble()
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
      val formatter = SimpleDateFormat(format, locale)
      return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
      return Calendar.getInstance().time
    }

    fun hideSoftKeyBoard(context: Context, view: View) {
      try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
      } catch (e: Exception) {
        // TODO: handle exception
        e.printStackTrace()
      }

    }
    fun getFileName(path:String):String {
      val file = File(path)
      return file.name.toString()
    }
  }
}