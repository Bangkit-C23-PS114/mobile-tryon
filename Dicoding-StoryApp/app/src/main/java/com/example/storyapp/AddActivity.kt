package com.example.storyapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivityAddBinding
import com.example.storyapp.ml.UserImgEnc
import com.example.storyapp.viewmodel.AddViewModel
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*
private const val MAXIMAL_SIZE = 1000000
class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var getFile: File? = null
    private lateinit var token: String
    private val addViewModel: AddViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        val pref = MyPreference.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]


        loginViewModel.getToken().observe(this) {
            token = it
        }

        loginViewModel.getName().observe(this) {
            binding.tvName.text = StringBuilder(getString(R.string.post_as)).append(" ").append(it)
        }

        addViewModel.message.observe(this) {
            showToast(it)
        }

        addViewModel.isLoading.observe(this) {
            showLoading(it)
        }


        binding.imgPhoto.setOnClickListener {
            select()
        }

        binding.btUpload.setOnClickListener {
            uploadImage()

        }


    }
    private fun reduceFileImage(file: File): File? {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
//        val originalBitmap = BitmapFactory.decodeFile(file.path)
//        val targetWidth = 192
//        val targetHeight = 256
//        // Calculate the aspect ratio of the original image
//        val originalWidth = originalBitmap.width
//        val originalHeight = originalBitmap.height
//        val scaleFactor = Math.min(targetWidth.toFloat() / originalWidth, targetHeight.toFloat() / originalHeight)
//
//        // Resize the image using the calculated scale factor
//        val resizedWidth = (originalWidth * scaleFactor).toInt()
//        val resizedHeight = (originalHeight * scaleFactor).toInt()
//        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, resizedWidth, resizedHeight, false)
//
//        // Create a file to save the resized image
//        val outputFile = File.createTempFile("resized_image", ".jpg")
//        val outputStream = FileOutputStream(outputFile)
//
//        // Compress and save the resized bitmap to the file
//        try {
//            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
//            outputStream.flush()
//        } catch (e: IOException) {
//            e.printStackTrace()
//            return null
//        } finally {
//            outputStream.close()
//        }
//        // Return the file object
//        return outputFile
    }
//    fun getMaskOutfit(imgFile: File): Array<ByteArray>? {
//        try {
//            val model = UserImgEnc.newInstance(applicationContext)
//            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 256, 192, 3), DataType.FLOAT32)
//
//            // Load and preprocess the image using TensorImage
//            val bitmap: Bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
//            val tensorImage = TensorImage(DataType.FLOAT32)
//            tensorImage.load(bitmap)
//            val inputBuffer: ByteBuffer = tensorImage.buffer
//
//            // Resize the input buffer to match the expected input shape
//            val reshapedBuffer = ByteBuffer.allocateDirect(1 * 256 * 192 * 3 * 4) // Assuming 4 bytes per float value
//            reshapedBuffer.order(ByteOrder.nativeOrder())
//            reshapedBuffer.rewind()
//
//            // Copy the input buffer to the resized buffer, assuming RGB pixel format
//            for (y in 0 until bitmap.height) {
//                for (x in 0 until bitmap.width) {
//                    val pixelValue = inputBuffer.getFloat((y * bitmap.width + x) * 3) // Assuming RGB order
//                    reshapedBuffer.putFloat(pixelValue)
//                }
//            }
//
//            inputFeature0.loadBuffer(reshapedBuffer)
//
//            // Run inference
//            val outputs = model.process(inputFeature0)
//            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
//
//            // Process the outputFeature0 to obtain the binary mask
//            val binaryMaskBitmap = Bitmap.createBitmap(256, 192, Bitmap.Config.ARGB_8888)
//            val outputArray = outputFeature0.floatArray
//            var index = 0
//            for (y in 0 until 192) {
//                for (x in 0 until 256) {
//                    val pixelValue = outputArray[index++]
//                    val color = if (pixelValue >= 0.5f) 255 else 0
//                    binaryMaskBitmap.setPixel(x, y, color)
//                }
//            }
//            model.close()
//            val resultMatrix = (Array(192) { ByteArray(256) })
//            if (resultMatrix != null) {
//                for (row in resultMatrix) {
//                    for (value in row) {
//                        val intValue = value.toInt() and 0xFF // Convert byte to unsigned int value
//                        println(intValue)
//                    }
//                }
//            }
//
//            return resultMatrix
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//
//        return (Array(192) { ByteArray(256) })
//    }
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
            val myFile = uriToFile(selectedImg, this@AddActivity)
            getFile = myFile
            binding.imgPhoto.setImageURI(selectedImg)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
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
            binding.imgPhoto.setImageBitmap(result)
        }
    }
    private val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
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
                this@AddActivity,
                getString(R.string.package_name),
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun uploadImage() {
        when {
            getFile == null -> {
                Toast.makeText(
                    this@AddActivity,
                    getString(R.string.input_picture),
                    Toast.LENGTH_SHORT
                ).show()


            }
            else -> {
                val file = reduceFileImage(getFile as File)
                val requestImageFile = file?.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                val maskOutfit = file?.let { getMaskOutfit(it) }
//                Log.d("TAG","mask content: $maskOutfit")
//                val imageMultipart: MultipartBody.Part? = requestImageFile?.let {
//                    MultipartBody.Part.createFormData(
//                        "photo",
//                        file?.name,
//                        it
//                    )
//                }
//                addViewModel.upload(imageMultipart, description, token)

            }
        }


    }

    private fun showToast(msg: String) {
        Toast.makeText(
            this@AddActivity,
            StringBuilder(getString(R.string.message)).append(msg),
            Toast.LENGTH_SHORT
        ).show()

        if (msg == "Story created successfully") {
            startActivity(Intent(this@AddActivity, HomeActivity::class.java))
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    private fun select() {
        val items = arrayOf<CharSequence>(

            getString(R.string.from_gallery),
            getString(R.string.take_picture),
            getString(R.string.cancel)
        )

        val title = TextView(this)
        title.text = getString(R.string.select_photo)
        title.gravity = Gravity.CENTER
        title.setPadding(10, 15, 15, 10)
        title.setTextColor(resources.getColor(R.color.dark_blue, theme))
        title.textSize = 22f
        val builder = AlertDialog.Builder(
            this
        )
        builder.setCustomTitle(title)
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == getString(R.string.from_gallery) -> {
                    startGallery()

                }
                items[item] == getString(R.string.take_picture) -> {
                    startTakePhoto()

                }
                items[item] == getString(R.string.cancel) -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }


    @SuppressLint("RestrictedApi")
    private fun setActionBar() {
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.share_story)
        actionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val FILENAME_FORMAT = "MMddyyyy"
    }
}

