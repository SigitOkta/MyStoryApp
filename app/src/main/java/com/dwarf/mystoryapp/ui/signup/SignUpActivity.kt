package com.dwarf.mystoryapp.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.databinding.ActivitySignUpBinding
import com.dwarf.mystoryapp.ui.login.LoginActivity

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivitySignUpBinding
    private val signupViewModel: SignupViewModel by viewModels {
      SignupViewModelFactory.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivStoryImg, View.TRANSLATION_X,-30f,30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.llTitle, View.ALPHA, 1f).setDuration(500)
        val tvName = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val edtName = ObjectAnimator.ofFloat(binding.edtTxtName, View.ALPHA, 1f).setDuration(500)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtTxtEmail, View.ALPHA, 1f).setDuration(500)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val edtPassword = ObjectAnimator.ofFloat(binding.edtTxtPassword, View.ALPHA, 1f).setDuration(500)
        val btnSignup = ObjectAnimator.ofFloat(binding.myButton, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(title,tvName,edtName,tvEmail,edtEmail,tvPassword,edtPassword,btnSignup)
            start()
        }
    }

    private fun setMyButtonEnable() {
        val resultName = binding.edtTxtName.text
        val resultEmail= binding.edtTxtEmail.text
        val resultPass = binding.edtTxtPassword.text
        binding.myButton.isEnabled = resultName != null && resultName.toString().isNotEmpty() &&
                resultEmail != null && resultEmail.toString().isNotEmpty() && isEmailValid(resultEmail.toString())
                && resultPass != null && resultPass.toString().isNotEmpty() && resultPass.toString().length >= 6
    }

    private fun isEmailValid(s: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(s).matches()
    }

    override fun onClick(p0: View?) {
        val resultName = binding.edtTxtName.text
        val resultEmail= binding.edtTxtEmail.text
        val resultPass = binding.edtTxtPassword.text
        val result = signupViewModel.signUpUser(resultName.toString(), resultEmail.toString(), resultPass.toString())
        result.observe(this){
            when (it) {
                is Result.Loading -> {

                }
                is Result.Error -> {
                    val error = it.error
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }

                is Result.Success -> {
                    val message = it.data.message
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                       startActivity(intent)
                }

            }
        }
    }
}