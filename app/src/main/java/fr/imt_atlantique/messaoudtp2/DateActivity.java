package fr.imt_atlantique.messaoudtp2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        // Initialize DatePickerDialog
        showDatePicker();

        // Set onClickListener for "Valider" button
        Button validerButton = findViewById(R.id.valider);
        validerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected date
                TextView dateTextView = findViewById(R.id.dateTextView);
                String selectedDate = dateTextView.getText().toString();

                // Create an intent to send back the selected date
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedDate", selectedDate);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        // Set onClickListener for "Abandonner" button
        Button abandonnerButton = findViewById(R.id.abondonner);
        abandonnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Display selected date in TextView
                        TextView dateTextView = findViewById(R.id.dateTextView);
                        dateTextView.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }
}
