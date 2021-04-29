package com.rkhvstnv.projectboard.activities

import android.os.Bundle
import com.rkhvstnv.projectboard.databinding.ActivityContainerBinding
import com.rkhvstnv.projectboard.fragments.SplashFragment

class ContainerActivity : BaseActivity() {
    private lateinit var binding: ActivityContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** Show splash fragment with app name */
        replaceToFragment(this, SplashFragment())
    }
}