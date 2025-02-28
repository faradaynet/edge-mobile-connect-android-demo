package net.faraday.mobileconnect.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.faraday.mobileconnect.sdk.FNEdgeMobileConnect;
import net.faraday.mobileconnect.sdk.FNEdgeWifiConnectionStatus;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText msisdnText, passportText, nationalIdText, countryCodeText, ssidNameText;
    private Button connectButton;
    private ProgressBar progressBar;

    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        countryCodeText = findViewById(R.id.country_code);
        msisdnText = findViewById(R.id.phone_number);
        passportText = findViewById(R.id.passport_number);
        nationalIdText = findViewById(R.id.national_id);

        statusTextView = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progressBar);
        connectButton = findViewById(R.id.connect_button);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                boolean isConnected = FNWifiUtils.isConnectedToWifi(getApplicationContext());
                if (isConnected) {
                    String msisdn = msisdnText.getText().toString();
                    String passport = passportText.getText().toString();
                    String nationalId = nationalIdText.getText().toString();
                    String countryCode = countryCodeText.getText().toString();

                    FNEdgeMobileConnect mobileConnect = new FNEdgeMobileConnect(getApplicationContext());
                    List<String> urls = List.of("https://service-provider-url.com", "https://service-provider-url2.com");
                    mobileConnect.setConnectionUrls(urls);
                    statusTextView.setText(getString(R.string.connection_started));

                    mobileConnect.connectWifi(countryCode, msisdn, passport, nationalId, true, false, true, new FNEdgeMobileConnect.WifiConnectionCallback() {
                        @Override
                        public void onSuccess(FNEdgeWifiConnectionStatus status) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    statusTextView.setText(String.format("- Status Code: %s\n- Message: %s\n- Client Mac: %s", status.getErrorCode(), status.getMessage(), status.getMac()));
                                    Log.d(FNConstants.TAG, "Connection Success: " + status.getMessage() + " mac:" + status.getMac());
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }

                        @Override
                        public void onError(FNEdgeWifiConnectionStatus status) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    statusTextView.setText(String.format("- Error Code: %s\n- Message: %s\n- Client Mac: %s", status.getErrorCode(), status.getMessage(), status.getMac()));
                                    Log.d(FNConstants.TAG, "Connection Error, Message: " + status.getMessage() + " mac:" + status.getMac());
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                } else {
                    statusTextView.setText(getString(R.string.wifi_connection_required));
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }
}