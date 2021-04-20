package com.rkhvstnv.projectboard.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rkhvstnv.projectboard.ContainerActivity
import com.rkhvstnv.projectboard.FireStoreClass
import com.rkhvstnv.projectboard.databinding.FragmentSplashBinding


class SplashFragment : BaseFragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentSplashBinding.inflate(inflater, container, false)

        Handler(Looper.getMainLooper()).postDelayed({
            if (FireStoreClass().getCurrentUserId().isNotEmpty()){
                startMainActivity()
            }else
                replaceToFragment(IntroFragment())
        }, 1500)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}