package com.example.diplomast.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomast.DTO.Point;
import com.example.diplomast.R;

import java.util.List;

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.PointViewHolder> {
    private List<Point> points;

    public PointAdapter(List<Point> points) {
        this.points = points;
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.point_sample, parent, false);
        return new PointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder holder, int position) {
        Point point = points.get(position);
        holder.pointNameTextView.setText(point.pointname);
    }

    @Override
    public int getItemCount() {
        return points.size();
    }

    public static class PointViewHolder extends RecyclerView.ViewHolder {
        TextView pointNameTextView;

        public PointViewHolder(@NonNull View itemView) {
            super(itemView);
            pointNameTextView = itemView.findViewById(R.id.point_name_text_view);
        }
    }
}