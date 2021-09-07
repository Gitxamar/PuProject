package cargill.com.purina.utils

import java.math.RoundingMode
import java.text.DecimalFormat
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
  }
}