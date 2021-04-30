package com.rkhvstnv.projectboard.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rkhvstnv.projectboard.*
import com.rkhvstnv.projectboard.models.TaskData

class TaskItemsAdapter(
    private val context: Context,
    private val boardId: String,
    private val taskList: ArrayList<TaskData>,
    private val onItemClicked: OnItemClicked): RecyclerView.Adapter<TaskItemsAdapter.MyViewHolder>() {
        inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
            val cbTaskStatus: CheckBox = view.findViewById(R.id.cb_item_task_check)
            val tvTaskName: TextView = view.findViewById(R.id.tv_item_task_name)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater
            .from(context).inflate(R.layout.item_task, parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val task = taskList[position]

        holder.tvTaskName.text = task.name
        //set ui depending on task status
        if (task.isFinished){
            holder.cbTaskStatus.isChecked = true
            holder.tvTaskName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }

        holder.itemView.setOnClickListener {
            if (task.isFinished){
                //make changes in existed list
                taskList[position].isFinished = false
                val boardHashMap = HashMap<String, Any>()
                boardHashMap[Constants.HASH_MAP_TASK_LIST] = taskList

                FirebaseClass().updateBoardData(boardId, boardHashMap, object : MyCallBack{
                    override fun onCallbackSuccess(any: Any) {
                        //update UI
                        holder.cbTaskStatus.isChecked = false
                        holder.tvTaskName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    override fun onCallbackErrorMessage(message: String) {}
                })
            } else{
                //make changes in existed list
                taskList[position].isFinished = true
                val boardHashMap = HashMap<String, Any>()
                boardHashMap[Constants.HASH_MAP_TASK_LIST] = taskList

                FirebaseClass().updateBoardData(boardId, boardHashMap, object : MyCallBack{
                    override fun onCallbackSuccess(any: Any) {
                        //update UI
                        holder.cbTaskStatus.isChecked = true
                        holder.tvTaskName.paintFlags = 0
                    }
                    override fun onCallbackErrorMessage(message: String) {}
                })
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}