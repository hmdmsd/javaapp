package fr.imt_atlantique.messaoudtp2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private ActivityResultLauncher<Intent> dateActivityLauncher;
    private static final int REQUEST_PICK_DATE = 1;
    private List<String> phoneNumbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the dateActivityLauncher
        dateActivityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // Retrieve the selected date from DateActivity
                            String selectedDate = data.getStringExtra("selectedDate");

                            // Set the selected date in MainActivity
                            TextView birthdayInput = findViewById(R.id.inputBirthday);
                            birthdayInput.setText(selectedDate);
                        }
                    }
                });

        loadUserData();

        findViewById(R.id.inputBirthday).setOnClickListener((
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePicker();
                    }
                }
        ));
        final Button button = findViewById(R.id.validateBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                validateAction();
            }
        });

        // Add phone number button click listener
        Button addPhoneBtn = findViewById(R.id.addPhoneBtn);
        addPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoneNumber();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_wikipedia) {
            String city = ((Spinner) findViewById(R.id.inputCity)).getSelectedItem().toString();
            openWikipedia(city);
            return true;
        }
        if (id == R.id.action_share) {
            String city = ((Spinner) findViewById(R.id.inputCity)).getSelectedItem().toString();
            shareCity(city);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openWikipedia(String city) {
        String wikipediaUrl = "http://fr.wikipedia.org/?search=" + city;
        Uri wikipediaUri = Uri.parse(wikipediaUrl);
        Intent wikipediaIntent = new Intent(Intent.ACTION_VIEW, wikipediaUri);

        // Verify if there is an activity to handle this intent
        if (wikipediaIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(wikipediaIntent);
        } else {
            // Treat the case where there is no activity to handle this intent
            Snackbar.make(findViewById(R.id.myLinearLayout), "Aucune application disponible pour ouvrir Wikipedia", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void shareCity(String city) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ville de naissance : " + city);

        // Create an Intent Chooser that permit user to choose the share app
        Intent chooserIntent = Intent.createChooser(shareIntent, "Partager via");

        // Verify if there is an activity to handle this intent
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooserIntent);
        } else {
            // Treat the case where there is no activity to handle this intent
            Snackbar.make(findViewById(R.id.myLinearLayout), "Aucune application disponible pour partager", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void saveUserData(String name, String surname, String birthday, String city) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", name);
        editor.putString("Surname", surname);
        editor.putString("Birthday", birthday);
        editor.putString("City", city);
        // Save phone numbers
        Set<String> phoneSet = new HashSet<>(phoneNumbers);
        editor.putStringSet("PhoneNumbers", phoneSet);
        editor.apply();
    }
    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("Name", "");
        String surname = sharedPreferences.getString("Surname", "");
        String birthday = sharedPreferences.getString("Birthday", "");
        String city = sharedPreferences.getString("City", "");
        // Load phone numbers
        Set<String> phoneSet = sharedPreferences.getStringSet("PhoneNumbers", new HashSet<>());
        phoneNumbers.clear();
        phoneNumbers.addAll(phoneSet);

        EditText nameInput = findViewById(R.id.inputName);
        EditText surnameInput = findViewById(R.id.inputSurname);
        TextView birthdayInput = findViewById(R.id.inputBirthday);
        Spinner cityInput = findViewById(R.id.inputCity);

        nameInput.setText(name);
        surnameInput.setText(surname);
        birthdayInput.setText(birthday);
        // Set the selection for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.departments, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cityInput.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(city);
        cityInput.setSelection(spinnerPosition);

        // Display phone numbers
        displayPhoneNumbers();
    }

    private void displayPhoneNumbers() {
        for (String phoneNumber : phoneNumbers) {
            LinearLayout phoneNumbersLayout = findViewById(R.id.phoneNumbersLayout);
            // Create a LinearLayout to hold the phone number and delete button
            LinearLayout phoneLayout = new LinearLayout(this);
            phoneLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            phoneLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Create a TextView for the phone number
            TextView phoneNumberTextView = new TextView(this);
            phoneNumberTextView.setText(phoneNumber);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.weight = 1;
            phoneNumberTextView.setLayoutParams(params);
            phoneLayout.addView(phoneNumberTextView);

            // Create a Button for deleting the phone number
            Button deleteButton = new Button(this);
            deleteButton.setText("-");
            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            phoneLayout.addView(deleteButton);

            // Set click listener for the delete button to remove the phone number
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove the phone number from the list
                    phoneNumbers.remove(phoneNumber);

                    // Remove the LinearLayout containing the phone number and delete button from the layout
                    LinearLayout phoneNumbersLayout = findViewById(R.id.phoneNumbersLayout);
                    phoneNumbersLayout.removeView(phoneLayout);
                }
            });

            // Add the LinearLayout to the layout
            phoneNumbersLayout.addView(phoneLayout);
        }
    }


    public void validateAction() {
        EditText nameInput = findViewById(R.id.inputName);
        EditText surnameInput = findViewById(R.id.inputSurname);
        TextView birthdayInput = findViewById(R.id.inputBirthday);
        Spinner cityInput = findViewById(R.id.inputCity);

        String name = nameInput.getText().toString();
        String surname = surnameInput.getText().toString();
        String birthday = birthdayInput.getText().toString();
        String city = cityInput.getSelectedItem().toString();

        saveUserData(name, surname, birthday, city);

        // Create user object
        User user = new User(name, surname, birthday, city, phoneNumbers);

        // Pass user to DisplayActivity
        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);

        String textToShow = "Name: " + name + "\nSurname: " + surname + "\nBirthday: " + birthday + "\nCity: " + city;

        Snackbar.make(findViewById(R.id.myLinearLayout), textToShow, Snackbar.LENGTH_LONG).show();
        Log.i("Validate", textToShow);
    }


    //    public void showDatePicker(){
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                (Context) this,
//                (DatePickerDialog.OnDateSetListener) this,
//                Calendar.getInstance().get(Calendar.YEAR),
//                Calendar.getInstance().get(Calendar.MONTH),
//                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
//
//        );
//        datePickerDialog.show();
//    }
    public void showDatePicker(){
        Intent intent = new Intent(this, DateActivity.class);
        dateActivityLauncher.launch(intent);
    }
    @Override
    public void onDateSet(DatePicker view , int year , int month , int dayOfMonth){
        String date = dayOfMonth + "/" + month + "/" + year ;
        TextView birthdayInput = findViewById(R.id.inputBirthday);
        birthdayInput.setText(date);

    }

    public void resetAction(MenuItem item) {
        EditText nameInput = findViewById(R.id.inputName);
        EditText surnameInput = findViewById(R.id.inputSurname);
        TextView birthdayInput = findViewById(R.id.inputBirthday);
        Spinner cityInput = findViewById(R.id.inputCity);

        // Clearing EditText fields
        nameInput.setText("");
        surnameInput.setText("");

        // Setting birthday TextView to empty
        birthdayInput.setText(R.string.dateformat);

        // Resetting Spinner to the first item
        cityInput.setSelection(0);

        // Clear phone numbers list and views
        phoneNumbers.clear();
        displayPhoneNumbers();


        Snackbar.make(findViewById(R.id.myLinearLayout), "Fields Reset", Snackbar.LENGTH_SHORT).show();
    }

    // Method to add phone number
    private void addPhoneNumber() {
        EditText inputPhone = findViewById(R.id.inputPhone);
        String phoneNumber = inputPhone.getText().toString();

        // Add the phone number to the list
        phoneNumbers.add(phoneNumber);

        // Create a LinearLayout to hold the phone number and delete button
        LinearLayout phoneLayout = new LinearLayout(this);
        phoneLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        phoneLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Create a TextView for the phone number
        TextView phoneNumberTextView = new TextView(this);
        phoneNumberTextView.setText(phoneNumber);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.weight = 1;
        phoneNumberTextView.setLayoutParams(params);
        phoneLayout.addView(phoneNumberTextView);

        // Create a Button for deleting the phone number
        Button deleteButton = new Button(this);
        deleteButton.setText("-");
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        phoneLayout.addView(deleteButton);

        // Set click listener for the delete button to remove the phone number
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the phone number from the list
                phoneNumbers.remove(phoneNumber);

                // Remove the LinearLayout containing the phone number and delete button from the layout
                LinearLayout phoneNumbersLayout = findViewById(R.id.phoneNumbersLayout);
                phoneNumbersLayout.removeView(phoneLayout);
            }
        });

        // Add the LinearLayout to the layout
        LinearLayout phoneNumbersLayout = findViewById(R.id.phoneNumbersLayout);
        phoneNumbersLayout.addView(phoneLayout);
    }

}
