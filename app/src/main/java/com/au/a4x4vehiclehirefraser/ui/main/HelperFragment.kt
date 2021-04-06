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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Service
import com.au.a4x4vehiclehirefraser.dto.ServiceItem
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.au.a4x4vehiclehirefraser.helper.Constants.SERVICE_ITEM_ID
import com.au.a4x4vehiclehirefraser.helper.Helper.roundTo
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
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

    inner class VehicleAdapter(val vehicles: List<Vehicle>,
                               val itemLayout: Int,
                               private val onClickListener: (View, Vehicle) -> Unit) : RecyclerView.Adapter<HelperFragment.VehicleViewHolder>() {

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
            holder.itemView.setOnClickListener { view -> onClickListener(view, vehicle) }

        }
    }

        inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private var lblYearModel: TextView = itemView.findViewById(R.id.lblYearModel)
            private var lblRego: TextView = itemView.findViewById(R.id.lblRego)
            private var lblVehicleDescription: TextView = itemView.findViewById(R.id.lblVehicle)

            fun showVehicles(vehicle: Vehicle) {

                lblYearModel.setText(vehicle.yearModel.toString())
                lblRego.setText(vehicle.rego)
                lblVehicleDescription.setText(vehicle.toString())

            }

        }


    inner class ServiceAdapter(val services: List<Service>,
                               val itemLayout: Int,
                               private val onClickListener: (View, Service) -> Unit) : RecyclerView.Adapter<HelperFragment.ServiceViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
            return ServiceViewHolder(view)
        }


        override fun getItemCount(): Int {
            return services.size
        }

        override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
            val service = services.get(position)
            holder.showService(service)
            holder.itemView.setOnClickListener { view -> onClickListener(view, service)}
        }
    }

    inner class ServiceViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private var lblServiceDate: TextView = itemView.findViewById(R.id.lblServiceDate)
        private var lblServicePrice: TextView = itemView.findViewById(R.id.lblServicePrice)
        private var lblServiceDescription: TextView = itemView.findViewById(R.id.lblServiceDescription)

        fun showService(service:Service){
            with(service){
                lblServiceDate.setText(date.toString())
                lblServicePrice.setText("$" + price!!.roundTo(2).toString())
                lblServiceDescription.setText(description.toString())
            }
        }
    }

    inner class ServiceItemAdapter(val services: List<ServiceItem>,
                                   val itemLayout: Int,
                                   private val onClickListener: (View, ServiceItem) -> Unit) : RecyclerView.Adapter<HelperFragment.ServiceItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceItemViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)
            return ServiceItemViewHolder(view)
        }


        override fun getItemCount(): Int {
            return services.size
        }

        override fun onBindViewHolder(holder: ServiceItemViewHolder, position: Int) {
            val serviceItem = services.get(position)
            holder.showService(serviceItem)
            holder.itemView.setOnClickListener { view ->
                onClickListener(view, serviceItem)
            }
        }
    }

    inner class ServiceItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        private var serviceItemWrapper: LinearLayout = itemView.findViewById(R.id.wrapperServiceItem)
        private var lblDescription: TextView = itemView.findViewById(R.id.lblServiceItemDescription)
        private var lblPrice: TextView = itemView.findViewById(R.id.lblServiceItemPrice)
        private var lblQuantity: TextView = itemView.findViewById(R.id.lblServiceItemQuantity)

        fun showService(serviceItem: ServiceItem){
            with(serviceItem){
                lblDescription.setText(description)
                lblPrice.setText("$" + price!!.roundTo(2).toString())
                lblQuantity.setText(quantity)

            }

//            serviceItemWrapper.setOnClickListener {
//                var xx = serviceItem.description
//            }
        }
    }
}

