package com.au.a4x4vehiclehirefraser.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Photo
import com.au.a4x4vehiclehirefraser.dto.Service
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.add_service_row.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

open class HelperFragment:Fragment() {

    protected val SAVE_IMAGE_REQUEST_CODE: Int = 1999
    protected val CAMERA_REQUEST_CODE: Int = 1998
    protected val CAMERA_PERMISSION_REQUEST_CODE = 1997
    private lateinit var currentPhotoPath: String
    protected var photoURI : Uri? = null
    protected lateinit var preference: SharedPreference


    //See if we have permission
    internal fun prepTakePhoto() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            takePhoto()
        } else {
            val permissionRequest = arrayOf(Manifest.permission.CAMERA);
            requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context!!.packageManager)
            if (takePictureIntent == null) {
                Toast.makeText(context, "Unable to save photo", Toast.LENGTH_LONG).show()
            } else {
                // if we are here, we have a valid intent.
                val photoFile: File = createImageFile()
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        activity!!.applicationContext,
                        "com.au.a4x4vehiclehirefraser.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, SAVE_IMAGE_REQUEST_CODE)
                }
            }
        }
    }

    private fun createImageFile(): File {
        // genererate a unique filename with date.
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // get access to the directory where we can write pictures.
        val storageDir: File? = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("PlantDiary${timestamp}", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted, let's do stuff.
                    takePhoto()
                } else {
                    Toast.makeText(context,"Unable to take photo without permission",Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }

        }
    }

    inner class VehicleAdapter(val vehicles: List<Vehicle>, val itemLayout: Int) : RecyclerView.Adapter<HelperFragment.VehicleViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
            return VehicleViewHolder(view)
        }


        override fun getItemCount(): Int {
            return vehicles.size
        }

        override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
            val vehicle = vehicles.get(position)
            holder.showVehicles(vehicle)
        }

    }

    inner class VehicleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private var imgVehicle : ImageView = itemView.findViewById(R.id.vehicle_image)
        private var lblVehicleDescription: TextView = itemView.findViewById(R.id.lblVehicle)

        fun showVehicles(vehicle:Vehicle){

            lblVehicleDescription.setText(vehicle.toString())

        }

    }

    inner class ServiceAdapter(val services: List<Service>, val itemLayout: Int) : RecyclerView.Adapter<HelperFragment.ServiceViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
            return ServiceViewHolder(view)
        }


        override fun getItemCount(): Int {
            return services.size
        }

        override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
            val service = services.get(position)
            holder.showVehicles(service)
        }
    }

    inner class ServiceViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private var lblService: TextView = itemView.findViewById(R.id.lblService)

        fun showVehicles(service:Service){
            lblService.setText(service.toString())
        }
    }
}