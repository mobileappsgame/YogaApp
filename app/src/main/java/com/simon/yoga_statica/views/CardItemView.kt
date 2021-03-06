package com.simon.yoga_statica.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.simon.yoga_statica.R
import com.simon.yoga_statica.classes.Card
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.simon.yoga_statica.fragments.AsunaFragment

class CardItemView(inflater: LayoutInflater, private val parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_view, parent, false)) {
    private var progressBar: ProgressBar = itemView.findViewById(R.id.progressBarRecyclerView)
    private var counterTwo: TextView = itemView.findViewById(R.id.counterTwo)
    private var counterFirst: TextView = itemView.findViewById(R.id.counterFirst)
    var titleCard: TextView = itemView.findViewById(R.id.asanaTitle)
    private var socialAll: TextView = itemView.findViewById(R.id.socialAll)
    private var publish: TextView = itemView.findViewById(R.id.publish)
    private var layoutDate1: FrameLayout = itemView.findViewById(R.id.layoutDate1)
    var social: FrameLayout = itemView.findViewById(R.id.social)
    private var likeImg: ImageView = itemView.findViewById(R.id.likeImg)
    private var yogaIconGrand: ImageView = itemView.findViewById(R.id.yogaIconGrand)
    var image: ImageView = itemView.findViewById(R.id.image)
    private var buttonSettings: ImageView = itemView.findViewById(R.id.buttonSettings)
    private var commentImg: ImageView = itemView.findViewById(R.id.commentImg)
    private var lane: TextView = itemView.findViewById(R.id.lane)

    private var asunaCard: RelativeLayout = itemView.findViewById(R.id.asunaCard)
    private var ads: RelativeLayout = itemView.findViewById(R.id.ads)

    private var mAdView: AdView = itemView.findViewById(R.id.adView)
    private var adRequest: AdRequest = AdRequest.Builder().build()

    var addAsuna: FrameLayout = itemView.findViewById(R.id.addAsuna)

    private val db = Firebase.firestore
    private val storage = Firebase.storage
    private lateinit var auth: FirebaseAuth
    private val thumbnails: StorageReference = storage.reference.child("thumbnails")
    private var isLiked: TextView = itemView.findViewById(R.id.isLiked)
    private lateinit var fragmentManager: FragmentManager

    private lateinit var prefs: SharedPreferences
    private val APP_PREFERENCES_THEME = "theme"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("HardwareIds", "UseCompatLoadingForDrawables")

