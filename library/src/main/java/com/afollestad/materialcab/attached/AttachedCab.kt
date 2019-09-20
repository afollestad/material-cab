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
package com.afollestad.materialcab.attached

import android.view.Menu
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import com.afollestad.materialcab.CabAnimator
import com.afollestad.materialcab.CreateCallback
import com.afollestad.materialcab.DestroyCallback
import com.afollestad.materialcab.SelectCallback

/**
 * A handle to a Contextual Action Bar instance.
 *
 * @author Aidan Follestad (@afollestad)
 */
interface AttachedCab {
  /** Sets the CAB's title. */
  fun title(
    @StringRes res: Int? = null,
    literal: CharSequence? = null
  )

  /** Sets the CAB's subtitle. */
  fun subtitle(
    @StringRes res: Int? = null,
    literal: CharSequence? = null
  )

  /** Sets the CAB's title text color. */
  fun titleColor(
    @ColorRes res: Int? = null,
    @ColorInt literal: Int? = null
  )

  /** Sets the CAB's subtitle text color. */
  fun subtitleColor(
    @ColorRes res: Int? = null,
    @ColorInt literal: Int? = null
  )

  /** Sets the CAB's popup (overflow) menu theme. */
  fun popupTheme(@StyleRes theme: Int)

  /** Sets the CAB's content inset (the start padding). */
  fun contentInsetStart(
    @DimenRes res: Int? = null,
    @Px literal: Int?
  )

  /** Sets the CAB's background color. */
  fun backgroundColor(
    @ColorRes res: Int? = null,
    @ColorInt literal: Int? = null
  )

  /** Sets the CAB's close (exit) drawable. */
  fun closeDrawable(@DrawableRes res: Int)

  /** Sets the CAB's menu. */
  fun menu(@MenuRes res: Int)

  /** Gets the CAB's menu. */
  fun getMenu(): Menu

  /** Sets a callback invoked when the CAB is being created/shown. */
  fun onCreate(callback: CreateCallback)

  /** Sets a callback invoked when a CAB menu item is selected. */
  fun onSelection(callback: SelectCallback)

  /** Sets a callback invoked when the CAB is being destroyed. */
  fun onDestroy(callback: DestroyCallback)

  /** Creates a custom animator for the CAB when it's creating/showing itself. */
  fun animateOnCreate(animator: CabAnimator)

  /** Creates a custom animator for the CAB when it's destroying/hiding itself. */
  fun animateOnDestroy(animator: CabAnimator)

  /** A shortcut around [animateOnCreate] and [animateOnDestroy] that fades in the CAB in. */
  fun fadeIn(durationMs: Long = 250)

  /** A shortcut around [animateOnCreate] and [animateOnDestroy] that slides the CAB in */
  fun slideDown(durationMs: Long = 200)
}

/** Returns true if the CAB is destroyed and unusable, or if the receiver is null. */
fun AttachedCab?.isDestroyed(): Boolean {
  return (this as? RealAttachedCab)?.isDestroyed() ?: true
}

/** Returns true if the CAB is active and usable. */
fun AttachedCab?.isActive(): Boolean = !isDestroyed()

/**
 * Destroys the contextual action bar, freeing up resources and references. Also makes this
 * CAB instance unusable. Returns true if the receiver was not null and the CAB was destroyed.
 */
fun AttachedCab?.destroy(): Boolean {
  return (this as? RealAttachedCab)?.startDestroy() ?: false
}
