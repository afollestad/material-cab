/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.afollestad.materialcab

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewStub
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/** @author Aidan Follestad (afollestad) */
class MaterialCab(
  private var ctxt: AppCompatActivity?,
  @IdRes private var attachToId: Int
) : Toolbar.OnMenuItemClickListener {

  private var createCallback: CreateCallback? = null
  private var selectCallback: SelectCallback? = null
  private var destroyCallback: DestroyCallback? = null

  internal var toolbar: Toolbar? = null
  internal var createAnimator: CabAnimator? = null
  internal var destroyAnimator: CabAnimator? = null

  var title: String? = null
    set(value) {
      field = value
      toolbar?.title = value
    }
  var subtitle: String? = null
    set(value) {
      field = value
      toolbar?.subtitle = value
    }

  fun titleRes(@StringRes res: Int) {
    title = context.string(res)
  }

  fun subtitleRes(@StringRes res: Int) {
    subtitle = context.string(res)
  }

  @ColorInt
  var titleColor: Int = Color.WHITE
    set(value) {
      field = value
      toolbar?.setTitleTextColor(value)
    }

  @ColorInt
  var subtitleColor: Int = Color.WHITE
    set(value) {
      field = value
      toolbar?.setSubtitleTextColor(value)
    }

  fun subtitleColorRes(@ColorRes res: Int) {
    subtitleColor = context.color(res)
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
  var backgroundColor: Int = context.colorAttr(R.attr.colorPrimaryDark, Color.GRAY)
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

  fun animateOnCreate(animator: CabAnimator) {
    this.createAnimator = animator
  }

  fun animateOnDestroy(animator: CabAnimator) {
    this.destroyAnimator = animator
  }

  fun fadeIn(durationMs: Long = 250) {
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

  fun slideDown(durationMs: Long = 200) {
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
      if (isNew) {
        instance = MaterialCab(context, attachToId)
      }
      with(instance!!) {
        exec()
        inject(isNew)
      }
    }

    fun tryRestore(
      context: AppCompatActivity,
      fromState: Bundle?,
      reconfigure: CabApply? = null
    ): Boolean {
      if (fromState == null || !fromState.containsKey(KEY_ATTACHTO_ID)) {
        return false
      }
      val attachToId = fromState.getInt(KEY_ATTACHTO_ID)
      attach(context, attachToId) {
        this.title = fromState.getString(KEY_TITLE)
        this.titleColor = fromState.getInt(KEY_TITLE_COLOR)
        this.subtitle = fromState.getString(KEY_SUBTITLE)
        this.subtitleColor = fromState.getInt(KEY_SUBTITLE_COLOR)
        this.popupTheme = fromState.getInt(KEY_POPUP_THEME)
        this.menuRes = fromState.getInt(KEY_MENU_RES)
        this.closeDrawableRes = fromState.getInt(KEY_CLOSE_DRAWABLE_RES)
        this.backgroundColor = fromState.getInt(KEY_BACKGROUND_COLOR)
        this.contentInsetStart = fromState.getInt(KEY_CONTENT_INSET_START)
        if (reconfigure != null) {
          this.reconfigure()
        }
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
        out.putString(KEY_SUBTITLE, subtitle)
        out.putInt(KEY_SUBTITLE_COLOR, subtitleColor)
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
      with(instance!!) {
        val canDestroy = destroyCallback?.invoke(this) ?: true
        if (!canDestroy) {
          // Callback signaled to block destruction
          return false
        }

        val animator = this.destroyAnimator
        if (animator != null) {
          val view = this.toolbar!!
          view.animate()
              .cancel()
          view.animate()
              .onAnimationEnd { finalizeDestroy() }
          animator(view, view.animate())
        } else {
          finalizeDestroy()
        }
        return true
      }
    }

    internal fun finalizeDestroy() = with(instance!!) {
      toolbar?.visibility = View.GONE
      toolbar = null
      ctxt = null
      instance = null
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
    this.subtitle = subtitle
    this.subtitleColor = subtitleColor
    this.popupTheme = popupTheme
    this.menuRes = menuRes
    this.closeDrawableRes = closeDrawableRes
    this.backgroundColor = backgroundColor
    this.contentInsetStart = contentInsetStart

    with(toolbar!!) {
      visibility = VISIBLE
      id = R.id.mcab_toolbar
      setNavigationOnClickListener { destroy() }

      if (isNew) {
        createCallback?.invoke(this@MaterialCab, menu)
        animate()
            .setListener(null)
            .cancel()
        onLayout { createAnimator?.invoke(this, animate()) }
      }
    }
  }

  override fun onMenuItemClick(item: MenuItem) = selectCallback?.invoke(item) ?: false
}
