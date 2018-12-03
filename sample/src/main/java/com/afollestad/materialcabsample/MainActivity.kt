/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.afollestad.materialcabsample

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

      onCreate { _, menu -> onCabCreated(menu) }
      onSelection {
        showToast(it.title as String)
        true
      }
      onDestroy {
        adapter.clearSelected()
        true
      }

      slideDown()
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

    MaterialCab.tryRestore(this, savedInstanceState) {
      slideDown()
    }

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

  private fun onCabCreated(menu: Menu): Boolean {
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
