package bu.ac.kr.anyfeeling.login

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "a7b005b86f00564d69b03fdd8f697751")
    }
}