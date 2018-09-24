/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialcab

import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.ViewTreeObserver
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
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

internal fun Context.integer(
  @AttrRes attr: Int,
  fallback: Int
): Int {
  val a = theme.obtainStyledAttributes(intArrayOf(attr))
  try {
    return a.getInt(0, fallback)
  } finally {
    a.recycle()
  }
}

internal fun Context.string(@StringRes res: Int): String {
  return resources.getString(res)
}

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
