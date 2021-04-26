package com.rkhvstnv.projectboard.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rkhvstnv.projectboard.MyFirebaseClass
import com.rkhvstnv.projectboard.activities.MainActivity
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
            if (MyFirebaseClass().getCurrentUserId().isNotEmpty()){
                val intent = Intent(activity, MainActivity::class.java)
                activity?.startActivity(intent)
                activity?.finish()
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