package com.rkhvstnv.projectboard.activities


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.rkhvstnv.projectboard.*
import com.rkhvstnv.projectboard.adapters.BoardItemsAdapter
import com.rkhvstnv.projectboard.databinding.ActivityMainBinding
import com.rkhvstnv.projectboard.databinding.NavHeaderMainBinding
import com.rkhvstnv.projectboard.models.BoardData
import com.rkhvstnv.projectboard.models.UserDataClass

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userData: UserDataClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        /** Show actionBar with app name*/
        setupActionBar(getString(R.string.app_name))
        /**Update user data in navigation View*/
        updateNavHeaderUserDetails()
        /**Set navigation listener*/
        binding.navView.setNavigationItemSelectedListener(this)

        /**Listener for new board creation*/
        binding.fabNewBoard.setOnClickListener {
            /** Start New board activity with extra data*/
            val intent = Intent(this, NewBoardActivity::class.java)
            /**Next line required for new board id
             *      New board documentID in fireStore collection will have name based on
             *      creatorID and time of first uploading*/
            intent.putExtra(Constants.INTENT_EXTRA_CURRENT_USER_ID, userData.id)

            startActivityForResult(intent, Constants.NEW_BOARD_CODE)
        }
    }


    /** Next method will be overridden because of adding
     *      navigationIcon and listener for opening/closing drawerLayout*/
    override fun setupActionBar(mTitle: String){
        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu)
        toolbar.title = mTitle

        toolbar.setNavigationOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }else{
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    /** Next method initialize corresponding method for each menuItem*/
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.m_profile ->{
                val intent = Intent(this, ProfileActivity::class.java)
                startActivityForResult(intent, Constants.PROFILE_UPDATE_CODE)
            }
            R.id.m_sign_out -> {
                FirebaseClass().signOutUser()
                val intent = Intent(this, ContainerActivity::class.java)
                /** Clear stack or flags as new launcher*/
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /** Next method update all userData in navigationView */
    private fun updateNavHeaderUserDetails(){
        /**data binding for header view*/
        val hBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        /**get data from fireStore*/
        FirebaseClass().getSignedInUserData(object : MyCallBack {
            override fun onCallbackSuccess(any: Any) {
                //user image
                userData = any as UserDataClass
                Glide.with(this@MainActivity)
                    .load(userData.imageProfile).fitCenter()
                    .placeholder(R.drawable.ic_profile).into(hBinding.civProfileImage)
                //username
                hBinding.tvUsername.text = userData.name
                //email address
                hBinding.tvUserEmail.text = userData.email
            }

            override fun onCallbackErrorMessage(message: String) {
                showSnackBarMessage(this@MainActivity, message)
            }

        })
    }

    /**Next method request boardList from fireStore*/
    private fun requestBoardList(){
        FirebaseClass().getBoardList(object : MyCallBack{
            override fun onCallbackSuccess(any: Any) {
                val boardList = any as ArrayList<BoardData>
                setupBoardRecyclerView(boardList)
            }

            override fun onCallbackErrorMessage(message: String) {
                showSnackBarMessage(this@MainActivity, message)
            }

        })
    }
    /** Next method load all boards in recyclerview,
     *  if there are exists and hide noBoards tv*/
    private fun setupBoardRecyclerView(boardList: ArrayList<BoardData>){
        showProgressDialog(this)
        //check amount of boards
        if (boardList.size > 0){
            //hide noBoards tv
            binding.tvNoBoards.visibility = View.VISIBLE

            //setup rv
            binding.rvBoardsMain.layoutManager = LinearLayoutManager(this)
            //save size fixed independent from adapter data
            binding.rvBoardsMain.setHasFixedSize(true)

            val boardItemsAdapter = BoardItemsAdapter(
                this, boardList, object : OnItemClicked {
                    override fun onClick(position: Int, any: Any) {
                        Toast.makeText(this@MainActivity, "Ok", Toast.LENGTH_SHORT).show()
                    }
                })
            binding.rvBoardsMain.adapter = boardItemsAdapter
            hideProgressDialog()
        } else{
            hideProgressDialog()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PROFILE_UPDATE_CODE){
                updateNavHeaderUserDetails()
            }
        }
    }
}