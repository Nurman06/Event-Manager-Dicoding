package com.dicoding.restaurantreview.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.restaurantreview.R
import com.dicoding.restaurantreview.data.Resource
import com.dicoding.restaurantreview.data.local.FavoriteEvent
import com.dicoding.restaurantreview.data.remote.response.Event
import com.dicoding.restaurantreview.databinding.ActivityDetailEventBinding
import com.dicoding.restaurantreview.ui.FavoriteEventViewModel
import com.dicoding.restaurantreview.ui.ViewModelFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private val viewModel: DetailEventViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    private val favoriteViewModel: FavoriteEventViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID) ?: return

        viewModel.getDetailEvent(eventId)
        setupObservers()

        binding.fabFavorite.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun setupObservers() {
        viewModel.detailEvent.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.let { event ->
                        displayEventDetails(event)
                        checkFavoriteStatus(event.id.toString())
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(resource.message ?: "Unknown error occurred")
                }
                is Resource.Loading -> showLoading(true)
            }
        }
    }

    private fun displayEventDetails(event: Event) {
        with(binding) {
            tvEventName.text = event.name
            tvEventDate.text = formatDate(event.beginTime, event.endTime)
            tvEventLocation.text = event.cityName
            tvEventDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
            tvEventCategory.text = event.category
            tvEventOwner.text = event.ownerName
            tvEventQuota.text = getString(R.string.event_quota, event.registrants, event.quota, event.quota - event.registrants)

            Glide.with(this@DetailEventActivity)
                .load(event.imageLogo)
                .into(ivEventImage)

            btnOpenLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                startActivity(intent)
            }
        }
    }

    private fun checkFavoriteStatus(eventId: String) {
        favoriteViewModel.getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
            isFavorite = favoriteEvent != null
            updateFavoriteIcon()
        }
    }

    private fun toggleFavorite() {
        val currentEvent = viewModel.detailEvent.value?.data ?: return
        if (isFavorite) {
            favoriteViewModel.delete(FavoriteEvent(currentEvent.id.toString(), currentEvent.name, currentEvent.mediaCover))
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
        } else {
            favoriteViewModel.insert(FavoriteEvent(currentEvent.id.toString(), currentEvent.name, currentEvent.mediaCover))
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
        }
        isFavorite = !isFavorite
        updateFavoriteIcon()
    }

    private fun updateFavoriteIcon() {
        binding.fabFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
        )
    }

    private fun formatDate(beginTime: String, endTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
        try {
            val beginDate = inputFormat.parse(beginTime)
            val endDate = inputFormat.parse(endTime)
            return "${beginDate?.let { outputFormat.format(it) }} - ${endDate?.let { outputFormat.format(it) }}"
        } catch (e: ParseException) {
            e.printStackTrace()
            return "$beginTime - $endTime" // Fallback to original strings if parsing fails
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_EVENT_ID = "extra_event_id"

        fun start(context: Context, eventId: String) {
            val intent = Intent(context, DetailEventActivity::class.java)
            intent.putExtra(EXTRA_EVENT_ID, eventId)
            context.startActivity(intent)
        }
    }
}