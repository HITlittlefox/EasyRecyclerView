package com.ktl.mvvm.act

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ada.popuphelper.CenteredPopupHelper
import com.chenliang.library.base.MyBaseActivity
import com.ktl.mvvm.R
import com.ktl.mvvm.ai.Utils.LogUtils.Companion.LogInObservable
import com.ktl.mvvm.ai.client.PaginationManager
import com.ktl.mvvm.ai.service.NetworkService
import com.ktl.mvvm.customedsnackbar.TopSnackbar
import com.ktl.mvvm.databinding.*
import com.ktl.mvvm.model.Product
import com.ktl.mvvm.viewmodel.PruductListViewModel
import com.ktl.mvvm.viewmodel.n
import com.ktl.mvvm.viewmodel.obs
import com.ktl.mvvm.viewmodel.y
import kotlinx.android.synthetic.main.activity_recycleview.*
import kotlin.time.Duration

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
            Toast.makeText(this,"what button",Toast.LENGTH_SHORT).show()


            // 此处应该放在repo或者viewmodel
            val networkService = NetworkService()  // 这里是模拟的网络服务
            val paginationManager = PaginationManager(networkService)

            paginationManager.fetchPageRecursively(currentPage = 1, pageSize = 20)
                .subscribe(
                    { allItems ->
                        LogInObservable("All items fetched successfully: $allItems")
                    },
                    { error ->
                        LogInObservable("Error: ${error.message}")
                    }
                )
        }

        initButtonSnackbar()

        initButtonPopupwindow()
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
            it.n {refresh.stop()}
        }
    }


    public fun onItemClick(view: View, p: Product) {
        Toast.makeText(this, "我被点击了", Toast.LENGTH_LONG).show()
    }


}