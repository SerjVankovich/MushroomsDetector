package com.example.mushroomsdetector.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.desmond.squarecamera.CameraActivity
import com.example.mushroomsdetector.MainActivity.Companion.CAMERA_REQUEST
import com.example.mushroomsdetector.R
import com.example.mushroomsdetector.common.Common
import com.example.mushroomsdetector.model.Picture
import com.example.mushroomsdetector.model.Prediction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class MainFragment(var fragmentTransaction: FragmentTransaction) : Fragment() {

    private lateinit var button: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button = view.findViewById(R.id.button)

        button.setOnClickListener {
            makePhoto()
        }
    }

    private fun makePhoto() {
        val cameraPermission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(requireContext(), cameraPermission) == PackageManager.PERMISSION_GRANTED) {
            val startCameraIntent = Intent(requireContext(), CameraActivity::class.java)
            startActivityForResult(startCameraIntent, CAMERA_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != AppCompatActivity.RESULT_OK) {
            return
        }
        if (requestCode == CAMERA_REQUEST) {
            val encodedImage = createEncodedImage(data)
            sendImageToRecognize(encodedImage)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendImageToRecognize(encodedImage: String) {
        button.isEnabled = false
        Common.retrofitService.sendPictureForRecognize(Picture(encodedImage)).enqueue(object :
            Callback<Prediction> {
            override fun onResponse(call: Call<Prediction>, response: Response<Prediction>) {
                if (response.isSuccessful) {
                    loadResultFragment(response.body()!!, encodedImage)
                } else {
                    Toast.makeText(requireContext(), "Empty mushroom name", Toast.LENGTH_SHORT).show()
                    enableButton()
                }
            }

            override fun onFailure(call: Call<Prediction>, t: Throwable) {
                Toast.makeText(requireContext(), "Server does not response", Toast.LENGTH_SHORT).show()
                enableButton()
            }

        })
    }

    fun enableButton() {
        button.isEnabled = true
    }

    fun setNewTransaction(fragmentTransaction: FragmentTransaction) {
        this.fragmentTransaction = fragmentTransaction
    }

    private fun createEncodedImage(data: Intent?): String {
        val photoUri = data?.data
        val bitMap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, photoUri)

        var outputStream = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG,30, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.URL_SAFE)
    }

    private fun loadResultFragment(prediction: Prediction, encodedImage: String) {
        val resultFragment = ResultFragment(prediction, encodedImage)
        fragmentTransaction.replace(R.id.fragment_container, resultFragment)
            .addToBackStack(null)
            .commit()
    }
}