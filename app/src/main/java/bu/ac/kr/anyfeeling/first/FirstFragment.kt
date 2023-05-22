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
            startMoodActivity("Happy")
        }
        mBinding.menuSad.setOnClickListener {
            startMoodActivity("Sad")
        }
        mBinding.menuRomantic.setOnClickListener {
            startMoodActivity("Romantic")
        }
        mBinding.menuGloomy.setOnClickListener {
            startMoodActivity("Gloomy")
        }
        mBinding.menuSexy.setOnClickListener {
            startMoodActivity("Sexy")
        }
        mBinding.menuRelaxing.setOnClickListener {
            startMoodActivity("Relaxing")
        }
        mBinding.menuDark.setOnClickListener {
            startMoodActivity("Dark")
        }
        mBinding.menuFunny.setOnClickListener {
            startMoodActivity("Funny")
        }

        return mBinding.root
    }

    private fun startMoodActivity(mood: String) {
        val intent = Intent(context, MoodActivity::class.java)
        intent.putExtra(MoodActivity.EXTRA_MOOD, mood)
        startActivity(intent)
    }
}


