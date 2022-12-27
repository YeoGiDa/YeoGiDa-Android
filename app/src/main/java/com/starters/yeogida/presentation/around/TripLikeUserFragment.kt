package com.starters.yeogida.presentation.around

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.data.remote.response.trip.TripLikeUserData
import com.starters.yeogida.databinding.FragmentTripLikeUserBinding

class TripLikeUserFragment : Fragment() {

    private val viewModel: AroundPlaceViewModel by viewModels()
    private lateinit var binding: FragmentTripLikeUserBinding

    private val userList = mutableListOf<TripLikeUserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTripLikeUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        setOnBackPressed()
        initAdapter(userList)
    }

    private fun initData() {
        val tripId = requireArguments().getLong("tripId")

    }

    private fun initAdapter(userList: MutableList<TripLikeUserData>) {
        TripLikeUserListAdapter(userList, viewModel).apply {
            binding.rvTripLikeUser.adapter = this
        }
    }

    private fun setOnBackPressed() {
        binding.tbTripLikeUserList.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}