package com.yacov.countingdays.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yacov.countingdays.R
import com.yacov.countingdays.data.local.LocalSharedPreferences
import com.yacov.countingdays.databinding.FragmentInitialBinding
import com.yacov.countingdays.ui.MainActivity
import com.yacov.countingdays.ui.MainViewModel
import com.yacov.countingdays.utils.Constants.SP_KEY

class InitialFragment : Fragment(R.layout.fragment_initial) {

    private var _binding: FragmentInitialBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel
        get() = (requireActivity() as MainActivity).viewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentInitialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBtn.isEnabled = false

        val isUserExists = checkIfLogged()

        if (isUserExists) findNavController().navigate(R.id.action_initialFragment_to_dayMessageFragment)

        binding.registerTv.setOnClickListener {
            setRegisterContainerVisible()
        }

        viewModel.getUsers()
        viewModel.usersLiveData.observe(viewLifecycleOwner) { list ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                list.map { it.nickname }
            )
            binding.spinnerNames.adapter = adapter

            binding.spinnerNames.onItemSelectedListener = spinnerListener
        }

        binding.loginBtn.setOnClickListener {
            LocalSharedPreferences(requireContext()).setSharedPreferences(SP_KEY, binding.spinnerNames.selectedItem as String)

            findNavController().navigate(R.id.action_initialFragment_to_dayMessageFragment)
        }

        binding.registerBtn.setOnClickListener { register() }
    }

    private fun register() {
        viewModel.registerUser(binding.nicknameEt.text.toString())

        viewModel.registerLiveData.observe(viewLifecycleOwner) { status ->
            if (!status) {
                Snackbar.make(
                    requireView(),
                    "O nickname ja existe ou ocorreu um erro",
                    Snackbar.LENGTH_SHORT
                ).show()
                return@observe
            }

            viewModel.getUsers()

            Snackbar.make(
                requireView(),
                "Nickname cadastrado com sucesso",
                Snackbar.LENGTH_SHORT
            ).show()

            binding.registerContainer.visibility = View.GONE
            binding.spinnerContainer.visibility = View.VISIBLE
        }
    }

    private fun setRegisterContainerVisible() {
        binding.registerContainer.visibility = View.VISIBLE
        binding.spinnerContainer.visibility = View.GONE
    }

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {

            binding.loginBtn.isEnabled = true
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}
    }

    private fun checkIfLogged(): Boolean =
        !LocalSharedPreferences(requireContext()).getSharedPreferences(SP_KEY).isNullOrBlank()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
