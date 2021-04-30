package com.rkhvstnv.projectboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rkhvstnv.projectboard.databinding.ActivityBoardDetailsBinding

class BoardDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoardDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}