package com.gorman.testapp_innowise.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.gorman.testapp_innowise.data.api.CollectionItem
import com.gorman.testapp_innowise.data.api.PexelsResponse
import com.gorman.testapp_innowise.data.api.Photo
import com.gorman.testapp_innowise.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Suppress("DEPRECATION")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var adapter = PhotoAdapter()
    private var query: String = "popular"

    @SuppressLint("DiscouragedApi")
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

        binding.picturesView.adapter = adapter

        binding.picturesView.layoutManager = layoutManager
        binding.picturesView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lm = recyclerView.layoutManager as? StaggeredGridLayoutManager ?: return

                val totalItemCount = lm.itemCount
                val visibleItemCount = lm.childCount
                val lastVisibleItemPositions = lm.findLastVisibleItemPositions(null)
                val lastVisibleItemPosition = lastVisibleItemPositions.maxOrNull() ?: 0

                if (visibleItemCount + lastVisibleItemPosition >= totalItemCount && dy > 0) {
                    homeViewModel.loadNextPage(query)
                }
            }
        })

        homeViewModel.loadNextPage(query)
        val title1: MaterialButton = binding.title1
        homeViewModel.loadPhotos(query)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.photos.collect { list ->
                adapter.appendList(list.takeLast(30))
            }
        }

        adapter.setOnItemClickListener(object : PhotoAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val photo = adapter.getItem(position)
//                Toast.makeText(
//                    requireContext(),
//                    "Нажато фото: ${photo.photographer}",
//                    Toast.LENGTH_SHORT
//                ).show()
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
            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(searchQuery: String): Boolean {
                query = searchQuery
                if (!query.isNullOrBlank()) {
                    Toast.makeText(context, "Поиск: $query", Toast.LENGTH_SHORT).show()
                }
                adapter.clearList()
                homeViewModel.loadPhotos(searchQuery)
                homeViewModel.loadNextPage(searchQuery)
                return true
            }
        })

        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}