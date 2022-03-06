package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    /**Declaration (viewModel, binding, map)*/
    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map: GoogleMap
    //Default map values
    private var latitude = 37.422160 // home
    private var longitude = -122.084270
    val zoomLevel = 15f


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    lateinit var locationManager: LocationManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        /**Binding*/
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)
        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        /**Map Fragment*/
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.buttonSave.setOnClickListener {
            onLocationSelected()
        }

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

        return binding.root
    }


    /**MAP SECTION*/

            /**Sets the marker at homeLatLng and moves the camera +
             * call the sub functions*/
            override fun onMapReady(p0: GoogleMap) {
                map=p0
                val homeLatLng = LatLng(latitude, longitude)
                map.addMarker(MarkerOptions().position(homeLatLng).title("Marker at Home"))
                //map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng,zoomLevel))

                setMapStyle(map)
                setPoiClick(map)
                setMapLongClick(map)
                enableLocation()
                // TODO Get the current location of the device and set the position of the map.
                getDeviceLocation()
            }


    private fun getDeviceLocation() {

    }
                    /**Map Style*/
                    private fun setMapStyle(map: GoogleMap) {
                        try {
                            val success = map.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(
                                    requireActivity(),
                                    R.raw.map_style
                                )
                            )
                        } catch (e: Resources.NotFoundException) { }
                    }
                    /**POI*/
                    private fun setPoiClick(map: GoogleMap) {
                        map.setOnPoiClickListener { poi ->
                            val poiMarker = map.addMarker(
                                MarkerOptions()
                                    .position(poi.latLng)
                                    .title(poi.name)
                            )
                            poiMarker.showInfoWindow()
                        }
                    }
                    /**Long click method = add a marker*/
                    private fun setMapLongClick(map:GoogleMap){
                        map.setOnMapLongClickListener { latLng ->
                            //Snippet info
                            val snippet = String.format(
                                Locale.getDefault(),
                                "Lat: %1$.5f, Long: %2$.5f",
                                latLng.latitude,
                                latLng.longitude
                            )
                            //Marker
                            map.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .snippet(snippet))
                            setSelectedLocation(latLng, resources.getString(R.string.selected_location))
                        }
                    }
                    /**Location permission manager with pop up request*/
                    private fun enableLocation() {
                        if (isPermissionGranted()) { return
                        } else {
                            // Permission to access the location is missing. Show rationale and request permission
                            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_LOCATION_PERMISSION)
                        }
                    }

            /**Map helpers*/

            /** Check if location permissions are granted and if so enable the location data layer.*/
            override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
                if (requestCode == REQUEST_LOCATION_PERMISSION) {
                    if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        enableLocation()
                    }
                }
            }

            private fun setSelectedLocation(latLng: LatLng, name: String, poi: PointOfInterest? = null) {
                _viewModel.latitude.value = latLng.latitude
                _viewModel.longitude.value = latLng.longitude
                _viewModel.selectedPOI.value = poi
                _viewModel.reminderSelectedLocationStr.value = name
            }

            private fun onLocationSelected() {
                /**When the user confirms on the selected location,
                 * send back the selected location details to the view model
                 * and navigate back to the previous fragment to save the reminder and add the geofence*/
                _viewModel.navigationCommand.value = NavigationCommand.Back
            }

            /**Method that checks if location permission is enabled*/
            private fun isPermissionGranted() : Boolean {
                return checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
            }

    /**MENU SECTION*/

            /**Inflate the menu from res*/
            override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.map_options, menu)
            }

            /**Change the map type based on the user's selection from the menu*/
            override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

                R.id.normal_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_NORMAL
                    true
                }
                R.id.hybrid_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_HYBRID
                    true
                }
                R.id.satellite_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                    true
                }
                R.id.terrain_map -> {
                    map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1
    }

}