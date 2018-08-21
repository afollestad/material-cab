package com.afollestad.materialcab

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

@Px
internal fun Context.dimen(@DimenRes res: Int): Int {
  return resources.getDimensionPixelSize(res)
}

@Px internal fun Context.dimenAttr(@AttrRes attr: Int, @DimenRes fallbackRes: Int = 0): Int {
  val a = theme.obtainStyledAttributes(intArrayOf(attr))
  try {
    return a.getDimensionPixelSize(0, resources.getDimension(fallbackRes).toInt())
  } finally {
    a.recycle()
  }
}

@ColorInt
internal fun Context.color(@ColorRes res: Int): Int {
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

internal fun Context.stringAttr(@AttrRes attr: Int): String {
  val v = TypedValue()
  theme.resolveAttribute(attr, v, true)
  return v.string as String
}

@IdRes internal fun Context.resId(
  @AttrRes attr: Int,
  fallback: Int = 0
): Int {
  val a = theme.obtainStyledAttributes(intArrayOf(attr))
  try {
    return a.getResourceId(0, fallback)
  } finally {
    a.recycle()
  }
}

internal fun Int.isColorDark(): Boolean {
  val darkness =
    1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
  return darkness >= 0.5
}

internal fun Drawable.tint(@ColorInt color: Int): Drawable {
  val wrapped = DrawableCompat.wrap(this)
  DrawableCompat.setTint(wrapped, color)
  return wrapped
}

internal fun Context.drawable(@DrawableRes res: Int): Drawable {
  return ContextCompat.getDrawable(this, res)!!
}
