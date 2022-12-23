package com.starters.yeogida.presentation.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.data.local.UserProfileData
import com.starters.yeogida.databinding.FragmentUserProfileBinding

class UserProfileFragment : Fragment() {

    private lateinit var binding: FragmentUserProfileBinding
    private val viewModel: UserProfileViewModel by viewModels()

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
        binding.userProfile = UserProfileData(
            memberId = requireArguments().getLong("memberId"),
            "여기다",
            "https://cdn.pixabay.com/photo/2020/11/28/06/15/cold-5783718_1280.jpg",
            232,
            232,
            true,
            3,
            12
        )

        setOnBackPressed()
    }

    private fun setOnBackPressed() {
        // 뒤로가기 리스너
        binding.tbUserProfile.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 뒤로가기 클릭 시 실행시킬 코드 입력
                findNavController().navigateUp()
            }
        }

        // Android 시스템 뒤로가기를 하였을 때, 콜백 설정
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }
}