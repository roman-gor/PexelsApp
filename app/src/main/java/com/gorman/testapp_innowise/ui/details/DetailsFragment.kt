package com.gorman.testapp_innowise.ui.details

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gorman.testapp_innowise.R
import com.gorman.testapp_innowise.databinding.FragmentDetailsBinding
import com.gorman.testapp_innowise.domain.models.Bookmark
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val detailsViewModel: DetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        val navView = requireActivity().findViewById<FrameLayout>(R.id.bottom_nav_container)
        navView.visibility = View.GONE
        requireActivity().findViewById<View>(R.id.indicator).visibility = View.GONE

        binding.detailImage.visibility = View.VISIBLE
        binding.phName.visibility = View.VISIBLE
        binding.downloadButton.visibility = View.VISIBLE
        binding.likeButton.visibility = View.VISIBLE
        binding.exploreButton.visibility = View.GONE
        binding.textView.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBar = binding.progressBar
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        val animator = ValueAnimator.ofInt(0, 100).apply {
            duration = 1000
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { animation ->
                progressBar.progress = animation.animatedValue as Int
            }
            start()
        }
        val photoJson = arguments?.getString("photoMap")
        val type = object : TypeToken<Map<String, String>>() {}.type
        val photoMap: Map<String, String> =
            if (photoJson != null) Gson().fromJson(photoJson, type)
            else emptyMap()
        val bookmarkId = arguments?.getInt("bookmarkId")
        Log.e("Photo", "Bookmark = $photoMap")
        if (photoMap.isNotEmpty()) {
            showPhoto(photoMap, animator, progressBar)
            detailsViewModel.searchInDBOnce(photoMap["src"]!!) { exists ->
                updateLikeButton(exists)
            }
            setupListenersForPhoto(photoMap)
        } else if (bookmarkId != null) {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailsViewModel.findBookmarkById(bookmarkId)
                    detailsViewModel.bookmark.filterNotNull().collect { bookmark ->
                        Log.e("Bookmark", "Bookmark = $bookmark")
                        showBookmark(bookmark, animator, progressBar)
                        detailsViewModel.searchInDBOnce(bookmark.imageUrl!!) { exists ->
                            updateLikeButton(exists)
                        }
                        setupListenersForBookmark(bookmark)
                    }
                }
            }
        } else {
            progressBar.visibility = View.GONE
            showEmptyState()
        }

        binding.exploreButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateLikeButton(exists: Boolean) {
        if (exists) {
            binding.likeButton.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bookmark_button_active_detail
            )
        } else {
            binding.likeButton.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.bookmark_button_inactive_detail
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListenersForPhoto(photoMap: Map<String, String>) {
        binding.downloadButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.downloadButton.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.download_button_click
                    )
                    true
                }
                MotionEvent.ACTION_UP -> {
                    binding.downloadButton.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.download_button
                    )
                    handleDownload(photoMap["src"]!!)
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.downloadButton.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.download_button
                    )
                    true
                }
                else -> false
            }
        }

        binding.likeButton.setOnClickListener {
            handleLikeButtonClick(photoMap["src"]!!, photoMap["photographer"]!!)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListenersForBookmark(bookmark: Bookmark) {
        binding.downloadButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.downloadButton.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.download_button_click
                    )
                    true
                }
                MotionEvent.ACTION_UP -> {
                    binding.downloadButton.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.download_button
                    )
                    handleDownload(bookmark.imageUrl!!)
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    binding.downloadButton.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.download_button
                    )
                    true
                }
                else -> false
            }
        }

        binding.likeButton.setOnClickListener {
            handleLikeButtonClick(bookmark.imageUrl!!, bookmark.phName!!)
        }
    }

    private fun handleDownload(url: String) {
        val request = DownloadManager.Request(url.toUri())
            .setTitle(ContextCompat.getString(requireContext(), R.string.DownloadStart))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "image_${System.currentTimeMillis()}.jpg")
        Toast.makeText(requireContext(), R.string.DownloadStart, Toast.LENGTH_SHORT).show()
        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    private fun handleLikeButtonClick(imageUrl: String, photographerName: String) {
        detailsViewModel.searchInDBOnce(imageUrl) { exists ->
            if (exists) {
                detailsViewModel.deleteByUrl(imageUrl)
                updateLikeButton(false)
                Toast.makeText(requireContext(), R.string.BookmarkDelete, Toast.LENGTH_SHORT).show()
            } else {
                detailsViewModel.addBookmark(imageUrl, photographerName)
                updateLikeButton(true)
                Toast.makeText(requireContext(), R.string.BookmarkAdd, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPhoto(photoMap: Map<String, String>, animator: ValueAnimator, progressBar: ProgressBar) {
        Glide.with(requireContext())
            .load(photoMap["src"])
            .transform(RoundedCorners(46))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>?, isFirstResource: Boolean
                ): Boolean {
                    animator.cancel()
                    progressBar.visibility = View.GONE
                    showEmptyState()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: Target<Drawable>?, dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    animator.cancel()
                    progressBar.progress = 100
                    progressBar.visibility = View.GONE
                    showContentViews()
                    return false
                }
            })
            .into(binding.detailImage)
        binding.phName.text = photoMap["photographer"]
    }

    private fun showBookmark(bookmark: Bookmark?, animator: ValueAnimator, progressBar: ProgressBar) {
        Glide.with(requireContext())
            .load(bookmark?.imageUrl)
            .transform(RoundedCorners(46))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>?, isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    animator.cancel()
                    showEmptyState()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: Target<Drawable>?, dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    progressBar.progress = 100
                    animator.cancel()
                    showContentViews()
                    return false
                }
            })
            .into(binding.detailImage)
        binding.phName.text = bookmark?.phName
    }

    private fun showEmptyState() {
        binding.detailImage.visibility = View.GONE
        binding.phName.visibility = View.GONE
        binding.downloadButton.visibility = View.GONE
        binding.likeButton.visibility = View.GONE
        binding.exploreButton.visibility = View.VISIBLE
        binding.textView.visibility = View.VISIBLE
    }

    private fun showContentViews() {
        binding.detailImage.visibility = View.VISIBLE
        binding.phName.visibility = View.VISIBLE
        binding.downloadButton.visibility = View.VISIBLE
        binding.likeButton.visibility = View.VISIBLE
        binding.exploreButton.visibility = View.GONE
        binding.textView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<FrameLayout>(R.id.bottom_nav_container).visibility = View.VISIBLE
        requireActivity().findViewById<View>(R.id.indicator).visibility = View.VISIBLE

    }
}