    fun bind(card: Card, fragmentManager: FragmentManager) {

        this.fragmentManager = fragmentManager

        prefs = parent.context.getSharedPreferences("settings", Context.MODE_PRIVATE)!!

        val theme = if (!prefs.contains(APP_PREFERENCES_THEME)) {
            "default"
        } else {
            when (prefs.getString(APP_PREFERENCES_THEME, "default")) {
                "coffee" -> "coffee"
                "default" -> "default"
                else -> "default"
            }
        }

        progressBar.indeterminateDrawable = when (theme) {
            "default" -> parent.context.getDrawable(R.drawable.spinner_ring)
            "coffee" -> parent.context.getDrawable(R.drawable.spinner_ring_coffee)
            else ->  parent.context.getDrawable(R.drawable.spinner_ring)
        }

        if (card.id != "ADV") {
            counterTwo.text = card.currentCardNum.toString()
            counterFirst.text = card.allCardCount.toString()
            titleCard.text = card.title
            socialAll.text = card.commentsCount.toString()
            publish.text = card.likesCount.toString()
            isLiked.text = "0"

            auth = Firebase.auth

            var id = auth.currentUser?.uid

            if (id.isNullOrEmpty()) {
                id = "null"
            }

            commentImg.setImageResource(
                when (theme) {
                    "default" -> R.drawable.ic_chat_bubble_outline_black_24dp
                    "coffee" -> R.drawable.ic_chat_bubble_outline_black_24dp_coffee
                    else ->  R.drawable.ic_chat_bubble_outline_black_24dp
                }
            )

            lane.setTextColor(
                when (theme) {
                    "default" -> R.color.colorPrimary
                    "coffee" -> R.color.colorPrimaryCoffee
                    else ->  R.color.colorPrimary
                }
            )

            buttonSettings.setImageResource(
                when (theme) {
                    "default" -> R.drawable.ic_more_horiz_black_24dp
                    "coffee" -> R.drawable.ic_more_horiz_black_24dp_coffee
                    else ->  R.drawable.ic_more_horiz_black_24dp
                }
            )

            thumbnails.child("${card.thumbPath}.jpeg")
                .downloadUrl
                .addOnSuccessListener {
                    Glide.with(parent.context)
                        .load(it)
                        .into(yogaIconGrand)
                    Glide.with(parent.context)
                        .load(it)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar.visibility = View.GONE
                                return false
                            }
                        })
                        .into(image)

                }.addOnFailureListener { exception ->
                    Log.d("log", "get failed with ", exception)
                }

            db.collection("likes").document(card.id)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        if (document.contains(id)) {
                            if (document.data?.get(id) as Boolean) {
                                isLiked.text = "1"
                            }
                        }
                    }
                    if (auth.currentUser != null) {
                        if (isLiked.text == "1") {
                            likeImg.setImageResource(
                                when (theme) {
                                    "default" -> R.drawable.ic_baseline_favorite_24
                                    "coffee" -> R.drawable.ic_baseline_favorite_24_coffee
                                    else ->  R.drawable.ic_baseline_favorite_24
                                }
                            )

                        } else {
                            likeImg.setImageResource(
                                when (theme) {
                                    "default" -> R.drawable.ic_favorite_border_black_24dp
                                    "coffee" -> R.drawable.ic_favorite_border_black_24dp_coffee
                                    else ->  R.drawable.ic_favorite_border_black_24dp
                                }
                            )
                        }
                    } else {
                        likeImg.setImageResource(
                            when (theme) {
                                "default" -> R.drawable.ic_favorite_border_black_24dp
                                "coffee" -> R.drawable.ic_favorite_border_black_24dp_coffee
                                else ->  R.drawable.ic_favorite_border_black_24dp
                            }
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("log", "get failed with ", exception)
                }
            if (auth.currentUser != null) {
                layoutDate1.setOnClickListener {
                    if (isLiked.text == "1") {
                        card.likesCount -= 1
                        db.collection("likes").document(card.id)
                            .update(id, false)
                        db.collection("asunaRU").document(card.id)
                            .update("likes", card.likesCount)
                        publish.text = (card.likesCount).toString()
                        likeImg.setImageResource(
                            when (theme) {
                                "default" -> R.drawable.ic_favorite_border_black_24dp
                                "coffee" -> R.drawable.ic_favorite_border_black_24dp_coffee
                                else ->  R.drawable.ic_favorite_border_black_24dp
                            }
                        )
                        isLiked.text = "0"
                    } else {
                        card.likesCount += 1
                        db.collection("likes").document(card.id)
                            .update(id, true)
                        db.collection("asunaRU").document(card.id)
                            .update("likes", card.likesCount)
                        publish.text = (card.likesCount).toString()
                        likeImg.setImageResource(
                            when (theme) {
                                "default" -> R.drawable.ic_baseline_favorite_24
                                "coffee" -> R.drawable.ic_baseline_favorite_24_coffee
                                else ->  R.drawable.ic_baseline_favorite_24
                            }
                        )
                        isLiked.text = "1"
                    }
                }
            }

            buttonSettings.setOnClickListener {
                val popupMenu = PopupMenu(parent.context, it)
                popupMenu.inflate(R.menu.option_menu)

                popupMenu.setOnMenuItemClickListener { item ->
                    return@setOnMenuItemClickListener when (item.itemId) {
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
        } else {
            asunaCard.visibility = View.GONE
            ads.visibility = View.VISIBLE

            mAdView.loadAd(adRequest)
        }
    }
}

