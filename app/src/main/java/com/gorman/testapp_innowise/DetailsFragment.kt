package com.gorman.testapp_innowise.ui

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gorman.testapp_innowise.R
import com.gorman.testapp_innowise.data.api.Photo
import com.gorman.testapp_innowise.databinding.FragmentDetailsBinding
import com.gorman.testapp_innowise.databinding.FragmentHomeBinding
import androidx.core.net.toUri

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val photo = arguments?.getParcelable<Photo>("photo")
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navView.visibility = View.GONE
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
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
        val photo = arguments?.getParcelable<Photo>("photo")
        if (photo != null)
        {
            Glide.with(requireContext())
                .load(photo.src.large)
                .transform(RoundedCorners(46))
                .into(binding.detailImage)
        }
        else
        {
            binding.detailImage.visibility = View.GONE
            binding.phName.visibility = View.GONE
            binding.downloadButton.visibility = View.GONE
            binding.likeButton.visibility = View.GONE
            binding.exploreButton.visibility = View.VISIBLE
            binding.textView.visibility = View.VISIBLE
        }
        binding.phName.text = photo?.photographer
        binding.exploreButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.downloadButton.setOnClickListener {
            val url = photo?.src?.large // или любой URL картинки
            val request = DownloadManager.Request(url?.toUri())
                .setTitle("Скачивание изображения")
                .setDescription("Скачивается картинка")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "image_${System.currentTimeMillis()}.jpg")
            val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val navView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        navView.visibility = View.VISIBLE
    }
}