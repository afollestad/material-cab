@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.afollestad.materialcab

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.CheckResult
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.MenuRes
import android.support.annotation.RestrictTo
import android.support.annotation.RestrictTo.Scope
import android.support.annotation.StringRes
import android.support.annotation.StyleRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub

typealias CreateCallback = (cab: MaterialCab, menu: Menu) -> Unit
typealias SelectCallback = (item: MenuItem) -> Boolean
typealias DestroyCallback = (cab: MaterialCab) -> Boolean

/** @author Aidan Follestad (afollestad) */
class MaterialCab(
  private var ctxt: AppCompatActivity?,
  @IdRes private var attachToId: Int
) : Toolbar.OnMenuItemClickListener {

  private var toolbar: Toolbar? = null
  private var createCallback: CreateCallback? = null
  private var selectCallback: SelectCallback? = null
  private var destroyCallback: DestroyCallback? = null

  var title: String? = null
    set(value) {
      field = value
      toolbar?.title = value
    }

  fun titleRes(@StringRes res: Int) {
    title = context.string(res)
  }

  @ColorInt
  var titleColor: Int = Color.WHITE
    set(value) {
      field = value
      toolbar?.setTitleTextColor(value)
    }

  fun titleColorRes(@ColorRes res: Int) {
    titleColor = context.color(res)
  }

  @StyleRes
  var popupTheme: Int = R.style.ThemeOverlay_AppCompat_Light
    set(value) {
      field = value
      toolbar?.popupTheme = value
    }

  var contentInsetStart: Int = context.dimen(R.dimen.mcab_default_content_inset)
    set(value) {
      field = value
      toolbar?.setContentInsetsRelative(value, 0)
    }

  fun contentInsetStartRes(@DimenRes res: Int) {
    contentInsetStart = context.dimen(res)
  }

  @MenuRes
  var menuRes: Int = 0
    set(value) {
      field = value
      toolbar?.menu?.clear()
      if (value != 0) {
        toolbar?.inflateMenu(value)
        toolbar?.setOnMenuItemClickListener(this)
      } else {
        toolbar?.setOnMenuItemClickListener(null)
      }
    }

  @ColorInt
  var backgroundColor: Int = context.colorAttr(R.attr.colorPrimary, Color.GRAY)
    set(value) {
      field = value
      toolbar?.setBackgroundColor(value)
    }

  fun backgroundColorRes(@ColorRes res: Int) {
    backgroundColor = context.color(res)
  }

  @DrawableRes
  var closeDrawableRes: Int = R.drawable.mcab_nav_close
    set(value) {
      field = value
      if (value == R.drawable.mcab_nav_close) {
        toolbar?.setNavigationIcon(value)
      } else {
        val iconRef = context.drawable(value)
        toolbar?.navigationIcon = iconRef.tint(titleColor)
      }
    }

  fun onCreate(callback: CreateCallback) {
    this.createCallback = callback
  }

  fun onSelection(callback: SelectCallback) {
    this.selectCallback = callback
  }

  fun onDestroy(callback: DestroyCallback) {
    this.destroyCallback = callback
  }

  private val context
    @CheckResult
    get() = ctxt!!

  companion object {

    @SuppressLint("StaticFieldLeak")
    var instance: MaterialCab? = null

    inline fun attach(
      context: AppCompatActivity,
      @IdRes attachToId: Int,
      exec: MaterialCab.() -> Unit
    ) {
      val isNew = instance == null
      if (isNew) instance = MaterialCab(context, attachToId)
      instance!!.exec()
      instance!!.inject(isNew)
    }

    fun tryRestore(
      context: AppCompatActivity,
      fromState: Bundle?
    ): Boolean {
      if (fromState == null || !fromState.containsKey(KEY_ATTACHTO_ID)) {
        return false
      }
      val attachToId = fromState.getInt(KEY_ATTACHTO_ID)
      attach(context, attachToId) {
        this.title = fromState.getString(KEY_TITLE)
        this.titleColor = fromState.getInt(KEY_TITLE_COLOR)
        this.popupTheme = fromState.getInt(KEY_POPUP_THEME)
        this.menuRes = fromState.getInt(KEY_MENU_RES)
        this.closeDrawableRes = fromState.getInt(KEY_CLOSE_DRAWABLE_RES)
        this.backgroundColor = fromState.getInt(KEY_BACKGROUND_COLOR)
        this.contentInsetStart = fromState.getInt(KEY_CONTENT_INSET_START)
      }
      return true
    }

    fun saveState(out: Bundle?) {
      if (out == null || instance == null) {
        return
      }
      with(instance!!) {
        out.putInt(KEY_ATTACHTO_ID, attachToId)
        out.putString(KEY_TITLE, title)
        out.putInt(KEY_TITLE_COLOR, titleColor)
        out.putInt(KEY_POPUP_THEME, popupTheme)
        out.putInt(KEY_MENU_RES, menuRes)
        out.putInt(KEY_CLOSE_DRAWABLE_RES, closeDrawableRes)
        out.putInt(KEY_BACKGROUND_COLOR, backgroundColor)
        out.putInt(KEY_CONTENT_INSET_START, contentInsetStart)
      }
    }

    var isActive: Boolean = false
      get() = instance != null

    fun destroy(): Boolean {
      if (instance == null) {
        return false
      }

      val canDestroy = instance?.destroyCallback?.invoke(instance!!) ?: true
      if (!canDestroy) {
        // Callback signaled to block destruction
        return false
      }

      instance?.toolbar?.visibility = View.GONE
      instance?.toolbar = null
      instance?.ctxt = null
      instance = null
      return true
    }
  }

  @RestrictTo(Scope.LIBRARY_GROUP)
  fun inject(isNew: Boolean) {
    with(context) {
      val attachToView = findViewById<View>(attachToId)
      when {
        findViewById<View>(R.id.mcab_toolbar) != null ->
          toolbar = findViewById<View>(R.id.mcab_toolbar) as Toolbar
        attachToView is ViewStub -> {
          attachToView.layoutResource = R.layout.mcab_toolbar
          attachToView.inflatedId = R.id.mcab_toolbar
          toolbar = attachToView.inflate() as Toolbar
        }
        attachToView is ViewGroup -> {
          toolbar =
              LayoutInflater.from(this).inflate(
                  R.layout.mcab_toolbar, attachToView, false
              ) as Toolbar
          attachToView.addView(toolbar)
        }
        else ->
          throw IllegalStateException(
              "MaterialCab was unable to attach to your Activity, attach to stub doesn't exist."
          )
      }
    }

    // Invalidates everything now that a Toolbar definitely exists
    this.title = title
    this.titleColor = titleColor
    this.popupTheme = popupTheme
    this.menuRes = menuRes
    this.closeDrawableRes = closeDrawableRes
    this.backgroundColor = backgroundColor
    this.contentInsetStart = contentInsetStart

    with(toolbar!!) {
      visibility = View.VISIBLE
      id = R.id.mcab_toolbar
      setNavigationOnClickListener { destroy() }
    }

    if (isNew) {
      createCallback?.invoke(this, toolbar!!.menu)
    }
  }

  override fun onMenuItemClick(item: MenuItem): Boolean {
    return selectCallback != null && selectCallback!!.invoke(item)
  }
}
