package com.example.storyapp.ui.story

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.R
import com.example.storyapp.data.local.pref.UserPreferencesRepositoryImpl
import com.example.storyapp.databinding.ActivityPostStoryBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.auth.dataStore
import com.example.storyapp.ui.homepage.HomePageActivity
import com.example.storyapp.ui.map.PickLocationActivity
import com.example.storyapp.ui.map.PickLocationActivity.Companion.DEFAULT_ZOOM
import com.example.storyapp.ui.map.PickLocationActivity.Companion.currentLagLng
import com.example.storyapp.ui.viewmodel.DataStoreViewModel
import com.example.storyapp.ui.viewmodel.MainViewModel
import com.example.storyapp.ui.viewmodel.MainViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class PostStoryActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPostStoryBinding
    private lateinit var token: String

    private var getFile: File? = null
    private lateinit var fileFinal: File
    private var latlng: LatLng? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAction()

        val preferences = UserPreferencesRepositoryImpl.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]

        dataStoreViewModel.getToken().observe(this) {
            token = it
        }

        dataStoreViewModel.getName().observe(this) {
            binding.tvUser.text = "Share " + it + "\'s Story."
        }

        viewModel.message.observe(this) {
            showToast(it)
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun setupAction() {
        binding.btnUploadYourStory.setOnClickListener {

            if (getFile == null) {
                showToast(resources.getString(R.string.warning_add_image))
                return@setOnClickListener
            }

            val des = binding.edDescription.text.toString().trim()
            if (des.isEmpty()) {
                binding.edDescription.error = resources.getString(R.string.warning_add_desc)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val file = getFile as File

                    var compressedFile: File? = null
                    var compressedFileSize = file.length()

                    // Compress the file until its size is less than or equal to 1MB
                    while (compressedFileSize > 1 * 1024 * 1024) {
                        compressedFile = withContext(Dispatchers.Default) {
                            Compressor.compress(applicationContext, file)
                        }
                        compressedFileSize = compressedFile.length()
                    }

                    fileFinal = compressedFile ?: file

                }

                // use the upload file to upload to server
                val requestImageFile =
                    fileFinal.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    fileFinal.name,
                    requestImageFile
                )

                val desPart = des.toRequestBody("text/plain".toMediaType())

                viewModel.upload(imageMultipart, desPart, latlng?.latitude, latlng?.longitude, token)
            }
        }

        binding.btnCamera.setOnClickListener {
            startTakePhoto()
        }

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.linearLayoutLocation.setOnClickListener {
            val intent = Intent(this, PickLocationActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding.switchLocation.setOnClickListener {
            if (binding.switchLocation.isChecked) {
                requestPermissionLauncher
            } else {
                currentLagLng = null
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }
    }

    private var anyPhoto = false
    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            anyPhoto = true
            binding.ivShowImage.setImageBitmap(result)
            binding.edDescription.requestFocus()
        }
    }

    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMATS,
        Locale.US
    ).format(System.currentTimeMillis())

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@PostStoryActivity,
                getString(R.string.package_name),
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@PostStoryActivity)
            getFile = myFile
            binding.ivShowImage.setImageURI(selectedImg)
            binding.edDescription.requestFocus()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }


    private fun showToast(msg: String) {
        Toast.makeText(
            this@PostStoryActivity,
            StringBuilder(getString(R.string.message)).append(msg),
            Toast.LENGTH_SHORT
        ).show()

        if (msg == "Story created successfully") {
            val intent = Intent(this@PostStoryActivity, HomePageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val address = data.getStringExtra("address")
                    val lat = data.getDoubleExtra("lat", 0.0)
                    val lng = data.getDoubleExtra("lng", 0.0)
                    latlng = LatLng(lat, lng)

                    binding.tvChooseLocation.text = address
                }
            }
        }


//    fun getLocationUpdates(context:Context, callback: (Location?) -> Unit) = lifecycleScope.launch {
//        if (ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                callback(location)
//            }.addOnFailureListener {
//                callback(null)
//            }
//        } else {
//            callback(null)
//        }
//    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    Toast.makeText(this, "Cannot get Permission", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Toast.makeText(this, "Location: " + location, Toast.LENGTH_SHORT).show()
                    currentLagLng = LatLng(
                        location.latitude,
                        location.longitude
                    )
                } else {
                    Toast.makeText(this, "NULL ERROR", Toast.LENGTH_SHORT).show()

                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            Toast.makeText(this, "ERROR GET LOCATION", Toast.LENGTH_SHORT).show()

        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbPostStory.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val FILENAME_FORMATS = "MMddyyyy"
    }
}