package com.bharat.newsappassignment.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bharat.newsappassignment.adapter.NewsAdapter
import com.bharat.newsappassignment.databinding.ActivityHomePageBinding
import com.bharat.newsappassignment.respoitory.NewsRepository
import com.bharat.newsappassignment.utils.Resource
import com.bharat.newsappassignment.viewmodels.NewsViewModel
import com.bharat.newsappassignment.viewmodels.NewsViewModelProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomePage : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var auth: FirebaseAuth


    var isLoading = false
    var isScrolling = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val newsRepository = NewsRepository()
        auth = Firebase.auth
        val newsViewModelProviderFactory = NewsViewModelProviderFactory(application, newsRepository)
        viewModel =
            ViewModelProvider(this, newsViewModelProviderFactory).get(NewsViewModel::class.java)
        setupRecyclerView()
        searchNews()
        breakingNews()
        binding.logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun breakingNews() {
        viewModel.breakingNews.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles?.toList())
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Log.d("Homepage",it)
                        Toast.makeText(this, "An Error occured: $it", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        }
    }



    private fun searchNews(){

        var job : Job? = null
        binding.searchBar.addTextChangedListener{ editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(1000L)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.getSearchNews(editable.toString())
                    }
                }
            }
        }
        viewModel.searchNews.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles?.toList())
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Toast.makeText(this, "An Error occured: $it", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        }
    }



private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.searchRecyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
           // addOnScrollListener(this@HomePage.scrollListener)
        }
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

    }
}