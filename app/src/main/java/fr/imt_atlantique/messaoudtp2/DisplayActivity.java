package fr.imt_atlantique.messaoudtp2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // Get user object from MainActivity
        User user = getIntent().getParcelableExtra("user");

        // Display user information
        TextView userInfoTextView = findViewById(R.id.userInfosView);
        userInfoTextView.setText("Nom: " + user.getName() + "\n" +
                "Prénom: " + user.getSurname() + "\n" +
                "Ville de naissance: " + user.getCity() + "\n" +
                "Date de naissance: " + user.getBirthday());

        // Display phone numbers
        List<String> phoneNumbers = user.getPhoneNumbers();
        StringBuilder phoneNumbersText = new StringBuilder("Numéros de téléphone:\n");
        for (String phoneNumber : phoneNumbers) {
            phoneNumbersText.append(phoneNumber).append("\n");
        }
        TextView phoneNumbersTextView = findViewById(R.id.phoneNumbersView);
        phoneNumbersTextView.setText(phoneNumbersText.toString());
    }
}
