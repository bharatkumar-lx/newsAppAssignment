package com.bharat.newsappassignment.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bharat.newsappassignment.NewsApplication
import com.bharat.newsappassignment.model.NewsResponse
import com.bharat.newsappassignment.respoitory.NewsRepository
import com.bharat.newsappassignment.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    app: Application,
    private val newsRepository : NewsRepository
) : AndroidViewModel(app) {

    var breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var breakingNewsResponse: NewsResponse? = null
    var searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    private var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("in")
    }
    fun getBreakingNews(countryCode: String)  = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }


    fun getSearchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{

        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                if(breakingNewsResponse == null){
                    breakingNewsResponse = resultResponse
                }else{
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticle?.addAll(newArticle!!)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }



    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                if(searchNewsResponse == null){
                    searchNewsResponse = resultResponse
                }else{
                    val oldArticle = searchNewsResponse?.articles
                    val newArticle = resultResponse.articles
                    oldArticle?.addAll(newArticle!!)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    private suspend fun safeBreakingNewsCall(countryCode: String){
        try {
            if(hasInternetConnection()){
                val response = newsRepository.getBreakingNews(countryCode,1)
                Log.d("internet",response.toString())
                breakingNews.postValue(handleBreakingNewsResponse(response!!))
            }else{
                Log.d("internet","device don't have internet")
                breakingNews.postValue(Resource.Error("Internet Error"))
            }
        }catch (t: Throwable){
            Log.d("internet"," inside the catch ${t.message}")
            when(t){
                is IOException ->  breakingNews.postValue(Resource.Error("Retrofit Failure"))
                else ->  breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }


    }

    private suspend fun safeSearchNewsCall(query: String){
        try {
            if(hasInternetConnection()){
                val response = newsRepository.searchNews(query,1)
                searchNews.postValue(handleSearchNewsResponse(response!!))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException ->  searchNews.postValue(Resource.Error("Retrofit Failure"))
                else ->  searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }


    }




    private fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) ->true
                capabilities.hasTransport(TRANSPORT_CELLULAR) ->true
                capabilities.hasTransport(TRANSPORT_ETHERNET) ->true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else ->false
                }
            }
        }
        return false
    }


}











