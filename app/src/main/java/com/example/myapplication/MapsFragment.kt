package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback {
    private var map : GoogleMap ?= null
    private val requestLocationCode = 200

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestLocationCode)
            return
        }
        obtenerUbicacionActual()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == requestLocationCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionActual()
        } else {
            Toast.makeText(requireContext(), "Permiso de ubicación requerido", Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("MissingPermission")
    private fun obtenerUbicacionActual() {
        map?.isMyLocationEnabled = true
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            if (location != null) {
                val miUbicacion = LatLng(location.latitude, location.longitude)
                map?.addMarker(
                    MarkerOptions()
                        .position(miUbicacion)
                        .title("Mi ubicación actual")
                )
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 16f))
            } else {
                Toast.makeText(requireContext(), "No se pudo obtener ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }
}