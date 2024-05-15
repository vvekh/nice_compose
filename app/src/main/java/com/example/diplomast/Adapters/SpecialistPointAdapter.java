package com.example.diplomast.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomast.DTO.Point;
import com.example.diplomast.R;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialistPointAdapter extends RecyclerView.Adapter<SpecialistPointAdapter.PointViewHolder>{

    private static final int TYPE_SELECTED = 0;
    private static final int TYPE_NORMAL = 1;

    private List<Point> points;
    private List<Point> specialistPoints;
    private int specialistId;
    private APIinterface api;

    public SpecialistPointAdapter(List<Point> points, List<Point> specialistPoints, int specialistId){
        this.points = points;
        this.specialistPoints = specialistPoints;
        this.specialistId = specialistId;
        this.api = APIclient.start().create(APIinterface.class);
    }

    @Override
    public int getItemViewType(int position) {
        Point point = points.get(position);
        int pointId = point.id;
        if(containsPointById(specialistPoints, pointId)){
            return TYPE_SELECTED;
        }else {
            return TYPE_NORMAL;
        }
    }

    private boolean containsPointById(List<Point> points, int id){
        if(points != null){
            for(Point point : points){
                if(point.id == id){
                    return true;
                }
            }
        }
        return false;
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SELECTED){
            View view = inflater.inflate(R.layout.point_sample, parent, false);
            return new PointViewHolder(view);
        }else {
            View view = inflater.inflate(R.layout.point_sample_nonselected, parent, false);
            return new PointViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder holder, int position) {
        Point point = points.get(position);
        holder.PointName.setText(point.pointname);

        holder.itemView.setOnClickListener(v -> {
            int pointId = point.id;
            List<Integer> list = new ArrayList<>();
            list.add(pointId);

            if(containsPointById(specialistPoints, pointId)){
                Call<Void> callDelete = api.deleteCriteriaFromSpecialist(specialistId, list);
                callDelete.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("SUCCESS", response.message());
                            specialistPoints.removeIf(p -> p.id == pointId);
                            notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {Log.d("FAIL", t.getMessage());}
                });
            }else {
                // Добавление критерия
                Call<Void> callAdd = api.addCriteriaToSpecialist(specialistId, list);
                callAdd.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d("SUCCESS", response.message());
                            specialistPoints.add(point);
                            notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {Log.d("FAIL", t.getMessage());}
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return points.size();
    }

    public class PointViewHolder extends RecyclerView.ViewHolder {
        TextView PointName;

        public PointViewHolder(@NonNull View itemView) {
            super(itemView);
            PointName = itemView.findViewById(R.id.point_name_text_view);
        }
    }
}
