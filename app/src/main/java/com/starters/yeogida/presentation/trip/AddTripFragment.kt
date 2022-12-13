package com.starters.yeogida.presentation.trip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.starters.yeogida.R
import com.starters.yeogida.databinding.FragmentAddTripBinding
import com.starters.yeogida.util.shortToast

class AddTripFragment : Fragment() {
    private lateinit var binding: FragmentAddTripBinding
    private val pickImage = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_trip, container, false)
        binding.view = this

        initBottomSheet()
        checkMaxLength()

        return binding.root
    }

    private fun initBottomSheet() {
        binding.tvAddTripRegion.setOnClickListener {
            // binding.tvAddTripRegion.setBackgroundResource(R.drawable.rectangle_border_main_blue_10)
            val bottomSheetDialog = RegionBottomSheetFragment {
                binding.tvAddTripRegion.text = it
            }
            bottomSheetDialog.show(parentFragmentManager, bottomSheetDialog.tag)
        }
    }

    private fun checkMaxLength() {
        with(binding) {
            etAddTripName.addTextChangedListener {
                if (etAddTripName.text.length == 10) {
                    requireContext().shortToast("최대 10글자까지 작성 가능합니다.")
                }
            }
            etAddTripSubtitle.addTextChangedListener {
                if (etAddTripSubtitle.text.length == 10) {
                    requireContext().shortToast("최대 10글자까지 작성 가능합니다.")
                }
            }
        }
    }

    var selectedPicUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImage) {
            data?.let {
                selectedPicUri = it.data!!
                binding.ivAddTripPhoto.visibility = View.VISIBLE
                binding.ivAddTripPhotoX.visibility = View.VISIBLE
                binding.tvAddTripPhotoDescription.visibility = View.VISIBLE
                Glide.with(this).load(selectedPicUri).into(binding.ivAddTripPhoto)
            }
        }
    }

    fun moveToAroundPlace(view: View) {
        findNavController().navigate(R.id.action_add_trip_to_around_place)
    }

    fun connectGallery(view: View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, pickImage)
    }

    fun close(view: View) {
        (activity as AddTripActivity).finish()
    }
}
