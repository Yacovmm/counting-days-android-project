package com.yacov.countingdays.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import coil.load
import com.yacov.countingdays.R
import com.yacov.countingdays.databinding.FragmentMessageDetailsBinding
import com.yacov.countingdays.ui.MainActivity
import com.yacov.countingdays.ui.MainViewModel

class MessageDetailsFragment : Fragment(R.layout.fragment_message_details) {

    private var _binding: FragmentMessageDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel
        get() = (requireActivity() as MainActivity).viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentMessageDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message = MessageDetailsFragmentArgs.fromBundle(requireArguments()).message

        if (message.imageUrl.isNotEmpty()) binding.bannerIv.load(message.imageUrl)

        binding.titleTv.text = message.title

        binding.msgTv.text = message.msg
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
