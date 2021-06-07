package sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charsaddachappalstore.MainActivity;
import com.example.charsaddachappalstore.R;
import com.example.charsaddachappalstore.SettingsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import Model.Products;
import Prevalent.Prevalent;
import buyer.CartActivity;
import buyer.HomeActivity;
import buyer.ProductDetailsActivity;
import io.paperdb.Paper;
import viewholder.ProductViewHolder;

public class SellerHomeActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private TextView sellerName;
    private ImageView sellerProfileImage;
    private FloatingActionButton sellerAddProductBtn;

    private DatabaseReference sellerProductsRef;
    private String sellerID="";

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        myToolbar = (Toolbar) findViewById(R.id.seller_toolbar);
        setSupportActionBar(myToolbar);


        sellerProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        sellerID=Prevalent.currentOnlineUser.getEmail();

        Paper.init(this);

        sellerName = (TextView)findViewById(R.id.seller_name);
        sellerProfileImage=(ImageView)findViewById(R.id.seller_profile_image);

        sellerName.setText(Prevalent.currentOnlineUser.getName());
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(sellerProfileImage);

        recyclerView = findViewById(R.id.seller_home_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        sellerAddProductBtn=(FloatingActionButton)findViewById(R.id.seller_home_add_button);

        sellerAddProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent4 = new Intent(SellerHomeActivity.this, SellerCategoryActivity.class);
                startActivity(intent4);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                       .setQuery(sellerProductsRef.orderByChild("sellerID")
                               .startAt(Prevalent.currentOnlineUser.getEmail())
                               .endAt(Prevalent.currentOnlineUser.getEmail()), Products.class)
                                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model)
                    {

                            holder.txtProductName.setText(model.getPname());
                            holder.txtProductDescription.setText(model.getDescription());
                            holder.txtProductPrice.setText("Price = " + model.getPrice() + " Rupees");
                            holder.txtProductCode.setText("Code: "+ model.getCode());
                            holder.txtProductDiscount.setText("Discount: " +model.getDiscount()+ "%");
                            Picasso.get().load(model.getImage1()).into(holder.imageView);


                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    CharSequence options[]=new CharSequence[]{
                                            "Edit",
                                            "Remove"

                                    };
                                    AlertDialog.Builder builder=new AlertDialog.Builder(SellerHomeActivity.this);
                                    builder.setTitle("Cart Options:");

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (which==0)
                                            {
                                                Intent intent2 = new Intent(SellerHomeActivity.this, AddNewProductactivity.class);
                                                intent2.putExtra("pid", model.getPid());
                                                intent2.putExtra("category", model.getCategory());
                                                startActivity(intent2);
                                            }
                                            if (which==1)
                                            {
                                                sellerProductsRef.child(model.getPid()).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(SellerHomeActivity.this, "Item Removed Successfully", Toast.LENGTH_SHORT).show();

                                                                    Intent intent = new Intent(SellerHomeActivity.this, SellerHomeActivity.class);
                                                                    startActivity(intent);
                                                                    finish();

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
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;

                    }


                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.seller_option_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.seller_menu_orders:
                Intent intent3 = new Intent(SellerHomeActivity.this, SellerOrdersActivity.class);
                startActivity(intent3);
                return true;
            case R.id.seller_menu_settings:
                Intent intent1 = new Intent(SellerHomeActivity.this, SettingsActivity.class);
                intent1.putExtra("parentDBname", "Sellers");
                startActivity(intent1);
                return true;

            case R.id.seller_menu_logout:
                Paper.book().destroy();

                Intent intent = new Intent(SellerHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}