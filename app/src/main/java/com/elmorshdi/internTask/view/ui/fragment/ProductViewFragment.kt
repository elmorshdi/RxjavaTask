package com.elmorshdi.internTask.view.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.elmorshdi.internTask.databinding.ProductFragmentBinding
import com.elmorshdi.internTask.helper.alertDialog
import com.elmorshdi.internTask.helper.toast
import com.elmorshdi.internTask.view.viewmodel.ShareViewModel
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProductViewFragment : Fragment() {

    private lateinit var binding: ProductFragmentBinding
    private val args: ProductViewFragmentArgs by navArgs()
    private  val viewModel: ShareViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProductFragmentBinding.inflate(inflater, container, false)
        binding.productV=args.product
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.slide_bottom)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
        binding.deleteButton.setOnClickListener {
            alertDialog(
                "Delete Product",
                "Are Sure Deleting Product",
                requireContext(),
                ::delete, args.product.id!!
            )
        }
        binding.editBackArrow.setOnClickListener {
            navigateToMain()

        }
        viewModel.uiStateFlow.observe(viewLifecycleOwner, Observer {event ->

                when (event) {
                    is com.elmorshdi.internTask.view.util.UiState.Success -> {
                        binding.viewSpinKit.isVisible=false
                        navigateToMain()
                        requireContext().toast("Product Deleted")
                    }
                    is com.elmorshdi.internTask.view.util.UiState.NetworkError -> {
                    binding.viewSpinKit.isVisible=false
                    requireContext().toast(event.errorMessage)
                     }

                    is com.elmorshdi.internTask.view.util.UiState.Loading -> {
                        binding.viewSpinKit.isVisible=true
                    }
                    else -> Unit
                }
            })
        }


    private fun delete(id: Int) {
        viewModel.deleteProduct(id = id)
    }
    private fun navigateToMain() {
        val action = ProductViewFragmentDirections.actionProductFragmentToMainFragment()
        binding.root.findNavController().navigate(action)
    }
}