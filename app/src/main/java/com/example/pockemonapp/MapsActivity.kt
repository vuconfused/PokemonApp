package com.example.pockemonapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pockemonapp.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.android.synthetic.main.activity_maps.*
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkPermission()
        LoadPockemon()
    }
    var ACCESSLOCATION = 123
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){

            if(ActivityCompat
                    .checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
                return
            }
        }
        GerUserLocation()
    }


    fun GerUserLocation(){
        Toast.makeText(this, "User location access on", Toast.LENGTH_LONG ).show()
        //TODO: Will implement later

        var myLocation = MylocationListener()

        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLocation)
        var mythread = myThread()
        mythread.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when(requestCode){

            ACCESSLOCATION->{

                if(grantResults[0]==PackageManager.PERMISSION_GRANTED ){
                    GerUserLocation()
                }else{
                    Toast.makeText(this, "We cannot access to your location", Toast.LENGTH_LONG ).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

        // Add a marker in Sydney and move the camera

    }

    // Get user location
    var location: Location?=null
    inner class MylocationListener: LocationListener{


        constructor(){
            location= Location("Start")
            location!!.longitude=0.0
            location!!.latitude=0.0
        }
        override fun onLocationChanged(p0: Location) {
            location=p0
        }
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

            //TODO("Not implemented") // to change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(p0: String) {
            //TODO("Not implemented") // to change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(p0: String) {
            //TODO("Not implemented") // to change body of created functions use File | Settings | File Templates.
        }

    }

    var oldLocation:Location?=null
    inner class myThread: Thread{
        constructor():super(){
            oldLocation= Location("Start")
            oldLocation!!.longitude=0.0
            oldLocation!!.latitude=0.0
        }
        override fun run(){

            while(true){
                try{

                    if(oldLocation!!.distanceTo(location)==0f){

                        continue
                    }

                    oldLocation=location
                    runOnUiThread {

                        mMap!!.clear()

                        //show me
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap!!.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("Me").snippet(" Here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ash_23)))
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 7f))
                        // show pockemons

                        for(i in 0..listPockemons.size-1) {
                            var newPockemon = listPockemons[i]

                            if(newPockemon.IsCatch==false){
                                val pockemonLoc = LatLng(newPockemon.location!!.latitude, newPockemon.location!!.longitude)
                                mMap!!.addMarker(
                                    MarkerOptions()
                                        .position(pockemonLoc)
                                        .title(newPockemon.name!!)
                                        .snippet(newPockemon.des!! + ",power:" + newPockemon!!.power)
                                        .icon(BitmapDescriptorFactory.fromResource(newPockemon.image!!)))

                                if(location!!.distanceTo(newPockemon.location)<2){
                                    newPockemon.IsCatch=true
                                    listPockemons[i] = newPockemon

                                    playerPower+=newPockemon.power!!
                                    Toast.makeText(applicationContext, " You catch new Pockemon your new power is "+ playerPower,Toast.LENGTH_LONG).show()
                                }

                            }
                        }
                    }
                    Thread.sleep(1000)
                }catch (ex:Exception){}
            }
        }
    }

    var playerPower=0.0
    var listPockemons = ArrayList<Pockemon>()

    fun LoadPockemon(){

        listPockemons.add(Pockemon(R.drawable.arceus,
            "Arceus", "Arceus living in japan", 30.0, 37.3323,-122.023))
        listPockemons.add(Pockemon(R.drawable.arceus_kleavor,
            "Arceus Kleavor", "Arceus Kleavor living in japan", 30.1, 37.3473,-122.021))
        listPockemons.add(Pockemon(R.drawable.rua_3,
            "rua_3", "rua_3 living in japan", 30.2, 37.3375,-122.086))
    }
}