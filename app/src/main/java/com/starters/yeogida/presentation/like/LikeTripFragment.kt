package com.starters.yeogida.presentation.like

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.starters.yeogida.data.local.LikeTripData
import com.starters.yeogida.databinding.FragmentLikeTripBinding
import com.starters.yeogida.presentation.around.TripSortBottomSheetFragment
import com.starters.yeogida.presentation.place.PlaceActivity

class LikeTripFragment : Fragment() {
    private lateinit var binding: FragmentLikeTripBinding
    private val viewModel: LikeTripViewModel by viewModels()

    companion object {
        val REGION_CATEGORY_ITEM = "region_category_item"

        @JvmStatic
        fun newInstance(position: Int) =
            LikeTripFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION_CATEGORY_ITEM, position)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLikeTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        val choice = arguments?.getInt(REGION_CATEGORY_ITEM)
        initAdapter(choice)
        initBottomSheet()

        viewModel.openAroundPlaceEvent.observe(viewLifecycleOwner) {
            Log.e("openAroundPlaceEvent", "Event Observed.")
            moveToAroundPlace()
        }

    }

    private fun initAdapter(choice: Int?) {

        choice?.let {
            when (choice) {
                0 -> setLikeAdapter(LikeTripLists.all)
                1 -> setLikeAdapter(LikeTripLists.seoul)
                2 -> setLikeAdapter(LikeTripLists.gyeonggi)
                3 -> setLikeAdapter(LikeTripLists.all)
                4 -> setLikeAdapter(LikeTripLists.seoul)
                5 -> setLikeAdapter(LikeTripLists.gyeonggi)
                6 -> setLikeAdapter(LikeTripLists.all)
                7 -> setLikeAdapter(LikeTripLists.seoul)
                8 -> setLikeAdapter(LikeTripLists.gyeonggi)
                9 -> setLikeAdapter(LikeTripLists.all)
                10 -> setLikeAdapter(LikeTripLists.seoul)
                11 -> setLikeAdapter(LikeTripLists.gyeonggi)
                12 -> setLikeAdapter(LikeTripLists.all)
                13 -> setLikeAdapter(LikeTripLists.seoul)
                14 -> setLikeAdapter(LikeTripLists.gyeonggi)
                15 -> setLikeAdapter(LikeTripLists.all)
                16 -> setLikeAdapter(LikeTripLists.seoul)
                17 -> setLikeAdapter(LikeTripLists.gyeonggi)
                else -> {}
            }
        }
    }

    private fun initBottomSheet() {
        binding.btnLikeTripSort.text = "최신순"

        binding.btnLikeTripSort.setOnClickListener {
            val bottomSheetDialog = TripSortBottomSheetFragment {
                binding.btnLikeTripSort.text = it
            }
            bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
        }
    }

    private fun moveToAroundPlace() {
        val intent = Intent(requireContext(), PlaceActivity::class.java)
        startActivity(intent)
    }

    private fun setLikeAdapter(likeTripList: List<LikeTripData>) {
        binding.rvTrip.adapter = LikeTripAdapter(likeTripList, viewModel)
    }
}