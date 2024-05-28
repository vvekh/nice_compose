package com.example.diplomast.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.R;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;
import com.example.diplomast.SpecialistProfileActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SpecialistAdapter extends RecyclerView.Adapter<SpecialistAdapter.SpecialistViewHolder> {
    private static final int TYPE_FAVORITE = 0;
    private static final int TYPE_NORMAL = 1;

    private List<Specialist> specialists;
    private List<Specialist> favoriteSpecialists;
    private Client client;
    private APIinterface api;

    public SpecialistAdapter(List<Specialist> specialists, List<Specialist> favoriteSpecialists, Client client) {
        this.specialists = specialists;
        this.favoriteSpecialists = favoriteSpecialists;
        this.client = client;
        this.api = APIclient.start().create(APIinterface.class);
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

        holder.itemView.setOnClickListener(v -> {
            showSpecialist(v.getContext(), specialist);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (favoriteSpecialists == null || favoriteSpecialists.size() < 1){
                favoriteSpecialists = new ArrayList<>();
                addFavoriteSpecialist(client.id, specialist.id);
                favoriteSpecialists.add(specialist);
                notifyDataSetChanged();
            }else if(!containsSpecialistById(favoriteSpecialists, specialist.id)) {
                addFavoriteSpecialist(client.id, specialist.id);
                favoriteSpecialists.add(specialist);
                notifyDataSetChanged();
            } else {
                removeFavoriteSpecialist(client.id, specialist.id);
                favoriteSpecialists.removeIf(s -> s.id == specialist.id);
                notifyDataSetChanged();
            }
            return true;
        });
    }

    private void addFavoriteSpecialist(int clientId, int specialistId) {
        Call<ResponseBody> call = api.addFavoriteSpecialist(clientId, specialistId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("SUCCESS", "Специалист добавлен в избранное");
                } else {
                    Log.d("FAIL", "Ошибка при добавлении специалиста в избранное");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("FAIL", t.getMessage());
            }
        });
    }

    private void removeFavoriteSpecialist(int clientId, int specialistId) {
        Call<ResponseBody> call = api.removeFavoriteSpecialist(clientId, specialistId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("SUCCESS", "Специалист удален из избранного");
                } else {
                    Log.d("FAIL", "Ошибка при удалении специалиста из избранного");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("FAIL", t.getMessage());
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
    public int getItemCount() {return specialists.size();}

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
