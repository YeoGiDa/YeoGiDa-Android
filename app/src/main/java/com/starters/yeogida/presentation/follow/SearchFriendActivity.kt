package com.starters.yeogida.presentation.follow

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.starters.yeogida.YeogidaApplication
import com.starters.yeogida.data.remote.response.trip.TripLikeUserData
import com.starters.yeogida.databinding.ActivitySearchFriendBinding
import com.starters.yeogida.network.YeogidaClient
import com.starters.yeogida.presentation.around.AroundPlaceViewModel
import com.starters.yeogida.presentation.around.TripLikeUserListAdapter
import com.starters.yeogida.presentation.common.EventObserver
import com.starters.yeogida.presentation.mypage.MyPageActivity
import com.starters.yeogida.presentation.user.profile.UserProfileActivity
import com.starters.yeogida.util.customEnqueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class SearchFriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchFriendBinding
    private val viewModel: AroundPlaceViewModel by viewModels()
    private val userList = mutableListOf<TripLikeUserData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchFriendBinding.inflate(layoutInflater)

        initAdapter(userList)
        getSearchResult()
        openUSerProfile()
        initClickListener()

        setContentView(binding.root)
    }

    private fun initAdapter(userList: MutableList<TripLikeUserData>) {
        TripLikeUserListAdapter(userList, viewModel).apply {
            binding.rvSearchResult.adapter = this
        }
    }

    private fun getSearchResult() {
        binding.etSearchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = binding.etSearchUser.text.toString()
                if (isValidSearchText(searchText)) {
                    YeogidaClient.followService.getAllUser(
                        searchText
                    ).customEnqueue(
                        onSuccess = {
                            val memberList = it.data!!.memberList
                            userList.clear()
                            userList.addAll(memberList)
                            binding.rvSearchResult.adapter?.notifyDataSetChanged()
                        }
                    )
                }

                if (searchText == "") {
                    userList.clear()
                    binding.rvSearchResult.adapter?.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun isValidSearchText(searchText: String): Boolean {
        val regex = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{1,8}\$"
        val pattern = Pattern.compile(regex)

        val matcher = pattern.matcher(searchText)

        return matcher.matches()
    }

    private fun openUSerProfile() {
        viewModel.openUserProfileEvent.observe(
            this,
            EventObserver { memberId ->
                CoroutineScope(Dispatchers.IO).launch {
                    val myMemberId = YeogidaApplication.getInstance().getDataStore().memberId.first()

                    if (myMemberId != memberId) {
                        withContext(Dispatchers.Main) {
                            Intent(this@SearchFriendActivity, UserProfileActivity::class.java).apply {
                                putExtra("memberId", memberId)
                                startActivity(this)
                            }
                        }
                    } else {
                        Intent(this@SearchFriendActivity, MyPageActivity::class.java).apply {
                            putExtra("memberId", memberId)
                            startActivity(this)
                        }
                    }
                }
            }
        )
    }

    private fun initClickListener() {
        binding.tbSearchUser.setNavigationOnClickListener {
            finish()
        }
    }
}
