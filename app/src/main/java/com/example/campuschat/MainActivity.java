package com.example.campuschat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    String yourName;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        yourName = getIntent().getStringExtra("name");
        setupToolbar();
        setupRecyclerView();
        setupFirebase();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(yourName);
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler);
        userAdapter = new UserAdapter(this);
        recyclerView.setAdapter(userAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAdapter.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "DataSnapshot: " + dataSnapshot.toString());
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        Log.d(TAG, "UserModel: " + userModel.getUserName() + ", " + userModel.getUserEmail());
                        if (!userModel.getUserId().equals(FirebaseAuth.getInstance().getUid())) {
                            userAdapter.add(userModel);
                        }
                    } else {
                        Log.d(TAG, "UserModel is null for DataSnapshot: " + dataSnapshot.toString());
                    }
                }
                List<UserModel> userModelList = userAdapter.getUserModelList();
                Log.d(TAG, "Total users: " + userModelList.size());
                for (UserModel userModel : userModelList) {
                    Log.d(TAG, "User: " + userModel.getUserName() + ", " + userModel.getUserEmail());
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "DatabaseError: " + error.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, SigninActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
