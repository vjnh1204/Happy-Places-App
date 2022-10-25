package com.example.happyplacesapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplacesapp.activity.AddHappyPlacesActivity
import com.example.happyplacesapp.activity.MainActivity
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.database.Models.HappyPlaceModel
import com.example.happyplacesapp.databinding.ItemHappyPlaceBinding

class HappyPlacesAdapter(
    private val context: Context,
    private var list: ArrayList<HappyPlaceModel>,
    private val detailListener: (position: Int,model:HappyPlaceModel) -> Unit
) : RecyclerView.Adapter<HappyPlacesAdapter.MyHolder>() {
    inner class MyHolder(val binding: ItemHappyPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: HappyPlaceModel) {
            binding.ivPlaceImage.setImageURI(Uri.parse(item.image))
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemHappyPlaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    fun removeAt(position: Int) {

        val dbHandler = DatabaseHandler(context)
        val isDeleted = dbHandler.deleteHappyPlace(list[position])

        if (isDeleted > 0) {
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun notifyEditItem(activity: Activity, position: Int, startActivityForResult: ActivityResultLauncher<Intent>) {
        val intent = Intent(context, AddHappyPlacesActivity::class.java)
        intent.putExtra("EXTRA_PLACE_DETAIL", list[position])
        startActivityForResult.launch(intent)
        notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
    }
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val model = list[position]
        holder.bindItem(model)
        holder.binding.root.setOnClickListener{
            detailListener.invoke(position,model)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}