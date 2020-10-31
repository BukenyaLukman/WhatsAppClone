package com.example.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private Button UpdateAccountSettings;
    private EditText UserName, UserStatus;
    private CircleImageView UserProfileImage;
    private String CurrentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        RootRef = FirebaseDatabase.getInstance().getReference();

        initializeFields();

        //UserName.setVisibility(View.INVISIBLE);

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });
        RetrieveUserInfo();
    }




    private void initializeFields() {
        UpdateAccountSettings = findViewById(R.id.update_settings_button);
        UserName = findViewById(R.id.set_user_name);
        UserStatus = findViewById(R.id.set_profile_status);
        UserProfileImage = findViewById(R.id.set_profile_image);
    }

    private void UpdateSettings() {
        String setUserName = UserName.getText().toString();
        String setUserStatus = UserStatus.getText().toString();

        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please write your name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(setUserStatus)){
            Toast.makeText(this, "Please write your status", Toast.LENGTH_SHORT).show();
        }else{
            HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid", CurrentUserID);
            profileMap.put("name",setUserName);
            profileMap.put("status",setUserStatus);

            RootRef.child("Users").child(CurrentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile Updated Successfully..", Toast.LENGTH_SHORT).show();
                            }else{
                                String message = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error :"+ message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void RetrieveUserInfo() {
        RootRef.child("Users").child(CurrentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if((snapshot.exists()) && (snapshot.hasChild("name") && (snapshot.hasChild("image")))){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("status").getValue().toString();
                            String retrieveProfileImage = snapshot.child("image").getValue().toString();

                            UserName.setText(retrieveUserName);
                            UserStatus.setText(retrieveStatus);


                        }else if((snapshot.exists()) && (snapshot.hasChild("name"))){
                            String retrieveUserName = snapshot.child("name").getValue().toString();
                            String retrieveStatus = snapshot.child("status").getValue().toString();

                            UserName.setText(retrieveUserName);
                            UserStatus.setText(retrieveStatus);
                        }else{
                            //UserName.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please set up your profile information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}