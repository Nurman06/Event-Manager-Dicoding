package com.dicoding.restaurantreview.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.restaurantreview.data.remote.response.ListEventsItem
import com.dicoding.restaurantreview.databinding.FragmentFavoriteBinding
import com.dicoding.restaurantreview.ui.EventAdapter
import com.dicoding.restaurantreview.ui.ViewModelFactory
import com.dicoding.restaurantreview.ui.detail.DetailEventActivity

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding should not be accessed before onCreateView or after onDestroyView.")

    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var adapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter { eventId ->
            DetailEventActivity.start(requireContext(), eventId)
        }
        binding.rvFavoriteEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            this.adapter = this@FavoriteFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { favoriteEvents ->
            val items = favoriteEvents.map { favoriteEvent ->
                ListEventsItem(
                    id = favoriteEvent.id.toInt(),
                    name = favoriteEvent.name,
                    imageLogo = favoriteEvent.mediaCover
                )
            }
            adapter.submitList(items)
            showEmptyState(items.isEmpty())
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvFavoriteEvents.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}