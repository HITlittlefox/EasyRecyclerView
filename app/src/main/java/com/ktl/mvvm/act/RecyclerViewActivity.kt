package com.ktl.mvvm.act

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.ada.accessibilitydemo.AccessibleTextHelper
import com.ada.accessibilitydemo.AppTargetActivity
import com.ada.accessibilitydemo.CustomClickableSpan
import com.ada.accessibilitydemo.WebViewActivity
import com.ada.popuphelper.CenteredPopupHelper
import com.chenliang.library.base.MyBaseActivity
import com.ktl.mvvm.R
import com.ktl.mvvm.ai.Utils.LogUtils.Companion.LogInObservable
import com.ktl.mvvm.ai.client.PaginationManager
import com.ktl.mvvm.ai.service.NetworkService
import com.ktl.mvvm.customedsnackbar.TopSnackbar
import com.ktl.mvvm.databinding.ActivityRecycleviewBinding
import com.ktl.mvvm.databinding.ItemProduct0Binding
import com.ktl.mvvm.databinding.ItemProduct1Binding
import com.ktl.mvvm.databinding.ItemProduct2Binding
import com.ktl.mvvm.model.Product
import com.ktl.mvvm.viewmodel.PruductListViewModel
import com.ktl.mvvm.viewmodel.n
import com.ktl.mvvm.viewmodel.obs
import com.ktl.mvvm.viewmodel.y
import kotlinx.android.synthetic.main.activity_recycleview.refresh

class RecyclerViewActivity : MyBaseActivity<ActivityRecycleviewBinding, PruductListViewModel>() {


    override fun layoutId(): Int {
        return R.layout.activity_recycleview;
    }


    override fun initViewModelClass(): Class<PruductListViewModel> {
        return PruductListViewModel::class.java
    }

    /**
     * model必须继承RecyclerViewData ，itemType对应布局类型，如下0，1，2，对应的布局依次是
     * 如果后台给的type类型不是itemType，请使用@SerializedName自定义名字为itemType，itemType为int类型数据
     */
    override fun initCreate() {

        refresh.bindTypeToItemView(0, R.layout.item_product_0)
        refresh.bindTypeToItemView(1, R.layout.item_product_1)
        refresh.bindTypeToItemView(2, R.layout.item_product_2)
        refresh.bindData<Product> {
            if (it.itemType == 0) (it.binding as ItemProduct0Binding).product = it
            if (it.itemType == 1) (it.binding as ItemProduct1Binding).product = it
            if (it.itemType == 2) (it.binding as ItemProduct2Binding).product = it
        }

        refresh.loadData { httpGetData() }

    }

    override fun initView() {
        binding.button1.setOnClickListener {
            Toast.makeText(this, "what button", Toast.LENGTH_SHORT).show()


            // 此处应该放在repo或者viewmodel
            val networkService = NetworkService()  // 这里是模拟的网络服务
            val paginationManager = PaginationManager(networkService)

            paginationManager.fetchPageRecursively(currentPage = 1, pageSize = 20)
                .subscribe({ allItems ->
                    LogInObservable("All items fetched successfully: $allItems")
                }, { error ->
                    LogInObservable("Error: ${error.message}")
                })
        }

        initButtonSnackbar()

        initButtonPopupwindow()

        initA11y()
    }

    private fun initA11y() {
//        setupFullClickableText(binding.tvFullClick)
//        setupPartialClickableText(binding.tvPartialClick)
//        setupWebLinkText(binding.tvWebviewClick)
        AccessibleTextHelper.setFullClickableText(binding.tvFullClick, "跳转 App 内部") {
            startActivity(Intent(this, AppTargetActivity::class.java))
        }
        AccessibleTextHelper.setPartialClickableText(
            binding.tvPartialClick, fullText = "请点击这里查看详情", clickablePart = "点击这里"
        ) {
            startActivity(Intent(this, AppTargetActivity::class.java))
        }
        AccessibleTextHelper.setWebLinkText(
            binding.tvWebviewClick,
            fullText = "点击这里访问网页",
            linkText = "点击这里访问网页",
            url = "https://example.com"
        )

    }


