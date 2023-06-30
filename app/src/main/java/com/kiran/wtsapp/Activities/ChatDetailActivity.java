package com.kiran.wtsapp.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kiran.wtsapp.Adapters.chatsAdapter;
import com.kiran.wtsapp.ModelClasses.MessageModel;
import com.kiran.wtsapp.R;
import com.kiran.wtsapp.databinding.ActivityChatDetailBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String profilePic = getIntent().getStringExtra("profilePic");
        String userName = getIntent().getStringExtra("userName");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.place).into(binding.profileImage);

        binding.back.setOnClickListener(view -> {
            finish();
        });


        final ArrayList<MessageModel> messageModels = new ArrayList<>();

        final chatsAdapter chatsAdapter = new chatsAdapter(messageModels, this);
        binding.chatRecycler.setAdapter(chatsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatRecycler.setLayoutManager(layoutManager);


        final String senderRoom = senderId + receiverId;
        final String receiverRoom = receiverId + senderId;

        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel model = snapshot1.getValue(MessageModel.class);
                    messageModels.add(model);
                }
                chatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatDetailActivity.this, "Failed to load chat.", Toast.LENGTH_SHORT).show();
            }
        });


        binding.send.setOnClickListener(view -> {
            String message = binding.etMessage.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "Enter your text here", Toast.LENGTH_SHORT).show();
                return;
            }
            final MessageModel model = new MessageModel(senderId, message);
            model.setTimestamp(new Date().getTime());
            binding.etMessage.setText("");


//            database.getReference().child("chats")
//                    .child(senderRoom)
//                    .push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            database.getReference().child("chats")
//                                    .child(receiverRoom)
//                                    .push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//
//                                        }
//                                    });
//                        }
//                    });
//
//
//        });

            database.getReference().child("chats")
                    .child(senderRoom)
                    .push().setValue(model)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .push().setValue(model)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(ChatDetailActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(ChatDetailActivity.this, "Failed to send message.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    }
