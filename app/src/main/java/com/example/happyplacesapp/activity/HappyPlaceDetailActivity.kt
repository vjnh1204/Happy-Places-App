package com.example.happyplacesapp.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplacesapp.database.Models.HappyPlaceModel
import com.example.happyplacesapp.databinding.ActivityHappyPlaceDetailBinding

class HappyPlaceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHappyPlaceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var happyPlaceDetailModel :HappyPlaceModel? = null
        if (intent.hasExtra("EXTRA_PLACE_DETAIL")){
            happyPlaceDetailModel  = intent.getParcelableExtra("EXTRA_PLACE_DETAIL")!!
        }
        if (happyPlaceDetailModel != null){
            setSupportActionBar(binding.toolbarHappyPlaceDetail)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            binding.toolbarHappyPlaceDetail.setNavigationOnClickListener {
                onBackPressed()
            }
            supportActionBar!!.title = happyPlaceDetailModel.title
            binding.ivPlaceImage.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            binding.tvDescription.text = happyPlaceDetailModel.description
            binding.tvLocation.text = happyPlaceDetailModel.location
        }

    }


}