package com.ktl.mvvm.ai.client

import com.ktl.mvvm.ai.Utils.LogUtils.Companion.LogInObservable
import com.ktl.mvvm.ai.datamodel.PaginationRequest
import com.ktl.mvvm.ai.datamodel.ServerResponse
import com.ktl.mvvm.ai.service.NetworkService
import io.reactivex.Observable

class PaginationManager(
    private val networkService: NetworkService
) {

    private val allItems = mutableListOf<String>() // 用于保存所有获取的数据

    // 优化后的递归分页请求函数：fetchPageRecursively
    fun fetchPageRecursively(currentPage: Int = 1, pageSize: Int = 20): Observable<List<String>> {
        return networkService.fetchFromServer(PaginationRequest(currentPage, pageSize))
            .flatMap { response ->
                val totalCount = response.totalCount ?: 0
                val items = response.items.orEmpty() // 获取当前页的数据

                allItems.addAll(items) // 将当前页的数据合并到 allItems 列表中
                LogInObservable("Fetched page $currentPage: $items")

                val totalPages = (totalCount + pageSize - 1) / pageSize // 计算总页数（向上取整）

                // 如果当前页小于总页数，则继续递归请求下一页
                if (currentPage < totalPages) {
                    fetchPageRecursively(currentPage + 1, pageSize) // 递归请求下一页
                } else {
                    Observable.just(allItems) // 所有数据请求完成，返回合并后的结果
                }
            }
            .retry(1) { throwable ->
                // 如果请求失败，则重试一次
                LogInObservable("Error fetching page $currentPage: ${throwable.message}. Retrying...")
                true // 返回 true 表示重试
            }
            .onErrorResumeNext { throwable: Throwable ->
                // 如果重试失败或发生其他错误，跳过当前请求，继续执行后续请求
                LogInObservable("Failed to fetch page $currentPage after retry. Skipping...")
                Observable.just(allItems) // 返回已获取的数据（即使失败）
            }
    }
}
