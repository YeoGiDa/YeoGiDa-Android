package com.starters.yeogida.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.starters.yeogida.R
import com.starters.yeogida.databinding.FragmentHomeBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.mypage.MyPageActivity
import com.starters.yeogida.presentation.trip.AddTripActivity
import com.starters.yeogida.presentation.user.profile.UserProfileActivity
import com.starters.yeogida.util.customEnqueue

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.view = this
        initNetwork()
        setUserProfileClicked()
    }

    private fun initNetwork() {
        val bestTripAdapter = TripAdapter()
        binding.rvBestMonthlyTrip.adapter = bestTripAdapter

        YeogidaClient.homeService.getMonthlyBest().customEnqueue(
            onSuccess = { responseData ->
                responseData.data?.let { data -> bestTripAdapter.tripList.addAll(data.tripList) }
                bestTripAdapter.notifyDataSetChanged()
            }
        )

        val bestTravelerAdapter = BestTravelerAdapter(viewModel)
        binding.rvBestTraveler.adapter = bestTravelerAdapter
        YeogidaClient.homeService.getBestTraveler().customEnqueue(
            onSuccess = { responseData ->
                responseData.data?.let { data -> bestTravelerAdapter.bestTravelerList.addAll(data.memberList) }
                bestTravelerAdapter.notifyDataSetChanged()
            }
        )
    }

    fun moveToMyPage(view: View) {
        val intent = Intent(activity, MyPageActivity::class.java)
        startActivity(intent)
    }

    fun moveToAddTrip(view: View) {
        val intent = Intent(activity, AddTripActivity::class.java)
        startActivity(intent)
    }

    private fun setUserProfileClicked() {
        viewModel.openUserProfileEvent.observe(viewLifecycleOwner, EventObserver { memberId ->
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            intent.putExtra("memberId", memberId)
            startActivity(intent)
        })
    }
}
