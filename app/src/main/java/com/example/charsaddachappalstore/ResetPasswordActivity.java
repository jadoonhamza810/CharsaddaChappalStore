package com.example.charsaddachappalstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import Prevalent.Prevalent;
import buyer.HomeActivity;
import sellers.SellerHomeActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private String check="";
    private String parentDBname="";

    private TextView pageTitle, pageQuestionsTitle;
    private EditText userEmail, securityQus1,securityQus2;
    private Button verifyORupdateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check=getIntent().getStringExtra("check");
        parentDBname=getIntent().getStringExtra("parentDBname");

        pageTitle=(TextView)findViewById(R.id.security_questions_slogan);
        pageQuestionsTitle=(TextView)findViewById(R.id.security_questions_slogan2);
        userEmail=(EditText) findViewById(R.id.security_questions_email);
        securityQus1=(EditText) findViewById(R.id.security_questions_1);
        securityQus2=(EditText) findViewById(R.id.security_questions_2);
        verifyORupdateBtn=(Button) findViewById(R.id.security_questions_verify_btn);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (check.equals("login"))
        {
            answerSecurityQuestions();
        }
        else if(check.equals("settings"))
        {
            setSecurityAnswers();
        }
    }

    private void answerSecurityQuestions()
    {
        verifyORupdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String answer1=securityQus1.getText().toString().toLowerCase();
                String answer2=securityQus2.getText().toString().toLowerCase();
                String email=userEmail.getText().toString();

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(ResetPasswordActivity.this, "First Enter Your Email", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(answer1) || TextUtils.isEmpty(answer2))
                {
                    Toast.makeText(ResetPasswordActivity.this, "Answer Both Questions", Toast.LENGTH_SHORT).show();
                }
                else{
                    email = email.replace(".", "_P%ë5nN*")
                            .replace("$", "_D%5nNë*")
                            .replace("#", "_H%ë5Nn*")
                            .replace("[", "_Oë5n%N*")
                            .replace("]", "_5nN*C%ë")
                            .replace("/", "*_S%ë5nN");

                    DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference()
                            .child(parentDBname)
                            .child(email);

                    RootRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (snapshot.exists()){

                                if (snapshot.hasChild("Security Questions")){

                                    String ans1=snapshot.child("Security Questions").child("qus1").getValue().toString();
                                    String ans2=snapshot.child("Security Questions").child("qus2").getValue().toString();

                                    if (ans1.equals(answer1) && ans2.equals(answer2)){

                                        AlertDialog.Builder builder=new AlertDialog.Builder(ResetPasswordActivity.this);
                                        builder.setTitle("Change Password");

                                        final EditText newPassword=new EditText(ResetPasswordActivity.this);
                                        newPassword.setHint("Enter New Password");
                                       builder.setView(newPassword);

                                       builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               if (!newPassword.getText().toString().equals("")){
                                                    RootRef.child("password")
                                                            .setValue(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(ResetPasswordActivity.this, "Password has been Changed Successfully. LOG IN AGAIN", Toast.LENGTH_SHORT).show();
                                                                dialog.cancel();

                                                                Intent i=new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                                startActivity(i);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                               }
                                               else{
                                                   Toast.makeText(ResetPasswordActivity.this, "First Enter New password", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       });
                                       builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialog, int which) {
                                               dialog.cancel();
                                           }
                                       });

                                       builder.show();

                                    }
                                    else{
                                        Toast.makeText(ResetPasswordActivity.this, "Your Entered Answers Does Not Match Our Records", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else{
                                    Toast.makeText(ResetPasswordActivity.this, "Security Answers are not set for this User", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else{
                                Toast.makeText(ResetPasswordActivity.this, "Account with This Email does not Exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void setSecurityAnswers()
    {
        userEmail.setVisibility(View.GONE);

        pageTitle.setText("Set Security Questions");
        pageQuestionsTitle.setText("Set Answers For The Following Security Questions");
        verifyORupdateBtn.setText("SET");

        displayPreviousAnswers();

        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference()
                .child(parentDBname)
                .child(Prevalent.currentOnlineUser.getEmail());

        verifyORupdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String answer1=securityQus1.getText().toString().toLowerCase();
                String answer2=securityQus2.getText().toString().toLowerCase();

                if(answer1.equals("") || answer2.equals("")){
                    Toast.makeText(ResetPasswordActivity.this, "Please Answer Both Questions", Toast.LENGTH_SHORT).show();
                }
                else{

                    HashMap<String, Object> securityQusMap = new HashMap<>();
                    securityQusMap.put("qus1", answer1);
                    securityQusMap.put("qus2", answer2);

                    RootRef.child("Security Questions").updateChildren(securityQusMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        if (parentDBname.equals("Sellers"))
                                        {
                                            Toast.makeText(ResetPasswordActivity.this, "Answers Set Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ResetPasswordActivity.this, SellerHomeActivity.class);
                                            startActivity(intent);
                                        }
                                        else if (parentDBname.equals("Users"))
                                        {
                                            Toast.makeText(ResetPasswordActivity.this, "Answers Set Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });
                }
            }
        });

    }

    private void displayPreviousAnswers() {

        DatabaseReference answersRef= FirebaseDatabase.getInstance().getReference().child(parentDBname)
                .child(Prevalent.currentOnlineUser.getEmail())
                .child("Security Questions");

        answersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String ans1=snapshot.child("qus1").getValue().toString();
                    String ans2=snapshot.child("qus2").getValue().toString();

                    securityQus1.setText(ans1);
                    securityQus2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}