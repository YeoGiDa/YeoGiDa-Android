package com.starters.yeogida.presentation.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.starters.yeogida.MainActivity
import com.starters.yeogida.R
import com.starters.yeogida.data.remote.response.BaseResponse
import com.starters.yeogida.data.remote.response.CheckMemberResponseData
import com.starters.yeogida.databinding.ActivityLoginBinding
import com.starters.yeogida.network.ApiClient
import com.starters.yeogida.util.customEnqueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var userPrefEmail: String = ""

    private var isLogined = false

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("카카오 로그인", "로그인 실패 $error")
        } else if (token != null) {
            Log.e("카카오 로그인", "로그인 성공 ${token.accessToken}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        /*lifecycleScope.launch {
            GlobalApplication.getInstance().getDataStore().userEmail
                .collect{

                    userPrefEmail = it
                    Log.d("collect" ,it)
                    Log.d("userPrefEmail" ,userPrefEmail)
                }
        }*/

        /*UserApiClient.instance.unlink { error ->
            if (error != null) {
                Toast.makeText(this, "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
            }
        }*/

        /*UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("카카오 로그인", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            }
            else {
                Log.i("카카오 로그인", "로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }*/

        binding.layoutLoginKakao.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                loginWithKakaoTalk()
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
            }
        }
    }

    private fun loginWithKakaoTalk() {

        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            if (error != null) { }
            else if (token != null) {
                Log.e("카카오 로그인", "accessToken : ${token.accessToken}")
                Log.e("카카오 로그인", "refreshToken : ${token.refreshToken}")

                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        Log.e("카카오 로그인", "토큰 정보 보기 실패", error)
                    } else if (tokenInfo != null) {
                        Log.i(
                            "카카오 로그인", "토큰 정보 보기 성공" +
                                    "\n회원번호: ${tokenInfo.id}" +
                                    "\n만료시간: ${tokenInfo.expiresIn} 초" +
                                    "\n앱 ID : ${tokenInfo.appId}"
                        )

                        UserApiClient.instance.me { user, error ->
                            user?.kakaoAccount?.let { userAccount ->
                                // 이메일
                                // Log.d("카카오 로그인", "email = ${userAccount.email}")

                                // TODO. 이메일로 가입되어있는 회원인지 확인
                                val service = ApiClient.userService

                                CoroutineScope(Dispatchers.IO).launch {
                                    Log.d("카카오 로그인", "email = ${userAccount.email}")
                                    /*
                                    val response = service.checkMember(userAccount.email.toString())

                                    if (response.isSuccessful) {
                                        val result = response.body()
                                        Log.d("회원가입 성공", "$result")
                                    } else {
                                        Log.d("회원가입 실패", response.code().toString())
                                    }*/

                                    /*withContext(Dispatchers.Main) {
                                        if(response.data?.isMember == true){
                                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                            finish()
                                        } else {
                                            // 신규회원이면 Join으로
                                            val intent = Intent(this@LoginActivity, JoinActivity::class.java)

                                            intent.putExtra("email", userAccount.email)     // 이메일
                                            intent.putExtra("userNum", tokenInfo.id.toString()) // 회원번호

                                            // 닉네임
                                            userAccount.profile?.nickname?.let { nickname ->
                                                intent.putExtra("nickname", nickname)
                                            }

                                            // 프로필 사진
                                            userAccount.profile?.profileImageUrl?.let { profileImageUrl ->
                                                intent.putExtra("profileImageUrl", profileImageUrl)
                                            }

                                            startActivity(intent)
                                        }

                                    }*/
                                }
                            }
                        }
                    }
                }
            }
        }
    }

/*private fun isJoinedMember(email: String): Boolean {
    var userEmail : String? = null

    // TODO. 서버 이메일 전달해서 가입되어있는 사용자인지 확인하기
    UserApiClient.instance.me { user, error ->
        userEmail = user?.kakaoAccount?.email
        Log.d("isJoinedMember", "userEmail = $userEmail , paramEmail = $email")
        Log.d("isJoinedMember", "${userEmail == email}")
    }

    return if(userEmail != null) userEmail == email else false
}*/
}