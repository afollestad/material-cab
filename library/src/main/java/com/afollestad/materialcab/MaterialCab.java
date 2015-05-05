package com.afollestad.materialcab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import java.io.Serializable;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MaterialCab implements Serializable, Toolbar.OnMenuItemClickListener {

    public interface Callback {
        boolean onCabCreated(MaterialCab cab, Menu menu);

        boolean onCabItemClicked(MenuItem item);

        boolean onCabFinished(MaterialCab cab);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return mCallback != null && mCallback.onCabItemClicked(item);
    }

    private transient AppCompatActivity mContext;
    private transient Toolbar mToolbar;
    private transient Callback mCallback;

    private int mAttacherId;
    private CharSequence mTitle;
    private int mPopupTheme;
    private int mContentInsetStart;
    private int mMenu;
    private int mBackgroundColor;
    private int mCloseDrawable;
    private boolean mActive;

    public MaterialCab(AppCompatActivity context, int attacherId) {
        mContext = context;
        mAttacherId = attacherId;
        reset();
    }

    public void setContext(AppCompatActivity context) {
        mContext = context;
    }

    public boolean isActive() {
        return mActive;
    }

    public MaterialCab reset() {
        mTitle = Util.resolveString(mContext, R.attr.mcab_title);
        mPopupTheme = Util.resolveResId(mContext, R.attr.mcab_popup_theme,
                R.style.ThemeOverlay_AppCompat_Light);
        mContentInsetStart = Util.resolveDimension(mContext, R.attr.mcab_contentinset_start,
                R.dimen.mcab_default_content_inset);
        mMenu = Util.resolveResId(mContext, R.attr.mcab_menu, 0);
        mBackgroundColor = Util.resolveColor(mContext, R.attr.mcab_background_color,
                Util.resolveColor(mContext, R.attr.colorPrimary, Color.GRAY));
        mCloseDrawable = Util.resolveResId(mContext, R.attr.mcab_close_drawable,
                Util.resolveResId(mContext, R.attr.actionModeCloseDrawable,
                        R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        if (mToolbar != null && mToolbar.getMenu() != null)
            mToolbar.getMenu().clear();
        return this;
    }

    public MaterialCab start(Callback callback) {
        mCallback = callback;
        invalidateVisibility(attach());
        return this;
    }

    public MaterialCab setTitle(CharSequence title) {
        mTitle = title;
        if (mToolbar != null)
            mToolbar.setTitle(title);
        return this;
    }

    public MaterialCab setMenu(@MenuRes int menuRes) {
        mMenu = menuRes;
        if (mToolbar != null) {
            if (mToolbar.getMenu() != null)
                mToolbar.getMenu().clear();
            if (menuRes != 0)
                mToolbar.inflateMenu(menuRes);
            mToolbar.setOnMenuItemClickListener(this);
        }
        return this;
    }

    public MaterialCab setPopupMenuTheme(@StyleRes int themeRes) {
        mPopupTheme = themeRes;
        if (mToolbar != null)
            mToolbar.setPopupTheme(themeRes);
        return this;
    }

    public MaterialCab setContentInsetStart(int contentInset) {
        mContentInsetStart = contentInset;
        if (mToolbar != null)
            mToolbar.setContentInsetsRelative(contentInset, 0);
        return this;
    }

    public MaterialCab setContentInsetStartRes(@DimenRes int contentInsetRes) {
        return setContentInsetStart((int) mContext.getResources().getDimension(contentInsetRes));
    }

    public MaterialCab setBackgroundColor(int color) {
        mBackgroundColor = color;
        if (mToolbar != null)
            mToolbar.setBackgroundColor(color);
        return this;
    }

    public MaterialCab setCloseDrawable(@DrawableRes int closeDrawable) {
        mCloseDrawable = closeDrawable;
        if (mToolbar != null)
            mToolbar.setNavigationIcon(closeDrawable);
        return this;
    }

    public Menu getMenu() {
        return mToolbar != null ? mToolbar.getMenu() : null;
    }

    public void finish() {
        invalidateVisibility(!(mCallback == null || mCallback.onCabFinished(this)));
    }

    private void invalidateVisibility(boolean active) {
        if (mToolbar == null) return;
        mToolbar.setVisibility(active ?
                View.VISIBLE : View.GONE);
        mActive = active;
    }

    private boolean attach() {
        final View attacher = mContext.findViewById(mAttacherId);
        if (mContext.findViewById(R.id.mcab_toolbar) != null) {
            mToolbar = (Toolbar) mContext.findViewById(R.id.mcab_toolbar);
        } else if (attacher instanceof ViewStub) {
            ViewStub stub = (ViewStub) attacher;
            stub.setLayoutResource(R.layout.mcab_toolbar);
            stub.setInflatedId(R.id.mcab_toolbar);
            mToolbar = (Toolbar) stub.inflate();
        } else {
            throw new IllegalStateException("MaterialCab was unable to attach to your Activity, attacher stub doesn't exist.");
        }

        if (mToolbar != null) {
            if (mTitle != null)
                setTitle(mTitle);
            if (mPopupTheme != 0)
                mToolbar.setPopupTheme(mPopupTheme);
            if (mMenu != 0)
                setMenu(mMenu);
            if (mCloseDrawable != 0)
                setCloseDrawable(mCloseDrawable);
            setBackgroundColor(mBackgroundColor);
            setContentInsetStart(mContentInsetStart);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return mCallback == null || mCallback.onCabCreated(this, mToolbar.getMenu());
        }
        return false;
    }

    public void saveState(Bundle dest) {
        dest.putSerializable("[mcab_state]", this);
    }

    public static MaterialCab restoreState(Bundle source, AppCompatActivity context, Callback callback) {
        if (source == null || !source.containsKey("[mcab_state]"))
            return null;
        MaterialCab cab = (MaterialCab) source.getSerializable("[mcab_state]");
        cab.mContext = context;
        if (cab.mActive)
            cab.start(callback);
        return cab;
    }
}