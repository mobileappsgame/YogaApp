package com.example.yoga.views

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yoga.R
import com.example.yoga.activies.AsunaActivity
import com.example.yoga.activies.MainActivity
import com.example.yoga.classes.Card
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class CardItemView(inflater: LayoutInflater, private val parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_view, parent, false)) {
    private var counterTwo: TextView = itemView.findViewById(R.id.counterTwo)
    private var counterFirst: TextView = itemView.findViewById(R.id.counterFirst)
    private var titleCard: TextView = itemView.findViewById(R.id.asanaTitle)
    private var socialAll: TextView = itemView.findViewById(R.id.socialAll)
    private var publish: TextView = itemView.findViewById(R.id.publish)
    private var layoutDate1: FrameLayout = itemView.findViewById(R.id.layoutDate1)
    private var social: FrameLayout = itemView.findViewById(R.id.social)
    private var likeImg: ImageView = itemView.findViewById(R.id.likeImg)
    private var yogaIconGrand: ImageView = itemView.findViewById(R.id.yogaIconGrand)
    private var image: ImageView = itemView.findViewById(R.id.image)
    private var buttonSettings: ImageView = itemView.findViewById(R.id.buttonSettings)
    private var addAsuna: FrameLayout = itemView.findViewById(R.id.addAsuna)

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private val thumbnails: StorageReference = storage.reference.child("thumbnails")
    private var isLiked: TextView = itemView.findViewById(R.id.isLiked)

    @SuppressLint("HardwareIds")
    private val androidID = Settings.Secure.getString(
        parent.context.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    fun bind(card: Card) {
        counterTwo.text = card.currentCardNum.toString()
        counterFirst.text = card.allCardCount.toString()
        titleCard.text = card.title
        socialAll.text = card.commentsCount.toString()
        publish.text = card.likesCount.toString()
        isLiked.text = "0"

        thumbnails.child("${card.thumbPath}.jpeg")
            .downloadUrl
            .addOnSuccessListener {
                Glide.with(parent.context)
                    .load(it)
                    .into(yogaIconGrand)
                Glide.with(parent.context)
                    .load(it)
                    .into(image)

            }.addOnFailureListener { exception ->
                Log.d("log", "get failed with ", exception)
            }

        db.collection("likes").document(card.id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    if (document.contains(androidID)) {
                        if (document.data?.get(androidID) as Boolean) {
                            isLiked.text = "1"
                        }
                    }
                }
                if (isLiked.text == "1") {
                    likeImg.setImageResource(R.drawable.ic_baseline_favorite_24)
                } else {
                    likeImg.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("log", "get failed with ", exception)
            }

        layoutDate1.setOnClickListener {
            if (isLiked.text == "1") {
                card.likesCount -= 1
                db.collection("likes").document(card.id)
                    .update(androidID,false)
                db.collection("asunaRU").document(card.id)
                    .update("likes", card.likesCount)
                publish.text = (card.likesCount).toString()
                likeImg.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                isLiked.text = "0"
            } else {
                card.likesCount += 1
                db.collection("likes").document(card.id)
                    .update(androidID,true)
                db.collection("asunaRU").document(card.id)
                    .update("likes", card.likesCount)
                publish.text = (card.likesCount).toString()
                likeImg.setImageResource(R.drawable.ic_baseline_favorite_24)
                isLiked.text = "1"
            }
        }

        titleCard.setOnClickListener {
            openAsuna(card.id)
        }

        image.setOnClickListener {
            openAsuna(card.id)
        }

        social.setOnClickListener {
            openAsuna(card.id)
        }

        addAsuna.setOnClickListener {

        }

        buttonSettings.setOnClickListener {
            val popupMenu = PopupMenu(parent.context, it)
            popupMenu.inflate(R.menu.option_menu)

            popupMenu.setOnMenuItemClickListener { item ->
                return@setOnMenuItemClickListener when(item.itemId) {
                    R.id.item1 -> {
                        true
                    }
                    R.id.item2 -> {
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun openAsuna(id: String) {
        val intent = Intent(
            parent.context,
            AsunaActivity::class.java
        )
        intent.putExtra("asunaID", id)
        parent.context.startActivity(intent)
    }
}