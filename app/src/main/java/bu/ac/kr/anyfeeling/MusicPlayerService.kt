package bu.ac.kr.anyfeeling

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class MusicPlayerService : Service() {
    private lateinit var player: SimpleExoPlayer
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    // 알림 채널 ID
    private val notificationChannelId = "music_channel"
    // 알림 ID
    private val notificationId = 2

    override fun onCreate() {
        super.onCreate()
        player = SimpleExoPlayer.Builder(this).build()
        Log.d("MusicPlayerService", "Service started.")

        // 알림 매니저 초기화
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 알림 채널 생성
        createNotificationChannel()

        // 플레이어 이벤트 리스너 설정
        player.addListener(object : Player.EventListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // 플레이어의 재생 상태가 변경되었을 때 호출되는 콜백
                updateNotification() // 알림 업데이트
            }


        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        startForeground(notificationId, notification)
        player.play()
        return START_STICKY
    }


    private fun buildNotification(): Notification {
        initNotificationBuilder()

        val playPauseIcon = if (player.isPlaying) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24
        val playPauseAction = if (player.isPlaying) PAUSE_ACTION else PLAY_ACTION

        notificationBuilder
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1)  // 컴팩트 뷰에서 보여줄 액션의 인덱스 설정
            )
            .setContentTitle("Now Playing")
            .setContentText("Song Title")
            .addAction(
                playPauseIcon,
                "Pause",
                createPlaybackAction(playPauseAction)
            )
            .addAction(
                R.drawable.ic_baseline_skip_next_24,
                "Next",
                createPlaybackAction(NEXT_ACTION)
            )

        return notificationBuilder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        notificationManager.cancel(notificationId)
    }

    // 알림 채널 생성
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Music Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 알림 빌더 초기화
    private fun initNotificationBuilder() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
            .setSmallIcon(R.drawable.appicon)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
    }



    // 알림 업데이트
    private fun updateNotification() {
        initNotificationBuilder()

        // 플레이어의 재생 상태에 따라 알림의 내용 및 액션 설정
        if (player.isPlaying) {
            // 재생 중인 경우
            notificationBuilder.setContentTitle("Now Playing")
                .setContentText("Song Title")
                .addAction(
                    R.drawable.ic_baseline_pause_24,
                    "Pause",
                    createPlaybackAction(PAUSE_ACTION)
                )
                .addAction(
                    R.drawable.ic_baseline_skip_next_24,
                    "Next",
                    createPlaybackAction(NEXT_ACTION)
                )
        } else {
            // 일시 정지 또는 정지 상태인 경우
            notificationBuilder.setContentTitle("Paused")
                .setContentText("Song Title")
                .addAction(
                    R.drawable.ic_baseline_play_arrow_24,
                    "Play",
                    createPlaybackAction(PLAY_ACTION)
                )
                .addAction(
                    R.drawable.ic_baseline_skip_next_24,
                    "Next",
                    createPlaybackAction(NEXT_ACTION)
                )
        }

        // 알림 업데이트
        val notification = notificationBuilder.build()
        startForeground(notificationId, notification)
    }

    private fun createPlaybackAction(action: String): PendingIntent {
        val playbackIntent = Intent(this, MusicPlayerService::class.java).apply {
            this.action = action
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getService(
                this,
                0,
                playbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getService(
                this,
                0,
                playbackIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val PLAY_ACTION = "com.example.musicplayer.action.PLAY"
        const val PAUSE_ACTION = "com.example.musicplayer.action.PAUSE"
        const val NEXT_ACTION = "com.example.musicplayer.action.NEXT"
        const val PREV_ACTION = "com.example.musicplayer.action.PREV"
    }
}
