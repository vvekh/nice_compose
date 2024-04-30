package com.example.diplomast.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.diplomast.DTO.Client;
import com.example.diplomast.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SpecialistAdapter extends RecyclerView.Adapter<SpecialistAdapter.SpecialistViewHolder> {
    private static final int TYPE_FAVORITE = 0;
    private static final int TYPE_NORMAL = 1;

    private List<Specialist> specialists;
    private List<Specialist> favoriteSpecialists; // Список избранных специалистов

    public SpecialistAdapter(List<Specialist> specialists, List<Specialist> favoriteSpecialists) {
        this.specialists = specialists;
        this.favoriteSpecialists = favoriteSpecialists;
    }

    @Override
    public int getItemViewType(int position) {
        Specialist specialist = specialists.get(position);
        int specialistId = specialist.id; // Предполагается, что у Specialist есть метод getId()
        if (containsSpecialistById(favoriteSpecialists, specialistId)) {
            return TYPE_FAVORITE;
        } else {
            return TYPE_NORMAL;
        }
    }

    private boolean containsSpecialistById(List<Specialist> specialists, int id) {
        for (Specialist specialist : specialists) {
            if (specialist.id == id) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public SpecialistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_FAVORITE) {
            View view = inflater.inflate(R.layout.specialist_liked_sample, parent, false);
            return new SpecialistViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.specialist_sample, parent, false);
            return new SpecialistViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialistViewHolder holder, int position) {
        Specialist specialist = specialists.get(position);
        holder.SpName.setText(specialist.username + " " + specialist.usersurname);
        if (specialist.graduationid == 1) {
            holder.SpEducate1.setText("Полное высшее образование");
        } else if (specialist.graduationid == 2) {
            holder.SpEducate1.setText("Два полных высших образования");
        }
    }

    @Override
    public int getItemCount() {
        return specialists.size();
    }

    public static class SpecialistViewHolder extends RecyclerView.ViewHolder {
        TextView SpName;
        TextView SpEducate1;

        public SpecialistViewHolder(@NonNull View itemView) {
            super(itemView);
            SpName = itemView.findViewById(R.id.sp_name);
            SpEducate1 = itemView.findViewById(R.id.sp_educate1);
        }
    }
}
