package com.au.a4x4vehiclehirefraser.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.au.a4x4vehiclehirefraser.MainActivity
import com.au.a4x4vehiclehirefraser.R
import com.au.a4x4vehiclehirefraser.dto.Photo
import com.au.a4x4vehiclehirefraser.dto.Vehicle
import com.au.a4x4vehiclehirefraser.helper.Constants.DELETED_VEHICLE
import com.au.a4x4vehiclehirefraser.helper.Constants.REGO
import com.au.a4x4vehiclehirefraser.helper.Constants.TYPE_INDEX
import com.au.a4x4vehiclehirefraser.helper.Constants.USER_ID
import com.au.a4x4vehiclehirefraser.helper.Constants.VEHICLE_INDEX
import com.au.a4x4vehiclehirefraser.helper.Helper.toast
import com.au.a4x4vehiclehirefraser.helper.SharedPreference
import kotlinx.android.synthetic.main.add_service_fragment.*
import kotlinx.android.synthetic.main.add_vehicle_fragment.*

class AddVehicleFragment : HelperFragment() {

    private lateinit var mainViewModel: MainViewModel
    private val IMAGE_GALLERY_REQUEST_CODE: Int = 2001
    private var photos: ArrayList<Photo> = ArrayList<Photo>()

    companion object {
        fun newInstance() = AddVehicleFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_vehicle_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity.let {
            mainViewModel = ViewModelProviders.of(it!!).get(MainViewModel::class.java)
        }

        preference = SharedPreference(requireContext())
        clearFields()

        vehicleModelSpinner.setAdapter(
            ArrayAdapter.createFromResource(
                context!!,
                R.array.vehicle_model,
                android.R.layout.simple_spinner_item
            )
        )

        saveRepairBtn.setOnClickListener {
            saveVehicle()
        }

        cmdReturnFromRepairToMain.setOnClickListener {
            clearFields()
            (activity as MainActivity).showMainFragment()
        }

        takePhotoBtn.setOnClickListener {
            prepTakePhoto()
            //prepOpenImageGallery()
        }


        deleteVehicleBtn.setOnClickListener {
            deleteVehicle()
        }

        mainViewModel.getAllVehicles()

        //Callback from MainViewModel --> getAllVehicles
        mainViewModel.showAllVehicles.observe(viewLifecycleOwner, Observer { lstVehicle ->
            lstVehicle?.getContentIfNotHandledOrReturnNull()?.let {

                rcyVehicle.hasFixedSize()
                rcyVehicle.layoutManager = LinearLayoutManager(context)
                rcyVehicle.itemAnimator = DefaultItemAnimator()
                vehicle_Recycler_Header.visibility = View.VISIBLE
                rcyVehicle.adapter = VehicleAdapter(it, R.layout.add_vehicle_row,
                    onClickListener = { view, vehicle -> openVehicle(view, vehicle) })

            }
        })

        mainViewModel.showVehiclePerRego.observe(viewLifecycleOwner, Observer { vehicle ->
            vehicle?.getContentIfNotHandledOrReturnNull()?.let { vehicle ->

                var models = resources.getStringArray(R.array.vehicle_model)
                var index = 0


                //Todo:Fix This
                run breaker@{
                    models.forEach {
                        if (it.equals(vehicle.model)) {
                            return@breaker
                        }
                        index++
                    }
                }

                with(vehicle) {
                    vehicleModelSpinner.setSelection(index)
                    vehicleDescripion.setText(description)
                    vehicleYearModel.setText(yearModel.toString())
                    vehicleKms.setText(kms.toString())
                    vehicleRego.setText(rego)
                    vehicleColor.setText(color)
                }

                preference.save(REGO,vehicle.rego)
                vehicle_add_scrollview.fullScroll(ScrollView.FOCUS_UP)
            }
        })

        mainViewModel.displayToast.observe(viewLifecycleOwner, Observer { message ->
            message?.getContentIfNotHandledOrReturnNull()?.let {
               DELETED_VEHICLE.toast(context!!,false)
                preference.save(VEHICLE_INDEX,0)
                preference.save(TYPE_INDEX,0)
                startActivity(Intent(activity,MainActivity::class.java))
            }
        })
    }

    private fun deleteVehicle() {
        mainViewModel.deleteVehicle(preference.getValueString(REGO))

    }

    private fun openVehicle(view: View, vehicle: Vehicle) {
        mainViewModel.getVehiclePerRego(vehicle.rego)
    }


    private fun clearFields() {
        vehicleRego.text.clear()
        var rego = vehicleRego.text.toString()
        vehicleDescripion.setText("")
        vehicleKms.text.clear()
        vehicleYearModel.text.clear()
        vehicleColor.text.clear()
    }

    private fun saveVehicle() {
        var vehicle = Vehicle()

        //Do Login Again
        if (preference.getValueString(USER_ID) == "") {
            (activity as MainActivity).showMainFragment()
        }
        preference.getValueString(USER_ID) ?: return

        vehicle.apply {
            rego = vehicleRego.text.toString()
            description = vehicleDescripion.text.toString()
            kms = vehicleKms.text.toString().toInt()
            model = vehicleModelSpinner.selectedItem.toString()
            yearModel = vehicleYearModel.text.toString().toInt()
            color = vehicleColor.text.toString()
        }.apply {
            clearFields()
            //rcyVehicle.adapter?.notifyDataSetChanged()
        }.apply {
            mainViewModel.saveVehicle(vehicle, photos, preference.getValueString(USER_ID))

        }.apply {
            (activity as MainActivity).showMainFragment()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                // now we can get the thumbnail
                val imageBitmap = data!!.extras!!.get("data") as Bitmap
                //vehicleImage.setImageBitmap(imageBitmap)
            } else if (requestCode == SAVE_IMAGE_REQUEST_CODE) {
                Toast.makeText(context, "Image Saved", Toast.LENGTH_LONG).show()
                //Only pass in the one
                var photo = Photo(localUri = photoURI.toString())
                photos.add(photo)
                //vehicleImage.setImageURI(photoURI)
            } else if (requestCode == IMAGE_GALLERY_REQUEST_CODE) {
                if (data != null && data.data != null) {
                    val image = data.data
                    val source = ImageDecoder.createSource(activity!!.contentResolver, image!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    //vehicleImage.setImageBitmap(bitmap)
                }
            }
        }
    }

}