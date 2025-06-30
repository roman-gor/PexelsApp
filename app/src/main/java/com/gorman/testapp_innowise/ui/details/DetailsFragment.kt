package com.gorman.testapp_innowise.ui.details

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.gorman.testapp_innowise.R
import com.gorman.testapp_innowise.data.models.BookmarkImage
import com.gorman.testapp_innowise.data.models.Photo
import com.gorman.testapp_innowise.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val detailsViewModel: DetailsViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val photo = arguments?.getParcelable<Photo>("photo", Photo::class.java)
        val bookmark = arguments?.getParcelable<BookmarkImage>("bookmark", BookmarkImage::class.java)


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
        if (photo != null) {
            Glide.with(requireContext())
                .load(photo.src.large)
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
            binding.phName.text = photo.photographer
        }
        else if (bookmark != null) {
            Glide.with(requireContext())
                .load(bookmark.imageUrl)
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
            binding.phName.text = bookmark.phName
        }
        else {
            progressBar.visibility = View.GONE
            showEmptyState()
        }

        binding.exploreButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        lifecycleScope.launch {
            if (photo != null)
            {
                val exists = detailsViewModel.searchInDBOnce(photo!!.src.large)
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
            else
            {
                val exists = detailsViewModel.searchInDBOnce(bookmark!!.imageUrl)
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
        }


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
                    if (photo != null)
                    {
                        val url = photo.src.large
                        val request = DownloadManager.Request(url.toUri())
                            .setTitle(ContextCompat.getString(requireContext(),R.string.DownloadStart))
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "image_${System.currentTimeMillis()}.jpg")
                        Toast.makeText(requireContext(), R.string.DownloadStart, Toast.LENGTH_SHORT).show()
                        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        downloadManager.enqueue(request)
                    }
                    else
                    {
                        val url = bookmark!!.imageUrl
                        val request = DownloadManager.Request(url.toUri())
                            .setTitle(ContextCompat.getString(requireContext(),R.string.DownloadStart))
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true)
                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "image_${System.currentTimeMillis()}.jpg")
                        Toast.makeText(requireContext(), R.string.DownloadStart, Toast.LENGTH_SHORT).show()
                        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        downloadManager.enqueue(request)
                    }
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
            if (photo != null) {
                lifecycleScope.launch {
                    val exists = detailsViewModel.searchInDBOnce(photo.src.large)
                    if (exists) {
                        detailsViewModel.deleteByUrl(photo.src.large)
                        binding.likeButton.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.bookmark_button_inactive_detail
                        )
                        Toast.makeText(requireContext(), R.string.BookmarkDelete, Toast.LENGTH_SHORT).show()
                    } else {
                        detailsViewModel.addBookmark(photo.src.large, photo.photographer)
                        binding.likeButton.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.bookmark_button_active_detail
                        )
                        Toast.makeText(requireContext(), R.string.BookmarkAdd, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else
            {
                lifecycleScope.launch {
                    val exists = detailsViewModel.searchInDBOnce(bookmark!!.imageUrl)
                    if (exists) {
                        detailsViewModel.deleteByUrl(bookmark.imageUrl)
                        binding.likeButton.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.bookmark_button_inactive_detail
                        )
                        Toast.makeText(requireContext(), R.string.BookmarkDelete, Toast.LENGTH_SHORT).show()
                    } else {
                        detailsViewModel.addBookmark(bookmark.imageUrl, bookmark.phName)
                        binding.likeButton.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.bookmark_button_active_detail
                        )
                        Toast.makeText(requireContext(), R.string.BookmarkAdd, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<FrameLayout>(R.id.bottom_nav_container).visibility = View.VISIBLE
        requireActivity().findViewById<View>(R.id.indicator).visibility = View.VISIBLE

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
}