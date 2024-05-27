package com.example.campuschat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private Context context;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    List<MessageModel> MessageModelList;
    public MessagesAdapter(Context context) {
        this.context = context;
        this.MessageModelList = new ArrayList<>();
    }
    public void add(MessageModel MessageModel) {
        MessageModelList.add(MessageModel);
        notifyDataSetChanged();
    }
    public  void clear() {
        MessageModelList.clear();
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        if (viewType ==VIEW_TYPE_SENT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_sent, parent, false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_receieved, parent, false);
            return new ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        MessageModel messageModel=MessageModelList.get(position);
        if (messageModel.getSenderId().equals(FirebaseAuth.getInstance().getUid())){
            holder.textViewSentMessage.setText(messageModel.getMessage());
        }else {
            holder.textViewReceivedMessage.setText(messageModel.getMessage());

        }



    }

    @Override
    public int getItemViewType(int position) {
        if (MessageModelList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getUid())){
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }

    }

    @Override
    public int getItemCount() {
        return MessageModelList.size();
    }
    public  List<MessageModel> getMessageModelList() {
        return MessageModelList;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSentMessage,textViewReceivedMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSentMessage=itemView.findViewById(R.id.textviewSentMessage);
            textViewReceivedMessage=itemView.findViewById(R.id.textviewReceivedMessage);
        }
    }
}
