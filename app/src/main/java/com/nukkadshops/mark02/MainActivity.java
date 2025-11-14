package com.nukkadshops.mark02;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    private Handler handler = new Handler();

    EditText editText, tranid;
    Button upi, card;
    ApiService apiService;

    private final int merchantID = 29610;
    private final String token = "a4c9741b-2889-47b8-be2f-ba42081a246e";
    private final String storeID = "1221258";
    private final int clientID = 1013483;

    private long ptrid = 0;
    private int pollCount = 0;
    private String allowPyamentMode = "";

    private AlertDialog alertDialog;   // Store dialog instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.amount);
        tranid = findViewById(R.id.textView3);
        upi = findViewById(R.id.upibt);
        card = findViewById(R.id.cardbt);

        apiService = ApiClient.getClient().create(ApiService.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        card.setOnClickListener(v -> showProcessingDialog("CARD"));
        upi.setOnClickListener(v -> showProcessingDialog("UPI"));

    }

    // ---------------- SHOW PROCESSING DIALOG ----------------

    private void showProcessingDialog(String mode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Payment Processing..!")
                .setCancelable(false)
                .setNegativeButton("Cancel", null);

        alertDialog = builder.create();
        alertDialog.show();

        // Override Cancel Button to prevent auto-dismiss
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(view -> {
            if (ptrid == 0) {
                Toast.makeText(MainActivity.this, "Please wait... transaction not started!", Toast.LENGTH_SHORT).show();
            } else {
                stopPollingCompletely();

                new Handler().postDelayed(() -> cancelTransaction(), 2000);
                alertDialog.dismiss();
            }
        });

        startTransaction(mode);
    }


    // ---------------- START TRANSACTION ----------------

    private void startTransaction(String mode) {

        allowPyamentMode = mode.equalsIgnoreCase("upi") ? "10" : "1";

        Log.d("PINE", "Uploading billed transaction...");

        UploadRequest request = new UploadRequest(
                tranid.getText().toString().trim(),
                1,
                allowPyamentMode,
                editText.getText().toString().trim(),
                "user123",
                merchantID,
                token,
                storeID,
                clientID,
                5
        );

        apiService.uploadBilledTransaction(request).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("PINE", "Upload failed: " + response.message());
                    return;
                }

                UploadResponse res = response.body();
                Log.i("PINE", "Upload Response: " + new Gson().toJson(res));

                if (res.ResponseCode == 0) {
                    ptrid = res.PlutusTransactionReferenceID;
                    Log.i("PINE", "PTRID: " + ptrid);
                    pollStatus();
                } else {
                    Log.e("PINE", "Upload error: " + res.ResponseMessage);
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.e("PINE", "Upload error: " + t.getMessage());
            }
        });
    }

    // ---------------- POLL STATUS EVERY 5 SEC ----------------

    private void pollStatus() {

        pollCount++;

        if (pollCount > 60) {
            Log.w("PINE", "Timeout → Auto cancel");
            stopPollingCompletely();

            new Handler().postDelayed(() -> cancelTransaction(), 2000);
            return;
        }

        StatusRequest req = new StatusRequest(merchantID, token, storeID, clientID, ptrid);
        Log.d("PINE", "Polling PTRID: " + ptrid);

        apiService.getTxnStatus(req).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    handler.postDelayed(() -> pollStatus(), 5000);
                    return;
                }

                StatusResponse res = response.body();
                Log.i("PINE", "Status Response: " + new Gson().toJson(res));

                if (res.TransactionData == null || res.TransactionData.isEmpty()) {
                    handler.postDelayed(() -> pollStatus(), 5000);
                    return;
                }

                String approvalCode = "";

                for (StatusResponse.TransactionData tag : res.TransactionData) {
                    if ("ApprovalCode".equalsIgnoreCase(tag.Tag)) {
                        approvalCode = tag.Value;
                        break;
                    }
                }

                if (approvalCode.equals("00")) {
                    stopPollingCompletely();

                    Intent intent = new Intent(MainActivity.this, Processing.class);
                    intent.putExtra("PTRID", ptrid);
                    intent.putExtra("success", res.ResponseMessage);
                    intent.putExtra("transaction number",tranid.getText().toString().trim());
                    intent.putExtra("merchant id",merchantID);
                    intent.putExtra("security token",token);
                    intent.putExtra("client id",clientID);
                    intent.putExtra("amount",editText.getText().toString().trim());
                    intent.putExtra("allowed payment mode",allowPyamentMode);
                    intent.putExtra("store id",storeID);
                    startActivity(intent);
                    return;
                }

                if (!approvalCode.isEmpty()) {
                    stopPollingCompletely();
                    Toast.makeText(MainActivity.this, "Payment Failed", Toast.LENGTH_LONG).show();
                    return;
                }

                handler.postDelayed(() -> pollStatus(), 5000);
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                handler.postDelayed(() -> pollStatus(), 5000);
            }
        });
    }


    // ---------------- STOP POLLING ----------------

    private void stopPollingCompletely() {
        handler.removeCallbacksAndMessages(null);
        pollCount = 0;
    }


    // ---------------- CANCEL TRANSACTION ----------------

    private void cancelTransaction() {

        Log.w("PINE", "Cancel transaction PTRID: " + ptrid);
        stopPollingCompletely();

        CancelRequest cancelReq = new CancelRequest(
                merchantID, token, storeID, clientID, ptrid, editText.getText().toString().trim(),true
        );

        apiService.cancelTransaction(cancelReq).enqueue(new Callback<CancelResponse>() {
            @Override
            public void onResponse(Call<CancelResponse> call, Response<CancelResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.w("PINE", "Cancel Response: " + new Gson().toJson(response.body()));
                }
            }

            @Override
            public void onFailure(Call<CancelResponse> call, Throwable t) {
                Log.e("PINE", "Cancel error: " + t.getMessage());
            }
        });
    }
}