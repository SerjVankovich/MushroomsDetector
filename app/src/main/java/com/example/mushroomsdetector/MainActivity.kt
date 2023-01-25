package com.example.mushroomsdetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mushroomsdetector.fragments.MainFragment


class MainActivity : AppCompatActivity() {
    companion object {
        val CAMERA_REQUEST = 1
        val REQUEST_CAMERA_PERMISSION = 2
    }

    lateinit var mainFragment: MainFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestCameraPermissions()
        mainFragment = MainFragment(supportFragmentManager.beginTransaction())
        loadMainFragment()
    }

    private fun loadMainFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, mainFragment)
        fragmentTransaction.commit()
    }

    private fun requestCameraPermissions() {
        val cameraPermission = Manifest.permission.CAMERA
        val readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val writeStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, listOf(cameraPermission, readStoragePermission, writeStoragePermission).toTypedArray(), REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mainFragment.setNewTransaction(supportFragmentManager.beginTransaction())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != REQUEST_CAMERA_PERMISSION) {
            return
        }
        for (result in grantResults) {
            if (result == 0) {
                return
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}