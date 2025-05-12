package com.ada.accessibilitydemo

import android.graphics.Color
import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

object AccessibleTextHelper {

    fun setFullClickableText(
        textView: TextView, text: String, onClick: () -> Unit
    ) {
        textView.text = text
        textView.setTextColor(Color.BLUE)
        textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        textView.isClickable = true
        textView.isFocusable = true
        textView.setOnClickListener { onClick() }

        ViewCompat.setAccessibilityDelegate(textView, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View, info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.className = android.widget.Button::class.java.name
                info.isScreenReaderFocusable = true
                info.isClickable = true
                info.contentDescription = text
                Log.e("ADA", info.contentDescription as String)
            }
        })
    }

    fun setPartialClickableText(
        textView: TextView, fullText: String, clickablePart: String, onClick: () -> Unit
    ) {
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf(clickablePart)
        val end = start + clickablePart.length

        if (start >= 0 && end <= fullText.length) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onClick()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.BLUE
                    ds.isUnderlineText = true
                }
            }
            spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT

        // 避免整个 TextView 误提示“点击”
        textView.isFocusable = false
        textView.isClickable = false
        textView.isLongClickable = false

        // ✅ 不设置 className 为 Button，只设置无障碍可聚焦
        ViewCompat.setAccessibilityDelegate(textView, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View, info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.className = android.widget.TextView::class.java.name
                info.isScreenReaderFocusable = true
                info.setContentInvalid(false)
            }
        })
    }

    fun setWebLinkText(
        textView: TextView, fullText: String, linkText: String, url: String
    ) {
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf(linkText)
        val end = start + linkText.length

        if (start >= 0 && end <= fullText.length) {
            val urlSpan = object : URLSpan(url) {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.BLUE
                    ds.isUnderlineText = true
                }
            }
            spannable.setSpan(urlSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = Color.TRANSPARENT

        ViewCompat.setAccessibilityDelegate(textView, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View, info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.className = android.widget.TextView::class.java.name
                info.isScreenReaderFocusable = true
                info.setContentInvalid(false)
            }
        })
    }
}
