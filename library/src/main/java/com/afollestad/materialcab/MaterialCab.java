package com.afollestad.materialcab;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

/** @author Aidan Follestad (afollestad) */
public class MaterialCab implements Toolbar.OnMenuItemClickListener, Parcelable {

  private static final String BUNDLE_NAME = "[mcab_parcel_state]";

  public interface Callback {
    boolean onCabCreated(@NonNull MaterialCab cab, @NonNull Menu menu);

    boolean onCabItemClicked(@NonNull MenuItem item);

    boolean onCabFinished(@NonNull MaterialCab cab);
  }

  @Override
  public boolean onMenuItemClick(MenuItem item) {
    return callback != null && callback.onCabItemClicked(item);
  }

  private transient AppCompatActivity context;
  private transient Toolbar toolbar;
  private transient Callback callback;

  @IdRes private int attacherId;
  private String title;
  @ColorInt private int titleColor;
  @StyleRes private int popupTheme;
  private int contentInsetStart;
  @MenuRes private int menu;
  @ColorInt private int backgroundColor;
  @DrawableRes private int closeDrawable;
  private boolean isActive;

  public MaterialCab(@NonNull AppCompatActivity context, @IdRes int attacherId) {
    this.context = context;
    this.attacherId = attacherId;
    reset();
  }

  public void context(@NonNull AppCompatActivity context) {
    this.context = context;
  }

  public boolean isActive() {
    return isActive;
  }

  @UiThread
  public MaterialCab reset() {
    title = Util.resolveString(context, R.attr.mcab_title);
    popupTheme =
        Util.resolveResId(context, R.attr.mcab_popup_theme, R.style.ThemeOverlay_AppCompat_Light);
    contentInsetStart =
        Util.resolveDimension(
            context, R.attr.mcab_contentinset_start, R.dimen.mcab_default_content_inset);
    menu = Util.resolveResId(context, R.attr.mcab_menu, 0);
    backgroundColor =
        Util.resolveColor(
            context,
            R.attr.mcab_background_color,
            Util.resolveColor(context, R.attr.colorPrimary, Color.GRAY));
    titleColor =
        Util.resolveColor(
            context,
            R.attr.mcab_title_color,
            Util.isColorDark(backgroundColor) ? Color.WHITE : Color.BLACK);
    closeDrawable =
        Util.resolveResId(
            context,
            R.attr.mcab_close_drawable,
            Util.resolveResId(context, R.attr.actionModeCloseDrawable, R.drawable.mcab_nav_back));
    if (toolbar != null && toolbar.getMenu() != null) {
      toolbar.getMenu().clear();
    }
    return this;
  }

  @UiThread
  public MaterialCab start(@Nullable Callback callback) {
    this.callback = callback;
    invalidateVisibility(attach());
    return this;
  }

  @UiThread
  public MaterialCab title(@Nullable String title) {
    this.title = title;
    if (toolbar != null) {
      toolbar.setTitle(title);
    }
    return this;
  }

  @UiThread
  public MaterialCab titleRes(@StringRes int titleRes) {
    return title(context.getResources().getString(titleRes));
  }

  @UiThread
  public MaterialCab titleRes(@StringRes int titleRes, Object... formatArgs) {
    return title(context.getResources().getString(titleRes, formatArgs));
  }

  @UiThread
  public MaterialCab menu(@MenuRes int menuRes) {
    menu = menuRes;
    if (toolbar != null) {
      if (toolbar.getMenu() != null) {
        toolbar.getMenu().clear();
      }
      if (menuRes != 0) {
        toolbar.inflateMenu(menuRes);
      }
      toolbar.setOnMenuItemClickListener(this);
    }
    return this;
  }

  @UiThread
  public MaterialCab popupMenuTheme(@StyleRes int themeRes) {
    popupTheme = themeRes;
    if (toolbar != null) {
      toolbar.setPopupTheme(themeRes);
    }
    return this;
  }

  @UiThread
  public MaterialCab contentInsetStart(int contentInset) {
    contentInsetStart = contentInset;
    if (toolbar != null) {
      toolbar.setContentInsetsRelative(contentInset, 0);
    }
    return this;
  }

  @UiThread
  public MaterialCab contentInsetStartRes(@DimenRes int contentInsetRes) {
    return contentInsetStart((int) context.getResources().getDimension(contentInsetRes));
  }

  @UiThread
  public MaterialCab contentInsetStartAttr(@AttrRes int contentInsetAttr) {
    return contentInsetStart(Util.resolveInt(context, contentInsetAttr, 0));
  }

