package sellers;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.charsaddachappalstore.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Model.Products;
import Prevalent.Prevalent;

public class AddNewProductactivity extends AppCompatActivity {


    private String  Description, Price, Pname, Pcode, Pcolor, Psize,Pdiscount,Pstock="in stock", saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage1, InputProductImage2, InputProductImage3;
    private EditText InputProductName,InputProductCode, InputproductColor,InputProductSize;
    private EditText InputProductDescription, InputProductPrice, InputProductDiscount;
    private static final int GalleryPick = 1;
    private Uri ImageUri,ImageUri1,ImageUri2,ImageUri3;
    private String productRandomKey, downloadImageUrl1,downloadImageUrl2,downloadImageUrl3;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;

    private String productID="";
    private String CategoryName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_productactivity);

        CategoryName = getIntent().getStringExtra("category");
        productID=getIntent().getStringExtra("pid");


        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        AddNewProductButton = (Button) findViewById(R.id.add_new_product);
        InputProductImage1 = (ImageView) findViewById(R.id.select_product_image1);
        InputProductImage2 = (ImageView) findViewById(R.id.select_product_image2);
        InputProductImage3 = (ImageView) findViewById(R.id.select_product_image3);
        InputProductName = (EditText) findViewById(R.id.product_name);
        InputProductCode=(EditText) findViewById(R.id.product_code);
        InputproductColor=(EditText) findViewById(R.id.product_color);
        InputProductSize=(EditText) findViewById(R.id.product_size);
        InputProductDescription = (EditText) findViewById(R.id.product_description);
        InputProductPrice = (EditText) findViewById(R.id.product_price);
        InputProductDiscount=(EditText) findViewById(R.id.product_price_discount);

        loadingBar = new ProgressDialog(this);

        if (!productID.equals(""))
        {
            getProductDetails(productID);

        }


        InputProductImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });
        InputProductImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });
        InputProductImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });


        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
            }
        });

    }



    private void getProductDetails(String productID)
    {
        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products product=snapshot.getValue(Products.class);

                    InputProductName.setText(product.getPname());
                    InputProductCode.setText(product.getCode());
                    InputproductColor.setText(product.getColor());
                    InputProductSize.setText(product.getSize());
                    InputProductPrice.setText(product.getPrice());
                    InputProductDiscount.setText(product.getDiscount());
                    InputProductDescription.setText(product.getDescription());

                    Picasso.get().load(product.getImage1()).into(InputProductImage1);
                    Picasso.get().load(product.getImage2()).into(InputProductImage2);
                    Picasso.get().load(product.getImage3()).into(InputProductImage3);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }



    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null) {
            ImageUri = data.getData();
            if (InputProductImage1.getBackground()==null)
            {
                InputProductImage1.setImageURI(ImageUri);
                InputProductImage1.setBackground(getDrawable(R.drawable.add_image));
                ImageUri1=ImageUri;
            }
            else if (InputProductImage2.getBackground()==null)
            {
                InputProductImage2.setImageURI(ImageUri);
                InputProductImage2.setBackground(getDrawable(R.drawable.add_image));
                ImageUri2=ImageUri;
            }
            else if (InputProductImage3.getBackground()==null)
            {
                InputProductImage3.setImageURI(ImageUri);
                InputProductImage3.setBackground(getDrawable(R.drawable.add_image));
                ImageUri3=ImageUri;
            }
            else {
                Toast.makeText(this, "Product images are already chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void ValidateProductData()
    {
        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();
        Pcode = InputProductCode.getText().toString();
        Pcolor = InputproductColor.getText().toString();
        Psize= InputProductSize.getText().toString();
        Pdiscount = InputProductDiscount.getText().toString();



        if (ImageUri1 == null)
        {
            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
       }
        else if (ImageUri2 == null)
        {
            Toast.makeText(this, "Enter 2 more Images", Toast.LENGTH_SHORT).show();
        }
        else if (ImageUri3 == null)
        {
            Toast.makeText(this, "Enter 1 more Image", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pname))
        {
            Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pcode))
        {
            Toast.makeText(this, "Please enter Product Code", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pcolor))
        {
            Toast.makeText(this, "Please enter Product Color", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(Psize))
        {
            Toast.makeText(this, "Please enter Product Size", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please write Product description...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this, "Please enter Product Size", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(TextUtils.isEmpty(Pdiscount)){
                Pdiscount="No discount";
                StoreProductInformation();
            }
            StoreProductInformation();

        }

    }



    private void StoreProductInformation()
    {
        loadingBar.setTitle("Adding New Product");
        loadingBar.setMessage("Dear Seller, please wait while new product is getting added");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;


        final StorageReference filePath1 = ProductImagesRef.child(ImageUri1.getLastPathSegment() + productRandomKey + ".jpg");
        final StorageReference filePath2 = ProductImagesRef.child(ImageUri2.getLastPathSegment() + productRandomKey + ".jpg");
        final StorageReference filePath3 = ProductImagesRef.child(ImageUri3.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask1 = filePath1.putFile(ImageUri1);

        final UploadTask uploadTask3 = filePath3.putFile(ImageUri3);

        uploadTask1.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AddNewProductactivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AddNewProductactivity.this, "Product Image 1 uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl1 = filePath1.getDownloadUrl().toString();
                        return filePath1.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl1 = task.getResult().toString();

                            Toast.makeText(AddNewProductactivity.this, "got the Product image 1 Url Successfully...", Toast.LENGTH_SHORT).show();

                            final UploadTask uploadTask2 = filePath2.putFile(ImageUri2);
                            uploadTask2.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    String message = e.toString();
                                    Toast.makeText(AddNewProductactivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    Toast.makeText(AddNewProductactivity.this, "Product Image 2 uploaded Successfully...", Toast.LENGTH_SHORT).show();

                                    Task<Uri> urlTask = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                                        {
                                            if (!task.isSuccessful())
                                            {
                                                throw task.getException();
                                            }

                                            downloadImageUrl2 = filePath2.getDownloadUrl().toString();
                                            return filePath2.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                downloadImageUrl2 = task.getResult().toString();

                                                Toast.makeText(AddNewProductactivity.this, "got the Product image 2 Url Successfully...", Toast.LENGTH_SHORT).show();

                                                final UploadTask uploadTask3 = filePath3.putFile(ImageUri3);
                                                uploadTask3.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e)
                                                    {
                                                        String message = e.toString();
                                                        Toast.makeText(AddNewProductactivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                                    {
                                                        Toast.makeText(AddNewProductactivity.this, "Product Image 3 uploaded Successfully...", Toast.LENGTH_SHORT).show();

                                                        Task<Uri> urlTask = uploadTask3.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                            @Override
                                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                                                            {
                                                                if (!task.isSuccessful())
                                                                {
                                                                    throw task.getException();
                                                                }

                                                                downloadImageUrl3 = filePath3.getDownloadUrl().toString();
                                                                return filePath3.getDownloadUrl();
                                                            }
                                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    downloadImageUrl3 = task.getResult().toString();

                                                                    Toast.makeText(AddNewProductactivity.this, "got the Product image 3 Url Successfully...", Toast.LENGTH_SHORT).show();
                                                                    SaveProductInfoToDatabase();

                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }



    private void SaveProductInfoToDatabase()
    {

        String productIDforinfostoring;
        HashMap<String, Object> productMap = new HashMap<>();
        if(!productID.equals("")){
            productMap.put("pid", productID);
            productIDforinfostoring=productID;
        }
        else{
            productMap.put("pid", productRandomKey);
            productIDforinfostoring=productRandomKey;
        }
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image1", downloadImageUrl1);
        productMap.put("image2", downloadImageUrl2);
        productMap.put("image3", downloadImageUrl3);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);
        productMap.put("code", Pcode);
        productMap.put("color", Pcolor);
        productMap.put("size", Psize);
        productMap.put("discount", Pdiscount);
        productMap.put("stock", Pstock);
        productMap.put("sellerID", Prevalent.currentOnlineUser.getEmail());

        ProductsRef.child(productIDforinfostoring).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AddNewProductactivity.this, SellerHomeActivity.class);
                            startActivity(intent);
                            finish();

                            loadingBar.dismiss();
                            Toast.makeText(AddNewProductactivity.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddNewProductactivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}