    private fun setupFullClickableText(tv: TextView) {
        tv.apply {
            text = "跳转 App 内部（整段）"
            setTextColor(Color.BLUE)
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            isClickable = true
            isFocusable = true
            setOnClickListener {
                startActivity(Intent(this@RecyclerViewActivity, AppTargetActivity::class.java))
            }

            // 设置 TalkBack className 为 Button
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            ViewCompat.setAccessibilityDelegate(tv, object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View, info: AccessibilityNodeInfoCompat
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info.className = Button::class.java.name
                }
            })
        }
    }

    private fun setupPartialClickableText(tv: TextView) {
        val fullText = "部分内容点击这里跳转 App 内部"
        val spannable = SpannableString(fullText)

        val clickableText = "点击这里"
        val start = fullText.indexOf(clickableText)
        val end = start + clickableText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@RecyclerViewActivity, AppTargetActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = true
            }
        }

        spannable.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv.text = spannable
        tv.movementMethod = LinkMovementMethod.getInstance()
        tv.highlightColor = Color.TRANSPARENT

        // ✅ 不设置 TextView 的点击事件
        tv.isFocusable = false
        tv.isClickable = false
        tv.isLongClickable = false

        // ✅ 不设置 className 为 Button，只设置无障碍可聚焦
        ViewCompat.setAccessibilityDelegate(tv, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                info.className = TextView::class.java.name
                info.isScreenReaderFocusable = true
//                info.setContentInvalid(false) // 显式标识内容有效
            }
        })
    }

    private fun setupWebLinkText(tv: TextView) {
        val html = "<a href='https://example.com'>跳转网页</a>"
        val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)

        val spannable = SpannableString(spanned)
        val spans = spannable.getSpans(0, spannable.length, URLSpan::class.java)
        for (urlSpan in spans) {
            val spanStart = spannable.getSpanStart(urlSpan)
            val spanEnd = spannable.getSpanEnd(urlSpan)
            spannable.removeSpan(urlSpan)

            spannable.setSpan(
                CustomClickableSpan {
                    startActivity(Intent(this, WebViewActivity::class.java).apply {
                        putExtra("url", urlSpan.url)
                    })
                },
                spanStart,
                spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        tv.text = spannable
        tv.movementMethod = LinkMovementMethod.getInstance()
        tv.highlightColor = Color.TRANSPARENT
    }

    private fun initButtonPopupwindow() {
        CenteredPopupHelper.attachToLongClick(
            targetView = binding.buttonPopupwindow,
            context = this,
            title = "1主标题：测试一下2",
            subtitle = "3副标题：这里是非常非常长的内容，可以上下滚动的哦4"
        )

        CenteredPopupHelper.attachToLongClick(
            targetView = binding.buttonPopupwindowLong,
            context = this,
            title = "1主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下主标题：测试一下2",
            subtitle = "3副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦副标题：这里是非常非常长的内容，可以上下滚动的哦4"
        )

        CenteredPopupHelper.attachToLongClick(
            targetView = binding.buttonPopupwindowShort,
            context = this,
            title = "12",
            subtitle = "34"
        )

    }

    private fun initButtonSnackbar() {
        binding.buttonSnackbar.setOnClickListener {
            // 显示在屏幕顶部
            TopSnackbar.show(this, "This is a title", R.drawable.ic_launcher_foreground, "Action") {
                Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()
            }
        }
        binding.buttonSnackbarTop.setOnClickListener {
            TopSnackbar.showAboveView(
                this,
                binding.buttonSnackbarTop,
                "This is a title",
                R.drawable.ic_launcher_foreground,
                "Action"
            ) {
                Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()
            }
        }
//        binding.button1.setOnClickListener(::onSnackbarButtonClick)
    }

    // 方法定义
    private fun onSnackbarButtonClick(view: View) {
        // 处理点击事件
        println("Button clicked!")
    }

    private fun httpGetData() {
        viewModel.getProducts(refresh.pageIndex, refresh.pageSize)
        viewModel.ps.obs(this) {
            it.y { refresh.addData(it.data) }
            it.n { refresh.stop() }
        }
    }


    public fun onItemClick(view: View, p: Product) {
        Toast.makeText(this, "我被点击了", Toast.LENGTH_LONG).show()
    }


}