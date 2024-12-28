package com.ktl.mvvm.ai.service

import com.ktl.mvvm.ai.datamodel.PaginationRequest
import com.ktl.mvvm.ai.datamodel.ServerResponse
import io.reactivex.Observable
import kotlin.random.Random

class NetworkService {

    // 模拟从服务端获取分页数据
    fun fetchFromServer(request: PaginationRequest): Observable<ServerResponse> {
        return Observable.create { emitter ->
            val totalData = List(100) { "Item ${it + 1}" } // 假设服务端有100条数据
            val totalCount = totalData.size
            val startIndex = (request.page - 1) * request.pageSize
            val endIndex = (startIndex + request.pageSize).coerceAtMost(totalCount)

            // 当页码超出总数据范围时，返回空数据
            if (startIndex >= totalCount) {
                emitter.onNext(ServerResponse(totalCount, emptyList()))
                emitter.onComplete()
                return@create
            }

            // 模拟成功响应
//            if (Random.nextDouble() < 0.8) { // 模拟80%成功率
                val items = totalData.subList(startIndex, endIndex)
                emitter.onNext(ServerResponse(totalCount, items))
                emitter.onComplete()
//            } else {
//                // 模拟失败
//                emitter.onError(Throwable("Simulated server failure"))
//            }
        }
    }
}
