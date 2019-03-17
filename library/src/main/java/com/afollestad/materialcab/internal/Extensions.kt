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
package com.afollestad.materialcab.internal

import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.ViewTreeObserver
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

@Px internal fun Context.dimen(@DimenRes res: Int): Int {
  return resources.getDimensionPixelSize(res)
}

@ColorInt internal fun Context.color(@ColorRes res: Int): Int {
  return ContextCompat.getColor(this, res)
}

@ColorInt internal fun Context.colorAttr(
  @AttrRes attr: Int,
  fallback: Int = 0
): Int {
  val a = theme.obtainStyledAttributes(intArrayOf(attr))
  try {
    return a.getColor(0, fallback)
  } finally {
    a.recycle()
  }
}

internal fun Context.string(
  @StringRes res: Int,
  vararg args: Any
): String = resources.getString(res, args)

internal fun Drawable.tint(@ColorInt color: Int): Drawable {
  val wrapped = DrawableCompat.wrap(this)
  DrawableCompat.setTint(wrapped, color)
  return wrapped
}

internal fun Context.drawable(@DrawableRes res: Int): Drawable {
  return ContextCompat.getDrawable(this, res)!!
}

internal inline fun ViewPropertyAnimator.onAnimationEnd(
  crossinline continuation: (android.animation.Animator) -> Unit
) {
  setListener(object : AnimatorListenerAdapter() {
    override fun onAnimationEnd(animation: android.animation.Animator) {
      continuation(animation)
      setListener(null)
    }
  })
}

@Suppress("DEPRECATION")
internal inline fun View.onLayout(crossinline callback: (view: View) -> Unit) {
  viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
    override fun onGlobalLayout() {
      callback(this@onLayout)
      viewTreeObserver.removeGlobalOnLayoutListener(this)
    }
  })
}

internal fun Context.requireOneString(
  literal: String?,
  @StringRes res: Int?,
  vararg args: Any
): String {
  return when {
    literal != null -> literal
    res != null -> string(res)
    else -> throw IllegalStateException(
        "You must provide either a literal or resource value."
    )
  }
}

internal fun Context.requireOneDimen(
  @Px literal: Int?,
  @DimenRes res: Int?
): Int {
  return when {
    literal != null -> literal
    res != null -> dimen(res)
    else -> throw IllegalStateException(
        "You must provide either a literal or resource value."
    )
  }
}

internal fun Context.requireOneColor(
  @ColorInt literal: Int?,
  @ColorRes res: Int?
): Int {
  return when {
    literal != null -> literal
    res != null -> color(res)
    else -> throw IllegalStateException(
        "You must provide either a literal or resource value."
    )
  }
}

internal fun Context.idName(@IdRes res: Int): String {
  return resources.getResourceName(res)
}

internal inline fun <reified T : View> ViewGroup.inflate(@LayoutRes res: Int): T {
  return LayoutInflater.from(context).inflate(res, this, false) as T
}

internal fun View.removeSelf() {
  (parent as? ViewGroup)?.removeView(this)
}
