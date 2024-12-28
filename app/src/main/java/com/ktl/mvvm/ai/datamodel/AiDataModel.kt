package com.ktl.mvvm.ai.datamodel

data class PaginationRequest(
    val page: Int, // 当前页数
    val pageSize: Int // 每页获取的条目数
)

data class ServerResponse(
    val totalCount: Int?, // 云端拥有的总字符串数量
    val items: List<String>? // 当前页的字符串列表
)
