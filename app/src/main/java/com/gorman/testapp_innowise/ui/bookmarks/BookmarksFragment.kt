package com.gorman.testapp_innowise.ui.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gorman.testapp_innowise.ui.adapters.BookmarksAdapter
import com.gorman.testapp_innowise.R
import com.gorman.testapp_innowise.databinding.FragmentBookmarksBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarksFragment : Fragment() {

    private var _binding: FragmentBookmarksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bookmarksViewModel: BookmarksViewModel by viewModels()

        _binding = FragmentBookmarksBinding.inflate(inflater, container, false)

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val adapter = BookmarksAdapter()
        binding.bookmarksImgView.adapter = adapter

        binding.bookmarksImgView.layoutManager = layoutManager
        bookmarksViewModel.loadBookmarks()
        adapter.setOnItemClickListener(object : BookmarksAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val bookmark = adapter.getItem(position)
                val bundle = Bundle().apply {
                    putParcelable("bookmark", bookmark)
                }
                findNavController().navigate(R.id.action_BookmarksFragment_to_DetailsFragment, bundle)
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookmarksViewModel.bookmarks.collect { list ->
                    if(list.isNotEmpty())
                    {
                        adapter.setList(list.takeLast(30))
                        binding.exploreButton3.visibility = View.GONE
                        binding.nothingSaveText.visibility = View.GONE
                        binding.bookmarksImgView.visibility = View.VISIBLE
                    }
                    else
                    {
                        binding.exploreButton3.visibility = View.VISIBLE
                        binding.nothingSaveText.visibility = View.VISIBLE
                        binding.bookmarksImgView.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            bookmarksViewModel.bookmarksProgress.collect { progress ->
                binding.progressBookmarkBar.progress = progress
                binding.progressBookmarkBar.visibility =
                    if (progress in 1..99) View.VISIBLE else View.GONE
            }
        }

        binding.exploreButton3.setOnClickListener {
            findNavController().navigate(R.id.action_BookmarksFragment_to_HomeFragment)
        }
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}