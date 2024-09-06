package com.leomarkpaway.todoapp.common.util

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu

class PopupMenu(context: Context, view: View, menuItem: Int) {
    private val popupMenu = PopupMenu(context, view)

    init {
        popupMenu.menuInflater.inflate(menuItem, popupMenu.menu)
        try {
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenu)
            menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(menu, true)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            popupMenu
        }
    }

    fun setOnMenuItemClickListener(onItemCLick: (MenuItem) -> Unit) {
        popupMenu.setOnMenuItemClickListener { items ->
            onItemCLick(items)
            true
        }
    }

    fun setMenuTitle(itemId: Int, title: String) {
        popupMenu.menu.findItem(itemId).title = title
    }

    fun setMenuIcon(itemId: Int, icon: Int) {
        popupMenu.menu.findItem(itemId).setIcon(icon)
    }

    fun hideMenuItem(itemId: Int) {
        popupMenu.menu.findItem(itemId).isVisible = false
    }

    fun apply(apply: (PopupMenu) -> Unit) {
        apply(popupMenu)
    }

    fun show() = popupMenu.show()

}
