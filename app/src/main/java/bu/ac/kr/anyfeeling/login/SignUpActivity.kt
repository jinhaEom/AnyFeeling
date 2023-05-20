package bu.ac.kr.anyfeeling.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bu.ac.kr.anyfeeling.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth


        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val passwordCheck = findViewById<EditText>(R.id.passwordCheck)
        val signupOk = findViewById<Button>(R.id.signupOk)




        signupOk.setOnClickListener {
            createAccount(
                email.text.toString(),
                password.text.toString(),
                passwordCheck.text.toString()
            )
        }

        cancel()


    }


    private fun createAccount(email: String, password: String, passwordCheck: String) {
        val signupOk = findViewById<Button>(R.id.signupOk)
        if (email.isNotEmpty() && password.isNotEmpty()) {
            if (password != passwordCheck) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()

            } else {
                auth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            signupOk.isEnabled = true
                            Toast.makeText(
                                this,
                                "회원가입에 성공했습니다!",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        }
                    }
            }


        } else {
            Toast.makeText(this, "이메일이 이미 존재하거나 회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()

        }


    }


    private fun cancel() {
        val signupCancel = findViewById<Button>(R.id.signupCancel)
        signupCancel.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }


}