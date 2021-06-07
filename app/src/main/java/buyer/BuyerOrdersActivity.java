package buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charsaddachappalstore.R;
import com.example.charsaddachappalstore.ShowOrderProductsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import Model.Orders;
import Model.Products;
import Prevalent.Prevalent;
import sellers.SellerOrdersActivity;
import viewholder.OrdersViewHolder;

public class BuyerOrdersActivity extends AppCompatActivity {
    public TextView orderCustomerName,orderCustomerPhoneNumber,orderPrice,orderCustomerAddress,orderDateTime;
    public Button orderShowProducts;
    private RelativeLayout showOrdersLayout;

    private DatabaseReference buyerOrdersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_orders);

        orderCustomerName=(TextView)findViewById(R.id.orders_user_name);
        orderCustomerPhoneNumber=(TextView)findViewById(R.id.orders_phone_number);
        orderPrice=(TextView)findViewById(R.id.orders_total_price);
        orderCustomerAddress=(TextView)findViewById(R.id.orders_address_city);
        orderDateTime=(TextView)findViewById(R.id.orders_date_time);
        orderShowProducts=(Button) findViewById(R.id.orders_show_products);

        showOrdersLayout=(RelativeLayout)findViewById(R.id.buyer_orders_relative_layout2);

        buyerOrdersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getEmail());

    }

    @Override
    protected void onStart() {
        super.onStart();

        buyerOrdersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Orders orders=snapshot.getValue(Orders.class);

                    orderCustomerName.setText("customer: "+orders.getName());
                    orderCustomerPhoneNumber.setText("Contact NO# "+orders.getPhone_number());
                    orderPrice.setText("Amount = " + orders.getOrder_price() + " Rupees");
                    orderCustomerAddress.setText("Shipping Address: "+ orders.getAddress());
                    orderDateTime.setText("Ordered At: " +orders.getDate()+"    "+orders.getTime());

                   orderShowProducts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent1 = new Intent(BuyerOrdersActivity.this, ShowOrderProductsActivity.class);
                            intent1.putExtra("customerID",orders.getCustomerID());
                            startActivity(intent1);
                        }
                    });
                }
                else{
                    showOrdersLayout.setVisibility(View.GONE);
                    Toast.makeText(BuyerOrdersActivity.this, "No Orders Placed Yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}