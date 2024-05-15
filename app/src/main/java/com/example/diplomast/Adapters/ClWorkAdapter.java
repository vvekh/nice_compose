package com.example.diplomast.Adapters;

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

public class ClWorkAdapter extends RecyclerView.Adapter<ClWorkAdapter.ClWorkViewHolder> {
    private List<Client> clients;
    private List<Work> works;
    private Specialist specialist;
    private APIinterface api;

    public ClWorkAdapter(List<Client> clients, List<Work> works, Specialist specialist){
        this.clients = clients;
        this.works = works;
        this.specialist = specialist;
        this.api = APIclient.start().create(APIinterface.class);

        this.clients = clients.stream()
                .filter(client -> getWorkByClientId(works, client.id) != null)
                .collect(Collectors.toList());
    }

    private Work getWorkByClientId(List<Work> works, int clientId){
        if (works != null) {
            for (Work work : works) {
                if (work.clientid == clientId) {
                    return work;
                }
            }
        }
        return null;
    }

    @NonNull
    @Override
    public ClWorkAdapter.ClWorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cl_work_sample, parent, false);
        return new ClWorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClWorkAdapter.ClWorkViewHolder holder, int position) {
        Client currentClient = clients.get(position);

        holder.ClName.setText(currentClient.username);
        holder.ClPhone.setText("Номер телефона: " + currentClient.phone);

        Work currentWork = getWorkByClientId(works, currentClient.id);
        if (currentWork != null){
            if (currentWork.status.equals("0")) {
                holder.ClPhone.setVisibility(View.GONE);
                holder.StatusText.setText("С вами хотят начать работу!");
                holder.SpBtn.setText("Начать работу");
                //НОВАЯ ЗАЯВКА ОТ КЛИЕНТА, КНОПКА ОДОБРИТЬ
            } else if (currentWork.status.equals("1")) {
                holder.ClPhone.setVisibility(View.VISIBLE);
                holder.StatusText.setText("Удачной работы!");
                holder.SpBtn.setText("Завершить работу");
                //КНОПКА О ЗАВЕРШЕНИИ РАБОТЫ СО СТОРОНЫ ВРАЧА
            } else if (currentWork.status.equals("2")){
                holder.ClPhone.setVisibility(View.GONE);
                holder.StatusText.setText("Возобновите работу в любой момент!");
                holder.SpBtn.setText("Возобновить работу");
                //КНОПКА О ВОЗОБНОВЛЕНИИ СО СТОРОНЫ ВРАЧА
            } else if (currentWork.status.equals("3")){
                holder.ClPhone.setVisibility(View.GONE);
                holder.SpBtn.setVisibility(View.GONE);
                holder.StatusText.setText("Клиент завершил вашу работу.");
                //НЕАКТИВНО, ЗАВЕРШЕНО СО СТОРОНЫ КЛИЕНТА
            }
        }
        holder.SpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (currentWork.status.equals("1")){
                    //Завершение по инициативе специалиста
                    Call<Void> call = api.endWorkSp(specialist.id, currentClient.id);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Toast.makeText(view.getContext(), "Успешно изменено!", Toast.LENGTH_SHORT).show();
                            currentWork.status = "2";
                            notifyDataSetChanged();
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {Log.e("FAIL", t.getMessage());}
                    });
                }else if (currentWork.status.equals("2")){
                    //Возобновление по инициативе специалиста
                    Call<Void> call = api.startWorkSp(specialist.id, currentClient.id);
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
                }else if (currentWork.status.equals("0")){
                    //Возобновление по инициативе специалиста
                    Call<Void> call = api.startWorkSp(specialist.id, currentClient.id);
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
    public int getItemCount() {return clients.size();}

    public static class ClWorkViewHolder extends RecyclerView.ViewHolder {
        TextView ClName, ClPhone, StatusText;
        Button SpBtn;
        public ClWorkViewHolder(@NonNull View itemView){
          super(itemView);
          ClName = itemView.findViewById(R.id.cl_name);
          ClPhone = itemView.findViewById(R.id.cl_phone);
          StatusText = itemView.findViewById(R.id.status_text);
          SpBtn = itemView.findViewById(R.id.sp_btn);
        }
    }
}
