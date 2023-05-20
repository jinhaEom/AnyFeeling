package bu.ac.kr.anyfeeling

import androidx.lifecycle.LiveData
import bu.ac.kr.anyfeeling.service.MusicModel


//음악이 다음 트랙으로 넘어갈 때
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayerViewModel : ViewModel() {
    private val playMusicList: MutableLiveData<List<MusicModel>> = MutableLiveData()
    private val currentPosition: MutableLiveData<Int> = MutableLiveData(-1)
    private val isWatchingPlayListView: MutableLiveData<Boolean> = MutableLiveData(true)

    fun getAdapterModels(): List<MusicModel> {
        val musicList = playMusicList.value ?: emptyList()
        val currentPositionValue = currentPosition.value ?: -1
        return musicList.mapIndexed { index, musicModel ->
            musicModel.copy(
                isPlaying = index == currentPositionValue
            )
        }
    }

    fun updateCurrentPosition(musicModel: MusicModel) {
        val position = playMusicList.value?.indexOf(musicModel) ?: -1
        currentPosition.value = position
    }

    fun nextMusic(): MusicModel? {
        val musicList = playMusicList.value ?: emptyList()
        val currentPositionValue = currentPosition.value ?: -1

        if (musicList.isEmpty()) return null

        val newPosition = if ((currentPositionValue + 1) == musicList.size) 0 else currentPositionValue + 1
        currentPosition.value = newPosition

        return musicList[newPosition]
    }

    fun prevMusic(): MusicModel? {
        val musicList = playMusicList.value ?: emptyList()
        val currentPositionValue = currentPosition.value ?: -1

        if (musicList.isEmpty()) return null

        val newPosition = if ((currentPositionValue - 1) < 0) musicList.lastIndex else currentPositionValue - 1
        currentPosition.value = newPosition

        return musicList[newPosition]
    }

    fun currentMusicModel(): MusicModel? {
        val musicList = playMusicList.value ?: emptyList()
        val currentPositionValue = currentPosition.value ?: -1

        if (musicList.isEmpty() || currentPositionValue < 0 || currentPositionValue >= musicList.size) return null

        return musicList[currentPositionValue]
    }

    fun setPlayMusicList(musicList: List<MusicModel>) {
        playMusicList.value = musicList
    }

    fun setIsWatchingPlayListView(isWatching: Boolean) {
        isWatchingPlayListView.value = isWatching
    }

    fun getIsWatchingPlayListView(): MutableLiveData<Boolean> {
        return isWatchingPlayListView
    }
    fun getCurrentPosition(): MutableLiveData<Int> {
        return currentPosition
    }
    fun getMusicList(): LiveData<List<MusicModel>> {
        return playMusicList
    }

}
