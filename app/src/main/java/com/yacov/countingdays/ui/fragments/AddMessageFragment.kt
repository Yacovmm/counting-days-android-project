package com.yacov.countingdays.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yacov.countingdays.R
import com.yacov.countingdays.data.entities.MessageEntity
import com.yacov.countingdays.data.local.LocalSharedPreferences
import com.yacov.countingdays.databinding.FragmentAddMessageBinding
import com.yacov.countingdays.ui.MainActivity
import com.yacov.countingdays.ui.MainViewModel
import com.yacov.countingdays.utils.Constants.SP_KEY
import com.yacov.countingdays.utils.ViewState
import java.io.IOException

class AddMessageFragment : Fragment(R.layout.fragment_add_message) {

    var _bindding: FragmentAddMessageBinding? = null
    val binding: FragmentAddMessageBinding
        get() = _bindding!!

    private val viewModel: MainViewModel
        get() = (requireActivity() as MainActivity).viewModel

    private val weekDayList = listOf(
        "Domingo",
        "Segunda-feira",
        "Terça-feira",
        "Quarta-feria",
        "Quinta-feira",
        "Sexta-feira",
        "Sábado"
    )

    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null
    private var downloadUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _bindding = FragmentAddMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

        setupDayWeekSpinner()

        setupAddBtn()

        setupSelectImgBtn()
    }

    private fun setupSelectImgBtn() {
        binding.imgBtn.setOnClickListener {
            selectImage()
        }
    }

    // Select Image method 
    private fun selectImage() {
        // Defining Implicit Intent to mobile gallery 
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == RESULT_OK &&
            data != null &&
            data.data != null
        ) {

            // Get the Uri of data 
            filePath = data.data

            try {
                // Setting image on image view using Bitmap

                filePath?.let { dataUri ->
                    val imageStream = requireContext().contentResolver.openInputStream(dataUri)
                    val bitmap = BitmapFactory.decodeStream(imageStream)
                    binding.img.visibility = View.VISIBLE
                    binding.img.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
            }
        }
    }

    private fun setupDayWeekSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            weekDayList
        )
        binding.spinnerDayWeekNames.adapter = adapter
    }

    private fun setupAddBtn() {
        binding.sendBtn.setOnClickListener {
            if (
                binding.spinnerNames.selectedItem as String == LocalSharedPreferences(requireContext()).getSharedPreferences(SP_KEY)
            ) {
                Snackbar.make(requireView(), "Selecione outro destinatario", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.titleEt.text.isNullOrEmpty()) {
                Snackbar.make(requireView(), "Preencha o titulo", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.msgEt.text.isNullOrEmpty()) {
                Snackbar.make(requireView(), "Preencha a mensagem", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            filePath?.let { viewModel.uploadImageAndSaveMessage(it) } ?: kotlin.run {
                viewModel.saveMessage(
                    MessageEntity(
                        sender = LocalSharedPreferences(requireContext()).getSharedPreferences(SP_KEY)
                            ?: "",
                        userReceiver = binding.spinnerNames.selectedItem as String,
                        dayOfWeek = binding.spinnerDayWeekNames.selectedItemPosition + 1,
                        msg = binding.msgEt.text.toString(),
                        title = binding.titleEt.text.toString(),
                        imageUrl = downloadUrl ?: ""
                    )
                )
            }
        }
    }

    private fun setupObservers() {
        if (viewModel.usersLiveData.value == null) viewModel.getUsers()
        viewModel.usersLiveData.observe(viewLifecycleOwner) { list ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                list.map { it.nickname }
            )
            binding.spinnerNames.adapter = adapter
        }

        viewModel.messageSavedLiveData.observe(viewLifecycleOwner) { isSaved ->
            if (isSaved)
                Snackbar.make(
                    requireView(),
                    "Mensagem enviada com sucesso",
                    Snackbar.LENGTH_SHORT
                ).show()
            else {
                Snackbar.make(
                    requireView(),
                    "Mensagem enviada com sucesso",
                    Snackbar.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }
        }

        viewModel.uploadImgLiveData.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewState.Loading -> {
                    binding.progressbar.visibility = View.VISIBLE
                }
                is ViewState.Error -> {
                    binding.progressbar.visibility = View.GONE
                    Snackbar.make(
                        requireView(),
                        getText(viewState.messageResId!!),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                is ViewState.Success -> {
                    binding.progressbar.visibility = View.GONE
                    downloadUrl = viewState.data!!
                    viewModel.saveMessage(
                        MessageEntity(
                            sender = LocalSharedPreferences(requireContext()).getSharedPreferences(SP_KEY)
                                ?: "",
                            userReceiver = binding.spinnerNames.selectedItem as String,
                            dayOfWeek = binding.spinnerDayWeekNames.selectedItemPosition + 1,
                            msg = binding.msgEt.text.toString(),
                            title = binding.titleEt.text.toString(),
                            imageUrl = downloadUrl ?: ""
                        )
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bindding = null
    }
}
