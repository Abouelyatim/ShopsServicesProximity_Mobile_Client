package com.smartcity.client.ui.main.flash_notification.flash

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.smartcity.client.R


class StoreBottomSheetDialog (
    val storeNameValue:String,
    val storeAddressValue:String,
    val latitude:Double?,
    val longitude:Double?)
    : BottomSheetDialogFragment() , OnMapReadyCallback {

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var mMapView: MapView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        bottomSheetDialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
        val  dialogView = layoutInflater.inflate(R.layout.dialog_store_detail, null)
        bindViews(dialogView,savedInstanceState)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(dialogView)

        return bottomSheetDialog
    }

    private fun bindViews(view: View, savedInstanceState: Bundle?) {
        loadMap(view,savedInstanceState)
        loadStoreName(view)
        loadStoreAddress(view)
        backButton(view)
        loadGoogleMap(view)
    }

    private fun loadGoogleMap(view: View) {
        val googleMapButton=view.findViewById<Button>(R.id.store_address_open_google_map)
        googleMapButton.setOnClickListener {
            if (latitude != null && longitude != null) {
                val gmmIntentUri = Uri.parse("geo:0,0?q=${latitude},${longitude}(Google)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                startActivity(mapIntent)
            }
        }
    }

    private fun backButton(view: View) {
        val backButton=view.findViewById<ImageView>(R.id.back_store)
        backButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    private fun loadStoreAddress(view: View) {
        val storeAddress=view.findViewById<TextView>(R.id.store_address)
        storeAddress.text = storeAddressValue
    }

    private fun loadStoreName(view: View) {
        val storeName=view.findViewById<TextView>(R.id.store_name)
        storeName.text = storeNameValue
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadMap(view: View, savedInstanceState: Bundle?) {
        mMapView = view.findViewById(R.id.store_address_map) as MapView
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume() // needed to get the map to display immediately
        mMapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap!!.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(context,R.raw.store_view_map_style)
        )
        googleMap.uiSettings.setAllGesturesEnabled(false)
        if(latitude!=null && longitude!=null){
            val marker=googleMap.addMarker(
                MarkerOptions()
                    .draggable(false)
                    .position(LatLng(latitude!!,longitude!!))
                    .title(storeNameValue)
            )
            marker.showInfoWindow()
            val zoomLevel = 14.0f
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!, longitude!!), zoomLevel))
            googleMap.setMinZoomPreference(16.0f)
        }
    }
}