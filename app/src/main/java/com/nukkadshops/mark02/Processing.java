//package com.nukkadshops.mark02;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class Processing extends AppCompatActivity {
//
//    TextView status;
//    Button voidres;
//    voidService VoidSer;
//
//    int ptrid, merid, clntid;
//    String Res, trnsnum, sectkn, amt, pay, strid;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_processing);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        status = findViewById(R.id.textView);
//        voidres = findViewById(R.id.button4);
//
//        // Get intent data
//        Res = getIntent().getStringExtra("success");
//        ptrid = (int)getIntent().getLongExtra("PTRID",0L );
//        trnsnum = getIntent().getStringExtra("transaction number");
//        merid = getIntent().getIntExtra("merchant id", 0);
//        sectkn = getIntent().getStringExtra("security token");
//        clntid = getIntent().getIntExtra("client id", 0);
//        amt = getIntent().getStringExtra("amount");
//        pay = getIntent().getStringExtra("allowed payment mode");
//        strid = getIntent().getStringExtra("store id");
//
//        status.setText(Res);
//
//        VoidSer = ApiVoid.getvoid().create(voidService.class);
//        if (Res == null || Res.isEmpty()) {
//            voidres.setVisibility(View.INVISIBLE);
//        } else {
//            voidres.setVisibility(View.VISIBLE);
//        }
//        voidres.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                vdcancel();
//                Log.e("PINE","void cancelled");
//            }
//        });
//    }
//
//    // -------- VOID FUNCTION FIXED HERE ------------
//    private void vdcancel() {
//
//        VoidRequest request = new VoidRequest(
//                trnsnum,
//                1,
//                pay,
//                strid,
//                amt,
//                merid,
//                sectkn,
//                clntid,
//                1,
//                ptrid,
//                5
//        );
//
//        VoidSer.uploadBilledTransaction(request).enqueue(new Callback<VoidResponse>() {
//            @Override
//            public void onResponse(Call<VoidResponse> call, Response<VoidResponse> response) {
//
//                if (!response.isSuccessful() || response.body() == null) {
//                    Log.e("PINE", "Void failed: " + response.message());
//                    return;
//                }
//                VoidResponse res = response.body();
//                String msg=res.responseMessage;
//                if(msg!=null||msg.equals("APPROVED")){
//                    Toast.makeText(Processing.this,"Void cancellation is approved",Toast.LENGTH_SHORT).show();
//                }
//                Log.i("PINE", "Void success!");
//            }
//
//
//            @Override
//            public void onFailure(Call<VoidResponse> call, Throwable t) {
//                Log.e("PINE", "Void failed: " + t.getMessage());
//            }
//        });
//    }
//}
package com.nukkadshops.mark02;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Processing extends AppCompatActivity {

    TextView status;
    Button voidres;
    voidService VoidSer;

    int ptrid, merid, clntid;
    String Res, trnsnum, sectkn, amt, pay, strid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_processing);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        status = findViewById(R.id.textView);
        voidres = findViewById(R.id.button4);

        // Get intent data
        Res = getIntent().getStringExtra("success");
        ptrid = (int) getIntent().getLongExtra("PTRID", 0L);
        trnsnum = getIntent().getStringExtra("transaction number");
        merid = getIntent().getIntExtra("merchant id", 0);
        sectkn = getIntent().getStringExtra("security token");
        clntid = getIntent().getIntExtra("client id", 0);
        amt = getIntent().getStringExtra("amount");
        pay = getIntent().getStringExtra("allowed payment mode");
        strid = getIntent().getStringExtra("store id");

        status.setText(Res);

        VoidSer = ApiVoid.getvoid().create(voidService.class);

        if (Res == null || Res.isEmpty()) {
            voidres.setVisibility(View.INVISIBLE);
        } else {
            voidres.setVisibility(View.VISIBLE);
        }

        voidres.setOnClickListener(v -> {
            Log.i("PINE", "Void request started...");
            voidres.setEnabled(false);
            vdcancel();
        });
    }

    private void vdcancel() {

        VoidRequest request = new VoidRequest(
                trnsnum,
                1,
                pay,
                strid,
                amt,
                merid,
                sectkn,
                clntid,
                1,
                ptrid,
                5
        );

        VoidSer.uploadBilledTransaction(request).enqueue(new Callback<VoidResponse>() {
            @Override
            public void onResponse(Call<VoidResponse> call, Response<VoidResponse> response) {

                voidres.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("PINE", "Void failed: " + response.message());
                    return;
                }

                VoidResponse res = response.body();
                String msg = res.responseMessage;

                if (msg != null && msg.equalsIgnoreCase("APPROVED")) {
                    Toast.makeText(Processing.this, "Void cancellation is approved", Toast.LENGTH_SHORT).show();
                }

                Log.i("PINE", "Void success!");
            }

            @Override
            public void onFailure(Call<VoidResponse> call, Throwable t) {
                voidres.setEnabled(true);
                Log.e("PINE", "Void failed: " + t.getMessage());
            }
        });
    }
}
