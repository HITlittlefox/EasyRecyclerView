package com.ada.popuphelper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.*
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.StyleRes
import com.ktl.mvvm.R
import kotlin.math.max

class CenteredPopupHelper(
    private val context: Context,
    @StyleRes private val animationStyle: Int = R.style.PopupAnimationStyle
) {
    fun showPopup(
        anchor: View, titleText: String, subtitleText: String
    ) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_content, null)

        popupView.findViewById<TextView>(R.id.title).text = titleText
        popupView.findViewById<TextView>(R.id.subtitle).text = subtitleText

        // 设置最大高度（300dp 转 px）
        val maxHeight = (300 * context.resources.displayMetrics.density).toInt()

        // 先测量 View
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST)
        popupView.measure(widthMeasureSpec, heightMeasureSpec)

        // 获取 ScrollView 并限制最大高度
        val scrollView = popupView.findViewById<ScrollView>(R.id.scrollContainer)
        val actualHeight = scrollView.measuredHeight
        if (actualHeight > maxHeight) {
            scrollView.layoutParams = scrollView.layoutParams.apply {
                height = maxHeight
            }
            scrollView.isVerticalScrollBarEnabled = true // 可选
        }

        val popupWindow = PopupWindow(
            popupView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isTouchable = true
            isFocusable = true
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            animationStyle = this@CenteredPopupHelper.animationStyle // 禁用动画，避免初次闪烁
        }

        // 添加阴影（仅 Android 5.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupView.elevation = 16f
        }

        popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0)
    }

    companion object {
        fun attachToLongClick(
            targetView: View,
            context: Context,
            title: String,
            subtitle: String,
            @StyleRes animationStyle: Int = R.style.PopupAnimationStyle
        ) {
            val popup = CenteredPopupHelper(context, animationStyle)
            targetView.setOnLongClickListener {
                popup.showPopup(it, title, subtitle)
                true
            }
        }
    }
}
