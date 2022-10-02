package com.dwarf.mystoryapp.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.dwarf.mystoryapp.R
import com.dwarf.mystoryapp.adapter.StoriesAdapter
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.databinding.ActivityMainBinding
import com.dwarf.mystoryapp.ui.login.LoginActivity


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(
            UserPreferences.getInstance(dataStore)
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

    }

    private fun getAllStories(token: String) {
        val result = mainViewModel.getAllStories(token)
        result.observe(this){
            when(it) {
                is Result.Loading -> {
                    /* binding.progressBar.visibility = View.VISIBLE*/
                }
                is Result.Error -> {
                    /* binding.progressBar.visibility = View.GONE*/
                    val data = it.error
                    Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    /* binding.progressBar.visibility = View.GONE*/
                    storiesAdapter.submitList(it.data)
                }
            }
        }
    }

    private fun setAdapter() {
       binding.rvStories.apply {
           layoutManager = LinearLayoutManager(this@MainActivity)
           setHasFixedSize(true)
           adapter = storiesAdapter
       }
    }

    private fun isTokenAvailable() {
        mainViewModel.getToken().observe(this){
            if (it == ""){
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                getAllStories(it)
                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_logout ->{
                mainViewModel.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show()
                true
            }
            else -> true
        }
    }
}