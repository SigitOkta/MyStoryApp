package com.dwarf.mystoryapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dwarf.mystoryapp.R
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.databinding.ActivityLoginBinding
import com.dwarf.mystoryapp.ui.main.MainActivity
import com.dwarf.mystoryapp.ui.signup.SignUpActivity
import com.dwarf.mystoryapp.utils.LoadingDialog

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        LoginModelFactory.getInstance(
            UserPreferences.getInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        playAnimation()
        setMyButtonEnable()
        binding.edtTxtPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.myButton.setOnClickListener(this)
        binding.tvSignUp.setOnClickListener(this)
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivStoryImg, View.TRANSLATION_X,-30f,30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.llTitle, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtTxtEmail, View.ALPHA, 1f).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val edtPassword = ObjectAnimator.ofFloat(binding.edtTxtPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.myButton, View.ALPHA, 1f).setDuration(500)
        val tvOrSignup = ObjectAnimator.ofFloat(binding.tvOrSign, View.ALPHA, 1f).setDuration(500)
        val tvSignup = ObjectAnimator.ofFloat(binding.tvSignUp, View.ALPHA, 1f).setDuration(500)

        val together =  AnimatorSet().apply {
            playTogether(tvOrSignup,tvSignup)
        }

        AnimatorSet().apply {
            playSequentially(title,tvEmail,edtEmail,tvPassword,edtPassword,btnLogin,together)
            start()
        }
    }

    private fun setMyButtonEnable() {
        val resultEmail = binding.edtTxtEmail.text
        val resultPass = binding.edtTxtPassword.text
        binding.myButton.isEnabled =
            resultEmail != null && resultEmail.toString()
                .isNotEmpty() && isEmailValid(resultEmail.toString())
                    && resultPass != null && resultPass.toString()
                .isNotEmpty() && resultPass.toString().length >= 6
    }

    private fun isEmailValid(s: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(s).matches()
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.my_button -> {
                val resultEmail = binding.edtTxtEmail.text
                val resultPass = binding.edtTxtPassword.text
                val result = loginViewModel.loginUser(resultEmail.toString(), resultPass.toString())
                result.observe(this) {
                    when (it) {
                        is Result.Loading -> {
                           LoadingDialog.startLoading(this)
                        }
                        is Result.Error -> {
                            LoadingDialog.hideLoading()
                            val data = it.error
                            Toast.makeText(this, data, Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            LoadingDialog.hideLoading()
                            val data = it.data
                            Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
                            loginViewModel.saveToken(data.loginResult.token)
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
            R.id.tv_sign_up -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }
}