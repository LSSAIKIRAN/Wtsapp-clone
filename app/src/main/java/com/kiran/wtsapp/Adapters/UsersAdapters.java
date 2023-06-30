package com.kiran.wtsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kiran.wtsapp.Activities.ChatDetailActivity;
import com.kiran.wtsapp.ModelClasses.Users;
import com.kiran.wtsapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapters extends RecyclerView.Adapter<UsersAdapters.viewHolder>{

    ArrayList<Users> list;
    Context context;

    public UsersAdapters(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Users users = list.get(position);
        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.place).into(holder.image);
        holder.userName.setText(users.getUserName());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatDetailActivity.class);
            intent.putExtra("userId",users.getUserId());
            intent.putExtra("profilePic",users.getProfilePic());
            intent.putExtra("userName",users.getUserName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView userName, lastMessage;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.userNameList);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
