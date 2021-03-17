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

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.MenuRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialcab.CabAnimator
import com.afollestad.materialcab.CreateCallback
import com.afollestad.materialcab.DestroyCallback
import com.afollestad.materialcab.R
import com.afollestad.materialcab.SelectCallback
import com.afollestad.materialcab.internal.*
import com.afollestad.materialcab.internal.colorAttr
import com.afollestad.materialcab.internal.drawable
import com.afollestad.materialcab.internal.onAnimationEnd
import com.afollestad.materialcab.internal.onLayout
import com.afollestad.materialcab.internal.removeSelf
import com.afollestad.materialcab.internal.requireOneColor
import com.afollestad.materialcab.internal.requireOneDimen
import com.afollestad.materialcab.internal.requireOneString
import com.afollestad.materialcab.internal.tint
import com.afollestad.materialcab.invokeAll

/** @author Aidan Follestad (@afollestad) */
class RealAttachedCab internal constructor(
  private var context: Activity?,
  private var toolbar: Toolbar?,
  private val replacedViewStub: Boolean
) : AttachedCab {
  init {
    titleColor(literal = Color.WHITE)
    backgroundColor(literal = attachedContext.colorAttr(R.attr.colorPrimaryDark, Color.GRAY))
  }

  private var isDestroying: Boolean = false
  private val attachedContext: Activity
    get() = context ?: throw IllegalStateException("Contextual action bar is already destroyed.")
  private val attachedToolbar: Toolbar
    get() = toolbar ?: throw IllegalStateException("Contextual action bar is already destroyed.")

  private var titleTextColor: Int = Color.WHITE
  private var closeDrawable: Drawable = attachedContext.drawable(R.drawable.mcab_nav_close)

  private var createCallbacks = mutableListOf<CreateCallback>()
  private var selectCallbacks = mutableListOf<SelectCallback>()
  private var destroyCallbacks = mutableListOf<DestroyCallback>()

  private var createAnimator: CabAnimator? = null
  private var destroyAnimator: CabAnimator? = null

  internal fun show() = attachedToolbar.run {
    isDestroying = false
    translationY = 0f
    alpha = 1f

    navigationIcon = closeDrawable.tint(titleTextColor)
    setNavigationOnClickListener { destroy() }
    createCallbacks.invokeAll(this@RealAttachedCab, menu)
    animate()
        .setListener(null)
        .cancel()

    visibility = VISIBLE
    bringToFront()
    onLayout { createAnimator?.invoke(this, animate()) }
  }

  override fun title(
    @StringRes res: Int?,
    literal: String?
  ) {
    attachedToolbar.title = attachedContext.requireOneString(literal, res)
  }

  override fun subtitle(
    @StringRes res: Int?,
    literal: String?
  ) {
    attachedToolbar.subtitle = attachedContext.requireOneString(literal, res)
  }

  override fun titleColor(
    @ColorRes res: Int?,
    @ColorInt literal: Int?
  ) {
    titleTextColor = attachedContext.requireOneColor(literal, res)
    attachedToolbar.setTitleTextColor(titleTextColor)
  }

  override fun subtitleColor(
    @ColorRes res: Int?,
    @ColorInt literal: Int?
  ) {
    attachedToolbar.setSubtitleTextColor(attachedContext.requireOneColor(literal, res))
  }

  override fun popupTheme(@StyleRes theme: Int) {
    attachedToolbar.popupTheme = theme
  }

  override fun contentInsetStart(
    @DimenRes res: Int?,
    @Px literal: Int?
  ) {
    attachedToolbar.setContentInsetsRelative(
        attachedContext.requireOneDimen(literal, res),
        0
    )
  }

  override fun backgroundColor(
    @ColorRes res: Int?,
    @ColorInt literal: Int?
  ) {
    attachedToolbar.setBackgroundColor(attachedContext.requireOneColor(literal, res))
  }

  override fun closeDrawable(@DimenRes res: Int) {
    closeDrawable = attachedContext.drawable(res)
    attachedToolbar.navigationIcon = closeDrawable.tint(titleTextColor)
  }

  override fun menu(@MenuRes res: Int) {
    attachedToolbar.run {
      menu?.clear()
      if (res != 0) {
        inflateMenu(res)
        setOnMenuItemClickListener(menuClickListener)
      } else {
        setOnMenuItemClickListener(null)
      }
    }
  }

  override fun getMenu(): Menu = attachedToolbar.menu

  override fun menuIconColor(res: Int?, literal: Int?) {
    val color = attachedContext.requireOneColor(literal, res)
    attachedToolbar.menu.tintAllIcons(color)
    attachedToolbar.overflowIcon = attachedToolbar.overflowIcon?.tint(color)
  }

  override fun onCreate(callback: CreateCallback) {
    this.createCallbacks.add(callback)
  }

  override fun onSelection(callback: SelectCallback) {
    this.selectCallbacks.add(callback)
  }

  override fun onDestroy(callback: DestroyCallback) {
    this.destroyCallbacks.add(callback)
  }

  override fun animateOnCreate(animator: CabAnimator) {
    this.createAnimator = animator
  }

  override fun animateOnDestroy(animator: CabAnimator) {
    this.destroyAnimator = animator
  }

  override fun fadeIn(durationMs: Long) {
    animateOnCreate { view, animator ->
      view.alpha = 0f
      animator.alpha(1f)
          .setDuration(durationMs)
          .start()
    }
    animateOnDestroy { view, animator ->
      view.alpha = 1f
      animator.alpha(0f)
          .setDuration(durationMs)
          .start()
    }
  }

  override fun slideDown(durationMs: Long) {
    animateOnCreate { view, animator ->
      view.translationY = (-view.measuredHeight).toFloat()
      animator.translationY(0f)
          .setDuration(durationMs)
          .start()
    }
    animateOnDestroy { view, animator ->
      view.translationY = 0f
      val endTranslation = (-view.measuredHeight).toFloat()
      animator.translationY(endTranslation)
          .setDuration(durationMs)
          .start()
    }
  }

  fun isDestroyed(): Boolean {
    return context == null || toolbar == null || isDestroying
  }

  fun startDestroy(): Boolean = synchronized(isDestroying) {
    if (isDestroyed()) return false
    isDestroying = true

    val canDestroy = destroyCallbacks.invokeAll(this@RealAttachedCab)
    if (!canDestroy) {
      isDestroying = false
      return false
    }

    val animator = destroyAnimator
    attachedToolbar.run {
      if (animator != null) {
        animate().cancel()
        animate().onAnimationEnd { finalizeDestroy() }
        animator(this, animate())
      } else {
        finalizeDestroy()
      }
    }

    return true
  }

  private fun finalizeDestroy() {
    attachedToolbar.visibility = GONE
    if (!replacedViewStub) {
      // If we replaced a view stub, we should not remove the toolbar because
      // we end up with nothing to attach to or replace later.
      attachedToolbar.removeSelf()
    }
    toolbar = null
    context = null
  }

  private val menuClickListener = Toolbar.OnMenuItemClickListener { item ->
    selectCallbacks.invokeAll(item)
  }
}
