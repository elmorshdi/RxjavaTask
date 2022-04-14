package com.elmorshdi.internTask.view.ui.fragment

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.elmorshdi.internTask.R
import com.elmorshdi.internTask.databinding.LoginFragmentBinding
import com.elmorshdi.internTask.helper.toast
import com.elmorshdi.internTask.view.viewmodel.ShareViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: LoginFragmentBinding
    private val viewModel: ShareViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmailEditText.text?.trim().toString()
            val password = binding.loginPasswordEditText.text?.trim().toString()
            viewModel.login(email, password)
        }
        viewModel.uiStateFlow.observe(viewLifecycleOwner, Observer {event ->

                when (event) {
                    is ShareViewModel.UiState.Success -> {
                        binding.loginSpinKit.isVisible = false

                        navigateToMain()
                    }
                    is ShareViewModel.UiState.NetworkError -> {
                        requireContext().toast(event.errorMessage)
                    }
                    is ShareViewModel.UiState.ValidationError -> {
                        clearError()
                        binding.loginSpinKit.isVisible = false
                        when (event.error) {

                            is ShareViewModel.Error.PasswordNotValid -> {
                                binding.loginPasswordTextField.boxStrokeColor = Color.RED
                                binding.errorTextView.isVisible = true
                                binding.errorTextView.text = getString(R.string.Invalid_password)
                            }
                            is ShareViewModel.Error.EmailNotValid -> {
                                binding.loginEmailTextField.error =
                                    getString(R.string.enter_valid_email)
                            }
                            else -> {
                                binding.loginEmailTextField.error =
                                    getString(R.string.an_error_occurred)
                            }
                        }
                    }
                    ShareViewModel.UiState.Loading -> {
                        clearError()
                        binding.loginSpinKit.isVisible = true
                    }
                    else -> Unit
                }
        })
    }
    private fun clearError() {
        binding.loginEmailTextField.isErrorEnabled = false
        binding.loginPasswordTextField.boxStrokeColor = Color.parseColor("#FF3E497A")
        binding.errorTextView.isVisible = false
    }

    private fun navigateToMain() {
        val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
        binding.root.findNavController().navigate(action)
    }

}