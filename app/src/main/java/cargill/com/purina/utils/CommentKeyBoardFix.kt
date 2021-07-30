package cargill.com.purina.utils

import android.R
import android.widget.FrameLayout

import android.app.Activity
import android.graphics.Rect
import android.view.View


class CommentKeyBoardFix(activity: Activity) {
  private val mChildOfContent: View
  private var usableHeightPrevious = 0
  private val frameLayoutParams: FrameLayout.LayoutParams
  private val contentAreaOfWindowBounds: Rect = Rect()
  private fun possiblyResizeChildOfContent() {
    val usableHeightNow = computeUsableHeight()
    if (usableHeightNow != usableHeightPrevious) {
      val heightDifference = 0
      if (heightDifference > usableHeightNow / 4) {
        // keyboard probably just became visible
        frameLayoutParams.height = usableHeightNow - heightDifference
      } else {
        // keyboard probably just became hidden
        frameLayoutParams.height = usableHeightNow
      }
      mChildOfContent.layout(
        contentAreaOfWindowBounds.left,
        contentAreaOfWindowBounds.top,
        contentAreaOfWindowBounds.right,
        contentAreaOfWindowBounds.bottom
      )
      mChildOfContent.requestLayout()
      usableHeightPrevious = usableHeightNow
    }
  }

  private fun computeUsableHeight(): Int {
    mChildOfContent.getWindowVisibleDisplayFrame(contentAreaOfWindowBounds)
    return contentAreaOfWindowBounds.height()
  }

  init {
    val content = activity.findViewById<FrameLayout>(R.id.content)
    mChildOfContent = content.getChildAt(0)
    mChildOfContent.getViewTreeObserver()
      .addOnGlobalLayoutListener { possiblyResizeChildOfContent() }
    frameLayoutParams = mChildOfContent.getLayoutParams() as FrameLayout.LayoutParams
  }
}