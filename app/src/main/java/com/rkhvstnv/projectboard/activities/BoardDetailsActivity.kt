package com.rkhvstnv.projectboard.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.rkhvstnv.projectboard.*
import com.rkhvstnv.projectboard.adapters.MemberItemsAdapter
import com.rkhvstnv.projectboard.adapters.TaskItemsAdapter
import com.rkhvstnv.projectboard.databinding.ActivityBoardDetailsBinding
import com.rkhvstnv.projectboard.models.BoardData
import com.rkhvstnv.projectboard.models.UserDataClass

class BoardDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityBoardDetailsBinding
    private lateinit var boardData: BoardData
    private lateinit var userList: ArrayList<UserDataClass>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_BOARD_DATA)){
            boardData = intent.getParcelableExtra(Constants.EXTRA_BOARD_DATA)!!
            setupActionBar(boardData.name)
            updateDataInViews()
        }
    }

    private fun updateDataInViews(){
        showProgressDialog(this)
        //binding.tvItemBoardDueTo.text = getString(R.string.st_due_to) todo
        FirebaseClass().getUserList(boardData.assignedToUserIds, object : MyCallBack{
            override fun onCallbackSuccess(any: Any) {
                userList = any as ArrayList<UserDataClass>

                binding.tvItemBoardCreator.text = userList[0].name

                setupMembersRecyclerView()
                setupTasksRecyclerView()

                hideProgressDialog()
            }

            override fun onCallbackErrorMessage(message: String) {
                hideProgressDialog()
                showSnackBarMessage(this@BoardDetailsActivity, message)
            }

        })
    }

    /** Next method load all Members in recyclerview,
     *  if there are exists */
    private fun setupMembersRecyclerView(){
        binding.rvBoardMembers.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvBoardMembers.setHasFixedSize(true)
        val membersAdapter = MemberItemsAdapter(
            this, userList, boardData.createdBy, object : OnItemClicked{
                override fun onClick(position: Int, any: Any) {
                    TODO("Not yet implemented")
                }

            })
        binding.rvBoardMembers.adapter = membersAdapter
    }

    /** Next method load all Members in recyclerview,
     *  if there are exists */
    private fun setupTasksRecyclerView(){
        if (boardData.taskList.size > 0){
            binding.rvBoardTasks.layoutManager = LinearLayoutManager(this)
            binding.rvBoardTasks.setHasFixedSize(true)
            val tasksAdapter = TaskItemsAdapter(
                this, boardData.id, boardData.taskList, object : OnItemClicked{
                override fun onClick(position: Int, any: Any) {
                    TODO("Not yet implemented")
                }

            })
            binding.rvBoardTasks.adapter = tasksAdapter
        }
    }
}