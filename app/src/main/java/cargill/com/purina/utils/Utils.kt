package cargill.com.purina.utils

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
  }
}