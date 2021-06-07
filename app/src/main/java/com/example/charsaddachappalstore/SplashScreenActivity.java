package com.example.charsaddachappalstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Users;
import Prevalent.Prevalent;
import buyer.HomeActivity;
import io.paperdb.Paper;
import sellers.SellerHomeActivity;

public class SplashScreenActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Paper.init(this);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                String userEmail=Paper.book().read(Prevalent.userEmail);
                String userPassword=Paper.book().read(Prevalent.userPassword);

                if(userEmail!="null" && userPassword!="null")
                {
                    if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPassword)){
                        Toast.makeText(SplashScreenActivity.this, "Already Logged IN!", Toast.LENGTH_SHORT).show();
                        AllowAccess(userEmail, userPassword);
                    }
                    else{
                        Intent intent=new Intent(SplashScreenActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        },3000);

    }

    private void AllowAccess(String email, String password) {


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();



        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("Users").child(email).exists())
                {
                    Users usersData = dataSnapshot.child("Users").child(email).getValue(Users.class);

                    if (usersData.getEmail().equals(email))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            Toast.makeText(SplashScreenActivity.this, "logged in Successfully...", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser=usersData;
                            startActivity(intent);
                            finish();

                        }
                        else
                        {
                            Toast.makeText(SplashScreenActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                }
                else if (dataSnapshot.child("Sellers").child(email).exists())
                {
                    Users usersData = dataSnapshot.child("Sellers").child(email).getValue(Users.class);


                    if (usersData.getEmail().equals(email))
                    {
                        if (usersData.getPassword() != null && usersData.getPassword().equals(password))
                        {
                            Toast.makeText(SplashScreenActivity.this, "logged in as Seller Successfully...", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SplashScreenActivity.this, SellerHomeActivity.class);
                            Prevalent.currentOnlineUser=usersData;
                            startActivity(intent);
                            finish();

                        }
                        else
                        {
                            Toast.makeText(SplashScreenActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                }

                else
                {
                    Toast.makeText(SplashScreenActivity.this, "Account with this " + email + " Email do not exists.", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}