  @UiThread
  public MaterialCab titleColor(@ColorInt int color) {
    titleColor = color;
    if (toolbar != null) {
      toolbar.setTitleTextColor(color);
    }
    return this;
  }

  @UiThread
  public MaterialCab backgroundColor(@ColorInt int color) {
    backgroundColor = color;
    if (toolbar != null) {
      toolbar.setBackgroundColor(color);
    }
    return this;
  }

  @UiThread
  public MaterialCab backgroundColorRes(@ColorRes int colorRes) {
    return backgroundColor(context.getResources().getColor(colorRes));
  }

  @UiThread
  public MaterialCab backgroundColorAttr(@AttrRes int colorAttr) {
    return backgroundColor(Util.resolveColor(context, colorAttr, 0));
  }

  @UiThread
  public MaterialCab closeDrawableRes(@DrawableRes int closeDrawableRes) {
    closeDrawable = closeDrawableRes;
    if (toolbar != null) {
      toolbar.setNavigationIcon(closeDrawable);
    }
    return this;
  }

  public Menu menu() {
    return toolbar != null ? toolbar.getMenu() : null;
  }

  public Toolbar toolbar() {
    return toolbar;
  }

  @UiThread
  public void finish() {
    invalidateVisibility(!(callback == null || callback.onCabFinished(this)));
  }

  private void invalidateVisibility(boolean active) {
    if (toolbar == null) {
      return;
    }
    toolbar.setVisibility(active ? View.VISIBLE : View.GONE);
    this.isActive = active;
  }

  private boolean attach() {
    final View attacher = context.findViewById(attacherId);
    if (context.findViewById(R.id.mcab_toolbar) != null) {
      toolbar = (Toolbar) context.findViewById(R.id.mcab_toolbar);
    } else if (attacher instanceof ViewStub) {
      ViewStub stub = (ViewStub) attacher;
      stub.setLayoutResource(R.layout.mcab_toolbar);
      stub.setInflatedId(R.id.mcab_toolbar);
      toolbar = (Toolbar) stub.inflate();
    } else if (attacher instanceof ViewGroup) {
      ViewGroup parent = (ViewGroup) attacher;
      toolbar =
          (Toolbar) LayoutInflater.from(context).inflate(R.layout.mcab_toolbar, parent, false);
      parent.addView(toolbar);
    } else {
      throw new IllegalStateException(
          "MaterialCab was unable to attach to your Activity, attacher stub doesn't exist.");
    }

    if (toolbar != null) {
      if (title != null) {
        title(title);
      }
      titleColor(titleColor);
      if (popupTheme != 0) {
        toolbar.setPopupTheme(popupTheme);
      }
      if (menu != 0) {
        menu(menu);
      }
      if (closeDrawable != 0) {
        closeDrawableRes(closeDrawable);
      }
      backgroundColor(backgroundColor);
      contentInsetStart(contentInsetStart);
      toolbar.setNavigationOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
          });
      return callback == null || callback.onCabCreated(this, toolbar.getMenu());
    }
    return false;
  }

  @UiThread
  public void saveState(Bundle dest) {
    dest.putParcelable(BUNDLE_NAME, this);
  }

  @UiThread
  public static MaterialCab restoreState(
      Bundle source, AppCompatActivity context, Callback callback) {
    if (source == null || !source.containsKey(BUNDLE_NAME)) {
      return null;
    }
    MaterialCab cab = source.getParcelable(BUNDLE_NAME);
    if (cab != null) {
      cab.context = context;
      if (cab.isActive) {
        cab.start(callback);
      }
    }
    return cab;
  }

  //// PARCELABLE STUFF

  private MaterialCab(Parcel in) {
    attacherId = in.readInt();
    title = in.readString();
    titleColor = in.readInt();
    popupTheme = in.readInt();
    contentInsetStart = in.readInt();
    menu = in.readInt();
    backgroundColor = in.readInt();
    closeDrawable = in.readInt();
    isActive = in.readByte() != 0;
  }

  public static final Creator<MaterialCab> CREATOR = new Creator<MaterialCab>() {
    @Override
    public MaterialCab createFromParcel(Parcel in) {
      return new MaterialCab(in);
    }

    @Override
    public MaterialCab[] newArray(int size) {
      return new MaterialCab[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {

    parcel.writeInt(attacherId);
    parcel.writeString(title);
    parcel.writeInt(titleColor);
    parcel.writeInt(popupTheme);
    parcel.writeInt(contentInsetStart);
    parcel.writeInt(menu);
    parcel.writeInt(backgroundColor);
    parcel.writeInt(closeDrawable);
    parcel.writeByte((byte) (isActive ? 1 : 0));
  }
}
