package com.example.charsaddachappalstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import Prevalent.Prevalent;
import buyer.HomeActivity;

public class RegisterActivity extends AppCompatActivity {


    private Button createAccount;
    private EditText inputName, inputEmail, newPassword, cnfrmPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccount = (Button) findViewById(R.id.register);
        inputName = (EditText) findViewById(R.id.register_name);
        inputEmail = (EditText) findViewById(R.id.register_emailAddress);
        newPassword = (EditText) findViewById(R.id.register_password);
        cnfrmPassword = (EditText) findViewById(R.id.register_confirm_password);

        loadingBar = new ProgressDialog(this);


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });

    }

    private void CreateAccount() {
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = newPassword.getText().toString();
        String confirm_Password = cnfrmPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write your name...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(confirm_Password)) {
            Toast.makeText(this, "Please re-enter your password...", Toast.LENGTH_LONG).show();
        } else if (!password.equalsIgnoreCase(confirm_Password)) {
            Toast.makeText(this, "your entered passwords does not match....", Toast.LENGTH_LONG).show();
        } else {

            email = email.replace(".", "_P%ë5nN*")
                    .replace("$", "_D%5nNë*")
                    .replace("#", "_H%ë5Nn*")
                    .replace("[", "_Oë5n%N*")
                    .replace("]", "_5nN*C%ë")
                    .replace("/", "*_S%ë5nN");
            name = name.replace(".", "_P%ë5nN*")
                    .replace("$", "_D%5nNë*")
                    .replace("#", "_H%ë5Nn*")
                    .replace("[", "_Oë5n%N*")
                    .replace("]", "_5nN*C%ë")
                    .replace("/", "*_S%ë5nN");
            password = password.replace(".", "_P%ë5nN*")
                    .replace("$", "_D%5nNë*")
                    .replace("#", "_H%ë5Nn*")
                    .replace("[", "_Oë5n%N*")
                    .replace("]", "_5nN*C%ë")
                    .replace("/", "*_S%ë5nN");


            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateEmail(name, email, password);
        }
    }


    private void ValidateEmail(final String name, final String email, final String password) {


        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(email).exists())) {

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", email);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    RootRef.child("Users").child(email).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Your account has been created successfully.", Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Email " + email + " already exists.", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Please try again using another Email address.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

