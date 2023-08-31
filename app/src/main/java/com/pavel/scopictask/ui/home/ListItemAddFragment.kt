package com.pavel.scopictask.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pavel.scopictask.data.network.model.ListItem
import com.pavel.scopictask.databinding.FragmentListItemAddBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListItemAddFragment : Fragment() {

    private var _binding: FragmentListItemAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListItemViewModel by activityViewModels()

    private val navigationArgs: ListItemAddFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar(title = navigationArgs.title)
        val item = navigationArgs.listItem
        if(item.id != ""){
            bind(item)
        } else {
            binding.btnSaveItem.setOnClickListener {
                addNewItem()
            }
        }

    }

    private fun addNewItem(){
        binding.apply {
            if (txtEnterItem.text.toString().isEmpty()) {
                txtEnterItem.text = null
            } else {
                val itemName = txtEnterItem.text.toString()
                viewModel.insertItem(itemName)
                findNavController().navigateUp()
            }
        }
    }

    private fun bind(item: ListItem) {
        binding.apply {
            txtEnterItem.setText(item.name)
            btnSaveItem.setOnClickListener {
                viewModel.updateItem(
                    ListItem(
                        id = item.id,
                        name = txtEnterItem.text.toString(),
                        done = item.done
                    )
                )
                findNavController().navigateUp()
            }
        }
    }

    private fun setToolbar(title: String) {
        binding.listItemAddFragmentToolbar.title = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}