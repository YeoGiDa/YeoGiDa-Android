package com.starters.yeogida.presentation.user.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.databinding.FragmentUserProfileBinding
import com.starters.yeogida.network.YeogidaClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private val viewModel: UserProfileViewModel by viewModels()
    private val userService = YeogidaClient.userService
    private val dataStore = YeogidaApplication.getInstance().getDataStore()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initUserProfile()
        setOnBackPressed()
    }

    private fun setOnBackPressed() {
        binding.tbUserProfile.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }

    private fun initUserProfile() {
        requireActivity().intent?.extras?.let {

            CoroutineScope(Dispatchers.IO).launch {
                val response = userService.getUserProfile(
                    dataStore.userBearerToken.first(),
                    it.getLong("memberId")
                )

                when (response.code()) {
                    200 -> {
                        withContext(Dispatchers.Main) {
                            binding.userProfile = response.body()?.data
                            binding.executePendingBindings()
                        }
                    }
                    else -> {
                        Log.e("UserProfile", "$response")
                    }
                }
            }
        }
    }
}