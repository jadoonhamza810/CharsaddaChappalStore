package buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.charsaddachappalstore.MainActivity;
import com.example.charsaddachappalstore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Prevalent.Prevalent;

public class ConfirmOrderActivity extends AppCompatActivity {

    private EditText buyerName, buyerPhoneNumber, buyerAddress, buyerCity;
    private Button confirmOrderBtn;

    private String orderPrice = "";
    private String orderRandomKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        buyerName = (EditText) findViewById(R.id.buyer_name);
        buyerPhoneNumber = (EditText) findViewById(R.id.buyer_phone_number);
        buyerAddress = (EditText) findViewById(R.id.buyer_address);
        buyerCity = (EditText) findViewById(R.id.buyer_city);

        confirmOrderBtn = (Button) findViewById(R.id.confirm_order_btn);

        orderPrice = getIntent().getStringExtra("total price");
        orderPrice = getIntent().getStringExtra("total price");

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkingDetails();

            }
        });


    }

    private void checkingDetails() {
        if (TextUtils.isEmpty(buyerName.getText().toString())) {
            Toast.makeText(this, "Please write your name", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(buyerPhoneNumber.getText().toString())) {
            Toast.makeText(this, "Please enter your Phone number", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(buyerAddress.getText().toString())) {
            Toast.makeText(this, "Please enter your Shipment Address ", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(buyerCity.getText().toString())) {
            Toast.makeText(this, "Please enter your City Name", Toast.LENGTH_LONG).show();
        } else {
            confirmOrder();

        }
    }

    private void confirmOrder() {
        String saveCurrentDate, saveCurrentTime;
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getEmail());
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child(Prevalent.currentOnlineUser.getEmail());

        HashMap<String, Object> orderMap = new HashMap<>();

        orderMap.put("customerID", Prevalent.currentOnlineUser.getEmail());
        orderMap.put("order_price", orderPrice);
        orderMap.put("name", buyerName.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("phone_number", buyerPhoneNumber.getText().toString());
        orderMap.put("address", buyerAddress.getText().toString());
        orderMap.put("city", buyerCity.getText().toString());
        orderMap.put("orderStatus", "order placed");

        ordersRef.updateChildren(orderMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){

                            FirebaseDatabase.getInstance().getReference().child("Cart List")
                                    .child("Users View")
                                    .child(Prevalent.currentOnlineUser.getEmail())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                Toast.makeText(ConfirmOrderActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        }

                    }
                });

    }

}