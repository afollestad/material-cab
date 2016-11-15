package com.afollestad.materialcabsample;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;
import com.afollestad.materialcab.Util;

import java.lang.reflect.Field;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainActivity extends AppCompatActivity implements MainAdapter.Callback, MaterialCab.Callback {

    private MainAdapter mAdapter;
    private MaterialCab mCab;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new MainAdapter(this);
        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);

        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        if (savedInstanceState != null) {
            mCab = MaterialCab.restoreState(savedInstanceState, this, this);
            mAdapter.restoreState(savedInstanceState);
        } else {
            for (int i = 0; i <= 80; i++)
                mAdapter.add("Item " + i);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCab != null)
            mCab.saveState(outState);
        if (mAdapter != null)
            mAdapter.saveState(outState);
    }

    private void showToast(String text) {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onItemClicked(int index, boolean longClick) {
        if (longClick || (mCab != null && mCab.isActive())) {
            onIconClicked(index);
            return;
        }
        showToast(mAdapter.getItem(index));
    }

    @Override
    public void onIconClicked(int index) {
        mAdapter.toggleSelected(index);
        if (mAdapter.getSelectedCount() == 0) {
            mCab.finish();
            return;
        }
        if (mCab == null)
            mCab = new MaterialCab(this, R.id.cab_stub).start(this);
        else if (!mCab.isActive())
            mCab.reset().start(this);
        mCab.setTitle(getString(R.string.x_selected, mAdapter.getSelectedCount()));
    }

    @Override
    public boolean onCabCreated(MaterialCab cab, Menu menu) {
        // Makes the icons in the overflow menu visible
        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Field field = menu.getClass().
                        getDeclaredField("mOptionalIconsVisible");
                field.setAccessible(true);
                field.setBoolean(menu, true);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        cab.getToolbar().setAlpha(0.0F);
        cab.getToolbar().animate().alpha(1.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            getStatusBarAnimator(ContextCompat.getColor(this, R.color.grey_dark))
                    .start();
        }
        return true; // allow creation
    }

    @Override
    public boolean onCabItemClicked(MenuItem item) {
        showToast((String) item.getTitle());
        return true;
    }

    @Override
    public boolean onCabFinished(MaterialCab cab) {
        mAdapter.clearSelected();
        cab.getToolbar().animate().alpha(0.0f);
        if (Build.VERSION.SDK_INT >= 21) {
            getStatusBarAnimator(Util.resolveColor(this, R.attr.colorPrimaryDark, Color.TRANSPARENT))
                    .start();
        }
        return true; // allow destruction
    }

    @Override
    public void onBackPressed() {
        if (mCab != null && mCab.isActive()) {
            mCab.finish();
            mCab = null;
        } else {
            super.onBackPressed();
        }
    }

    @TargetApi(21)
    private Animator getStatusBarAnimator(int endColor) {
        return ObjectAnimator.ofObject(getWindow(), "statusBarColor", new ArgbEvaluator(),
                getWindow().getStatusBarColor(),
                endColor);
    }
}