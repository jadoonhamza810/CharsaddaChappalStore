package com.example.charsaddachappalstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import Prevalent.Prevalent;
import buyer.HomeActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import sellers.SellerCategoryActivity;
import sellers.SellerHomeActivity;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, addressEditText;
    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;
    private EditText  newPassword, confirmNewPassword;
    private Button setSecurityQuestions;

    private String newPass,confirmedNewPass;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";
    private String parentDBname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        profileChangeTextBtn = (TextView) findViewById(R.id.profile_image_change_btn);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_btn);
        setSecurityQuestions=(Button)findViewById(R.id.settings_set_security_questions);

        newPassword = (EditText) findViewById(R.id.settings_new_password);
        confirmNewPassword = (EditText) findViewById(R.id.settings_confirm_new_password);

        parentDBname=getIntent().getStringExtra("parentDBname");

        userInfoDisplay(profileImageView, fullNameEditText, addressEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });


        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
        setSecurityQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "settings");
                intent.putExtra("parentDBname", parentDBname);
                startActivity(intent);
            }
        });

    }

    private void updateOnlyUserInfo()
    {
        if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Toast.makeText(this, "Please Enter new Address.", Toast.LENGTH_SHORT).show();
        }
        else if (!TextUtils.isEmpty(newPassword.getText().toString()))
        {
            if(!TextUtils.isEmpty(confirmNewPassword.getText().toString()))
            {
                if(newPassword.getText().toString().equals(confirmNewPassword.getText().toString())){
                    saveUserInfo();
                }
                else{
                    Toast.makeText(this, "Both entered Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Please Confirm New Address", Toast.LENGTH_SHORT).show();
            }
        }
        else {

            saveUserInfo();
        }
    }

    private void saveUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(parentDBname);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Wait! while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", fullNameEditText.getText().toString());
        userMap. put("address", addressEditText.getText().toString());
        if(!newPassword.getText().toString().equals("") && !confirmNewPassword.getText().toString().equals("")){
            userMap. put("password", newPassword.getText().toString());
        }

        ref.child(Prevalent.currentOnlineUser.getEmail()).updateChildren(userMap);

        progressDialog.dismiss();
        if(parentDBname.equals("Users")){
            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        }
        else{
            startActivity(new Intent(SettingsActivity.this, SellerHomeActivity.class));
        }
        Toast.makeText(SettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_LONG).show();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_LONG).show();

            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, "Please Enter new Address.", Toast.LENGTH_SHORT).show();
        }
        else if (!TextUtils.isEmpty(newPassword.getText().toString()))
        {
            if(!TextUtils.isEmpty(confirmNewPassword.getText().toString()))
            {
                if(newPassword.getText().toString().equals(confirmNewPassword.getText().toString())){
                    uploadImage();
                }
                else{
                    Toast.makeText(this, "Both entered Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Please Confirm New Address", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            uploadImage();
        }
    }



    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Wait! while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(Prevalent.currentOnlineUser.getEmail() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(parentDBname);

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("name", fullNameEditText.getText().toString());
                                userMap. put("address", addressEditText.getText().toString());
                                userMap. put("image", myUrl);

                                if(!newPassword.getText().toString().equals("") && !confirmNewPassword.getText().toString().equals("")){
                                    userMap. put("password", newPassword.getText().toString());
                                }

                                ref.child(Prevalent.currentOnlineUser.getEmail()).updateChildren(userMap);

                                progressDialog.dismiss();

                                if(parentDBname.equals("Users")){
                                    startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                }
                                else{
                                    startActivity(new Intent(SettingsActivity.this, SellerHomeActivity.class));
                                }

                                Toast.makeText(SettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Error occurred while updating profile Info", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else
        {
            progressDialog.dismiss();
            Toast.makeText(this, "Please, first select the Image", Toast.LENGTH_LONG).show();
        }
    }


    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText,  final EditText addressEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child(parentDBname).child(Prevalent.currentOnlineUser.getEmail());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);

                    }
                    if (dataSnapshot.child("address").exists())
                    {
                        String address = dataSnapshot.child("address").getValue().toString();
                        addressEditText.setText(address);

                    }
                    String name = dataSnapshot.child("name").getValue().toString();
                    fullNameEditText.setText(name);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}