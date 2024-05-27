package com.example.campuschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

   String receiverId,receiverName,receiverRoom,senderRoom;
   DatabaseReference dbReferenceSend,dbReferenceReceiver,userReference;
   ImageView sendButton;
   EditText messageText;
   RecyclerView recyclerView;
   MessagesAdapter messagesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userReference= FirebaseDatabase.getInstance().getReference("users");
        receiverId=getIntent().getStringExtra("id");
        receiverName=getIntent().getStringExtra("name");
        getSupportActionBar().setTitle(receiverName);
        if(receiverId!=null){
            senderRoom= FirebaseAuth.getInstance().getUid()+receiverId;
            receiverRoom=receiverId+FirebaseAuth.getInstance().getUid();
        }
        sendButton=findViewById(R.id.send_message_icon);
        messagesAdapter=new MessagesAdapter(this);
        recyclerView=findViewById(R.id.chatrecycler);
        messageText=findViewById(R.id.message_edit);
        recyclerView.setAdapter(messagesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbReferenceSend= FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        dbReferenceReceiver= FirebaseDatabase.getInstance().getReference("chats").child(receiverRoom);
        dbReferenceSend.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MessageModel> messages=new ArrayList<>();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MessageModel messageModel=dataSnapshot.getValue(MessageModel.class);
                    messages.add(messageModel);
                }
                messagesAdapter.clear();
                for(MessageModel message:messages){
                    messagesAdapter.add(message);
                }
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=messageText.getText().toString().trim();
                if(message.isEmpty()){
                    Toast.makeText(ChatActivity.this,"Please enter a message",Toast.LENGTH_SHORT).show();
                    return;

                }else{
                    sendMessage(message);
                }

            }
        });
    }
    private void sendMessage(String message){
        String messageId= UUID.randomUUID().toString();
        MessageModel messageModel=new MessageModel(messageId,FirebaseAuth.getInstance().getUid(),message);
        messagesAdapter.add(messageModel);
        dbReferenceSend.child(messageId).setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
            }
        });
        dbReferenceReceiver.child(messageId).setValue(messageModel);
        recyclerView.scrollToPosition(messagesAdapter.getItemCount()-1);
        messageText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ChatActivity.this,SigninActivity.class));
            finish();
            return true;
        }
        return false;
        }
}