package com.aicodix.rattlegram;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private List<Entry> entries = new ArrayList<>();

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        return new EntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Entry entry = entries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEntries(List<Entry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;
        private final TextView timestampTextView;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgDisplayPicture);
            textView = itemView.findViewById(R.id.txtDisplayText);
            timestampTextView = itemView.findViewById(R.id.txtDisplayTime);
        }

        public void bind(Entry entry) {
            if (entry.getImageBitmap() != null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(entry.getImageBitmap());
            } else {
                imageView.setVisibility(View.GONE);
            }
            if (entry.getText() != null) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(entry.getText());
            } else {
                textView.setVisibility(View.GONE);
            }
            timestampTextView.setText(entry.getTimestamp());
        }
    }
}