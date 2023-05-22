package bu.ac.kr.anyfeeling.first

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import bu.ac.kr.anyfeeling.PlayerViewModel
import bu.ac.kr.anyfeeling.R
import bu.ac.kr.anyfeeling.adapter.PlayListAdapter
import bu.ac.kr.anyfeeling.databinding.FragmentPlayerBinding
import bu.ac.kr.anyfeeling.service.*
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class MoodActivity : AppCompatActivity() {
    private lateinit var service: MusicService
    private lateinit var player: SimpleExoPlayer
    private lateinit var playListAdapter: PlayListAdapter
    private lateinit var binding: FragmentPlayerBinding
    private val myHandler = Handler()
    private val updateSeekRunnable = Runnable { updateSeek() }
    private lateinit var playerViewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerViewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)

        val mood = intent.getStringExtra(EXTRA_MOOD)
        if (mood != null) {
            service = createMusicService()
            fetchMusicListByMood(mood)
        } else {
            // Handle no mood selected case
        }

        initPlayView()
        initPlayListButton()
        initPlayControlButtons()
        initRecyclerView()
        initSeekBar()
    }

    private fun createMusicService(): MusicService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(MusicService::class.java)
    }

    private fun fetchMusicListByMood(mood: String) {
        val call: Call<MusicDto> = when (mood) {
            "Happy" -> service.listHappyMusics()
            "Sad" -> service.listSadMusics()
            "Romantic" -> service.listRomanticMusics()
            "Gloomy" -> service.listGloomyMusics()
            "Sexy" -> service.listSexyMusics()
            "Relaxing" -> service.listRelaxingMusics()
            "Dark" -> service.listDarkMusics()
            "Funny" -> service.listFunnyMusics()
            else -> throw IllegalArgumentException("Invalid mood: $mood")
        }

        call.enqueue(object : Callback<MusicDto> {
            override fun onResponse(call: Call<MusicDto>, response: Response<MusicDto>) {
                response.body()?.let { musicDto ->
                    val musicList = musicDto.musics.mapIndexed { index, musicEntity ->
                        musicEntity.mapper(index.toLong())
                    }
                    playerViewModel.setPlayMusicList(musicList)
                    playListAdapter.submitList(playerViewModel.getAdapterModels())
                }
            }

            override fun onFailure(call: Call<MusicDto>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun initPlayView() {
        player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = player

        player.addListener(object : Player.EventListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    binding.playControlImageView.setImageResource(R.drawable.ic_baseline_pause_24)
                } else {
                    binding.playControlImageView.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                updateSeek()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                val newIndex = mediaItem?.mediaId ?: return
                val musicModel = playerViewModel.getMusicList().value?.get(newIndex.toInt())
                musicModel?.let {
                    playerViewModel.updateCurrentPosition(it)
                }
                updatePlayerView(playerViewModel.currentMusicModel())
                playListAdapter.submitList(playerViewModel.getAdapterModels())
            }
        })
    }

    private fun initPlayListButton() {
        binding.playlistImageView.setOnClickListener {
            if (playerViewModel.getCurrentPosition().value == -1) return@setOnClickListener
            binding.playerViewGroup.visibility =
                if (playerViewModel.getIsWatchingPlayListView().value == true) View.VISIBLE else View.GONE
            binding.playListViewGroup.visibility =
                if (playerViewModel.getIsWatchingPlayListView().value != true) View.VISIBLE else View.GONE
            playerViewModel.setIsWatchingPlayListView(!(playerViewModel.getIsWatchingPlayListView().value ?: true))
        }
    }

    private fun initPlayControlButtons() {
        binding.playControlImageView.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }
        binding.skipNextImageView.setOnClickListener {
            val nextMusic = playerViewModel.nextMusic() ?: return@setOnClickListener
            playMusic(nextMusic)
        }
        binding.skipPrevImageView.setOnClickListener {
            val prevMusic = playerViewModel.prevMusic() ?: return@setOnClickListener
            playMusic(prevMusic)
        }
    }

    private fun initRecyclerView() {
        playListAdapter = PlayListAdapter {
            playMusic(it)
        }
        binding.playListRecyclerView.apply {
            adapter = playListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initSeekBar() {
        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                player.seekTo((seekBar.progress * 1000).toLong())
            }
        })
        binding.playListSeekBar.setOnTouchListener { v, event ->
            false
        }
    }

    private fun updateSeek() {
        val duration = if (player.duration >= 0) player.duration else 0
        val position = player.currentPosition
        updateSeekUi(duration, position)
        val state = player.playbackState
        myHandler.removeCallbacks(updateSeekRunnable)
        if (state != Player.STATE_IDLE && state != Player.STATE_ENDED) {
            myHandler.postDelayed(updateSeekRunnable, 1000)
        }
    }

    private fun updateSeekUi(duration: Long, position: Long) {
        binding.playListSeekBar.max = (duration / 1000).toInt()
        binding.playListSeekBar.progress = (position / 1000).toInt()
        binding.playerSeekBar.max = (duration / 1000).toInt()
        binding.playerSeekBar.progress = (position / 1000).toInt()
        binding.playTimeTextView.text = String.format(
            "%02d:%02d",
            TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS),
            (position / 1000) % 60
        )
        binding.totalTimeTextView.text = String.format(
            "%02d:%02d",
            TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS),
            (duration / 1000) % 60
        )
    }

    private fun setMusicList(modelList: List<MusicModel>) {
        player.addMediaItems(modelList.map { musicModel ->
            MediaItem.Builder()
                .setMediaId(musicModel.id.toString())
                .setUri(musicModel.streamUrl)
                .build()
        })
        player.prepare()
    }

    private fun playMusic(musicModel: MusicModel) {
        playerViewModel.updateCurrentPosition(musicModel)
        player.seekTo(playerViewModel.getCurrentPosition().value ?: 0, 0)
        player.play()
    }

    private fun updatePlayerView(currentMusicModel: MusicModel?) {
        currentMusicModel ?: return
        binding.trackTextView.text = currentMusicModel.track
        binding.artistTextView.text = currentMusicModel.artist
        Glide.with(binding.coverImageView.context)
            .load(currentMusicModel.coverUrl)
            .into(binding.coverImageView)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        myHandler.removeCallbacks(updateSeekRunnable)
    }

    companion object {
        const val EXTRA_MOOD = "extra_mood"
    }
}
