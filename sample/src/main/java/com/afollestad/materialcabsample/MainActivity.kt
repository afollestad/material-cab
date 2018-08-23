/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialcabsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.widget.Toast
import com.afollestad.materialcab.MaterialCab
import kotlinx.android.synthetic.main.activity_main.list

/** @author Aidan Follestad (afollestad) */
class MainActivity : AppCompatActivity() {

  private lateinit var adapter: MainAdapter
  private var toast: Toast? = null

  private fun onItemClicked(
    index: Int,
    longClick: Boolean
  ) {
    if (longClick || MaterialCab.isActive) {
      onIconClicked(index)
      return
    }
    showToast(adapter.getItem(index))
  }

  private fun onIconClicked(index: Int) {
    adapter.toggleSelected(index)
    if (adapter.selectedCount == 0) {
      MaterialCab.destroy()
      return
    }

    MaterialCab.attach(this, R.id.cab_stub) {
      title = getString(R.string.x_selected, adapter.selectedCount)
      menuRes = R.menu.menu_cab

      onCreate { cab, menu -> onCabCreated(cab, menu) }
      onSelection {
        showToast(it.title as String)
        true
      }
      onDestroy {
        adapter.clearSelected()
        true
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    adapter = MainAdapter(
        { i, b -> onItemClicked(i, b) },
        { onIconClicked(it) })

    list.layoutManager = LinearLayoutManager(this)
    list.adapter = adapter

    setSupportActionBar(findViewById(R.id.main_toolbar))

    MaterialCab.tryRestore(this, savedInstanceState)

    adapter.restoreState(savedInstanceState) {
      for (i in 0..80) {
        adapter.add("Item $i")
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    MaterialCab.saveState(outState)
    adapter.saveState(outState)
  }

  private fun showToast(text: String) {
    toast?.cancel()
    toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
    toast!!.show()
  }

  private fun onCabCreated(
    cab: MaterialCab,
    menu: Menu
  ): Boolean {
    // Makes the icons in the overflow menu visible
    if (menu.javaClass.simpleName == "MenuBuilder") {
      try {
        val field = menu.javaClass.getDeclaredField("mOptionalIconsVisible")
        field.isAccessible = true
        field.setBoolean(menu, true)
      } catch (ignored: Exception) {
        ignored.printStackTrace()
      }
    }
    return true // allow creation
  }

  override fun onBackPressed() {
    if (!MaterialCab.destroy()) {
      super.onBackPressed()
    }
  }
}
