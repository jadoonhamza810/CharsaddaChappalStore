package sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.charsaddachappalstore.R;
import com.example.charsaddachappalstore.ShowOrderProductsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import Model.Orders;
import Prevalent.Prevalent;
import buyer.CartActivity;
import buyer.ProductDetailsActivity;
import viewholder.OrdersViewHolder;

public class SellerOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersView;
    private DatabaseReference sellerOrdersRef,cartSelllersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_orders);

        ordersView=(RecyclerView)findViewById(R.id.seller_orders_view);
        ordersView.setLayoutManager(new LinearLayoutManager(this));

        sellerOrdersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        cartSelllersView = FirebaseDatabase.getInstance().getReference().child("Cart List")
                .child("Sellers View");

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                        .setQuery(sellerOrdersRef, Orders.class)
                        .build();

        FirebaseRecyclerAdapter<Orders, OrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<Orders, OrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull OrdersViewHolder holder, int position, @NonNull @NotNull Orders model) {

                holder.orderCustomerName.setText("customer: "+model.getName());
                holder.orderCustomerPhoneNumber.setText("Contact NO# "+model.getPhone_number());
                holder.orderPrice.setText("Amount = " + model.getOrder_price() + " Rupees");
                holder.orderCustomerAddress.setText("Shipping Address: "+ model.getAddress());
                holder.orderDateTime.setText("Ordered At: " +model.getDate()+"    "+model.getTime());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CharSequence options[]=new CharSequence[]{
                                "YES",
                                "NO"

                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(SellerOrdersActivity.this);
                        builder.setTitle("Have You Delivered this Order?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0)
                                {
                                    String customerID=getRef(position).getKey();

                                    removeOrder(customerID);
                                }
                                if (which==1)
                                {
                                    finish();
                                }
                            }
                        });
                        builder.show();

                    }

                });

                holder.orderShowProducts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String customerID=getRef(position).getKey();

                        Intent intent1 = new Intent(SellerOrdersActivity.this, ShowOrderProductsActivity.class);
                        intent1.putExtra("customerID",customerID);
                        startActivity(intent1);
                    }
                });
            }

            @NonNull
            @NotNull
            @Override
            public OrdersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                OrdersViewHolder holder = new OrdersViewHolder(view);
                return holder;
            }
        };
        ordersView.setAdapter(adapter);
        adapter.startListening();

    }

    private void removeOrder(String customerID)
    {
        cartSelllersView.child(customerID).removeValue();
        sellerOrdersRef.child(customerID).removeValue();

    }
}