package com.starters.yeogida.presentation.user

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.kakao.sdk.user.UserApiClient
import com.starters.yeogida.GlideApp
import com.starters.yeogida.R
import com.starters.yeogida.databinding.ActivityJoinBinding
import com.starters.yeogida.network.ApiClient
import com.starters.yeogida.util.shortToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)

        setNicknameFilter()
        setTextChangedListener()
        setNickname()
        setProfileImage()

        binding.btnPlus.setOnClickListener {

        }

        binding.btnJoinSubmit.setOnClickListener {
            if ( hasSameNickname() ) {
                with(binding) {
                    tvProfileDescription.visibility = View.VISIBLE // 중복된다는 문구 보이기.
                    etNick.text.clear()
                    etNick.setBackgroundResource(R.drawable.rectangle_border_red_10)
                    etNick.requestFocus()
                }
                keyboardShow()
                shortToast("닉네임이 중복됩니다.")
            } else {
                intent.getStringExtra("email")?.let {
                    Log.d("JoinActivity", "이메일 : $it")
                    CoroutineScope(Dispatchers.IO).launch {
                        /*val service = ApiClient.userService
                        service.addUser()*/
                    }
                }

                intent.getStringExtra("userNum")?.let {
                    Log.d("JoinActivity", "회원번호 : $it")
                }
            }
        }
    }

    private fun hasSameNickname() : Boolean{
        var hasSameNick = false
        // TODO. 서버 중복확인
        return hasSameNick
    }

    private fun setTextChangedListener() {
        binding.etNick.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 완료 버튼 Enabled 이벤트
                binding.btnJoinSubmit.isEnabled = !binding.etNick.text.isNullOrBlank()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun setNickname() {
        intent.getStringExtra("nickname")?.let {
            Log.d("JoinActivity", "닉네임 : $it")
            with(binding.etNick) {
                requestFocus()  // Focusing 하기
                setText(it)
                setSelection(length())  // 커서 끝으로
            }

            keyboardShow()  // 키보드도 올라오게
        }
    }

    private fun setProfileImage() {
        intent.getStringExtra("profileImageUrl")?.let { // 프로필 이미지 URL
            Log.d("JoinActivity", "사진 url : $it")
            GlideApp.with(this)
                .load(it)
                .circleCrop()
                .into(binding.ivProfile)
        }
    }

    private fun setNicknameFilter() {
        /** 문자열필터(EditText Filter) */
        var filterAlphaNumSpace = InputFilter { source, start, end, dest, dstart, dend ->
            /*
                [요약 설명]
                1. 정규식 패턴 ^[a-z] : 영어 소문자 허용
                2. 정규식 패턴 ^[A-Z] : 영어 대문자 허용
                3. 정규식 패턴 ^[ㄱ-ㅣ가-힣] : 한글 허용
                4. 정규식 패턴 ^[0-9] : 숫자 허용
                5. 정규식 패턴 ^[ ] or ^[\\s] : 공백 허용
            */
            val ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9\\s]+$")
            if (!ps.matcher(source).matches()) {
                ""
            } else source
        }
        var lengthFilter = InputFilter.LengthFilter(8)

        binding.etNick.filters = arrayOf(filterAlphaNumSpace, lengthFilter)
    }

    private fun keyboardShow() {
        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.ime())
    }
}