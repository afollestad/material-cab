package com.afollestad.materialcabsample;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/** @author Aidan Follestad (afollestad) */
class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder>
    implements View.OnClickListener, View.OnLongClickListener {

  interface Callback {
    void onItemClicked(int index, boolean longClick);

    void onIconClicked(int index);
  }

  private ArrayList<String> itemsList;
  private ArrayList<Integer> selectedList;
  private Callback callback;

  MainAdapter(Callback callback) {
    this.callback = callback;
    itemsList = new ArrayList<>();
    selectedList = new ArrayList<>();
  }

  void add(String item) {
    itemsList.add(item);
    notifyItemInserted(itemsList.size() - 1);
  }

  void toggleSelected(int index) {
    final boolean newState = !selectedList.contains(index);
    if (newState) {
      selectedList.add(index);
    } else {
      selectedList.remove((Integer) index);
    }
    notifyItemChanged(index);
  }

  @SuppressWarnings("unchecked")
  void restoreState(Bundle in) {
    itemsList = (ArrayList<String>) in.getSerializable("[main_adapter_items]");
    selectedList = (ArrayList<Integer>) in.getSerializable("[main_adapter_selected]");
    notifyDataSetChanged();
  }

  void saveState(Bundle out) {
    out.putSerializable("[main_adapter_items]", itemsList);
    out.putSerializable("[main_adapter_selected]", selectedList);
  }

  void clearSelected() {
    selectedList.clear();
    notifyDataSetChanged();
  }

  @Override
  public MainViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View view =
        LayoutInflater.from(viewGroup.getContext())
            .inflate(R.layout.listitem_main, viewGroup, false);
    return new MainViewHolder(view);
  }

  @Override
  public void onBindViewHolder(MainViewHolder mainViewHolder, int i) {
    mainViewHolder.view.setActivated(selectedList.contains(i));
    mainViewHolder.view.setTag("item:" + i);
    mainViewHolder.view.setOnClickListener(this);
    mainViewHolder.view.setOnLongClickListener(this);

    mainViewHolder.icon.setTag("icon:" + i);
    mainViewHolder.icon.setOnClickListener(this);

    mainViewHolder.title.setText(itemsList.get(i));
  }

  @Override
  public int getItemCount() {
    return itemsList.size();
  }

  int getSelectedCount() {
    return selectedList.size();
  }

  String getItem(int index) {
    return itemsList.get(index);
  }

  @Override
  public void onClick(View v) {
    String[] tag = ((String) v.getTag()).split(":");
    int index = Integer.parseInt(tag[1]);
    if (callback != null) {
      if (tag[0].equals("icon")) {
        callback.onIconClicked(index);
      } else {
        callback.onItemClicked(index, false);
      }
    }
  }

  @Override
  public boolean onLongClick(View v) {
    String[] tag = ((String) v.getTag()).split(":");
    int index = Integer.parseInt(tag[1]);
    if (callback != null) {
      callback.onItemClicked(index, true);
    }
    return false;
  }

  static class MainViewHolder extends RecyclerView.ViewHolder {

    final View view;
    final TextView title;
    final View icon;

    MainViewHolder(View itemView) {
      super(itemView);
      view = itemView;
      title = (TextView) itemView.findViewById(R.id.title);
      icon = itemView.findViewById(R.id.icon);
    }
  }
}
