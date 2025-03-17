package com.example.mindease;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Admin_Minimal_Takers_ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_minimal_takers_result);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<User> userList = new ArrayList<>();
        userList.add(new User("User1", "1-Feb-25"));
        userList.add(new User("User2", "2-Feb-25"));
        userList.add(new User("User3", "3-Feb-25"));
        userList.add(new User("User4", "4-Feb-25"));
        userList.add(new User("User5", "5-Feb-25"));
        userList.add(new User("User6", "6-Feb-25"));
        userList.add(new User("User7", "7-Feb-25"));
        userList.add(new User("User8", "8-Feb-25"));

        UserAdapter adapter = new UserAdapter(userList);
        recyclerView.setAdapter(adapter);
    }
}