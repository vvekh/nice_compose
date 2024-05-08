package com.example.diplomast.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.R;
import com.example.diplomast.SpecialistProfileActivity;

import java.io.Serializable;
import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder> {

    private List<Specialist> favoriteSpecialists;

    public FavouritesAdapter(List<Specialist> favoriteSpecialists){
        this.favoriteSpecialists = favoriteSpecialists;
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritesAdapter.FavouritesViewHolder holder, int position) {
        Specialist specialist = favoriteSpecialists.get(position);
        holder.SpName.setText(specialist.username + " " + specialist.usersurname);
        if (specialist.graduationid == 1) {
            holder.SpEducate1.setText("Полное высшее образование");
        } else if (specialist.graduationid == 2) {
            holder.SpEducate1.setText("Два полных высших образования");
        }

        holder.itemView.setOnClickListener(v -> {
            showSpecialist(v.getContext(), specialist);
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
        return favoriteSpecialists.size();
    }

    @NonNull
    @Override
    public FavouritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.specialist_liked_sample, parent, false);
        return new FavouritesViewHolder(view);
    }

    public static class FavouritesViewHolder extends RecyclerView.ViewHolder {
        TextView SpName;
        TextView SpEducate1;
        public FavouritesViewHolder(@NonNull View itemView) {
            super(itemView);
            SpName = itemView.findViewById(R.id.sp_name);
            SpEducate1 = itemView.findViewById(R.id.sp_educate1);
        }
    }
}
