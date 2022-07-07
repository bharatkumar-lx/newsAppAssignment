package com.bharat.newsappassignment.respoitory

import com.bharat.newsappassignment.apis.RetrofitInstance

class NewsRepository {
    suspend fun getBreakingNews(countryCode: String,pageNumber: Int) =
        RetrofitInstance.api?.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api?.searchNew(searchQuery,pageNumber)

}