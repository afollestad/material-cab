/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialcabsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listitem_main.view.icon
import kotlinx.android.synthetic.main.listitem_main.view.title
import java.util.ArrayList

typealias ItemClickCallback = ((index: Int, longClick: Boolean) -> Unit)?
typealias IconClickCallback = ((index: Int) -> Unit)?

/** @author Aidan Follestad (afollestad) */
internal class MainAdapter(
  private val itemCallback: ItemClickCallback,
  private val iconCallback: IconClickCallback
) : RecyclerView.Adapter<MainAdapter.MainViewHolder>(),
    View.OnClickListener,
    View.OnLongClickListener {

  private var itemsList: MutableList<String> = mutableListOf()
  private var selectedList: MutableList<Int> = mutableListOf()

  val selectedCount: Int
    get() = selectedList.size

  fun add(item: String) {
    itemsList.add(item)
    notifyItemInserted(itemsList.size - 1)
  }

  fun toggleSelected(index: Int) {
    val newState = !selectedList.contains(index)
    if (newState) {
      selectedList.add(index)
    } else {
      selectedList.remove(index)
    }
    notifyItemChanged(index)
  }

  fun restoreState(
    from: Bundle?,
    ifNone: MainAdapter.() -> Unit
  ) {
    if (from == null || !from.containsKey("[main_adapter_items]")) {
      ifNone.invoke(this)
      return
    }
    itemsList = from.getStringArrayList("[main_adapter_items]") as ArrayList<String>
    selectedList = from.getIntegerArrayList("[main_adapter_selected]") as ArrayList<Int>
    notifyDataSetChanged()
  }

  fun saveState(out: Bundle) {
    out.putStringArrayList("[main_adapter_items]", ArrayList(itemsList))
    out.putIntegerArrayList("[main_adapter_selected]", ArrayList(selectedList))
  }

  fun clearSelected() {
    selectedList.clear()
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(
    viewGroup: ViewGroup,
    i: Int
  ): MainViewHolder {
    val view = LayoutInflater.from(viewGroup.context)
        .inflate(R.layout.listitem_main, viewGroup, false)
    return MainViewHolder(view)
  }

  override fun onBindViewHolder(
    mainViewHolder: MainViewHolder,
    i: Int
  ) {
    mainViewHolder.view.isActivated = selectedList.contains(i)
    mainViewHolder.view.tag = "item:$i"
    mainViewHolder.view.setOnClickListener(this)
    mainViewHolder.view.setOnLongClickListener(this)

    mainViewHolder.itemView.icon.tag = "icon:$i"
    mainViewHolder.itemView.icon.setOnClickListener(this)

    mainViewHolder.itemView.title.text = itemsList[i]
  }

  override fun getItemCount(): Int {
    return itemsList.size
  }

  fun getItem(index: Int): String {
    return itemsList[index]
  }

  override fun onClick(v: View) {
    val tag = (v.tag as String).split(":")
        .dropLastWhile { it.isEmpty() }
        .toTypedArray()
    val index = Integer.parseInt(tag[1])
    if (tag[0] == "icon") {
      iconCallback?.invoke(index)
    } else {
      itemCallback?.invoke(index, false)
    }
  }

  override fun onLongClick(v: View): Boolean {
    val tag = (v.tag as String).split(":".toRegex())
        .dropLastWhile { it.isEmpty() }
        .toTypedArray()
    val index = Integer.parseInt(tag[1])
    itemCallback?.invoke(index, true)
    return false
  }

  internal class MainViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
