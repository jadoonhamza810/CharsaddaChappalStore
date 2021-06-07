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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import Model.Users;
import Prevalent.Prevalent;
import buyer.HomeActivity;
import sellers.SellerCategoryActivity;
import io.paperdb.Paper;
import sellers.SellerHomeActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button loginButton;
    private ProgressDialog loadingBar;
    private TextView sellerLink, customerLink, forgotPassword;

    private String parentDbName = "Users";
   private CheckBox chkBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.login);
        inputPassword = (EditText) findViewById(R.id.login_password);
        inputEmail = (EditText) findViewById(R.id.login_emailAddress);

        sellerLink = (TextView) findViewById(R.id.seller_panel_link);
        customerLink = (TextView) findViewById(R.id.customer_panel_link);
        forgotPassword = (TextView) findViewById(R.id.login_forgot_password);

        loadingBar = new ProgressDialog(this);
        chkBoxRememberMe = (CheckBox) findViewById(R.id.login_remember_me_chk_box);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                LoginUser();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                intent.putExtra("parentDBname",parentDbName);
                startActivity(intent);
            }
        });

        sellerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loginButton.setText("Login Seller");
                sellerLink.setVisibility(View.INVISIBLE);
                customerLink.setVisibility(View.VISIBLE);
                parentDbName = "Sellers";
            }
        });

        customerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loginButton.setText("Login");
                sellerLink.setVisibility(View.VISIBLE);
                customerLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }



    private void LoginUser()
    {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write your email address...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please write your password...", Toast.LENGTH_LONG).show();
        }
        else
        {

            email = email.replace(".", "_P%ë5nN*")
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

            loadingBar.setTitle("Logging in!");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            AllowAccessToAccount(email, password);
        }
    }


    private void AllowAccessToAccount(final String email, final String password)
    {
        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.userEmail, email);
            Paper.book().write(Prevalent.userPassword, password);
        }


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


       RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
              if (dataSnapshot.child(parentDbName).child(email).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(email).getValue(Users.class);

                    if (usersData.getEmail().equals(email))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Sellers"))
                           {
                                Toast.makeText(LoginActivity.this, "Welcome, you are logged in as seller Successfully...", Toast.LENGTH_LONG).show();
                                Prevalent.currentOnlineUser = usersData;
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, SellerHomeActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "logged in Successfully...", Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }

                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else
                 {
                    Toast.makeText(LoginActivity.this, "Account with this " + email + " Email do not exists.", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
