package com.ktl.mvvm.customedsnackbar

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.ktl.mvvm.R

object TopSnackbar {

    // 定义回调接口
    interface SnackbarCallback {
        fun onShow()   // Snackbar 显示时调用
        fun onDismiss() // Snackbar 隐藏时调用
    }

    // 全局回调变量
    private var snackbarCallback: SnackbarCallback? = null

    // 设置全局回调
    fun setSnackbarCallback(callback: SnackbarCallback) {
        snackbarCallback = callback
    }

    // 显示自定义顶部Snackbar，触发回调
    private fun showSnackbar(activity: Activity, customView: View, targetView: View? = null) {
        val rootView = activity.findViewById<FrameLayout>(android.R.id.content)

        // 将自定义视图添加到根布局中
        rootView.addView(customView)

        // 执行回调：Snackbar 显示时
        snackbarCallback?.onShow()

        // 计算目标View的位置，如果指定了目标视图
        targetView?.let {
            val location = IntArray(2)
            it.getLocationOnScreen(location)
            val viewTop = location[1]
            (customView.layoutParams as FrameLayout.LayoutParams).apply {
                gravity = Gravity.TOP
                topMargin = viewTop - customView.height
            }
        } ?: run {
            // 默认显示在屏幕顶部
            (customView.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.TOP
        }

        // 设置定时移除Snackbar的效果，并在移除时触发回调
        customView.postDelayed({
            rootView.removeView(customView)
            // 执行回调：Snackbar 隐藏时
            snackbarCallback?.onDismiss()
        }, 3000) // 3秒后自动移除
    }

    // 显示顶部Snackbar的方法，不需要传入回调
    fun show(
        activity: Activity,
        title: String,
        imageResId: Int,
        buttonText: String,
        onButtonClick: () -> Unit
    ) {
        val customView = createCustomView(activity, title, imageResId, buttonText, onButtonClick)
        showSnackbar(activity, customView)
    }

    // 显示顶部Snackbar在指定view的上方的方法，不需要传入回调
    fun showAboveView(
        activity: Activity,
        anchorView: View,
        title: String,
        imageResId: Int,
        buttonText: String,
        onButtonClick: () -> Unit
    ) {
        val customView = createCustomView(activity, title, imageResId, buttonText, onButtonClick)
        showSnackbar(activity, customView, anchorView)
    }

    // 创建自定义Snackbar布局
    private fun createCustomView(
        activity: Activity,
        title: String,
        imageResId: Int,
        buttonText: String,
        onButtonClick: () -> Unit
    ): View {
        val customView =
            LayoutInflater.from(activity).inflate(R.layout.layout_top_snackbar, null, false)

        // 设置数据
        customView.findViewById<ImageView>(R.id.snackbar_image).setImageResource(imageResId)
        customView.findViewById<TextView>(R.id.snackbar_title).text = title
        customView.findViewById<Button>(R.id.snackbar_button).apply {
            text = buttonText
            setOnClickListener { onButtonClick() }
        }
        customView.findViewById<ImageView>(R.id.snackbar_close).setOnClickListener {
            // 关闭Snackbar
            (customView.parent as FrameLayout).removeView(customView)
            // 执行回调：Snackbar 隐藏时
            snackbarCallback?.onDismiss()
        }

        // 设置布局参数，使其适应内容
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, // 宽度填充父布局
            FrameLayout.LayoutParams.WRAP_CONTENT  // 高度适应内容
        )
        customView.layoutParams = params

        return customView
    }
}
