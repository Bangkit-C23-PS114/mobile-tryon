package com.example.storyapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.databinding.ActivityRegisBinding
import com.example.storyapp.dataclass.RequestLogin
import com.example.storyapp.dataclass.RequestRegister
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.viewmodel.MainViewModel
import com.example.storyapp.viewmodel.RegisterViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisBinding
    private val regisViewModel: RegisterViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()
    private var isPasswordMatch: Boolean = false
    lateinit var firstname: String
    lateinit var lastname: String

    private lateinit var email: String
    private lateinit var pass: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        setAction()
        val pref = MyPreference.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[LoginViewModel::class.java]



        loginViewModel.getLoginState().observe(this) { state ->
            if (state) {
                val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                loginViewModel.saveToken("")
                loginViewModel.saveName("")
            }
        }

        regisViewModel.message.observe(this) {
            checkResponseeRegister(it, regisViewModel.isError)
        }

        regisViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        mainViewModel.userlogin.observe(this) {
            loginViewModel.saveLoginState(true)
            loginViewModel.saveName(it.data.get(0).firstName)

        }
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }


    }

    private fun setAction() {
        binding.tiCpass.setOnFocusChangeListener { v, focused ->
            if (v != null) {
                if (!focused) {
                    isPasswordMatch()
                }
            }
        }

        binding.tiPass.setOnFocusChangeListener { v, focused ->
            if (v != null) {
                if (!focused) {
                    isPasswordMatch()
                }
            }
        }


        binding.btRegis.setOnClickListener {
            Log.d("TAG","DATA BALID")

            binding.apply {
                tiEmail.clearFocus()
                tiFirstname.clearFocus()
                tiLastname.clearFocus()
                tiPass.clearFocus()
                tiCpass.clearFocus()
            }


                Log.d("TAG","DATA BALID")
                firstname = binding.tiFirstname.text.toString().trim()
                lastname = binding.tiLastname.text.toString().trim()
                email = binding.tiEmail.text.toString().trim()
                pass = binding.tiPass.text.toString().trim()
                val user = RequestRegister(
                    email,
                    pass,
                    firstname,
                    lastname,
                )

                regisViewModel.getResponseRegister(user)
        }

        binding.seePassword.setOnClickListener {
            if (binding.seePassword.isChecked) {
                binding.tiPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.tiCpass.transformationMethod = HideReturnsTransformationMethod.getInstance()

            } else {
                binding.tiPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.tiCpass.transformationMethod = PasswordTransformationMethod.getInstance()

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun checkResponseeRegister(msg: String, isError: Boolean) {
        if (!isError) {
            Toast.makeText(this, getString(R.string.user_created), Toast.LENGTH_SHORT).show()
            val user = RequestLogin(
                email,
                pass
            )
            mainViewModel.getResponseLogin(user)

        } else {
            when (msg) {
                "Bad Request" -> {
                    Toast.makeText(this, getString(R.string.email_taken), Toast.LENGTH_SHORT).show()
                    binding.tiEmail.apply {
                        setText("")
                        requestFocus()
                    }
                }
                "timeout" -> {
                    Toast.makeText(this, getString(R.string.timeout), Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Toast.makeText(
                        this,
                        "${getString(R.string.error_message)} $msg",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun isDataValid(): Boolean {
        return binding.tiFirstname.isNameValid && binding.tiEmail.isEmailValid &&
                binding.tiPass.isPassValid && binding.tiCpass.isCPassValid && isPasswordMatch
    }

    private fun isPasswordMatch() {
        if (binding.tiPass.text.toString().trim() != binding.tiCpass.text.toString().trim()) {
            binding.tiCpass.error = resources.getString(R.string.pass_not_match)
            isPasswordMatch = false
        } else {
            isPasswordMatch = true
            binding.tiCpass.error=null
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    @SuppressLint("RestrictedApi")
    private fun setActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


}