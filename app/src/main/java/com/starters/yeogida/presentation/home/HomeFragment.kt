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
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentHomeBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.mypage.MyPageActivity
import com.starters.yeogida.presentation.place.PlaceActivity
import com.starters.yeogida.presentation.trip.AddTripActivity
import com.starters.yeogida.presentation.user.profile.UserProfileActivity
import com.starters.yeogida.util.customEnqueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        setUserProfileClicked()
    }

    override fun onResume() {
        super.onResume()
        initNetwork()
    }

    private fun initNetwork() {
        val bestTripAdapter = TripAdapter { tripId: Long ->
            moveToTrip(tripId)
        }
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

        val recentTripAdapter = TripAdapter { tripId: Long ->
            moveToTrip(tripId)
        }
        binding.rvRecentTrip.adapter = recentTripAdapter
        YeogidaClient.homeService.getRecentTrip().customEnqueue(
            onSuccess = {
                if (it.code == 200) {
                    binding.tvHomeRecentTripEmpty.text = ""
                    it.data?.let { data -> recentTripAdapter.tripList.addAll(data.tripList) }
                    recentTripAdapter.notifyDataSetChanged()
                }
            },
            onError = {
                if (it.message == "No one Follow Error!") {
                    binding.tvHomeRecentTripEmpty.text = "아직 팔로잉한 사람이 없어요\n사람들을 팔로잉 해보세요!"
                } else if (it.message == "Trip NotFound Error") {
                    binding.tvHomeRecentTripEmpty.text = "팔로잉들이 아직 게시글을 올리지 않았어요!"
                }
            }
        )
    }

    private fun moveToTrip(tripId: Long) {
        val intent = Intent(requireContext(), PlaceActivity::class.java)
        intent.putExtra("tripId", tripId)
        startActivity(intent)
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
        viewModel.openUserProfileEvent.observe(
            viewLifecycleOwner,
            EventObserver { memberId ->
                CoroutineScope(Dispatchers.IO).launch {
                    val myMemberId = YeogidaApplication.getInstance().getDataStore().memberId.first()

                    if (myMemberId != memberId) {
                        withContext(Dispatchers.Main) {
                            Intent(requireContext(), UserProfileActivity::class.java).apply {
                                putExtra("memberId", memberId)
                                startActivity(this)
                            }
                        }
                    } else {
                        Intent(requireContext(), MyPageActivity::class.java).apply {
                            putExtra("memberId", memberId)
                            startActivity(this)
                        }
                    }
                }
            }
        )
    }

    fun moveToMoreTraveler(view: View) {
        startActivity(Intent(requireContext(), MoreTravelerActivity::class.java))
    }

    fun moveToMoreMonthlyTrip(view: View) {
        startActivity(Intent(requireContext(), MoreMonthlyTripActivity::class.java))
    }

    fun moveToMoreRecentTrip(view: View) {
        startActivity(Intent(requireContext(), MoreRecentTripActivity::class.java))
    }
}
