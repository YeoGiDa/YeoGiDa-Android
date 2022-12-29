package com.starters.yeogida.presentation.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.R
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentMyPageBinding
import com.starters.yeogida.network.YeogidaClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPageFragment : Fragment() {

    private val myPageService = YeogidaClient.myPageService
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    private lateinit var binding: FragmentMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.view = this

        initClickListener()
    }

    override fun onResume() {
        super.onResume()
        initMyProfile()

    }

    private fun initMyProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = myPageService.getMyProfile(
                dataStore.userBearerToken.first()
            )

            when (response.code()) {
                200 -> {
                    val data = response.body()?.data
                    data?.let {
                        withContext(Dispatchers.Main) {
                            binding.user = data
                            binding.executePendingBindings()
                        }
                    }
                }
                else -> {

                }
            }
        }
    }

    private fun initClickListener() {
        binding.tbMyPage.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    fun moveToNotification(view: View) {
        findNavController().navigate(R.id.action_mypage_to_alarm)
    }

    fun moveToCommentPlace(view: View) {
        findNavController().navigate(R.id.action_mypage_to_comment)
    }

    fun moveToMyTrip(view: View) {
        findNavController().navigate(R.id.action_mypage_to_my_trip)
    }

    fun moveToSetting(view: View) {
        findNavController().navigate(R.id.action_myPage_to_setting)
    }
}
