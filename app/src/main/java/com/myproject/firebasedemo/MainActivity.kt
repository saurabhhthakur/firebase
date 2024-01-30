package com.myproject.firebasedemo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.myproject.firebasedemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.getImage.setOnClickListener {
            getImage()
        }

        binding.addFirebase.setOnClickListener {
            if (image != null) {
                val storage = FirebaseStorage.getInstance().reference
                val imageRef =
                    storage.child("abc").child(System.currentTimeMillis().toString() + ".jpg")
                imageRef.putFile(image!!)
                    .addOnSuccessListener {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@MainActivity,
                            "Image Successfully added",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    .addOnFailureListener {
                        Toast.makeText(this@MainActivity, "Image not added", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        binding.progressBar.visibility = View.VISIBLE
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                        // Update the progress bar
                        binding.progressBar.progress = progress
                    }
            } else
                Toast.makeText(this@MainActivity, "Please Select an Image", Toast.LENGTH_SHORT)
                    .show()
        }

    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                binding.setImage.setImageURI(it.data?.data)
                image = it.data?.data
            }
        }

    private fun getImage() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
           return
        }


        else{
            pickImageLauncher.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            )
        }

    }


    val request =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        {
            if (it[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                pickImageLauncher.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                )
            }
        }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ){
            pickImageLauncher.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            )
        }
        else
            request.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
    }


}