package com.example.systemcapabilitiesapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;


public class SystemCapabilitiesActivity extends AppCompatActivity {
    private static final int REQUEST_CALL_PERMISSION = 1;

    EditText phoneNumberEditText, smsPhoneNumberEditText, smsContentEditText, emailAddressEditText, subjectEditText, bodyEditText,contentEditText;
    Button callButton, smsButton, emailButton, shareButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saystem_capabilities);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.systemCapabilities), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        View rootLayout = findViewById(R.id.systemCapabilities); // Replace with the ID of your root layout

        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });
         phoneNumberEditText=(EditText)findViewById(R.id.phoneNumberEditText);
         callButton=(Button)findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });

        smsPhoneNumberEditText=(EditText)findViewById(R.id.smsPhoneNumberEditText);
        smsContentEditText=(EditText)findViewById(R.id.smsContentEditText);
        smsButton=(Button)findViewById(R.id.smsButton);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });
        emailAddressEditText=(EditText)findViewById(R.id.emailAddressEditText);
        subjectEditText=(EditText)findViewById(R.id.subjectEditText);
        bodyEditText=(EditText)findViewById(R.id.bodyEditText);
        emailButton=(Button)findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        contentEditText=(EditText)findViewById(R.id.contentEditText);
        shareButton=(Button)findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareContent();
            }
        });
    }
    private void makePhoneCall() {
        String phoneNumber = phoneNumberEditText.getText().toString();

        if (phoneNumber.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            // Permission already granted
            initiateCall(phoneNumber);
        }
    }

    private void initiateCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        PackageManager packageManager = getPackageManager();
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (callIntent.resolveActivity(packageManager) != null) {
            startActivity(callIntent);
        } else {
            Toast.makeText(this, "No activity found to handle this action", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initiate the call
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED to make phone calls", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void sendSMS() {
        String phoneNumber = smsPhoneNumberEditText.getText().toString();
        String message = smsContentEditText.getText().toString();
        PackageManager packageManager = getPackageManager();
        if (phoneNumber.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        } else if (message.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null));
        smsIntent.putExtra("sms_body", message);
        if (smsIntent.resolveActivity(packageManager) != null) {
            startActivity(smsIntent);
        }else {
            Toast.makeText(this, "No activity found to handle this action", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendEmail() {
        PackageManager packageManager = getPackageManager();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + emailAddressEditText.getText().toString()));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectEditText.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, bodyEditText.getText().toString());
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(emailIntent, "Choose an Email client"));
        }else {
            Toast.makeText(this, "No activity found to handle this action", Toast.LENGTH_SHORT).show();
        }
    }
    private void shareContent() {
        PackageManager packageManager = getPackageManager();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, contentEditText.getText().toString());

        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }else {
            Toast.makeText(this, "No activity found to handle this action", Toast.LENGTH_SHORT).show();
        }
    }



    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
