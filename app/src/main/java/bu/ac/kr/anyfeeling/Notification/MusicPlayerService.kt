package bu.ac.kr.anyfeeling.Notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MusicPlayerService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            Actions.START_FOREGROUND -> {
                Log.e(TAG, "start Foreground 인텐트 받음")
                startForegroundService()

            }
            Actions.STOP_FOREGROUND ->{
                Log.e(TAG, "stop Foreground 인텐트 받음")
                stopForegroundService()
            }
            Actions.PREV -> Log.e(TAG , "Clicked = 이전")
            Actions.PLAY -> Log.e(TAG , "Clicked = 재생")
            Actions.NEXT -> Log.e(TAG , "Clicked = 다음")

        }

        return START_STICKY
    }

    private fun startForegroundService() {
        val notification = MusicNotification.createNotification(this)
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val TAG = "[MusicPlayerService]"
        const val NOTIFICATION_ID = 20
    }
}