package com.example.muhasib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muhasib.models.UserKind;

import java.util.List;

public class AddUserActivity extends AppCompatActivity {
    private AppDatabase db;
    private UserAdapterRecyclerView userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        db = AppDatabase.getDatabase(getApplicationContext());

        EditText etUserName = findViewById(R.id.et_user_name);
        Button btnSaveUser = findViewById(R.id.btn_save_user);
        Button btnBackUser = findViewById(R.id.btn_back_user);
        RecyclerView recyclerView = findViewById(R.id.users_list_id);

        // Haal de gebruikersgegevens op als LiveData
        LiveData<List<UserKind>> usersLiveData = db.userKindDao().getAllUsers();

        // Initialiseer de RecyclerView en UserAdapterRecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapterRecyclerView(usersLiveData, this);
        recyclerView.setAdapter(userAdapter);

        // Observeren van de LiveData om de RecyclerView bij te werken
        usersLiveData.observe(this, new Observer<List<UserKind>>() {
            @Override
            public void onChanged(List<UserKind> userKinds) {
                userAdapter.notifyDataSetChanged(); // Update de RecyclerView
            }
        });

        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString();
                if (!userName.isEmpty()) {
                    // Voeg nieuwe gebruiker toe aan de database
                    UserKind user = new UserKind(userName);
                    db.userKindDao().insert(user);
                    Toast.makeText(AddUserActivity.this, "Gebruiker opgeslagen", Toast.LENGTH_SHORT).show();
                    etUserName.setText(""); // Wis het invoerveld
                } else {
                    Toast.makeText(AddUserActivity.this, "Vul een gebruikersnaam in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBackUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Terug naar MainActivity
                Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Sluit de huidige activiteit
            }
        });
    }
}
