package com.rkhvstnv.projectboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.rkhvstnv.projectboard.R
import com.rkhvstnv.projectboard.databinding.FragmentIntroBinding


class IntroFragment : BaseFragment() {
    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIntroBinding.inflate(inflater, container, false)

        binding.llLogIn.setOnClickListener {
            replaceToFragmentAndBackStack(LogInFragment())
        }
        binding.llSignUp.setOnClickListener {
            replaceToFragmentAndBackStack(SignUpFragment())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}