package com.example.top10downloader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private List<FeedEntry> applications;
    private final LayoutInflater layoutInflater;
    private final int layoutResource;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.applications = applications;
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        TextView tvName = convertView.findViewById(R.id.tvName);
//        TextView tvArtist = convertView.findViewById(R.id.tvArtist);
//        TextView tvSummary = convertView.findViewById(R.id.tvSummary);
        FeedEntry entry = applications.get(position);
        viewHolder.tvName.setText(entry.getName());
        viewHolder.tvArtist.setText(entry.getArtist());
        viewHolder.tvSummary.setText(entry.getSummary());
        return convertView;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    private class ViewHolder {
        TextView tvName;
        TextView tvArtist;
        TextView tvSummary;

        ViewHolder(View v) {
            tvName = v.findViewById(R.id.tvName);
            tvArtist = v.findViewById(R.id.tvArtist);
            tvSummary = v.findViewById(R.id.tvSummary);
        }
    }
}
