package buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.charsaddachappalstore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Model.Products;
import Prevalent.Prevalent;

public class ProductDetailsActivity extends AppCompatActivity {
    private ViewFlipper productImageViewFlipper;
    private ImageView productImage1,productImage2,productImage3;
    private TextView productStock,productName,productCode,productSize,productColor,productPrice,productDiscount,productDescription;

    private ElegantNumberButton productQuantityBtn;
   private Button productAddToCartBtn;

    private String productID="";
    private String productQuantity="1";
    private String sellerID="";

    private String orderStatus="not placed";

    private String productImageForCart;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productImageViewFlipper = (ViewFlipper) findViewById(R.id.product_details_image_flipper);
        productImage1=(ImageView)findViewById(R.id.product_details_image1);
        productImage2=(ImageView)findViewById(R.id.product_details_image2);
        productImage3=(ImageView)findViewById(R.id.product_details_image3);

        productStock=(TextView)findViewById(R.id.product_details_stock);

        productName=(TextView)findViewById(R.id.product_details_name);
        productCode=(TextView)findViewById(R.id.product_details_code);
        productColor=(TextView)findViewById(R.id.product_details_color);
        productSize=(TextView)findViewById(R.id.product_details_size);
        productPrice=(TextView)findViewById(R.id.product_details_price);
        productDiscount=(TextView)findViewById(R.id.product_details_discount);
        productDescription=(TextView)findViewById(R.id.product_details_description);

        productQuantityBtn=(ElegantNumberButton) findViewById(R.id.product_details_quantity_button);
        productAddToCartBtn=(Button) findViewById(R.id.product_details_add_to_cart);


        productID=getIntent().getStringExtra("pid");
        productQuantity=getIntent().getStringExtra("quantity");

        getProductdetails(productID);

        productAddToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (!orderStatus.equals("not placed")){

                   Toast.makeText(ProductDetailsActivity.this, "You can Add more items to cart, Once You Receive your Previous Placed Order!", Toast.LENGTH_LONG).show();

               }else{
                   addToCartList();
               }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        checkOrderStatus();
    }

    private void checkOrderStatus() {

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getEmail());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String orderState=snapshot.child("orderStatus").getValue().toString();
                    orderStatus=orderState;
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void addToCartList()
    {
        String saveCurrentDate, saveCurrentTime;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final  DatabaseReference cartRef=FirebaseDatabase.getInstance().getReference().child("Cart List");


        HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("discount", productDiscount.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("quantity", productQuantityBtn.getNumber());
        cartMap.put("stock", productStock.getText().toString());
        cartMap.put("image",productImageForCart);
        cartMap.put("sellerID",sellerID);

        cartRef.child("Users View").child(Prevalent.currentOnlineUser.getEmail()).child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            cartRef.child("Sellers View").child(Prevalent.currentOnlineUser.getEmail()).child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ProductDetailsActivity.this, "Added To Cart Successfully", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            }
                                    });
                        }
                    }
                });
    }


    private void getProductdetails(String productID)
    {
        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products product=snapshot.getValue(Products.class);

                    productName.setText(product.getPname());
                    productColor.setText(product.getColor());
                    productSize.setText(product.getSize());
                    productPrice.setText(product.getPrice());
                    productDiscount.setText(product.getDiscount());
                    productDescription.setText(product.getDescription());
                    productStock.setText(product.getStock());
                    productQuantityBtn.setNumber(productQuantity);

                    sellerID=product.getSellerID();

                    Picasso.get().load(product.getImage1()).into(productImage1);

                    productImageForCart=product.getImage1();

                    Picasso.get().load(product.getImage2()).into(productImage2);
                    Picasso.get().load(product.getImage3()).into(productImage3);

                    productImageViewFlipper.startFlipping();

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}