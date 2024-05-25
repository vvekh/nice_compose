package com.example.diplomast.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.DTO.Work;
import com.example.diplomast.R;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpWorkAdapter extends RecyclerView.Adapter<SpWorkAdapter.SpWorkViewHolder> {
    private List<Specialist> specialists;
    private List<Work> works;
    private Client client;
    private APIinterface api;

    public SpWorkAdapter(List<Specialist> specialists, List<Work> works, Client client){
        this.specialists = specialists;
        this.works = works;
        this.client = client;
        this.api = APIclient.start().create(APIinterface.class);

        this.specialists = specialists.stream()
                .filter(specialist -> getWorkBySpecialistId(works, specialist.id) != null)
                .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public SpWorkAdapter.SpWorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sp_work_sample, parent, false);
        return new SpWorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpWorkAdapter.SpWorkViewHolder holder, int position) {
        Specialist currentSpecialist = specialists.get(position);

        holder.SpName.setText(currentSpecialist.usersurname + " " + currentSpecialist.username);
        if (currentSpecialist.graduationid == 1) {
            holder.SpEducate1.setText("Полное высшее образование");
        } else if (currentSpecialist.graduationid == 2) {
            holder.SpEducate1.setText("Два полных высших образования");
        }
        holder.SpEmail.setText("Email: " + currentSpecialist.email);
        holder.SpPhone.setText("Номер телефона: " + currentSpecialist.phone);

        // Проверяем, есть ли специалист в списке работ
        Work currentWork = getWorkBySpecialistId(works, currentSpecialist.id);
        if (currentWork != null) {
            if (currentWork.status.equals("0")) {
                holder.SpEmail.setVisibility(View.GONE);
                holder.SpPhone.setVisibility(View.GONE);
                holder.ClBtn.setVisibility(View.GONE);
                holder.StatusText.setText("Вскоре вы сможете связаться со специалистом!");
            } else if (currentWork.status.equals("1")) {
                holder.SpEmail.setVisibility(View.VISIBLE);
                holder.SpPhone.setVisibility(View.VISIBLE);
                holder.StatusText.setText("Удачной работы!");
                holder.ClBtn.setText("Завершить работу");
                //КНОПКА О ЗАВЕРШЕНИИ РАБОТЫ СО СТОРОНЫ КЛИЕНТА
            } else if (currentWork.status.equals("2")){
                holder.SpEmail.setVisibility(View.GONE);
                holder.SpPhone.setVisibility(View.GONE);
                holder.ClBtn.setVisibility(View.GONE);
                holder.StatusText.setText("Специалист заврешил вашу работу.");
            } else if (currentWork.status.equals("3")){
                holder.SpEmail.setVisibility(View.GONE);
                holder.SpPhone.setVisibility(View.GONE);
                holder.StatusText.setText("Возобновите работу в любой момент!");
                holder.ClBtn.setText("Возобновить работу");
                //КНОПКА О ВОЗОБНОВЛЕНИИ РАБОТЫ СО СТОРОНЫ КЛИЕНТА
            }
        }
        holder.ClBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (currentWork.status.equals("1")){
                    //Завершение по инициативе клиента
                    Call<Void> call = api.endWorkCl(client.id, currentSpecialist.id);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(view.getContext(), "Успешно изменено!", Toast.LENGTH_SHORT).show();
                            currentWork.status = "3";
                            notifyDataSetChanged();
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {Log.e("FAIL", t.getMessage());}
                    });
                }else if (currentWork.status.equals("3")){
                    //Возобновление по инициативе клиента
                    Call<Void> call = api.startWorkCl(client.id, currentSpecialist.id);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(view.getContext(), "Успешно изменено!", Toast.LENGTH_SHORT).show();
                            currentWork.status = "1";
                            notifyDataSetChanged();
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {Log.e("FAIL", t.getMessage());}
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {return specialists.size();}

    private Work getWorkBySpecialistId(List<Work> works, int specialistId) {
        if (works != null) {
            for (Work work : works) {
                if (work.specialistid == specialistId) {
                    return work;
                }
            }
        }
        return null;
    }

    public static class SpWorkViewHolder extends RecyclerView.ViewHolder {
        TextView SpName, SpEducate1, SpEmail, SpPhone, StatusText;
        Button ClBtn;
        public SpWorkViewHolder(@NonNull View itemView){
            super(itemView);
            SpName = itemView.findViewById(R.id.sp_name);
            SpEducate1 = itemView.findViewById(R.id.sp_educate1);
            SpEmail = itemView.findViewById(R.id.sp_email);
            SpPhone = itemView.findViewById(R.id.sp_phone);
            StatusText = itemView.findViewById(R.id.status_text);
            ClBtn = itemView.findViewById(R.id.cl_btn);
        }
    }
}
