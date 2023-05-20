package bu.ac.kr.anyfeeling.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bu.ac.kr.anyfeeling.MainActivity
import bu.ac.kr.anyfeeling.R
import com.facebook.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause.*
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun handleError(error: Throwable?) {
        val message = when (error?.javaClass?.simpleName) {
            "AccessDenied" -> "접근이 거부 됨(동의 취소)"
            "InvalidClient" -> "유효하지 않은 앱"
            "InvalidGrant" -> "인증 수단이 유효하지 않아 인증할 수 없는 상태"
            "InvalidRequest" -> "요청 파라미터 오류"
            "InvalidScope" -> "유효하지 않은 scope ID"
            "Misconfigured" -> "설정이 올바르지 않음(android key hash)"
            "ServerError" -> "서버 내부 에러"
            "Unauthorized" -> "앱이 요청 권한이 없음"
            else -> "기타 에러"
        }
        showToast(message)
    }

    private fun firebaseAuth(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        showToast("로그인에 성공 하였습니다.")
                        moveMainPage(auth.currentUser)
                    } else {
                        showToast("로그인에 실패 하였습니다.")
                    }
                }
        }
    }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        setContentView(R.layout.activity_login)

        val kakao_login_button = findViewById<ImageButton>(R.id.kakao_login_button)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)



        kakao_login_button.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    handleError(error)
                } else if (token != null) {
                    showToast("로그인에 성공하였습니다.")
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        auth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()

        signUpButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        loginButton.setOnClickListener {
            firebaseAuth(emailEditText.text.toString(), passwordEditText.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        moveMainPage(auth.currentUser)
    }

    private fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
