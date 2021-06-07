package buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charsaddachappalstore.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import Model.Cart;
import Prevalent.Prevalent;
import viewholder.CartViewHolder;

public class CartActivity extends AppCompatActivity {

    private TextView cartTotalPrice;
    private Button cartNextBtn,cartBackBtn;

    private RecyclerView cartItemsView;
    private RecyclerView.LayoutManager layoutManager;

    private int totalPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartTotalPrice=(TextView)findViewById(R.id.cart_total_price);

        cartNextBtn=(Button) findViewById(R.id.cart_next_btn);
        cartBackBtn=(Button) findViewById(R.id.cart_back_btn);

        cartItemsView=findViewById(R.id.cart_items_view);
        cartItemsView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        cartItemsView.setLayoutManager(layoutManager);

        cartNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, ConfirmOrderActivity.class);
                intent.putExtra("total price", String.valueOf(totalPrice));
                startActivity(intent);
                finish();
            }
        });

        cartBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List").child("Users View");
        final DatabaseReference cartListSellersViewRef= FirebaseDatabase.getInstance().getReference().child("Cart List").child("Sellers View");

        cartListRef.child(Prevalent.currentOnlineUser.getEmail()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(!snapshot.exists())
                        {
                            cartNextBtn.setVisibility(View.GONE);
                            cartBackBtn.setVisibility(View.VISIBLE);

                            Toast.makeText(CartActivity.this, "No Items in cart", Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                }
        );

        FirebaseRecyclerOptions<Cart> options=new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child(Prevalent.currentOnlineUser.getEmail()),Cart.class)
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

                int oneItemTotalPrice=(Integer.valueOf(model.getPrice())) * (Integer.valueOf(model.getQuantity()));
                totalPrice=totalPrice + oneItemTotalPrice;

                cartTotalPrice.setText("Total Price= Rs "+ String.valueOf(totalPrice));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CharSequence options[]=new CharSequence[]{
                                "Edit",
                                "Remove"

                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {

                                 if (which==0)
                                 {
                                     Intent intent2 = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                     intent2.putExtra("pid", model.getPid());
                                     intent2.putExtra("quantity",model.getQuantity());
                                     startActivity(intent2);
                                 }
                                 if (which==1)
                                 {
                                     cartListRef.child(Prevalent.currentOnlineUser.getEmail())
                                             .child(model.getPid()).removeValue()
                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull @NotNull Task<Void> task) {

                                                     if(task.isSuccessful()){

                                                         cartListSellersViewRef.child(Prevalent.currentOnlineUser.getEmail())
                                                                 .child(model.getPid()).removeValue()
                                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                     @Override
                                                                     public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                         if (task.isSuccessful()){
                                                                             Toast.makeText(CartActivity.this, "Item Removed Successfully", Toast.LENGTH_SHORT).show();

                                                                             Intent intent = new Intent(CartActivity.this, CartActivity.class);
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
                         });
                        builder.show();

                    }

                });

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

        cartItemsView.setAdapter(adapter);
        adapter.startListening();

    }
}