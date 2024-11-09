package com.firman.dicodingevent.ui.ui.home

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.firman.dicodingevent.R
import com.firman.dicodingevent.databinding.FragmentHomeBinding
import com.firman.dicodingevent.data.Result
import com.firman.dicodingevent.ui.HomeEventUpcomingAdapter
import com.firman.dicodingevent.ui.HomeEventFinishedAdapter
import com.firman.dicodingevent.util.NetworkReceiver
import androidx.appcompat.app.AppCompatActivity // Make sure to import AppCompatActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val factory: HomeViewModelFactory by lazy {
        HomeViewModelFactory.getInstance(requireContext())
    }

    private val homeViewModel: HomeViewModel by viewModels { factory }
    private lateinit var upcomingEventAdapter: HomeEventUpcomingAdapter
    private lateinit var finishedEventAdapter: HomeEventFinishedAdapter

    private lateinit var networkReceiver: NetworkReceiver

    companion object {
        const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        upcomingEventAdapter = HomeEventUpcomingAdapter()
        finishedEventAdapter = HomeEventFinishedAdapter()

        setupRecyclerViews()
        observeUpcomingEvents()
        observeFinishedEvents()
        observeLoadingState()

        binding.btnSelengkapnya.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_navigation_finished)
        }

        networkReceiver = NetworkReceiver(requireActivity() as AppCompatActivity)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(networkReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(networkReceiver)
    }

    private fun setupRecyclerViews() {
        binding.carouselRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingEventAdapter
        }

        binding.finishedEventRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = finishedEventAdapter
        }
    }

    private fun observeUpcomingEvents() {
        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    upcomingEventAdapter.updateEvents(result.data)
                }
                is Result.Error -> {
                    Log.d(TAG, "Error fetching")
                }
                Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observeFinishedEvents() {
        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    finishedEventAdapter.updateEvents(result.data)
                }
                is Result.Error -> {
                    Log.d(TAG, "Error fetching")
                }
                Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observeLoadingState() {
        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
