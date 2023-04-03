package io.usys.report.ui.ludi.roster

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import io.usys.report.R
import io.usys.report.realm.model.PlayerRef
import io.usys.report.utils.bind
import io.usys.report.utils.bindTextView
import io.usys.report.utils.views.loadUriIntoImgView

class PlayerMediumGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imgPlayerProfile = itemView.bind<CircleImageView>(R.id.cardPlayerMediumGridImgProfile)
    var txtItemPlayerName = itemView.bindTextView(R.id.cardPlayerMediumGridTxtName)
    var txtItemPlayerOne = itemView.bindTextView(R.id.cardPlayerMediumGridTxtOne)
    var txtItemPlayerTwo = itemView.bindTextView(R.id.cardPlayerMediumGridTxtTwo)

    fun bind(player: PlayerRef?, position: Int?= null) {
        player?.let {
            txtItemPlayerName?.text = it.name
            txtItemPlayerOne?.text = "Position: ${position.toString()}"
            txtItemPlayerTwo?.text = it.orderIndex.toString()
            it.imgUrl?.let { url ->
                imgPlayerProfile.loadUriIntoImgView(url)
            }
        }
    }
}

class PlayerTinyHorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var imgPlayerProfile = itemView.bind<CircleImageView>(R.id.cardPlayerTinyHorizontalImgProfile)
    var txtItemPlayerName = itemView.bindTextView(R.id.cardPlayerTinyHorizontalTxtName)

    fun bind(player: PlayerRef?, position: Int?= null) {
        player?.let {
            txtItemPlayerName?.text = it.name
            it.imgUrl?.let { url ->
                imgPlayerProfile.loadUriIntoImgView(url)
            }
        }
    }
}