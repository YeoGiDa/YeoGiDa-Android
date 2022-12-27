package com.starters.yeogida.presentation.around

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.response.trip.TripLikeUserData
import com.starters.yeogida.databinding.FragmentTripLikeUserBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.mypage.MyPageActivity
import com.starters.yeogida.presentation.user.profile.UserProfileActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripLikeUserFragment : Fragment() {

    private val viewModel: AroundPlaceViewModel by viewModels()
    private lateinit var binding: FragmentTripLikeUserBinding

    private val userList = mutableListOf<TripLikeUserData>()

    private val tripService = YeogidaClient.tripService

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

        initAdapter(userList)
        setOnBackPressed()
        initData()
        setOpenUserProfile()
    }

    private fun initData() {
        val tripId = requireArguments().getLong("tripId")
        CoroutineScope(Dispatchers.IO).launch {
            val response = tripService.getTripLikeUserList(
                tripId
            )

            when (response.code()) {
                200 -> {
                    val memberList = response.body()!!.data!!.memberList
                    userList.clear()
                    userList.addAll(memberList)
                    withContext(Dispatchers.Main) {
                        binding.rvTripLikeUser.adapter?.notifyDataSetChanged()
                    }
                }
                else -> {
                    Log.e("TripLikeUserList", "$response")
                }
            }

        }
    }

    private fun initAdapter(userList: MutableList<TripLikeUserData>) {
        TripLikeUserListAdapter(userList, viewModel).apply {
            binding.rvTripLikeUser.adapter = this
        }
    }

    private fun setOpenUserProfile() {
        viewModel.openUserProfileEvent.observe(viewLifecycleOwner, EventObserver { memberId ->
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
        })
    }

    private fun setOnBackPressed() {
        binding.tbTripLikeUserList.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}