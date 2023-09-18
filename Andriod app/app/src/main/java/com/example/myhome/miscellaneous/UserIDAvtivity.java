package com.example.myhome.miscellaneous;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myhome.R;
import com.example.myhome.maincontroller.BottomNavBarActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.HashMap;
import java.util.Map;

public class UserIDAvtivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_id_printer);

        TextView userIdTextView = findViewById(R.id.userIdTextView);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userIdTextView.setText("User ID: " + userId);
        }

        Button copyButton = findViewById(R.id.copyButton);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyUserIdToClipboard();
            }
        });
    }

    private void copyUserIdToClipboard() {
        TextView userIdTextView = findViewById(R.id.userIdTextView);
        String userId = getUserIdFromTextView(userIdTextView);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("User ID", userId);
        clipboard.setPrimaryClip(clip);

        DynamicToast.makeSuccess(this, "User ID copied to clipboard").show();
    }

    private String getUserIdFromTextView(TextView userIdTextView) {
        String userIdPrefix = "User ID: ";
        String userIdText = userIdTextView.getText().toString();
        if (userIdText.startsWith(userIdPrefix)) {
            return userIdText.substring(userIdPrefix.length());
        }
        return userIdText;
    }
}