package com.rkhvstnv.projectboard.activities


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.rkhvstnv.projectboard.*
import com.rkhvstnv.projectboard.databinding.ActivityMainBinding
import com.rkhvstnv.projectboard.databinding.NavHeaderMainBinding
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
            /** Next arrayList will be updated after new board creation
             *      More rationally transfer it through intent
             *      instead of additional request to fireBase*/
            intent.putExtra(Constants.INTENT_EXTRA_BEEN_ATTACHED_TO_BOARDS_LIST,
                userData.beenAttachedToBoards)

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PROFILE_UPDATE_CODE){
                updateNavHeaderUserDetails()
            }
        }
    }
}