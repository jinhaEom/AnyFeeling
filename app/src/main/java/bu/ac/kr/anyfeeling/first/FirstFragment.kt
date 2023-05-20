package bu.ac.kr.anyfeeling.first

import bu.ac.kr.anyfeeling.service.MusicService

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import bu.ac.kr.anyfeeling.R
import bu.ac.kr.anyfeeling.databinding.FragmentFirsttabBinding

class FirstFragment : Fragment(R.layout.fragment_firsttab) {
    private lateinit var mBinding: FragmentFirsttabBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFirsttabBinding.inflate(inflater, container, false)

        mBinding.menuHappy.setOnClickListener {
            startMoodActivity<MusicService>()
        }
        mBinding.menuSad.setOnClickListener {
            startMoodActivity<MusicService>()
        }
        mBinding.menuRomantic.setOnClickListener {
            startMoodActivity<MusicService>()
        }
        mBinding.menuGloomy.setOnClickListener {
            startMoodActivity<MusicService>()
        }
        mBinding.menuSexy.setOnClickListener {
            startMoodActivity<MusicService>()
        }
        mBinding.menuRelaxing.setOnClickListener {
            startMoodActivity<MusicService>()
        }
        mBinding.menuDark.setOnClickListener {
            startMoodActivity<MusicService>()
        }
        mBinding.menuFunny.setOnClickListener {
            startMoodActivity<MusicService>()
        }

        return mBinding.root
    }

    private inline fun <reified T : MusicService> startMoodActivity() {
        val intent = Intent(context, MoodActivity::class.java)
        intent.putExtra(MoodActivity.EXTRA_SERVICE_CLASS, T::class.java)
        startActivity(intent)
    }


}


