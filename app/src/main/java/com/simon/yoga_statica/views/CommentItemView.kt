package com.simon.yoga_statica.views

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simon.yoga_statica.R
import com.simon.yoga_statica.classes.Comment

class CommentItemView(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.comment_item, parent, false)) {
    private var commentName: TextView = itemView.findViewById(R.id.nameCommentCard)
    private var commentText: TextView = itemView.findViewById(R.id.textCommentCard)
    private var timeComment: TextView = itemView.findViewById(R.id.timeComment)

    fun bind(comment: Comment) {
        commentName.text = comment.name
        commentText.text = comment.comment
        timeComment.text = comment.time
    }
}