package com.dicoding.restaurantreview.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.restaurantreview.data.Resource
import com.dicoding.restaurantreview.databinding.FragmentHomeBinding
import com.dicoding.restaurantreview.ui.ViewModelFactory
import com.dicoding.restaurantreview.ui.EventAdapter
import com.dicoding.restaurantreview.ui.detail.DetailEventActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding should not be accessed before onCreateView or after onDestroyView.")

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var upcomingAdapter: HomeAdapter
    private lateinit var finishedAdapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        upcomingAdapter = HomeAdapter { eventId ->
            navigateToDetailEvent(eventId)
        }
        finishedAdapter = EventAdapter { eventId ->
            navigateToDetailEvent(eventId)
        }

        binding.rvUpcomingEvents.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        binding.rvFinishedEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = finishedAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoading(false)
                    upcomingAdapter.submitList(resource.data)
                }
                is Resource.Loading -> showLoading(true)
                is Resource.Error -> {
                    showLoading(false)
                    showError(resource.message ?: "Unknown error occurred")
                }
            }
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoading(false)
                    finishedAdapter.submitList(resource.data)
                }
                is Resource.Loading -> showLoading(true)
                is Resource.Error -> {
                    showLoading(false)
                    showError(resource.message ?: "Unknown error occurred")
                }
            }
        }
    }

    private fun navigateToDetailEvent(eventId: String) {
        DetailEventActivity.start(requireContext(), eventId)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}