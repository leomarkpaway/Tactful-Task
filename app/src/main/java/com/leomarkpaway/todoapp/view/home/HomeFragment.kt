package com.leomarkpaway.todoapp.view.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.leomarkpaway.todoapp.view.home.adapter.LabelAdapter
import com.leomarkpaway.todoapp.view.home.adapter.TaskAdapter
import com.leomarkpaway.todoapp.view.home.adapter.model.HomeFilterModel
import com.leomarkpaway.todoapp.view.home.viewmodel.HomeSharedViewModel
import com.leomarkpaway.todoapp.MyApp
import com.leomarkpaway.todoapp.R
import com.leomarkpaway.todoapp.common.enum.TaskStatus
import com.leomarkpaway.todoapp.common.extension.gone
import com.leomarkpaway.todoapp.common.extension.hideKeyboard
import com.leomarkpaway.todoapp.common.extension.setImage
import com.leomarkpaway.todoapp.common.extension.startAnimation
import com.leomarkpaway.todoapp.common.extension.visible
import com.leomarkpaway.todoapp.common.util.HorizontalSpaceItemDecoration
import com.leomarkpaway.todoapp.common.util.getCurrentDate
import com.leomarkpaway.todoapp.common.util.getCurrentWeekDateAndDayName
import com.leomarkpaway.todoapp.common.util.getPositionAndAdapterId
import com.leomarkpaway.todoapp.common.util.getPositionCurrentDatePosition
import com.leomarkpaway.todoapp.common.util.viewModelFactory
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import com.leomarkpaway.todoapp.databinding.FragmentHomeBinding
import com.leomarkpaway.todoapp.view.dialog.TaskDetailsBottomSheetDialog
import com.leomarkpaway.todoapp.view.dialog.create_update_delete_task.DeleteTaskDialog
import com.leomarkpaway.todoapp.view.dialog.create_update_delete_task.NewTaskBottomSheetDialog
import com.leomarkpaway.todoapp.view.home.adapter.MainFilterAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val sharedViewModel: HomeSharedViewModel by activityViewModels {
        viewModelFactory { HomeSharedViewModel(MyApp.appModule.repository) }
    }
    private var pairDateName: ArrayList<Pair<String,String>> = ArrayList()
    private var selectedFilter = HomeFilterModel(getPositionCurrentDatePosition() + 1, getCurrentDate())
    private var allTaskAdapter: ConcatAdapter
    private var allTaskAdapterArrayList: ArrayList<Pair<Int, String>>
    private var pinnedLabelAdapter: LabelAdapter
    private var pendingLabelAdapter: LabelAdapter
    private var completedLabelAdapter: LabelAdapter
    private var pinnedTasksAdapter: TaskAdapter
    private var pendingTaskAdapter: TaskAdapter
    private var completedTaskAdapter: TaskAdapter
    private var isVisiblePinnedTask = true
    private var isVisiblePendingTask = true
    private var isVisibleCompletedTask = true
    private var currentTasksList: List<Task> = emptyList()
    private var weeklyTasksList: List<Task> = emptyList()
    private var searchValue = ""
    private var searchResult: List<Task> = emptyList()
    private var isOpenFabMenu = false

    init {
        // Labels
        pinnedLabelAdapter = LabelAdapter("Pinned") {
            sharedViewModel.updateIsVisiblePinnedTask(!isVisiblePinnedTask)
        }
        pendingLabelAdapter = LabelAdapter("Pending") {
            sharedViewModel.updateIsVisiblePendingTask(!isVisiblePendingTask)
        }
        completedLabelAdapter = LabelAdapter("Completed") {
            sharedViewModel.updateIsVisibleCompletedTask(!isVisibleCompletedTask)
        }
        // Task item
        pinnedTasksAdapter = createTaskItemAdapter(emptyList())
        pendingTaskAdapter = createTaskItemAdapter(emptyList())
        completedTaskAdapter = createTaskItemAdapter(emptyList())
        // concat adapter
        allTaskAdapter = ConcatAdapter(pendingLabelAdapter)
        allTaskAdapterArrayList = allTaskAdapter.getPositionAndAdapterId()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observer()
    }

    private fun initView() {
        initMainFilter()
        setupFabMenu()
        onClickAddTask()
        onClickOpenAllTask()
        onClickOpenNotification()
        onClickSearch()
    }

    private fun observer() {
        observeSelectedFilter()
        observeIsOpenFabMenu()
        observeIsVisiblePinnedTask()
        observeIsVisiblePendingTask()
        observeIsVisibleCompletedTask()
        observeSearchValue()
        observeSearchResult()
        observeCurrentTaskList()
        observeAllTaskAdapterArrayList()
    }

    private fun initMainFilter() {
        val arrayListDateName: ArrayList<Pair<String,String>> = ArrayList()
        arrayListDateName.add(Pair("", "All"))
        arrayListDateName.addAll(getCurrentWeekDateAndDayName())
        pairDateName = arrayListDateName
        binding.rvMainFilter.addItemDecoration(HorizontalSpaceItemDecoration(50))
        sharedViewModel.updateSelectedFilter(selectedFilter)
        getWeeklyTasks()
    }

    private fun  setupFabMenu() {
        binding.fabMenu.setOnClickListener {
            sharedViewModel.updateIsOpenFabMenu(!isOpenFabMenu)
        }
    }

    private fun observeIsOpenFabMenu() {
        sharedViewModel.isOpenFabMenu.observe(viewLifecycleOwner) {
            isOpenFabMenu = it
            if (isOpenFabMenu) openFabMenu() else closeFabMenu()
        }
    }

    private fun openFabMenu() = with(binding) {
        fabMenu.setImage(requireContext(), R.drawable.ic_cross)
        fabMenu.startAnimation(requireContext(), R.anim.rotate_forward)
        lifecycleScope.launch {
            fabAdd.apply {
                visible()
                isClickable = true
                startAnimation(requireContext(), R.anim.upward_reverse_fade)
            }
            delay(70)
            fabTasks.apply {
                visible()
                isClickable = true
                startAnimation(requireContext(), R.anim.upward_reverse_fade)
            }
            delay(70)
            fabNotifications.apply {
                visible()
                isClickable = true
                startAnimation(requireContext(), R.anim.upward_reverse_fade)
            }
        }
    }

    private fun closeFabMenu() = with(binding) {
        fabMenu.setImage(requireContext(), R.drawable.ic_menu)
        fabMenu.startAnimation(requireContext(), R.anim.rotate_backward)
        lifecycleScope.launch {
            fabAdd.apply {
                isClickable = false
                startAnimation(requireContext(), R.anim.downward_fade)
            }
            delay(70)
            fabTasks.apply {
                isClickable = false
                startAnimation(requireContext(), R.anim.downward_fade)
            }
            delay(70)
            fabNotifications.apply {
                isClickable = false
                startAnimation(requireContext(), R.anim.downward_fade)
            }
        }
    }

    private fun onClickAddTask() {
        binding.fabAdd1.setOnClickListener {
            NewTaskBottomSheetDialog().show(requireActivity().supportFragmentManager, "new_task")
            sharedViewModel.updateIsOpenFabMenu(false)
        }
    }

    private fun onClickOpenAllTask() {
        binding.fabTasks.setOnClickListener {
            sharedViewModel.updateIsOpenFabMenu(false)
            findNavController().navigate(R.id.action_homeFragment_to_allTaskFragment)
        }
    }

    private fun onClickOpenNotification() {
        binding.fabNotifications.setOnClickListener {
            sharedViewModel.updateIsOpenFabMenu(false)
            findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
        }
    }

    private fun onClickSearch() = with(binding.searchBar) {
        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                requireContext().hideKeyboard(this)
                true
            } else false
        }
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                sharedViewModel.updateSearchValue(s.toString())
            }
        })
        binding.imgClearSearch.setOnClickListener {
            if (searchValue.isNotEmpty()) {
                binding.searchBar.setText("")
            }
        }
    }

    private fun getWeeklyTasks() {
        lifecycleScope.launch(Dispatchers.Main) {
            sharedViewModel.getWeeklyTasks().observe(viewLifecycleOwner) { weeklyTasks ->
                weeklyTasksList = weeklyTasks
                if (selectedFilter.index == 0) sharedViewModel.updateCurrentTaskList(weeklyTasks)
            }
        }
    }

    private fun getTaskByDate() {
        lifecycleScope.launch(Dispatchers.Main) {
            sharedViewModel.getTasksByDate(selectedFilter.date).observe(viewLifecycleOwner) { tasksByDate ->
                if (selectedFilter.index != 0) sharedViewModel.updateCurrentTaskList(tasksByDate)
            }
        }
    }


    private fun observeIsVisiblePinnedTask() {
        sharedViewModel.isVisiblePinnedTask.observe(viewLifecycleOwner) { isVisible ->
            pinnedLabelAdapter.updateVisibilityIndicator(isVisible)
            isVisiblePinnedTask = isVisible

            if (isVisiblePinnedTask) {
                if (selectedFilter.index == 0) {
                    allTaskAdapter.addAdapter(0, pinnedLabelAdapter)
                    allTaskAdapter.addAdapter(1, pinnedTasksAdapter)
                } else {
                    allTaskAdapter.addAdapter(0, pendingLabelAdapter)
                    allTaskAdapter.addAdapter(1, pinnedTasksAdapter)
                }
            } else {
                if (selectedFilter.index == 0) {
                    if ( pinnedTasksAdapter.itemCount == 0) {
                        allTaskAdapter.removeAdapter(pinnedLabelAdapter)
                        allTaskAdapter.removeAdapter(pinnedTasksAdapter)
                    } else {
                        allTaskAdapter.removeAdapter(pinnedTasksAdapter)
                    }
                }
            }
            val adapterArrayList = allTaskAdapter.getPositionAndAdapterId()
            sharedViewModel.updateAllTaskAdapterArrayList(adapterArrayList)
        }
    }

    private fun observeIsVisiblePendingTask() {
        sharedViewModel.isVisiblePendingTask.observe(viewLifecycleOwner) { isVisible ->
            pendingLabelAdapter.updateVisibilityIndicator(isVisible)
            isVisiblePendingTask = isVisible

            if (isVisiblePendingTask) {
                if (selectedFilter.index == 0) {
                    val position =
                        if (isVisiblePinnedTask) 2 else if (pinnedTasksAdapter.itemCount == 0) 0 else 1
                    allTaskAdapter.addAdapter(position, pendingLabelAdapter)
                    allTaskAdapter.addAdapter(position.plus(1), pendingTaskAdapter)
                } else {
                    allTaskAdapter.addAdapter(1, pinnedTasksAdapter)
                    allTaskAdapter.addAdapter(2, pendingTaskAdapter)
                }
            } else {
                if (selectedFilter.index == 0) {
                    if (pendingTaskAdapter.itemCount == 0) {
                        allTaskAdapter.removeAdapter(pendingLabelAdapter)
                        allTaskAdapter.removeAdapter(pendingTaskAdapter)
                    } else {
                        allTaskAdapter.removeAdapter(pendingTaskAdapter)
                    }
                } else {
                    if (pinnedTasksAdapter.itemCount == 0 && pendingTaskAdapter.itemCount == 0) {
                        allTaskAdapter.removeAdapter(pendingLabelAdapter)
                        allTaskAdapter.removeAdapter(pinnedTasksAdapter)
                        allTaskAdapter.removeAdapter(pendingTaskAdapter)
                    } else {
                        allTaskAdapter.removeAdapter(pinnedTasksAdapter)
                        allTaskAdapter.removeAdapter(pendingTaskAdapter)
                    }
                }
            }
            val adapterArrayList = allTaskAdapter.getPositionAndAdapterId()
            sharedViewModel.updateAllTaskAdapterArrayList(adapterArrayList)
        }
    }

    private fun observeIsVisibleCompletedTask() {
        sharedViewModel.isVisibleCompletedTask.observe(viewLifecycleOwner) { isVisible ->
            completedLabelAdapter.updateVisibilityIndicator(isVisible)
            isVisibleCompletedTask = isVisible

            if (isVisibleCompletedTask) {
                if (selectedFilter.index == 0) {
                    var position = 0
                    if (isVisiblePinnedTask) position+=2
                    if (isVisiblePendingTask) position+=2
                    if (!isVisiblePinnedTask && pinnedTasksAdapter.itemCount != 0) position+=1
                    if (!isVisiblePendingTask && pendingTaskAdapter.itemCount != 0) position+=1
                    allTaskAdapter.addAdapter(position.plus(1), completedTaskAdapter)
                } else {
                    var position = 0
                    if (isVisiblePendingTask) {
                        if (pinnedTasksAdapter.itemCount != 0 || pendingTaskAdapter.itemCount != 0) position+=3
                    } else {
                        position+=1
                    }
                    allTaskAdapter.addAdapter(position.plus(1), completedTaskAdapter)
                }
            } else {
                if (completedTaskAdapter.itemCount == 0) {
                    allTaskAdapter.removeAdapter(completedLabelAdapter)
                    allTaskAdapter.removeAdapter(completedTaskAdapter)
                } else {
                    allTaskAdapter.removeAdapter(completedTaskAdapter)
                }
            }
            val adapterArrayList = allTaskAdapter.getPositionAndAdapterId()
            sharedViewModel.updateAllTaskAdapterArrayList(adapterArrayList)
        }
    }

    private fun observeSearchValue() {
        sharedViewModel.searchValue.observe(viewLifecycleOwner) { searchValue ->
            this.searchValue = searchValue
            val searchResult = currentTasksList.filter { it.title.contains(searchValue, ignoreCase = true) }
            sharedViewModel.updateSearchResult(searchResult)
        }
    }

    private fun observeSearchResult() {
        sharedViewModel.searchResult.observe(viewLifecycleOwner) { searchResult ->
            if (searchResult != null) {
                this@HomeFragment.searchResult = searchResult
                setupSearchResult(searchResult)
                if (selectedFilter.index == 0) getWeeklyTasks() else getTaskByDate()
            }
        }
    }

    private fun observeCurrentTaskList() {
        sharedViewModel.currentTaskList.observe(viewLifecycleOwner) { currentTaskList ->
            if (currentTaskList != null) {
                this@HomeFragment.currentTasksList = currentTaskList
                val pinnedTasksList: List<Task>
                val pendingTasksList: List<Task>
                val completedTasksList: List<Task>

                if (searchValue.isNotEmpty()) {
                    pinnedTasksList = searchResult.filter { it.isPinned && it.status == TaskStatus.PENDING.name }
                    pendingTasksList = searchResult.filter { !it.isPinned && it.status == TaskStatus.PENDING.name || it.status == TaskStatus.EXPIRED.name }
                    completedTasksList = searchResult.filter { it.status == TaskStatus.DONE.name }
                } else {
                    pinnedTasksList = currentTaskList.filter { it.isPinned && it.status == TaskStatus.PENDING.name }
                    pendingTasksList = currentTaskList.filter { !it.isPinned && it.status == TaskStatus.PENDING.name || it.status == TaskStatus.EXPIRED.name }
                    completedTasksList = currentTaskList.filter { it.status == TaskStatus.DONE.name }
                }
                pinnedTasksAdapter = createTaskItemAdapter(pinnedTasksList)
                pendingTaskAdapter = createTaskItemAdapter(pendingTasksList)
                completedTaskAdapter = createTaskItemAdapter(completedTasksList)
                setupTaskList()
                setupFilterOption(weeklyTasksList, this.selectedFilter)
            }
            lifecycleScope.launch() {
                binding.flLoadingScreen.visible()
                delay(500)
                binding.flLoadingScreen.gone()
            }
        }
    }

    private fun observeAllTaskAdapterArrayList() {
        sharedViewModel.allTaskAdapterArrayList.observe(viewLifecycleOwner) { allAdapter ->
            allTaskAdapterArrayList = allAdapter
             for (adapter in allAdapter) {
                 val (position, adapterId) = adapter
                 when(adapterId) {
                     pinnedLabelAdapter.toString() -> {
                         Log.d(TAG, "$position - pinnedLabelAdapter")
                     }
                     pinnedTasksAdapter.toString() -> {
                         Log.d(TAG, "$position - pinnedTasksAdapter")
                     }
                     pendingLabelAdapter.toString() -> {
                         Log.d(TAG, "$position - pendingLabelAdapter")
                     }
                     pendingTaskAdapter.toString() -> {
                         Log.d(TAG, "$position - pendingTaskAdapter")
                     }
                     completedLabelAdapter.toString() -> {
                         Log.d(TAG, "$position - completedLabelAdapter")
                     }
                     completedTaskAdapter.toString() -> {
                         Log.d(TAG, "$position - completedTodayTaskAdapter")
                     }
                 }
             }
            Log.d(TAG, "----------------------------")
        }
    }

    private fun observeSelectedFilter() {
        sharedViewModel.selectedFilter.observe(viewLifecycleOwner) { selectedFilter ->
            this.selectedFilter = selectedFilter
            setupFilterOption(weeklyTasksList, this.selectedFilter)
            if (selectedFilter.index == 0) getWeeklyTasks() else getTaskByDate()
            resetSearchBar()
        }
    }

    private fun resetSearchBar() {
        sharedViewModel.updateSearchValue("")
        requireContext().hideKeyboard(binding.searchBar)
        binding.searchBar.setText("")
        binding.searchBar.clearFocus()
    }

    private fun setupSearchConcatAdapter() {
        allTaskAdapter = ConcatAdapter(pinnedTasksAdapter, pendingTaskAdapter, completedTaskAdapter)
    }

    private fun setupSearchResult(searchResult: List<Task>) = with(binding) {
        tvResultCount.text = getString(R.string.search_result_count, searchResult.size.toString())
        if (searchValue.isNotEmpty()) {
            tvEmptyTask.text = requireContext().getText(R.string.no_result_found)
            if (searchResult.isEmpty()) tvEmptyTask.visible() else tvEmptyTask.gone()
            imgClearSearch.visible()
            tvResultCount.visible()
            fabGroup.gone()
            fabAdd1.gone()
        } else {
            tvEmptyTask.text = requireContext().getText(R.string.empty_today_task)
            if (currentTasksList.isEmpty()) tvEmptyTask.visible() else tvEmptyTask.gone()
            imgClearSearch.gone()
            tvResultCount.gone()
            fabGroup.visible()
            fabAdd1.visible()
        }
    }

    private fun setupTaskList() = with(binding) {
        tvEmptyTask.apply { if (currentTasksList.isEmpty()) visible() else gone() }
        if (searchValue.isNotEmpty()) {
            tvTitle.gone()
            setupSearchConcatAdapter()
        } else {
            if (selectedFilter.index == 0) {
                setupAllTaskConcatAdapter(
                    pinnedTasksAdapter,
                    pendingTaskAdapter,
                    completedTaskAdapter
                )
            } else {
                setupTaskListByDateConcatAdapter(
                    pinnedTasksAdapter,
                    pendingTaskAdapter,
                    completedTaskAdapter
                )
            }
        }
        rvTasks.adapter = allTaskAdapter
        rvTasks.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        sharedViewModel.updateAllTaskAdapterArrayList(allTaskAdapter.getPositionAndAdapterId())
    }

    private fun setupAllTaskConcatAdapter(
        pinnedTasksAdapter: TaskAdapter,
        pendingTaskAdapter: TaskAdapter,
        completedTaskAdapter: TaskAdapter
    ) {
        binding.tvTitle.apply {
            if (currentTasksList.isEmpty()) {
                text = requireContext().getString(R.string.all_tasks)
                visible()
            } else gone()
        }
        pendingLabelAdapter = LabelAdapter("Pending") {
            sharedViewModel.updateIsVisiblePendingTask(!isVisiblePendingTask)
        }
        allTaskAdapter = ConcatAdapter(
            pinnedLabelAdapter,
            pinnedTasksAdapter,
            pendingLabelAdapter,
            pendingTaskAdapter,
            completedLabelAdapter,
            completedTaskAdapter
        )
        sharedViewModel.updateIsVisiblePinnedTask(pinnedTasksAdapter.itemCount != 0)
        sharedViewModel.updateIsVisiblePendingTask(pendingTaskAdapter.itemCount != 0)
        sharedViewModel.updateIsVisibleCompletedTask(completedTaskAdapter.itemCount != 0)
    }

    private fun setupTaskListByDateConcatAdapter(
        pinnedTasksAdapter: TaskAdapter,
        pendingTaskAdapter: TaskAdapter,
        completedTaskAdapter: TaskAdapter
    ) {
        val arrayWeek: Array<String> = requireContext().resources.getStringArray(R.array.array_week)
        val pendingLabel =
            if (selectedFilter.date == getCurrentDate()) getString(R.string.today_s_tasks)
            else "${arrayWeek[selectedFilter.index - 1]} tasks"
        binding.tvTitle.apply {
            if (currentTasksList.isEmpty()) {
                text = pendingLabel
                visible()
            } else gone()
        }
        pendingLabelAdapter = LabelAdapter(pendingLabel) {
            sharedViewModel.updateIsVisiblePendingTask(!isVisiblePendingTask)
        }
        allTaskAdapter = ConcatAdapter(
            pendingLabelAdapter,
            pinnedTasksAdapter,
            pendingTaskAdapter,
            completedLabelAdapter,
            completedTaskAdapter
        )
        sharedViewModel.updateIsVisiblePendingTask(pinnedTasksAdapter.itemCount != 0 || pendingTaskAdapter.itemCount != 0)
        sharedViewModel.updateIsVisibleCompletedTask(completedTaskAdapter.itemCount != 0)
    }

    private fun createTaskItemAdapter(taskList: List<Task>?) : TaskAdapter {
        return TaskAdapter(
            itemList = taskList,
            onItemClicked = { onPressItem(it) },
            onItemDelete = { onDeleteItem(it) },
            onItemPin = { onPinItem(it) },
            onClickDone = { onMarkAsDone(it) })
    }

    private fun onPinItem(task: Task) {
        val updatedPin = task.copy(isPinned = !task.isPinned)
        sharedViewModel.updateTaskPin(updatedPin)
        if (selectedFilter.index == 0) getWeeklyTasks() else getTaskByDate()
        if (searchValue.isNotEmpty()) resetSearchBar()
    }

    private fun onPressItem(task: Task) {
        TaskDetailsBottomSheetDialog(task.id).show(requireActivity().supportFragmentManager, "update_task")
    }

    private fun onDeleteItem(task: Task) {
        DeleteTaskDialog(task).show(requireActivity().supportFragmentManager, "delete_task")
        if (selectedFilter.index == 0) getWeeklyTasks() else getTaskByDate()
    }

    private fun onMarkAsDone(task: Task) {
        sharedViewModel.updateMarkAsDone(task)
        if (selectedFilter.index == 0) getWeeklyTasks() else getTaskByDate()
    }

    private fun setupFilterOption(weeklyTaskList: List<Task>, selectedFilter: HomeFilterModel) = with(binding) {
        rvMainFilter.adapter = MainFilterAdapter(weeklyTaskList, selectedFilter.index, pairDateName) { sharedViewModel.updateSelectedFilter(it) }
        rvMainFilter.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }

}