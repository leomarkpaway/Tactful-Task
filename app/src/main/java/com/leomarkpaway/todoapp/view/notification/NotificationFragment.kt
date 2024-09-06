package com.leomarkpaway.todoapp.view.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.leomarkpaway.todoapp.MyApp
import com.leomarkpaway.todoapp.common.extension.gone
import com.leomarkpaway.todoapp.common.extension.visible
import com.leomarkpaway.todoapp.common.util.viewModelFactory
import com.leomarkpaway.todoapp.data.source.local.entity.Notification
import com.leomarkpaway.todoapp.databinding.FragmentNotificationBinding
import com.leomarkpaway.todoapp.view.notification.adapter.NotificationAdapter
import com.leomarkpaway.todoapp.receiver.viewmodel.ReceiverViewModel
import kotlinx.coroutines.launch

// TODO for future development notification screen
class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private val viewModel: ReceiverViewModel by viewModels {
        viewModelFactory { ReceiverViewModel(MyApp.appModule.repository) }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClickBackButton()
        onClearNotificationAction()
        observeNotifications()
    }

    private fun onClickBackButton() {
        binding.imgBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun onClearNotificationAction() {
       binding.tvClear.setOnClickListener {
           viewModel.clearNotifications()
       }
    }

    private fun observeNotifications() {
        lifecycleScope.launch {
            viewModel.getNotifications().observe(viewLifecycleOwner) { notifications ->
                setupNotifiedTaskList(notifications as ArrayList)
                binding.tvEmptyNotification.apply { if (notifications.isEmpty()) visible() else gone() }
            }
        }
    }

    private fun setupNotifiedTaskList(notifications: ArrayList<Notification>) = with(binding.rvNotifiedTask) {
        adapter = NotificationAdapter(notifications) { onCLickItem(it) }
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun onCLickItem(id: Long) {
        val action = NotificationFragmentDirections.actionNotificationFragmentToTaskDetailFragment(id)
        findNavController().navigate(action)
    }

}