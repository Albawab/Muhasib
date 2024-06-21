package com.example.muhasib;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muhasib.models.Amount;
import com.example.muhasib.models.UserKind;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private LiveData<List<Amount>> amountLiveData;
    private AmountAdapter amountAdapter;
    private boolean isPositiveChecked = true;
    private TextView totalAmountTextView;
    private Spinner userSpinner;
    private List<UserKind> userList;
    private List<Amount> allAmounts; // Houd alle bedragen bij voor alle gebruikers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getDatabase(getApplicationContext());

        totalAmountTextView = findViewById(R.id.total_amount_text_view);
        userSpinner = findViewById(R.id.user_spinner);

        Button showPopupButton = findViewById(R.id.main_add_id);
        showPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupForm();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.main_recycler_view_di);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Haal de LiveData op van de database en observeer deze in de MainActivity
        amountLiveData = db.amountDao().getAll(); // Ophalen van de amounts uit de database

        // Observer voor de LiveData om de RecyclerView bij te werken wanneer de data verandert
        amountLiveData.observe(this, new Observer<List<Amount>>() {
            @Override
            public void onChanged(List<Amount> amounts) {
                allAmounts = amounts; // Bewaar alle bedragen voor alle gebruikers
                updateRecyclerView(allAmounts); // Update de RecyclerView met alle bedragen
            }
        });

        // Initializeer de RecyclerView met de adapter
        amountAdapter = new AmountAdapter(this, new ArrayList<>(), new AmountAdapter.OnAmountClickListener() {
            @Override
            public void onAmountDeleteClick(Amount amount) {
                db.amountDao().delete(amount);
                Toast.makeText(MainActivity.this, "Bedrag verwijderd", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(amountAdapter);

        // Set up user spinner
        setUpUserSpinner();
    }

    private void showPopupForm() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.popup_form, null);

        EditText etAmount = alertLayout.findViewById(R.id.et_amount);
        Button btnAddUser = alertLayout.findViewById(R.id.btn_add_user);
        Button btnAddAmount = alertLayout.findViewById(R.id.btn_add_amount);
        Button btnClose = alertLayout.findViewById(R.id.btn_close);
        EditText etDescription = alertLayout.findViewById(R.id.et_description);
        CheckBox cbIsPositive = alertLayout.findViewById(R.id.cb_is_positive);

        // Set the current status of the checkbox
        cbIsPositive.setChecked(isPositiveChecked);

        // Set up the user spinner
        Spinner spinner = alertLayout.findViewById(R.id.spinner);
        ArrayAdapter<UserKind> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();

        setCloseButtonListener(btnClose, dialog);
        setAddUserButtonListener(btnAddUser);
        setAddAmountButtonListener(btnAddAmount, etAmount, etDescription, cbIsPositive, spinner, dialog);
    }

    private void setCloseButtonListener(Button btnClose, AlertDialog dialog) {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setAddUserButtonListener(Button btnAddUser) {
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setAddAmountButtonListener(Button btnAddAmount, EditText etAmount, EditText etDescription, CheckBox cbIsPositive, Spinner spinner, AlertDialog dialog) {
        btnAddAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = etAmount.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                boolean isPositive = cbIsPositive.isChecked();

                if (amountStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Voer een bedrag in", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Voer een geldig bedrag in", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserKind selectedUser = (UserKind) spinner.getSelectedItem();
                if (selectedUser == null || selectedUser.getId() == -1) {
                    Toast.makeText(MainActivity.this, "Selecteer een gebruiker", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (description.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Voer een beschrijving in", Toast.LENGTH_SHORT).show();
                    return;
                }

                Amount newAmount = new Amount(amount, description, new Date(), isPositive, selectedUser.getId());
                db.amountDao().insert(newAmount);

                Toast.makeText(MainActivity.this, "Bedrag succesvol toegevoegd", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void setUpUserSpinner() {
        // Maak een lege lijst aan voor de gebruikers
        userList = new ArrayList<>();

        // Voeg een "Alle gebruikers" optie toe als eerste item
        UserKind allUsersOption = new UserKind("Alle gebruikers");
        allUsersOption.setId(-1); // Bijvoorbeeld, geef een negatieve ID voor de "Alle gebruikers" optie
        userList.add(allUsersOption);

        // Haal daarna de echte gebruikers op uit de database
        db.userKindDao().getAllUsers().observe(this, new Observer<List<UserKind>>() {
            @Override
            public void onChanged(List<UserKind> users) {
                userList.addAll(users); // Voeg alle echte gebruikers toe na de "Alle gebruikers" optie

                ArrayAdapter<UserKind> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, userList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userSpinner.setAdapter(adapter);

                userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        UserKind selectedUser = (UserKind) parent.getSelectedItem();
                        if (selectedUser != null && selectedUser.getId() != -1) {
                            filterAmountsByUser(selectedUser.getId());
                        } else {
                            updateRecyclerView(allAmounts); // Toon alle bedragen als "Alle gebruikers" is geselecteerd
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });
            }
        });
    }

    private void filterAmountsByUser(int userId) {
        List<Amount> filteredAmounts = new ArrayList<>();
        for (Amount amount : allAmounts) {
            if (amount.getUserId() == userId) {
                filteredAmounts.add(amount);
            }
        }
        updateRecyclerView(filteredAmounts);
    }

    private void updateRecyclerView(List<Amount> amounts) {
        amountAdapter.updateAmounts(amounts); // Update de RecyclerView
        calculateAndDisplayTotalAmount(amounts); // Bereken en toon de totale hoeveelheid
    }

    private void calculateAndDisplayTotalAmount(List<Amount> amounts) {
        double totalAmount = 0;
        for (Amount amount : amounts) {
            if (amount.isPositive()) {
                totalAmount += amount.getAmount();
            } else {
                totalAmount -= amount.getAmount();
            }
        }

        // Pas de tekst en de kleur van de tekst aan op basis van het totaalbedrag
        totalAmountTextView.setText(String.format(Locale.getDefault(), "Total Amount: €%.2f", totalAmount));

        if (totalAmount >= 0) {
            totalAmountTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.green)); // Groene kleur
        } else {
            totalAmountTextView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red)); // Rode kleur
        }
    }
}
