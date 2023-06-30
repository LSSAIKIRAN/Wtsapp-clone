package com.kiran.wtsapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.kiran.wtsapp.ModelClasses.MessageModel;
import com.kiran.wtsapp.R;

import java.util.ArrayList;

public class chatsAdapter extends RecyclerView.Adapter {

    private ArrayList<MessageModel> messageModels;
    private Context context;

    private static final int SENDER_VIEW_TYPE = 1;
    private static final int RECEIVER_VIEW_TYPE = 2;

    public chatsAdapter(ArrayList<MessageModel> messageModels, Context context) {
        this.messageModels = messageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == SENDER_VIEW_TYPE) {
            view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);

        if (holder instanceof SenderViewHolder senderViewHolder) {
            senderViewHolder.senderMsg.setText(messageModel.getMessage());
        } else if (holder instanceof ReceiverViewHolder receiverViewHolder) {
            receiverViewHolder.receiverMsg.setText(messageModel.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    public static class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView senderMsg;
        TextView senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg = itemView.findViewById(R.id.senderText); // Make sure the ID matches the one in sample_sender.xml
            senderTime = itemView.findViewById(R.id.senderTime); // Make sure the ID matches the one in sample_sender.xml
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView receiverMsg;
        TextView receiverTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg = itemView.findViewById(R.id.receiverText); // Make sure the ID matches the one in sample_receiver.xml
            receiverTime = itemView.findViewById(R.id.receiverTime); // Make sure the ID matches the one in sample_receiver.xml
        }
    }
}