package bu.ac.kr.anyfeeling.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bu.ac.kr.anyfeeling.R
import bu.ac.kr.anyfeeling.service.MusicModel
import com.bumptech.glide.Glide

class PlayListAdapter(private val callback :(MusicModel)-> Unit) : ListAdapter<MusicModel, PlayListAdapter.ViewHolder>(diffUtil)
{

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: MusicModel) {
            val trackTextview = view.findViewById<TextView>(R.id.itemTrackTextView)
            val artistTextView = view.findViewById<TextView>(R.id.itemArtistTextView)
            val coverImageView = view.findViewById<ImageView>(R.id.itemCoverImageView)

            trackTextview.text = item.track
            artistTextView.text = item.artist

            Glide.with(coverImageView.context)
                .load(item.coverUrl)
                .into(coverImageView)


            if(item.isPlaying){
                itemView.setBackgroundColor(Color.GRAY)

            }else{
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
            itemView.setOnClickListener {
                callback(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        currentList[position].also { musicModel ->
            holder.bind(musicModel)

        }
    }
    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<MusicModel>() {
            override fun areItemsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
                //안에 있는 컨텐츠들을 비교하는 것
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
                //ID 값을 비교하는것
                return oldItem == newItem
            }

        }
    }

}