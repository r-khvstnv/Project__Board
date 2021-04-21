package com.rkhvstnv.projectboard.activities

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.rkhvstnv.projectboard.R
import com.rkhvstnv.projectboard.databinding.ActivityContainerBinding
import com.rkhvstnv.projectboard.fragments.SplashFragment

class ContainerActivity : BaseActivity() {
    private lateinit var binding: ActivityContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.ll_container, SplashFragment())
            setReorderingAllowed(true)
            commit()
        }
    }
}