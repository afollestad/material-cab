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
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialcab.attached.AttachedCab
import com.afollestad.materialcab.attached.destroy
import com.afollestad.materialcab.attached.isActive
import com.afollestad.materialcab.createCab
import com.afollestad.recyclical.datasource.emptySelectableDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.viewholder.isSelected
import com.afollestad.recyclical.withItem
import kotlinx.android.synthetic.main.activity_main.list

/** @author Aidan Follestad (afollestad) */
class MainActivity : AppCompatActivity() {
  private val dataSource = emptySelectableDataSource().apply {
    onSelectionChange { invalidateCab() }
  }
  private var mainCab: AttachedCab? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(findViewById(R.id.main_toolbar))

    dataSource.set(
        IntArray(100) { it + 1 }
            .map { MainItem("Item #$it") }
    )

    list.setup {
      withDataSource(dataSource)
      withItem<MainItem, MainViewHolder>(R.layout.listitem_main) {
        onBind(::MainViewHolder) { index, item ->
          itemView.isActivated = isSelected()
          title.text = item.title
          icon.setOnClickListener {
            dataSource.toggleSelectionAt(index)
          }
        }
        onClick {
          if (hasSelection()) {
            toggleSelection()
          } else {
            toast("Clicked $item")
          }
        }
        onLongClick { toggleSelection() }
      }
    }
  }

  private fun invalidateCab() {
    if (!dataSource.hasSelection()) {
      mainCab?.destroy()
      return
    }

    if (mainCab.isActive()) {
      mainCab?.apply {
        title(literal = getString(R.string.x_selected, dataSource.getSelectionCount()))
      }
    } else {
      mainCab = createCab(R.id.cab_stub) {
        title(literal = getString(R.string.x_selected, dataSource.getSelectionCount()))
        menu(R.menu.menu_cab)
        popupTheme(R.style.ThemeOverlay_AppCompat_Light)
        slideDown()

        onCreate { _, menu -> onCabCreated(menu) }
        onSelection {
          toast(it.title as String)
          true
        }
        onDestroy {
          dataSource.deselectAll()
          true
        }
      }
    }
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
    if (!mainCab.destroy()) {
      super.onBackPressed()
    }
  }
}
