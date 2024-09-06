package com.leomarkpaway.todoapp.view.all_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.leomarkpaway.todoapp.R
import com.leomarkpaway.todoapp.MyApp
import com.leomarkpaway.todoapp.common.enum.TaskStatus
import com.leomarkpaway.todoapp.common.extension.gone
import com.leomarkpaway.todoapp.common.extension.setRippleEffect
import com.leomarkpaway.todoapp.common.extension.visible
import com.leomarkpaway.todoapp.common.util.PopupMenu
import com.leomarkpaway.todoapp.common.util.viewModelFactory
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import com.leomarkpaway.todoapp.databinding.FragmentAllTaskBinding
import com.leomarkpaway.todoapp.view.dialog.create_update_delete_task.DeleteTaskDialog
import com.leomarkpaway.todoapp.view.dialog.create_update_delete_task.UpdateTaskBottomSheetDialog
import com.leomarkpaway.todoapp.view.all_task.adapter.AllTaskAdapter
import com.leomarkpaway.todoapp.view.all_task.viewmodel.AllTaskViewModel
import kotlinx.coroutines.launch


// TODO convert this screen into calendar to view all task scheduled.
class AllTaskFragment : Fragment() {

    private lateinit var binding: FragmentAllTaskBinding
    private val viewModel: AllTaskViewModel by viewModels {
        viewModelFactory { AllTaskViewModel(MyApp.appModule.repository) }
    }
    private lateinit var allTaskAdapter: AllTaskAdapter
    private var allTask: List<Task>? = null
    private var selectedFilter = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAllTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickBackButton()
        setupTaskOptionFilter()
        observeAllTask()
        observeSelectedTaskFilter()
    }

    private fun onClickBackButton() {
        binding.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setupTaskOptionFilter() = with(binding) {
        imgDropdown.setRippleEffect()
        imgDropdown.setOnClickListener { showDropdownMenu(it) }
        tvFilter.setOnClickListener { showDropdownMenu(imgDropdown) }
    }

    private fun showDropdownMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view, R.menu.popup_menu_filter_option)
        popupMenu.setOnMenuItemClickListener { selectedMenu ->
            when (selectedMenu.itemId) {
                R.id.menu_pinned -> viewModel.updateSelectedTaskFilter(0)
                R.id.menu_pending -> viewModel.updateSelectedTaskFilter(1)
                R.id.menu_completed -> viewModel.updateSelectedTaskFilter(2)
                R.id.menu_overdue -> viewModel.updateSelectedTaskFilter(3)
            }
        }
        popupMenu.show()
    }

    private fun observeAllTask() {
        lifecycleScope.launch {
            viewModel.getAllTask().observe(viewLifecycleOwner) { allTask ->
                this@AllTaskFragment.allTask = allTask
                val filteredTask = filterTasks()
                setupTaskList(filteredTask)
                setEmptyStateMessage(selectedFilter, filteredTask.isNullOrEmpty())
            }
        }
    }

    private fun observeSelectedTaskFilter() {
        viewModel.selectedTaskFilter.observe(viewLifecycleOwner) { selectedFilter ->
            this@AllTaskFragment.selectedFilter = selectedFilter
            val filteredTask = filterTasks()
            if (filteredTask != null) setupTaskList(filteredTask)
            setEmptyStateMessage(selectedFilter, filteredTask.isNullOrEmpty())
            setFilterText(selectedFilter)
        }
    }

    private fun filterTasks(): List<Task>? {
        return when (selectedFilter) {
            0 -> allTask?.filter { it.isPinned }
            1 -> allTask?.filter { it.status == TaskStatus.PENDING.name }
            2 -> allTask?.filter { it.status == TaskStatus.DONE.name }
            3 -> allTask?.filter { it.status == TaskStatus.EXPIRED.name }
            else -> allTask
        }
    }

    private fun setupTaskList(filteredTask: List<Task>?) {
        allTaskAdapter = AllTaskAdapter(filteredTask as ArrayList<Task>,
            onItemClicked = { task ->
                UpdateTaskBottomSheetDialog(task)
                    .show(requireActivity().supportFragmentManager, "view_task")
            },
            onItemShowFullDetail = { taskId ->
                val action = AllTaskFragmentDirections.actionAllTaskFragmentToTaskDetailFragment(taskId)
                findNavController().navigate(action)
            },
            onItemPin = { task ->
                val updatedPin = task.copy(isPinned = !task.isPinned)
                viewModel.updateTaskPin(updatedPin)
            },
            onItemUpdate = { task ->
                UpdateTaskBottomSheetDialog(task)
                    .show(requireActivity().supportFragmentManager, "update_task")
            },
            onItemDelete = { task ->
                DeleteTaskDialog(task)
                    .show(requireActivity().supportFragmentManager, "delete_task")
            })
        binding.rvTasks.adapter = allTaskAdapter
        binding.rvTasks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun setFilterText(index: Int) {
        val filterOption: Array<String> = requireContext().resources.getStringArray(R.array.task_filter_options)
        binding.tvFilter.text = filterOption[index]
    }

    private fun setEmptyStateMessage(selectedFilter: Int, isShowMessage: Boolean) = with(binding.tvEmptyTask) {
        val filterOptions: Array<String> = requireContext().resources.getStringArray(R.array.task_filter_options)
        text = getString(R.string.empty_all_task, filterOptions[selectedFilter])
        if (isShowMessage) visible() else gone()
    }

}