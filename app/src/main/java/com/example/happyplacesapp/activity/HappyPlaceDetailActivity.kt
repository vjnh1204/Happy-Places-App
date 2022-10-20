package com.example.happyplacesapp.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplacesapp.databinding.ActivityHappyPlaceDetailBinding

class HappyPlaceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHappyPlaceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarHappyPlaceDetail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbarHappyPlaceDetail.setNavigationOnClickListener {
            onBackPressed()
        }

    }


}