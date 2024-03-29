package com.dwarf.mystoryapp.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.dwarf.mystoryapp.R
import com.dwarf.mystoryapp.adapter.LoadingStateAdapter
import com.dwarf.mystoryapp.adapter.StoriesAdapter
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.databinding.ActivityMainBinding
import com.dwarf.mystoryapp.ui.StoryViewModelFactory
import com.dwarf.mystoryapp.ui.addstory.AddStoryActivity
import com.dwarf.mystoryapp.ui.login.LoginActivity
import com.dwarf.mystoryapp.ui.map.MapsActivity


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        StoryViewModelFactory.getInstance(
            UserPreferences.getInstance(dataStore),this
        )
    }

    private lateinit var binding: ActivityMainBinding
    private val storiesAdapter = StoriesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isTokenAvailable()
        setAdapter()
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAllStories(token: String) {
        val result = mainViewModel.getAllStories(token)
        result.observe(this){
            val storyData = it
            storiesAdapter.submitData(lifecycle,storyData)
        }
    }

    private fun setAdapter() {
       binding.rvStories.apply {
           layoutManager = LinearLayoutManager(this@MainActivity)
           setHasFixedSize(true)
           adapter = storiesAdapter.withLoadStateFooter(
               footer = LoadingStateAdapter {
                   storiesAdapter.retry()
               }
           )
       }
    }

    private fun isTokenAvailable() {
        mainViewModel.getUser().observe(this){ user ->
            if (user.token.isEmpty()){
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                getAllStories(user.token)
            }
            Log.d("Main", user.token)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_logout ->{
                mainViewModel.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                Toast.makeText(this, getString(R.string.toast_logout_success), Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_language ->{
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> true
        }
    }
}