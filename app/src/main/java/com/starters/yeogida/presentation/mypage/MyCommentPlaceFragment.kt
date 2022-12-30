package com.starters.yeogida.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.response.common.PlaceData
import com.starters.yeogida.databinding.FragmentMyCommentPlaceBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.around.AroundPlaceAdapter
import com.starters.yeogida.presentation.around.AroundPlaceViewModel
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.place.PlaceActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyCommentPlaceFragment : Fragment() {
    private lateinit var binding: FragmentMyCommentPlaceBinding
    private val viewModel: AroundPlaceViewModel by viewModels()

    private val placeList = mutableListOf<PlaceData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCommentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.view = this
        initClickListener()

        viewModel.openPlaceDetailEvent.observe(viewLifecycleOwner, EventObserver { placeId ->
            Intent(requireContext(), PlaceActivity::class.java).apply {
                putExtra("placeId", placeId)
                putExtra("type", "my_comment")
                startActivity(this)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getMyCommentedPlace()
    }

    private fun initView() {
        with(binding) {
            if (placeList.isEmpty()) {
                layoutMyCommentTop.visibility = View.GONE
                layoutMyCommentEmpty.visibility = View.VISIBLE
            } else {
                layoutMyCommentTop.visibility = View.VISIBLE
                layoutMyCommentEmpty.visibility = View.GONE
            }
        }
    }

    private fun getMyCommentedPlace() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = YeogidaClient.myPageService.getMyCommentedPlace(
                YeogidaApplication.getInstance().getDataStore().userBearerToken.first()
            )

            when (response.code()) {
                200 -> {
                    response.body()?.data?.let { data ->
                        placeList.clear()
                        placeList.addAll(data.placeList)


                    }
                }
                else -> {}
            }

            withContext(Dispatchers.Main) {
                initAdapter()
                initView()
            }
        }
    }

    private fun initAdapter() {
        val aroundPlaceAdapter = AroundPlaceAdapter(viewModel).apply {
            aroundPlaceList.addAll(placeList)
        }
        binding.rvCommentPlace.adapter = aroundPlaceAdapter
        aroundPlaceAdapter.notifyDataSetChanged()
    }

    private fun initClickListener() {
        binding.tbCommentPlace.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun moveToTop(view: View) {
        binding.scrollViewCommentPlace.smoothScrollTo(0, 0)
    }
}
