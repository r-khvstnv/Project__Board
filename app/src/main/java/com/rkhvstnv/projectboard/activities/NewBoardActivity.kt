package com.rkhvstnv.projectboard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rkhvstnv.projectboard.databinding.ActivityNewBoardBinding

class NewBoardActivity : BaseActivity() {
    private lateinit var binding: ActivityNewBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}