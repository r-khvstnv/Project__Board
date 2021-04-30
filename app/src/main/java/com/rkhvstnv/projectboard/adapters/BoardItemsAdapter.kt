package com.rkhvstnv.projectboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rkhvstnv.projectboard.OnItemClicked
import com.rkhvstnv.projectboard.R
import com.rkhvstnv.projectboard.models.BoardData
import de.hdodenhof.circleimageview.CircleImageView

class BoardItemsAdapter(private val context: Context,
                        private val boardList: ArrayList<BoardData>,
                        private val onItemClicked: OnItemClicked):
    RecyclerView.Adapter<BoardItemsAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val civBoardImage: CircleImageView = view.findViewById(R.id.iv_item_board_image)
        val tvBoardName: TextView = view.findViewById(R.id.tv_item_board_name)
        val tvBoardCreator: TextView = view.findViewById(R.id.tv_item_board_creator)
        val tvBoardDueToDate: TextView = view.findViewById(R.id.tv_item_board_due_to)
    }
    private fun requestBoardCreatorAndDates(creatorId: String){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       return MyViewHolder(LayoutInflater.from(context)
           .inflate(R.layout.item_board, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val board = boardList[position]

        Glide.with(context).load(board.image).fitCenter().placeholder(R.color.mLightGreen).into(holder.civBoardImage)
        holder.tvBoardName.text = board.name
        holder.tvBoardCreator.text =
            context.resources.getString(R.string.st_created_by) + board.createdBy
        holder.tvBoardDueToDate.text = context.resources.getString(R.string.st_due_to) //+ board.dueDate

        holder.itemView.setOnClickListener {
            onItemClicked.onClick(position, board)
        }
    }

    override fun getItemCount(): Int {
       return boardList.size
    }


}