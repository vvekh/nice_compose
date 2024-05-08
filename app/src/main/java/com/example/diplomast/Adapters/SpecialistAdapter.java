package com.example.diplomast.Adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
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
import com.example.diplomast.SpecialistEditActivity;
import com.example.diplomast.SpecialistProfileActivity;

import java.io.Serializable;
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
        int specialistId = specialist.id;
        if (containsSpecialistById(favoriteSpecialists, specialistId)) {
            return TYPE_FAVORITE;
        } else {
            return TYPE_NORMAL;
        }
    }

    private boolean containsSpecialistById(List<Specialist> specialists, int id) {
        if (specialists != null){
            for (Specialist specialist : specialists) {
                if (specialist.id == id) {
                    return true;
                }
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Обработка клика на элементе списка
                // Открываем профиль специалиста
                showSpecialist(v.getContext(), specialist);
            }
        });
    }

    private void showSpecialist(Context context, Specialist specialist){
        String Enable = "false";
        Intent intent = new Intent(context, SpecialistProfileActivity.class);
        intent.putExtra("ActiveSpecialist", (Serializable) specialist);
        intent.putExtra("Enable", Enable);
        context.startActivity(intent);
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
