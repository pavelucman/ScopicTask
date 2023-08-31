package com.pavel.scopictask.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.pavel.scopictask.data.network.model.Response
import com.google.android.material.snackbar.Snackbar
import com.pavel.scopictask.R
import com.pavel.scopictask.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.userAuthenticatedStatus) {
            findNavController().navigate(R.id.action_listItemFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userAuthorizedEmail.collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            binding.apply {
                                btLogin.visibility = View.INVISIBLE
                                emailProgressBar.visibility = View.VISIBLE
                            }
                        }
                        is Response.Success -> {
                            findNavController().navigate(R.id.action_listItemFragment)
                        }
                        is Response.Error -> {
                            binding.apply {
                                btLogin.visibility = View.VISIBLE
                                emailProgressBar.visibility = View.GONE
                            }
                            Snackbar.make(
                                view, "${response.e?.message}", Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        binding.apply {
            tvRegister.setOnClickListener {
                val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
                findNavController().navigate(action)
            }

            btLogin.setOnClickListener {
                val signInEmail = binding.etLoginEmail.text.toString()
                val signInPassword = binding.etLoginPassword.text.toString()
                if (signInEmail.isNotEmpty() && signInPassword.isNotEmpty()) {
                    viewModel.signInUser(signInEmail, signInPassword)
                } else {
                    Snackbar.make(view, "Fill the required fields", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.apply {
            etLoginEmail.text = null
            etLoginPassword.text = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}