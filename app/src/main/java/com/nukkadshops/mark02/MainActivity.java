package com.nukkadshops.mark02;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //TextView textView;
    private Handler handler = new Handler();

    EditText editText;
    Button upi;
    Button card;
    ApiService apiService;
    EditText tranid;
    private final int merchantID = 29610;
    private final String token = "a4c9741b-2889-47b8-be2f-ba42081a246e";
    private final String storeID = "1221258";
    private final int clientID = 1013483;
    //private final int amount;
    private long ptrid = 0;
    private int pollCount = 0; // To avoid infinite loop in test mode
    private String allowPyamentMode="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.amount);
        upi = findViewById(R.id.upibt);
        card = findViewById(R.id.cardbt);
        tranid = findViewById(R.id.textView3);

        apiService = ApiClient.getClient().create(ApiService.class);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTransaction(card.getText().toString());

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
                                cancelTransaction();
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
                startTransaction(upi.getText().toString());



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
                                cancelTransaction();
                                dialog.dismiss(); // closes the dialog
                                // You can also handle cancel logic here if needed
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

    }

    @Nullable
    // Step 1: UploadBilledTransaction
    private void startTransaction(String mode) {
        if(mode.equalsIgnoreCase("upi")){
            allowPyamentMode="10";
        }else{
            allowPyamentMode="1";
        }
        Log.d("PINE", "Uploading billed transaction...");

        UploadRequest request = new UploadRequest(
                tranid.getText().toString().trim(), // unique TransactionNumber
                1,                      // Sequence number
                allowPyamentMode,                    // AllowedPaymentMode (1 = Card)
                editText.getText().toString().trim(), // Amount
                "user123",              // UserID
                merchantID,
                token,
                storeID,
                clientID,
                1                 // AutoCancelDurationInMinutes
        );

        apiService.uploadBilledTransaction(request).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UploadResponse res = response.body();
                    Log.i("PINE", "Upload Response: " + new Gson().toJson(res));

                    if (res.ResponseCode == 0) {
                        ptrid = res.PlutusTransactionReferenceID;
                        Log.i("PINE", "PTRID received: " + ptrid);
                        Log.i("PINE", "Ask cashier to enter PTRID on terminal.");
                        pollStatus(); // Start polling status
                    } else {
                        Log.e("PINE", "Upload failed: " + res.ResponseMessage);
                    }
                } else {
                    Log.e("PINE", "Upload failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.e("PINE", "Upload Error: " + t.getMessage());
            }
        });
    }

    // Step 2: Poll GetCloudBasedTxnStatus
    private void pollStatus() {
        pollCount++;
        if (pollCount > 50) { // safety stop after 1 minute (12 * 5s)
            Log.w("PINE", "Polling timeout reached, cancelling transaction...");
            cancelTransaction();
            return;
        }

        StatusRequest req = new StatusRequest(merchantID, token, storeID, clientID, ptrid);
        Log.d("PINE", "Checking status for PTRID: " + ptrid);

        apiService.getTxnStatus(req).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StatusResponse res = response.body();
                    Log.i("PINE", "Status Response: " + new Gson().toJson(res));
                    if (res.TransactionData != null) {
                        String status = res.TransactionData.TransactionStatus;
                        Log.i("PINE", "Transaction Status: " + status);

                        switch (status.toUpperCase()) {
                            case "SUCCESS":
                                Log.i("PINE", "✅ Payment Successful!");
                                Intent intent = new Intent(MainActivity.this, Processing.class);
                                startActivity(intent);
                                break;
                            case "FAILED":
                                Log.e("PINE", "❌ Payment Failed!");
                                break;
                            case "CANCELLED":
                                Log.w("PINE", "⚠️ Payment Cancelled!");
                                break;
                            default:
                                Log.d("PINE", "⏳ Still waiting... (" + pollCount + ")");
                                handler.postDelayed(() -> pollStatus(), 5000);
                                break;
                        }
                    }
                } else {
                    Log.e("PINE", "Status check failed: " + response.message());
                    handler.postDelayed(() -> pollStatus(), 5000);
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Log.e("PINE", "Status check error: " + t.getMessage());
                handler.postDelayed(() -> pollStatus(), 5000);
            }
        });
    }

    // Step 3: CancelTransaction (optional)
    private void cancelTransaction() {
        Log.w("PINE", "Cancelling transaction with PTRID: " + ptrid);

        CancelRequest cancelReq = new CancelRequest(
                merchantID, token, storeID, clientID, ptrid, editText.getText().toString().trim());

        apiService.cancelTransaction(cancelReq).enqueue(new Callback<CancelResponse>() {
            @Override
            public void onResponse(Call<CancelResponse> call, Response<CancelResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.w("PINE", "Cancel Response: " + new Gson().toJson(response.body()));
                } else {
                    Log.e("PINE", "Cancel failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CancelResponse> call, Throwable t) {
                Log.e("PINE", "Cancel error: " + t.getMessage());
            }
        });
    }
}
