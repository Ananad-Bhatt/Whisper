package project.social.whisper

import adapters.GlobalStaticAdapter
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import project.social.whisper.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var lat:Double = 0.0
    private var long:Double = 0.0

    private lateinit var marker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lat = intent.getDoubleExtra("lat",0.0)
        long = intent.getDoubleExtra("long",0.0)
        //Log.d("MAPS_ERROR","HEllo1")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //Log.d("MAPS_ERROR","HEllo2")

        binding.btnLocActSend.setOnClickListener {

            val returnIntent = Intent()
            returnIntent.putExtra("lat", (marker.position.latitude).toString())
            returnIntent.putExtra("long", (marker.position.longitude).toString())
            setResult(Activity.RESULT_OK, returnIntent)
            finish()

        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //Log.d("MAPS_ERROR","HEllo")
        // Add a marker in location and move the camera
        val sydney = LatLng(lat, long)
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID;
        marker = mMap.addMarker(MarkerOptions().position(sydney).title("User location").draggable(true))!!
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18f))
        //Log.d("MAPS_ERROR","HEllo3")
    }
}