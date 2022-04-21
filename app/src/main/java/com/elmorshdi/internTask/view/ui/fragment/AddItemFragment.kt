package com.elmorshdi.internTask.view.ui.fragment

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
import com.elmorshdi.internTask.databinding.FragmentAddItemBinding
import com.elmorshdi.internTask.helper.toast
import com.elmorshdi.internTask.view.viewmodel.ShareViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class AddItemFragment : Fragment() {
    private val viewModel: ShareViewModel by viewModels()
    private lateinit var binding: FragmentAddItemBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addButton.setOnClickListener {
            viewModel.postProduct(
                binding.addNameEditText.text?.trim().toString(),
                binding.addPriceEditText.text?.trim().toString(),
                binding.addQuantityEditText.text?.trim().toString()
            )
        }
        binding.addBackArrow.setOnClickListener {
            navigateToMain()
        }
        viewModel.uiStateFlow.observe(viewLifecycleOwner, Observer {event ->

                when (event) {
                    is com.elmorshdi.internTask.view.util.UiState.Success -> {
                        binding.addSpinKit.isVisible = false
                        navigateToMain()
                        requireContext().toast("Product Added")
                    }
                    is com.elmorshdi.internTask.view.util.UiState.NetworkError -> {
                    requireContext().toast(event.errorMessage)
                }
                    is com.elmorshdi.internTask.view.util.UiState.ValidationError -> {
                        clearError()
                        binding.addSpinKit.isVisible = false
                        when (event.error) {

                            is com.elmorshdi.internTask.view.util.Error.NameNotValid -> {
                                binding.addNameTextField.error =
                                    "Invalid name can't be a blank or a number"
                            }
                            is com.elmorshdi.internTask.view.util.Error.PriceNotValid -> {
                                binding.addPriceTextField.error =
                                    "Invalid price can't be empty or zero"
                            }
                            is com.elmorshdi.internTask.view.util.Error.QuantityNotValid -> {
                                binding.addQuantityTextField.error =
                                    "Invalid quantity cannot be empty or zero"
                            }
                            else -> {
                                binding.addQuantityTextField.error =
                                    getString(R.string.an_error_occurred)
                            }
                        }
                    }
                    is com.elmorshdi.internTask.view.util.UiState.Loading -> {
                        clearError()
                        binding.addSpinKit.isVisible = true
                    }
                    else -> Unit
                }
            })
        }


    private fun navigateToMain() {
        val action = AddItemFragmentDirections.actionAddItemFragmentToMainFragment()
        binding.root.findNavController().navigate(action)
    }
    private fun clearError() {
        binding.addNameTextField.isErrorEnabled = false
        binding.addPriceTextField.isErrorEnabled = false
        binding.addQuantityTextField.isErrorEnabled = false
    }
}