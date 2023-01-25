package com.example.mushroomsdetector.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.util.Base64.URL_SAFE
import android.util.Base64.decode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mushroomsdetector.R
import com.example.mushroomsdetector.adapters.ImageAdapter
import com.example.mushroomsdetector.common.Common
import com.example.mushroomsdetector.model.Prediction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ResultFragment(var prediction: Prediction, var encodedImage: String) : Fragment() {

    lateinit var yourPhoto: ImageView
    lateinit var mushroomPhoto: ImageView
    lateinit var probabilityField: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var mushroomName: TextView
    lateinit var description: TextView
    lateinit var progressBar1: ProgressBar
    lateinit var progressBar2: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.result_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mushroomName = view.findViewById(R.id.mushroomName)
        probabilityField = view.findViewById(R.id.probability)
        yourPhoto = view.findViewById(R.id.yourPhoto)
        mushroomPhoto = view.findViewById(R.id.mushroomPhoto)
        recyclerView = view.findViewById(R.id.recyclerView)
        description = view.findViewById(R.id.wikiText)
        progressBar1 = view.findViewById(R.id.progressBar)
        progressBar2 = view.findViewById(R.id.progressBar2)

        mushroomName.text = prediction.mushroomName
        probabilityField.text = "${prediction.probability.toString()}%"
        description.text = prediction.description

        setProbabilityColor(prediction.probability)
        setYourPhoto()
        setUpRecyclerView()
        Common.retrofitService.getMushroomSamples(prediction.mushroomName!!, 11).enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    setMushroomPhoto(response.body())
                    recyclerView.adapter = ImageAdapter(response.body()!!)
                    progressBar1.visibility = View.GONE
                    progressBar2.visibility = View.GONE
                } else {
                    Toast.makeText(requireContext(), "Error to get mushroom samples", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error to get mushroom samples", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setUpRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.isNestedScrollingEnabled = false
    }

    @SuppressLint("ResourceAsColor")
    private fun setProbabilityColor(probability: Int?) {
        if (probability != null) {
            if (probability < 25) {
                probabilityField.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            } else if (probability < 50) {
                probabilityField.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
            } else {
                probabilityField.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
        }
    }

    private fun setMushroomPhoto(mushrooms: List<String>?) {
        val imageAsBytes: ByteArray = decode(mushrooms?.get(0), URL_SAFE)
        mushroomPhoto.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size))
    }

    private fun setYourPhoto() {
        val imageAsBytes: ByteArray = decode(encodedImage, URL_SAFE)
        yourPhoto.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size), 800, 800, true))
    }
}