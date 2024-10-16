package com.dicoding.restaurantreview.ui.upcoming

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.restaurantreview.data.Resource
import com.dicoding.restaurantreview.databinding.FragmentUpcomingBinding
import com.dicoding.restaurantreview.ui.ViewModelFactory
import com.dicoding.restaurantreview.ui.EventAdapter
import com.dicoding.restaurantreview.ui.detail.DetailEventActivity

class UpcomingFragment : Fragment() {
    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding should not be accessed before onCreateView or after onDestroyView.")

    private val viewModel: UpcomingViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val eventAdapter = EventAdapter { eventId ->
        Log.d(TAG, "Event clicked: $eventId")  // Log saat event diklik
        DetailEventActivity.start(requireContext(), eventId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d(TAG, "onCreateView called")
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated called")

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")
        binding.rvEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun setupSearchView() {
        Log.d(TAG, "Setting up SearchView")
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchView.text.toString()
            Log.d(TAG, "Search query: $query")  // Log query pencarian
            performSearch(query)
            true
        }
    }

    private fun observeViewModel() {
        Log.d(TAG, "Observing ViewModel data")
        viewModel.events.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    Log.d(TAG, "Data successfully fetched")
                    showLoading(false)
                    eventAdapter.submitList(resource.data)
                }
                is Resource.Loading -> {
                    Log.d(TAG, "Loading data")
                    showLoading(true)
                }
                is Resource.Error -> {
                    Log.e(TAG, "Error fetching data: ${resource.message}")  // Log error
                    showLoading(false)
                    showError(resource.message ?: "Unknown error occurred")
                }
            }
        }
    }

    private fun performSearch(query: String) {
        Log.d(TAG, "Performing search with query: $query")
        if (query.isNotBlank()) {
            viewModel.searchEvents(query)
        } else {
            viewModel.getEvents()
        }
        binding.searchView.hide()
        hideKeyboard()
    }

    private fun showLoading(isLoading: Boolean) {
        Log.d(TAG, "Show loading: $isLoading")
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Log.e(TAG, "Show error: $message")
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        Log.d(TAG, "Hiding keyboard")
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView called")
        _binding = null
    }

    companion object {
        private const val TAG = "UpcomingFragment"  // Tambahkan TAG untuk logging
    }
}