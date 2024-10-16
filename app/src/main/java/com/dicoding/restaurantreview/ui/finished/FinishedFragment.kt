package com.dicoding.restaurantreview.ui.finished

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.restaurantreview.data.Resource
import com.dicoding.restaurantreview.databinding.FragmentFinishedBinding
import com.dicoding.restaurantreview.ui.ViewModelFactory
import com.dicoding.restaurantreview.ui.EventAdapter
import com.dicoding.restaurantreview.ui.detail.DetailEventActivity

class FinishedFragment : Fragment() {
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding should not be accessed before onCreateView or after onDestroyView.")

    private val viewModel: FinishedViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val eventAdapter = EventAdapter { eventId ->
        DetailEventActivity.start(requireContext(), eventId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchView.text.toString()
            performSearch(query)
            true
        }
    }

    private fun observeViewModel() {
        viewModel.events.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoading(false)
                    eventAdapter.submitList(resource.data)
                }
                is Resource.Loading -> showLoading(true)
                is Resource.Error -> {
                    showLoading(false)
                    showError(resource.message ?: "Unknown error occurred")
                }
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isNotBlank()) {
            viewModel.searchEvents(query)
        } else {
            viewModel.getEvents()
        }
        binding.searchView.hide()
        hideKeyboard()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}