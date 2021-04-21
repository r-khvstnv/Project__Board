package com.rkhvstnv.projectboard.activities


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.rkhvstnv.projectboard.FireStoreClass
import com.rkhvstnv.projectboard.MyCallBack
import com.rkhvstnv.projectboard.R
import com.rkhvstnv.projectboard.UserDataClass
import com.rkhvstnv.projectboard.databinding.ActivityMainBinding
import com.rkhvstnv.projectboard.databinding.NavHeaderMainBinding

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //action bar with title and icon
        setupActionBar()
        //update user data
        updateNavHeaderUserDetails()
        //set navigation listener
        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun setupActionBar(){
        val toolbar = findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu)

        toolbar.setNavigationOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }else{
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.m_profile ->{}
            R.id.m_sign_out -> {
                FireStoreClass().signOutUser()
                val intent = Intent(this, ContainerActivity::class.java)
                //clear stack or flags as new launcher
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun updateNavHeaderUserDetails(){
        //data binding for header view
        val hBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        //get data from fireStore
        FireStoreClass().getSignedInUserData(object : MyCallBack {
            override fun onCallbackObject(userData: UserDataClass) {
                //user image
                Glide.with(this@MainActivity)
                    .load(userData.imageProfile).centerCrop()
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
}