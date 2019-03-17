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
package com.afollestad.materialcab

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewPropertyAnimator
import com.afollestad.materialcab.attached.AttachedCab

typealias CreateCallback = (cab: AttachedCab, menu: Menu) -> Unit

typealias SelectCallback = (item: MenuItem) -> Boolean

typealias DestroyCallback = (cab: AttachedCab) -> Boolean

typealias CabAnimator = (view: View, animator: ViewPropertyAnimator) -> Unit

typealias CabApply = AttachedCab.() -> Unit

internal fun List<CreateCallback>.invokeAll(
  cab: AttachedCab,
  menu: Menu
) = forEach { it(cab, menu) }

internal fun List<SelectCallback>.invokeAll(menuItem: MenuItem): Boolean {
  if (isEmpty()) {
    return false
  }
  return all { it(menuItem) }
}

internal fun List<DestroyCallback>.invokeAll(cab: AttachedCab): Boolean {
  if (isEmpty()) {
    return true
  }
  return all { it(cab) }
}
