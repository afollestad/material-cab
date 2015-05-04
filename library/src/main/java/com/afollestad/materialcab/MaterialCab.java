package com.afollestad.materialcab;

import android.graphics.Color;
import android.support.annotation.DimenRes;
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
    private transient Toolbar mAppBar;
    private final int mAttacherId;
    private Callback mCallback;

    private CharSequence mFutureTitle;
    private int mPopupTheme;
    private int mContentInsetStart;
    private int mMenu;
    private int mBackgroundColor;

    public MaterialCab(AppCompatActivity context, int attacherId) {
        mContext = context;
        mAttacherId = attacherId;
        reset();
    }

    public void setContext(AppCompatActivity context) {
        mContext = context;
    }

    public boolean isActive() {
        if (mContext == null) return false;
        View mcab = mContext.findViewById(R.id.mcab_appbar);
        return mcab != null && mcab.getVisibility() == View.VISIBLE;
    }

    public MaterialCab reset() {
        mFutureTitle = Util.resolveString(mContext, R.attr.mcab_title);
        mPopupTheme = Util.resolveResId(mContext, R.attr.mcab_popup_theme,
                R.style.ThemeOverlay_AppCompat_Light);
        mContentInsetStart = Util.resolveDimension(mContext, R.attr.mcab_contentinset_start,
                R.dimen.mcab_default_content_inset);
        mMenu = Util.resolveResId(mContext, R.attr.mcab_menu, 0);
        mBackgroundColor = Util.resolveColor(mContext, R.attr.mcab_background_color,
                Util.resolveColor(mContext, R.attr.colorPrimary, Color.GRAY));
        if (mAppBar != null && mAppBar.getMenu() != null)
            mAppBar.getMenu().clear();
        return this;
    }

    public MaterialCab start(Callback callback) {
        mCallback = callback;
        invalidateVisibility(attach());
        return this;
    }

    public MaterialCab setTitle(CharSequence title) {
        if (mAppBar == null) {
            mFutureTitle = title;
            return this;
        }
        mAppBar.setTitle(title);
        mFutureTitle = null;
        return this;
    }

    public MaterialCab setMenu(@MenuRes int menuRes) {
        mMenu = menuRes;
        if (mAppBar != null) {
            if (mAppBar.getMenu() != null)
                mAppBar.getMenu().clear();
            if (menuRes != 0)
                mAppBar.inflateMenu(menuRes);
            mAppBar.setOnMenuItemClickListener(this);
        }
        return this;
    }

    public MaterialCab setPopupMenuTheme(@StyleRes int themeRes) {
        mPopupTheme = themeRes;
        if (mAppBar != null)
            mAppBar.setPopupTheme(themeRes);
        return this;
    }

    public MaterialCab setContentInsetStart(int contentInset) {
        mContentInsetStart = contentInset;
        if (mAppBar != null)
            mAppBar.setContentInsetsRelative(contentInset, 0);
        return this;
    }

    public MaterialCab setContentInsetStartRes(@DimenRes int contentInsetRes) {
        return setContentInsetStart((int) mContext.getResources().getDimension(contentInsetRes));
    }

    public MaterialCab setBackgroundColor(int color) {
        mBackgroundColor = color;
        if (mAppBar != null)
            mAppBar.setBackgroundColor(color);
        return this;
    }

    public void finish() {
        invalidateVisibility(!(mCallback == null || mCallback.onCabFinished(this)));
    }

    private void invalidateVisibility(boolean active) {
        if (mAppBar == null) return;
        mAppBar.setVisibility(active ?
                View.VISIBLE : View.GONE);
    }

    private boolean attach() {
        final View attacher = mContext.findViewById(mAttacherId);
        if (mContext.findViewById(R.id.mcab_appbar) != null) {
            mAppBar = (Toolbar) mContext.findViewById(R.id.mcab_appbar);
        } else if (attacher instanceof ViewStub) {
            ViewStub stub = (ViewStub) attacher;
            stub.setLayoutResource(R.layout.mcab_appbar);
            stub.setInflatedId(R.id.mcab_appbar);
            mAppBar = (Toolbar) stub.inflate();
        } else if (attacher instanceof Toolbar) {
            mAppBar = (Toolbar) attacher;
        }

        if (mAppBar != null) {
            if (mFutureTitle != null)
                setTitle(mFutureTitle);
            if (mPopupTheme != 0)
                mAppBar.setPopupTheme(mPopupTheme);
            if (mMenu != 0)
                setMenu(mMenu);
            setBackgroundColor(mBackgroundColor);
            setContentInsetStart(mContentInsetStart);
            mAppBar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            mAppBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return mCallback == null || mCallback.onCabCreated(this, mAppBar.getMenu());
        }
        return false;
    }
}