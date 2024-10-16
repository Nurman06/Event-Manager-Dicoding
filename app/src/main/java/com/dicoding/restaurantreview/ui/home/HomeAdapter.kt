package com.dicoding.restaurantreview.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.restaurantreview.data.remote.response.ListEventsItem
import com.dicoding.restaurantreview.databinding.ItemEventHorizontalBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<ListEventsItem, HomeAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        holder.itemView.setOnClickListener {
            event.id?.toString()?.let { id -> onItemClick(id) }
        }
    }

    class EventViewHolder(private val binding: ItemEventHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            with(binding) {
                tvEventName.text = event.name
                tvEventLocation.text = event.cityName
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
                try {
                    val date = event.beginTime?.let { inputFormat.parse(it) }
                    tvEventDate.text = date?.let { outputFormat.format(it) }
                } catch (e: Exception) {
                    tvEventDate.text = event.beginTime
                }

                Glide.with(itemView.context)
                    .load(event.imageLogo)
                    .into(ivEventImage)
            }
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<ListEventsItem>() {
        override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
            return oldItem == newItem
        }
    }
}