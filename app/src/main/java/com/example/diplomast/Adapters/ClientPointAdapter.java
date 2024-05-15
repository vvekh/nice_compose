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

public class ClientPointAdapter extends RecyclerView.Adapter<ClientPointAdapter.PointViewHolder> {
    private static final int NORMAL_VIEW_TYPE = 0;
    private static final int SELECTED_VIEW_TYPE = 1;

    private List<Point> points;
    private List<Integer> selectedIds;

    public ClientPointAdapter(List<Point> points, List<Integer> selectedIds) {
        this.points = points;
        this.selectedIds = selectedIds;
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SELECTED_VIEW_TYPE) {
            View view = inflater.inflate(R.layout.point_sample, parent, false);
            return new PointViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.point_sample_nonselected, parent, false);
            return new PointViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder holder, int position) {
        Point point = points.get(position);
        holder.bind(point);

        holder.itemView.setOnClickListener(v -> {
            int pointId = point.id;
            if (selectedIds.contains(pointId)) {
                selectedIds.remove(Integer.valueOf(pointId));
            } else {
                selectedIds.add(pointId);
            }
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemViewType(int position) {
        int pointId = points.get(position).id;
        return selectedIds.contains(pointId) ? SELECTED_VIEW_TYPE : NORMAL_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return points.size();
    }

    public class PointViewHolder extends RecyclerView.ViewHolder {
        private TextView pointNameTextView;

        public PointViewHolder(@NonNull View itemView) {
            super(itemView);
            pointNameTextView = itemView.findViewById(R.id.point_name_text_view);
        }

        public void bind(Point point) {
            pointNameTextView.setText(point.pointname);
        }
    }
}