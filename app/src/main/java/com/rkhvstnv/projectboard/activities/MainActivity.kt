package com.rkhvstnv.projectboard.activities


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.rkhvstnv.projectboard.*
import com.rkhvstnv.projectboard.databinding.ActivityMainBinding
import com.rkhvstnv.projectboard.databinding.NavHeaderMainBinding
import com.rkhvstnv.projectboard.fragments.ProfileFragment

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

        //get info that user changes own data or not
        supportFragmentManager.setFragmentResultListener(Constants.PROFILE_CHANGES_KEY,
            this){ _, bundle ->
            val result: Boolean = bundle.getBoolean(Constants.PROFILE_BUNDLE_KEY)
            if (result){
                updateNavHeaderUserDetails()
            }
        }
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
            R.id.m_profile ->{
                replaceToFragmentAndBackStack(this, ProfileFragment())
            }
            R.id.m_sign_out -> {
                MyFirebaseClass().signOutUser()
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
        MyFirebaseClass().getSignedInUserData(object : MyCallBack {
            override fun onCallbackObject(userData: UserDataClass) {
                //user image
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
}