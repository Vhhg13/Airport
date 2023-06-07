package com.vhhg.airport;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.username.setText(context.getResources().getString(R.string.FIO_format, user.getLastName(), user.getFirstName(), user.getThirdName()));
        holder.itemView.setOnClickListener(v -> {
            Log.d("SHOWROOTPROFILE", String.valueOf(user.getID()));
            context.startActivity(new Intent(context, ShowProfileActivity.class).putExtra("user", user.getID()));
        });
        if(user.getID() != 1) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(v -> {
                users.remove(position);
                notifyDataSetChanged();
                Server.get(context).deleteUser(user, res -> {
                });
            });
        }else{
            holder.delete.setOnClickListener(v -> {
                holder.delete.setVisibility(View.INVISIBLE);
            });
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    private LayoutInflater inflater;
    private Context context;
    private List<User> users;

    public UserListAdapter(Context context, List<User> users){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.users = users;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView username = itemView.findViewById(R.id.destpoint);
        Button delete = itemView.findViewById(R.id.fav);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
