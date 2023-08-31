package com.pavel.scopictask.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pavel.scopictask.data.network.model.ListItem
import com.pavel.scopictask.data.network.model.Response
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pavel.scopictask.R
import com.pavel.scopictask.databinding.FragmentListItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListItemFragment : Fragment(), ItemEvents {

    private var _binding: FragmentListItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var listItemAdapter: ListItemAdapter
    private val viewModel: ListItemViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collect { response ->
                    when (response) {
                        is Response.Success -> {
                            if (response.data.isEmpty()) {
                                binding.tvEmptyList.visibility = View.VISIBLE
                            } else {
                                listItemAdapter.submitList(response.data)
                                binding.tvEmptyList.visibility = View.GONE
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val currItem = listItemAdapter.currentList[position]
                viewModel.deleteItem(currItem.id)
                Snackbar.make(view, "Article deleted successfully", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        viewModel.insertItem(currItem.name)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(listRecyclerView)
        }

        binding.btnAddItem.setOnClickListener {
            val action = ListItemFragmentDirections.actionListItemFragmentToListItemAddFragment(
                getString(R.string.add), ListItem()
            )
            findNavController().navigate(action)
        }
    }

    private fun showItemDialog(item: ListItem) {
        val options = arrayOf("Edit","Delete")
        MaterialAlertDialogBuilder(requireContext())
            .setCancelable(true)
            .setItems(options){ _,which ->
                when(options[which]){
                    "Edit" -> onItemEdit(item)
                    "Delete" -> viewModel.deleteItem(item.id)
                }
            }
            .show()
    }

    private fun setRecyclerView(){
        listItemAdapter = ListItemAdapter(this)
        listRecyclerView = binding.listRecyclerView
        listRecyclerView.apply{
            adapter = listItemAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun setToolbar() {
        binding.itemFragmentToolbar.title = findNavController().currentDestination?.label
    }

    private fun onItemEdit(item: ListItem) {
        val action = ListItemFragmentDirections.actionListItemFragmentToListItemAddFragment(
            getString(R.string.edit),item
        )
        findNavController().navigate(action)
    }

    override fun onItemUpdate(item: ListItem) {
        viewModel.updateItem(item)
    }
    override fun callItemDialog(item: ListItem) {
        showItemDialog(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}