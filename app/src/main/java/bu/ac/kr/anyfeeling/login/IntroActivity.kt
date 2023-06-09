package bu.ac.kr.anyfeeling.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import bu.ac.kr.anyfeeling.MainActivity
import bu.ac.kr.anyfeeling.R

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_layout)

        var handler = Handler()
        handler.postDelayed({
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}