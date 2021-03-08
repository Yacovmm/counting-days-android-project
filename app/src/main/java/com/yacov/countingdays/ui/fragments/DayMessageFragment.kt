package com.yacov.countingdays.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yacov.countingdays.R
import com.yacov.countingdays.data.entities.MessageEntity
import com.yacov.countingdays.data.local.LocalSharedPreferences
import com.yacov.countingdays.databinding.FragmentDayMessageBinding
import com.yacov.countingdays.ui.MainActivity
import com.yacov.countingdays.ui.MainViewModel
import com.yacov.countingdays.ui.adapters.GenericAdapter
import com.yacov.countingdays.utils.Constants.SP_KEY

class DayMessageFragment : Fragment(R.layout.fragment_day_message), GenericAdapter.GenericRecylerAdapterDelegate {

    private var _binding: FragmentDayMessageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel
        get() = (requireActivity() as MainActivity).viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentDayMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMessages(LocalSharedPreferences(requireContext()).getSharedPreferences(SP_KEY) ?: "")

        binding.restingDaysTv.text = viewModel.getDaysResting()

        binding.chronogramviewFab.setOnClickListener {
            findNavController().navigate(R.id.action_dayMessageFragment_to_addMessageFragment)
        }

        setupRecyclerView()

        setupObservers()
    }

    private fun setupRecyclerView() {
        viewModel.adapter = GenericAdapter(diffCallback)
        viewModel.adapter!!.delegate = this
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = viewModel.adapter
    }

    private val diffCallback = object : DiffUtil.ItemCallback<MessageEntity>() {
        override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private fun setupObservers() {
        viewModel.messagesLiveData.observe(viewLifecycleOwner) { list ->
            viewModel.adapter?.submitList(list)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Generic adapter
    override fun cellForPosition(adapter: GenericAdapter<*>, cell: RecyclerView.ViewHolder, position: Int) {
        (cell as? MessageCell)?.setModel(viewModel.adapter!!.differ.currentList[position])
    }

    override fun didSelectItemAt(adapter: GenericAdapter<*>, index: Int) {
        super.didSelectItemAt(adapter, index)
        findNavController().navigate(
            DayMessageFragmentDirections.actionDayMessageFragmentToMessageDetailsFragment(
                viewModel.adapter!!.differ.currentList[index]
            )
        )
    }

    override fun cellType(adapter: GenericAdapter<*>, position: Int): GenericAdapter.AdapterHolderType {
        return GenericAdapter.AdapterHolderType(MessageCell.resId, MessageCell::class.java, 1)
    }
}
