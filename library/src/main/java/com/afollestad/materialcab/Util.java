package com.afollestad.materialcab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;

/** @author Aidan Follestad (afollestad) */
class Util {

  static int resolveDimension(Context context, @AttrRes int attr, @DimenRes int fallbackRes) {
    TypedArray a = context.getTheme().obtainStyledAttributes(new int[] {attr});
    try {
      return a.getDimensionPixelSize(0, (int) context.getResources().getDimension(fallbackRes));
    } finally {
      a.recycle();
    }
  }

  @ColorInt
  static int resolveColor(Context context, @AttrRes int attr, int fallback) {
    TypedArray a = context.getTheme().obtainStyledAttributes(new int[] {attr});
    try {
      return a.getColor(0, fallback);
    } finally {
      a.recycle();
    }
  }

  static int resolveInt(Context context, @AttrRes int attr, int fallback) {
    TypedArray a = context.getTheme().obtainStyledAttributes(new int[] {attr});
    try {
      return a.getInt(0, fallback);
    } finally {
      a.recycle();
    }
  }

  static String resolveString(Context context, @AttrRes int attr) {
    TypedValue v = new TypedValue();
    context.getTheme().resolveAttribute(attr, v, true);
    return (String) v.string;
  }

  static int resolveResId(Context context, @AttrRes int attr, int fallback) {
    TypedArray a = context.getTheme().obtainStyledAttributes(new int[] {attr});
    try {
      return a.getResourceId(0, fallback);
    } finally {
      a.recycle();
    }
  }

  static boolean isColorDark(int color) {
    double darkness =
        1
            - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color))
                / 255;
    return darkness >= 0.5;
  }

  static Drawable tintDrawable(Drawable drawable, @ColorInt int color) {
    Drawable wrapped = DrawableCompat.wrap(drawable);
    DrawableCompat.setTint(wrapped, color);
    return wrapped;
  }
}
