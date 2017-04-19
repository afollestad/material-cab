package com.afollestad.materialcabsample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;

import java.lang.reflect.Field;

/** @author Aidan Follestad (afollestad) */
public class MainActivity extends AppCompatActivity
    implements MainAdapter.Callback, MaterialCab.Callback {

  private MainAdapter adapter;
  private MaterialCab cab;
  private Toast toast;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    adapter = new MainAdapter(this);
    RecyclerView list = (RecyclerView) findViewById(R.id.list);
    list.setLayoutManager(new LinearLayoutManager(this));
    list.setAdapter(adapter);

    setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

    if (savedInstanceState != null) {
      cab = MaterialCab.restoreState(savedInstanceState, this, this);
      adapter.restoreState(savedInstanceState);
    } else {
      for (int i = 0; i <= 80; i++) {
        adapter.add("Item " + i);
      }
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (cab != null) {
      cab.saveState(outState);
    }
    if (adapter != null) {
      adapter.saveState(outState);
    }
  }

  private void showToast(String text) {
    if (toast != null) {
      toast.cancel();
    }
    toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
    toast.show();
  }

  @Override
  public void onItemClicked(int index, boolean longClick) {
    if (longClick || (cab != null && cab.isActive())) {
      onIconClicked(index);
      return;
    }
    showToast(adapter.getItem(index));
  }

  @Override
  public void onIconClicked(int index) {
    adapter.toggleSelected(index);
    if (adapter.getSelectedCount() == 0) {
      cab.finish();
      return;
    }
    if (cab == null) {
      cab = new MaterialCab(this, R.id.cab_stub).start(this);
    } else if (!cab.isActive()) {
      cab.reset().start(this);
    }
    cab.title(getString(R.string.x_selected, adapter.getSelectedCount()));
  }

  @Override
  public boolean onCabCreated(@NonNull MaterialCab cab, Menu menu) {
    // Makes the icons in the overflow menu visible
    if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
      try {
        Field field = menu.getClass().getDeclaredField("mOptionalIconsVisible");
        field.setAccessible(true);
        field.setBoolean(menu, true);
      } catch (Exception ignored) {
        ignored.printStackTrace();
      }
    }
    return true; // allow creation
  }

  @Override
  public boolean onCabItemClicked(@NonNull MenuItem item) {
    showToast((String) item.getTitle());
    return true;
  }

  @Override
  public boolean onCabFinished(@NonNull MaterialCab cab) {
    adapter.clearSelected();
    return true; // allow destruction
  }

  @Override
  public void onBackPressed() {
    if (cab != null && cab.isActive()) {
      cab.finish();
      cab = null;
    } else {
      super.onBackPressed();
    }
  }
}
