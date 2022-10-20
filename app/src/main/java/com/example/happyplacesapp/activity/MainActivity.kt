package com.example.happyplacesapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplacesapp.adapter.HappyPlacesAdapter
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.database.Models.HappyPlaceModel
import com.example.happyplacesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val startActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == RESULT_OK && result.data != null){
            getHappyPlaceList()
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.fabAddHappyPlace?.setOnClickListener {
            startActivityForResult.launch(Intent(this@MainActivity, AddHappyPlacesActivity::class.java))
        }
        getHappyPlaceList()
    }
    private fun getHappyPlaceList(){
        val dbHandler = DatabaseHandler(this)
        val happyPlaceList = dbHandler.getHappyPlacesList()
        Log.d("AAA",happyPlaceList.size.toString())
        if (happyPlaceList.size >0){
            setUpHappyPlaceRecyclerView(happyPlaceList)
            binding?.rvHappyPlacesList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE

        }
        else{
            binding?.rvHappyPlacesList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }
    private fun setUpHappyPlaceRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>){
        binding?.rvHappyPlacesList?.apply {
            this.setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = HappyPlacesAdapter(this@MainActivity,happyPlaceList) {
                    position, model ->
                startActivity(Intent(this@MainActivity,HappyPlaceDetailActivity::class.java))
            }
        }
    }

}