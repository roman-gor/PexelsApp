package com.gorman.testapp_innowise.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.button.MaterialButton
import com.gorman.testapp_innowise.PhotoAdapter
import com.gorman.testapp_innowise.R
import com.gorman.testapp_innowise.data.models.CollectionItem
import com.gorman.testapp_innowise.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var adapter = PhotoAdapter()
    private var query: String = ""
    private var collectionList: List<CollectionItem>? = null
    private var isNew = false
    private var shouldHandleQueryChange = true
    private var searchJob: Job? = null

    @SuppressLint("DiscouragedApi", "ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val searchView = binding.searchView
        val title1: MaterialButton = binding.title1
        val titlesList: List<MaterialButton> = listOf(
            binding.title1,
            binding.title2,
            binding.title3,
            binding.title4,
            binding.title5,
            binding.title6,
            binding.title7
        )

        showContextView()

        binding.picturesView.adapter = adapter

        binding.picturesView.layoutManager = layoutManager
        binding.picturesView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                showContextView()
                val lm = recyclerView.layoutManager as? StaggeredGridLayoutManager ?: return

                val totalItemCount = lm.itemCount
                val visibleItemCount = lm.childCount
                val lastVisibleItemPositions = lm.findLastVisibleItemPositions(null)
                val lastVisibleItemPosition = lastVisibleItemPositions.maxOrNull() ?: 0

                if (visibleItemCount + lastVisibleItemPosition >= totalItemCount && dy > 0) {
                    if (query.isEmpty())
                        homeViewModel.loadNextCuratedPhotos()
                    else
                        homeViewModel.loadNextPage(query)
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.isEmpty.collect { isEmpty ->
                if (isEmpty) {
                    showEmptyResult()
                } else {
                    showContextView()
                }
            }
        }

        binding.exploreButton.setOnClickListener {
            homeViewModel.loadCuratedPhotos()
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                homeViewModel.photos.collect { list ->
                    adapter.appendList(list.takeLast(30))
                }
            }
            showContextView()
            query = ""
            searchView.setQuery("", false)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.loadResult.collect { result ->
                when (result) {
                    is LoadResult.Success -> {
                        showContextView()
                    }
                    is LoadResult.Empty -> {

                    }
                    is LoadResult.Error -> {
                        showNetworkError()
                        when (result.exception) {
                            is java.net.UnknownHostException,
                            is java.net.SocketTimeoutException,
                            is java.io.IOException -> {
                                Toast.makeText(requireContext(), "Нет подключения к сети", Toast.LENGTH_SHORT).show()
                            }
                            is retrofit2.HttpException -> {
                                if (result.exception.code() == 429)
                                    Toast.makeText(requireContext(), "Слишком много запросов. Подожди немного", Toast.LENGTH_SHORT).show()
                                else
                                    Toast.makeText(requireContext(), "Ошибка сервера: ${result.exception.code()}", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(requireContext(), "Неизвестная ошибка", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        binding.tryAgainButton.setOnClickListener {
            showContextView()
            homeViewModel.loadFeatureCollections()
            if (query.isEmpty()) {
                homeViewModel.loadCuratedPhotos()
            } else {
                homeViewModel.loadPhotos(query)
            }
        }

        val collectionSelected = homeViewModel.selectedFeaturedButton
        if (collectionSelected in titlesList.indices)
        {
            val selectedButton = titlesList[collectionSelected]
            selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            selectedButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.choose)
        }

        if (homeViewModel.collections.value.isEmpty()) {
            homeViewModel.loadFeatureCollections()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.collections.collect { list ->
                collectionList = list.toList()
                if (!collectionList.isNullOrEmpty() && collectionList!!.size > 1) {
                    Log.d("Collections", collectionList!![1].title)
                    title1.text = collectionList!![1].title
                    for (i in 0 until 7) {
                        titlesList[i].text = collectionList!![i].title
                    }
                } else {
                    Log.d("Collections", "Список пустой или недостаточно элементов")
                }
            }
        }
        for ((index, title) in titlesList.withIndex())
        {
            title.setOnClickListener {
                for (btn in titlesList)
                {
                    btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    btn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.main)
                }
                shouldHandleQueryChange = false
                searchView.setQuery(title.text.toString(), true)
                homeViewModel.selectedFeaturedButton = index
                title.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                title.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.choose)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.progress.collect { progress ->
                binding.progressBar.progress = progress
                binding.progressBar.visibility = if (progress in 1..99) View.VISIBLE else View.GONE
            }
        }

        if (query.isEmpty())
        {
            showContextView()
            homeViewModel.loadCuratedPhotos()
            if (binding.progressBar.progress != 100)
                binding.progressBar.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                homeViewModel.photos.collect { list ->
                    adapter.appendList(list.takeLast(30))
                }
            }
        }
        else
        {
            showContextView()
            homeViewModel.loadPhotos(query)
            if (binding.progressBar.progress != 100)
                binding.progressBar.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                homeViewModel.photos.collect { list ->
                    adapter.appendList(list.takeLast(30))
                }
            }
        }
        adapter.setOnItemClickListener(object : PhotoAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val photo = adapter.getItem(position)
                val bundle = Bundle().apply {
                    putParcelable("photo", photo)
                }
                findNavController().navigate(R.id.action_HomeFragment_to_DetailsFragment, bundle)
            }
        })
        searchView.background = ContextCompat.getDrawable(requireContext(), R.drawable.searchview_background)
        searchView.clearFocus()

        val textId = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val underlineId = searchView.context.resources.getIdentifier("android:id/search_plate", null, null)

        val searchText = searchView.findViewById<TextView>(textId)
        val mulishFont = ResourcesCompat.getFont(requireContext(), R.font.mulish)
        searchText.typeface = mulishFont
        searchText.textSize = 14.0F
        val underlineView = searchView.findViewById<View>(underlineId)
        underlineView.background = null

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(p0: String): Boolean {
                if (!shouldHandleQueryChange) {
                    shouldHandleQueryChange = true
                    return true
                }
                if (p0.isBlank() && isNew) {
                    showContextView()
                    query = ""
                    isNew = false
                    for (btn in titlesList)
                    {
                        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        btn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.main)
                    }
                    adapter.clearList()
                    Log.d("Query", query)
                    homeViewModel.loadCuratedPhotos()
                }
                else if (p0 != query)
                {
                    showContextView()
                    searchJob?.cancel()
                    searchJob = viewLifecycleOwner.lifecycleScope.launch {
                        delay(400L)
                        for (btn in titlesList) {
                            btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            btn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.main)
                        }
                        query = p0
                        adapter.clearList()
                        homeViewModel.loadPhotos(query)
                    }
                }
                return true
            }

            override fun onQueryTextSubmit(searchQuery: String): Boolean {
                showContextView()
                query = searchQuery.trim().orEmpty()
                adapter.clearList()
                isNew = true
                homeViewModel.loadPhotos(searchQuery)
                return true
            }
        })
        searchView.clearFocus()

        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showContextView()
    {
        binding.picturesView.visibility = View.VISIBLE
        binding.exploreButton.visibility = View.GONE
        binding.textView.visibility = View.GONE
        binding.noNetworkImage.visibility = View.GONE
        binding.tryAgainButton.visibility = View.GONE
    }

    private fun showNetworkError()
    {
        binding.picturesView.visibility = View.GONE
        binding.exploreButton.visibility = View.GONE
        binding.textView.visibility = View.GONE
        binding.noNetworkImage.visibility = View.VISIBLE
        binding.tryAgainButton.visibility = View.VISIBLE
    }

    private fun showEmptyResult()
    {
        binding.picturesView.visibility = View.GONE
        binding.exploreButton.visibility = View.VISIBLE
        binding.textView.visibility = View.VISIBLE
        binding.noNetworkImage.visibility = View.GONE
        binding.tryAgainButton.visibility = View.GONE
    }
}