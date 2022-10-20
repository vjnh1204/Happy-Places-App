package com.example.happyplacesapp.activity

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.happyplacesapp.R
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.database.Models.HappyPlaceModel
import com.example.happyplacesapp.databinding.ActivityAddHappyPlacesBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlacesActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddHappyPlacesBinding? = null
    private val cal = Calendar.getInstance()
    private lateinit var dateSetPicker: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0
    @RequiresApi(Build.VERSION_CODES.P)
    private var openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val thumbnail = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver,result?.data?.data!!))
                saveImageToInternalStorage = saveImage(thumbnail)
                binding?.ivPlaceImage?.setImageBitmap(thumbnail)
            }
        }
    private var openCameraLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val thumbnail: Bitmap = result?.data?.extras!!.get("data") as Bitmap
                saveImageToInternalStorage = saveImage(thumbnail)
                binding?.ivPlaceImage?.setImageBitmap(thumbnail)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlacesBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarAddPlace)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }
        dateSetPicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()
        binding?.etDate?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlacesActivity,
                    dateSetPicker,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems) { _, which ->
                    when (which) {
                        0 -> choosePictureFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
            R.id.btn_save -> {
                when {
                    binding?.etTitle?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                    }
                    binding?.etDescription?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT)
                            .show()
                    }
                    binding?.etLocation?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please select location", Toast.LENGTH_SHORT)
                            .show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please add image", Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                        val happyPlaceModel = HappyPlaceModel(
                            0,
                            binding?.etTitle?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding?.etDescription?.text.toString(),
                            binding?.etDate?.text.toString(),
                            binding?.etLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                        val dbHandler = DatabaseHandler(this)

                        val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)

                        if (addHappyPlace > 0) {
                            val intent = Intent()
                            setResult(RESULT_OK,intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun saveImage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("image", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.flush()
            fos.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        Log.e("AAA", file.absolutePath)
        return Uri.parse(file.absolutePath)
    }

    private fun takePhotoFromCamera() {
        Dexter.withContext(this@AddHappyPlacesActivity).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                if (p0!!.areAllPermissionsGranted()) {
                    try {
                        val pickIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        openCameraLauncher.launch(pickIntent)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                showRationaleDialogForPermissions()
            }


        }).check()
    }

    private fun choosePictureFromGallery() {
        Dexter.withContext(this@AddHappyPlacesActivity).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            @RequiresApi(Build.VERSION_CODES.P)
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                if (p0!!.areAllPermissionsGranted()) {
                    try {
                        val pickIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        openGalleryLauncher.launch(pickIntent)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                showRationaleDialogForPermissions()
            }


        }).check()
    }

    private fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this).setMessage(
            "" +
                    "It looks like you have turned off permissions required" +
                    "for this feature. It can be enable under the" +
                    "Applications Settings. "
        )
            .setPositiveButton("GO TO SETTINGS.") { _, _ ->
                try {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val simpleDateFormat = SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.etDate?.setText(simpleDateFormat.format(cal.time).toString())
    }

}