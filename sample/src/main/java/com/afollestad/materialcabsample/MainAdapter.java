package com.afollestad.materialcabsample;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    public interface Callback {
        void onItemClicked(int index, boolean longClick);

        void onIconClicked(int index);
    }

    private ArrayList<String> mItems;
    private ArrayList<Integer> mSelected;
    private Callback mCallback;

    public MainAdapter(Callback callback) {
        mCallback = callback;
        mItems = new ArrayList<>();
        mSelected = new ArrayList<>();
    }

    public void add(String item) {
        mItems.add(item);
        notifyItemInserted(mItems.size() - 1);
    }

    public void toggleSelected(int index) {
        final boolean newState = !mSelected.contains(index);
        if (newState)
            mSelected.add(index);
        else
            mSelected.remove((Integer) index);
        notifyItemChanged(index);
    }

    public void restoreState(Bundle in) {
        //noinspection unchecked
        mItems = (ArrayList<String>) in.getSerializable("[main_adapter_items]");
        //noinspection unchecked
        mSelected = (ArrayList<Integer>) in.getSerializable("[main_adapter_selected]");
        notifyDataSetChanged();
    }

    public void saveState(Bundle out) {
        out.putSerializable("[main_adapter_items]", mItems);
        out.putSerializable("[main_adapter_selected]", mSelected);
    }

    public void clearSelected() {
        mSelected.clear();
        notifyDataSetChanged();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_main, viewGroup, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder mainViewHolder, int i) {
        mainViewHolder.view.setActivated(mSelected.contains(i));
        mainViewHolder.view.setTag("item:" + i);
        mainViewHolder.view.setOnClickListener(this);
        mainViewHolder.view.setOnLongClickListener(this);

        mainViewHolder.icon.setTag("icon:" + i);
        mainViewHolder.icon.setOnClickListener(this);

        mainViewHolder.title.setText(mItems.get(i));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public int getSelectedCount() {
        return mSelected.size();
    }

    public String getItem(int index) {
        return mItems.get(index);
    }

    @Override
    public void onClick(View v) {
        String[] tag = ((String) v.getTag()).split(":");
        int index = Integer.parseInt(tag[1]);
        if (tag[0].equals("icon")) {
            if (mCallback != null)
                mCallback.onIconClicked(index);
        } else if (mCallback != null) {
            mCallback.onItemClicked(index, false);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        String[] tag = ((String) v.getTag()).split(":");
        int index = Integer.parseInt(tag[1]);
        if (mCallback != null)
            mCallback.onItemClicked(index, true);
        return false;
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        final View view;
        final TextView title;
        final View icon;

        public MainViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
