package com.nukkadshops.mark02;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //TextView textView;
    EditText editText;
    Button upi;
    Button card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.amount);
        upi = findViewById(R.id.upibt);
        card = findViewById(R.id.cardbt);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*here api calling
                        .
                .
                .
                */

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Payment Processing..!")
                        .setCancelable(false) // prevents closing by tapping outside
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // closes the dialog
                                // You can also handle cancel logic here if needed
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        upi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*write api code here
                .
                .
                .
                .*/

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Payment Processing..!")
                        .setCancelable(false) // prevents closing by tapping outside
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // closes the dialog
                                // You can also handle cancel logic here if needed
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

    }
}