package com.example.charsaddachappalstore;

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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import Model.Cart;
import Prevalent.Prevalent;
import buyer.CartActivity;
import buyer.ProductDetailsActivity;
import viewholder.CartViewHolder;

public class ShowOrderProductsActivity extends AppCompatActivity {

    private RecyclerView ordersView;
    private  RecyclerView.LayoutManager layoutManager;
    private DatabaseReference sellerOrderProductsRef;
    private String customerID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order_products);


        customerID=getIntent().getStringExtra("customerID");

        ordersView=findViewById(R.id.seller_orders_products_view);
        ordersView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ordersView.setLayoutManager(layoutManager);


        sellerOrderProductsRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Sellers View").child(customerID);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options=new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(sellerOrderProductsRef,Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull CartViewHolder holder, int position, @NonNull @NotNull Cart model)
            {

                holder.cartItemName.setText(model.getPname());
                holder.cartItemStock.setText(model.getStock());
                holder.cartItemPrice.setText("Price: Rs "+ model.getPrice());
                holder.cartItemDiscount.setText("Discount in %: "+model.getDiscount());
                holder.cartItemQuantity.setText("Quantity: "+model.getQuantity());

                Picasso.get().load(model.getImage()).into(holder.cartItemImage);

            }

            @NonNull
            @NotNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder=new CartViewHolder(view);
                return  holder;

            }
        };

        ordersView.setAdapter(adapter);
        adapter.startListening();

    }
}