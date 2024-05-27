package com.example.campuschat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG = "UserAdapter";
    private Context context;
    private List<UserModel> userModelList;

    public UserAdapter(Context context) {
        this.context = context;
        this.userModelList = new ArrayList<>();
    }

    public void add(UserModel userModel) {
        userModelList.add(userModel);
        Log.d(TAG, "User added: " + userModel.userName + ", " + userModel.getUserEmail());
        notifyDataSetChanged();
    }

    public void clear() {
        userModelList.clear();
        Log.d(TAG, "User list cleared");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        if (userModel != null) {
            holder.name.setText(userModel.userName);
            holder.email.setText(userModel.getUserEmail());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("name", userModel.userName);
                    intent.putExtra("id", userModel.getUserId());
                    context.startActivity(intent);
                }
            });
            Log.d(TAG, "User displayed: " + userModel.userName + ", " + userModel.getUserEmail());
        }
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.username);
            email = itemView.findViewById(R.id.useremail);
        }
    }
}
