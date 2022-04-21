package com.elmorshdi.internTask.view.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.elmorshdi.internTask.databinding.FragmentMainBinding
import com.elmorshdi.internTask.domain.model.Product
import com.elmorshdi.internTask.helper.Constant.BASE_URL
import com.elmorshdi.internTask.helper.alertDialog
import com.elmorshdi.internTask.helper.checkForInternet
import com.elmorshdi.internTask.view.adapter.HorizontalProductAdapter
import com.elmorshdi.internTask.view.util.SharedPreferencesManager
import com.elmorshdi.internTask.view.util.SharedPreferencesManager.getUsername
import com.elmorshdi.internTask.view.util.UiState
import com.elmorshdi.internTask.view.viewmodel.ShareViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint

class MainFragment : Fragment(),
    HorizontalProductAdapter.Interaction {
    private val viewModel: ShareViewModel by viewModels()
    private lateinit var binding: FragmentMainBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signOutArrow.setOnClickListener {
            alertDialog(
                "sign Out", "Are you sure you want to sign out ?",
                view.context, ::signOut, view
            )
        }
        binding.mainAddButton.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToAddItemFragment()
            view.findNavController().navigate(action)}
        if (checkForInternet(requireContext())) {
            viewModel.getProducts()
            println("online")
        }
        else {
            viewModel.getCashedData()
            println("offline")

        }
//       viewModel.getProducts()
        viewModel.uiStateFlow.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UiState.Loading ->
                    binding.spinKit.isVisible = true
                is UiState.NetworkError -> {
                    println("is ShareViewModel.Error.NetworkError")
                            binding.spinKit.isVisible = false
                            binding.mainRecyclerHor.isVisible = false
                            binding.errorText.isVisible = true
                            binding.errorText.text = it.errorMessage
                }
                is UiState.Success -> {
                    binding.spinKit.isVisible = false
                    binding.errorText.isVisible = false
                    setUpRecyclerView(viewModel.productsList)
                }
                else -> UInt
            }
        })


    }


    private fun setUpRecyclerView(products: LiveData<List<Product>>) {
        //Setup Hor recyclerView
        val adapterHor = HorizontalProductAdapter(interaction = this)
        adapterHor.submitList(products.value)
        binding.mainRecyclerHor.adapter = adapterHor
    }

    private fun signOut(view: View) {
        SharedPreferencesManager.signOutShared(sharedPreferences.edit())
        val action = MainFragmentDirections.actionMainFragmentToLoginFragment()
        view.findNavController().navigate(action)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.viewModels = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        // setUpRecyclerView()
        binding.welcomeTextView.text = getUsername(sharedPreferences)
        return binding.root
    }

    override fun onItemSelected(product: Product) {
        val action = MainFragmentDirections.actionMainFragmentToProductFragment(product)
        view?.findNavController()?.navigate(action)
    }
}