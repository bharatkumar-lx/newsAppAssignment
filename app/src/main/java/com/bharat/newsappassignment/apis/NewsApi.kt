package com.bharat.newsappassignment.apis
import com.bharat.newsappassignment.model.NewsResponse
import com.bharat.newsappassignment.utils.Constants.Companion
import com.bharat.newsappassignment.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String ="in",
        @Query("page")
        pageNumber:Int =1,
        @Query("apiKey")
        apiKey :String = API_KEY
    ) :Response<NewsResponse>


    @GET("v2/top-headlines")
    suspend fun searchNew(
        @Query("q")
        searchQuery: String ,
        @Query("page")
        pageNumber:Int =1,
        @Query("apiKey")
        apiKey :String = API_KEY
    ) :Response<NewsResponse>

}