package com.example.pulseid

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseid.adapter.HospitalAdapter
import com.example.pulseid.model.Hospital
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class NearbyHospitalActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var recyclerView: RecyclerView
    private lateinit var hospitalAdapter: HospitalAdapter
    private val hospitals = mutableListOf<Hospital>()

    private val API_KEY = "AIzaSyCha03B6DUP9JBqsTWN-Lknvpt-px01dUc"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_hospital)

        recyclerView = findViewById(R.id.recyclerHospitalList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        hospitalAdapter = HospitalAdapter(hospitals) { hospital ->
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hospital.latLng, 16f))
            val markerOptions = MarkerOptions().position(hospital.latLng).title(hospital.name)
            mMap.addMarker(markerOptions)?.showInfoWindow()
        }
        recyclerView.adapter = hospitalAdapter

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val latLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                mMap.addMarker(MarkerOptions().position(latLng).title("You are here"))
                fetchNearbyHospitals(latLng)
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchNearbyHospitals(latLng: LatLng) {
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${latLng.latitude},${latLng.longitude}&radius=3000&type=hospital&key=$API_KEY"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@NearbyHospitalActivity, "API Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: "")
                val results = json.getJSONArray("results")
                hospitals.clear()

                for (i in 0 until minOf(5, results.length())) {
                    val obj = results.getJSONObject(i)
                    val name = obj.getString("name")
                    val loc = obj.getJSONObject("geometry").getJSONObject("location")
                    val lat = loc.getDouble("lat")
                    val lng = loc.getDouble("lng")
                    val latLng = LatLng(lat, lng)

                    val hospital = Hospital(name, latLng)
                    hospitals.add(hospital)
                }

                runOnUiThread {
                    hospitalAdapter.notifyDataSetChanged()
                    addHospitalMarkers()
                }
            }
        })
    }

    private fun addHospitalMarkers() {
        for (hospital in hospitals) {
            mMap.addMarker(
                MarkerOptions().position(hospital.latLng).title(hospital.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }
    }
}




