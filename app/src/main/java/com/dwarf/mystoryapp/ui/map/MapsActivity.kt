package com.dwarf.mystoryapp.ui.map

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dwarf.mystoryapp.R
import com.dwarf.mystoryapp.adapter.CustomInfoWindowForGoogleMap
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.databinding.ActivityMapsBinding
import com.dwarf.mystoryapp.ui.StoryViewModelFactory
import com.dwarf.mystoryapp.ui.storydetail.StoryDetailActivity
import com.dwarf.mystoryapp.ui.storydetail.StoryDetailActivity.Companion.EXTRA_STORY
import com.dwarf.mystoryapp.utils.LoadingDialog
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()

    private val mapViewModel: MapViewModel by viewModels {
        StoryViewModelFactory.getInstance(
            UserPreferences.getInstance(dataStore), this
        )
    }

    companion object{
        private val TAG = MapsActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getToken()
        setMapStyle()
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, getString(R.string.text_parsing_failed))
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, getString(R.string.text_error_cant_find_style), exception)
        }
    }

    private fun getToken() {
        mapViewModel.getUser().observe(this) { user ->
            if (user.token.isNotEmpty()) loadAllMarker(user.token)
        }
    }

    private fun loadAllMarker(token: String) {
        val result = mapViewModel.getAllStoriesWithLocation(token)
        result.observe(this) {
            when (it) {
                is Result.Error -> {
                    LoadingDialog.hideLoading()
                    val data = it.error
                    Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    LoadingDialog.hideLoading()
                    val listStory = it.data
                    addMarkers(listStory)
                }
                is Result.Loading -> LoadingDialog.startLoading(this)
            }
        }

    }

    private fun addMarkers(listStory: List<StoryEntity>) {
        listStory.forEach { stories ->
            val latLng = LatLng(stories.lat, stories.lon)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(stories.name)
            )
            boundsBuilder.include(latLng)
            marker?.tag = stories
            mMap.setInfoWindowAdapter(CustomInfoWindowForGoogleMap(this))
            mMap.setOnInfoWindowClickListener {
                val intent = Intent(this, StoryDetailActivity::class.java)
                intent.putExtra(EXTRA_STORY, it.tag as StoryEntity)
                startActivity(intent)
            }
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}