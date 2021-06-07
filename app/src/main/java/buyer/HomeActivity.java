package buyer;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.charsaddachappalstore.MainActivity;
import com.example.charsaddachappalstore.R;
import com.example.charsaddachappalstore.SettingsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Products;
import Prevalent.Prevalent;
import io.paperdb.Paper;
import sellers.SellerHomeActivity;
import sellers.SellerOrdersActivity;
import viewholder.ProductViewHolder;


public class HomeActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private TextView userName;
    private ImageView userProfileImage;
    private FloatingActionButton userCartBtn;

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myToolbar = (Toolbar) findViewById(R.id.user_toolbar);
        setSupportActionBar(myToolbar);


        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Paper.init(this);

        userName = (TextView)findViewById(R.id.user_name);
        userProfileImage=(ImageView)findViewById(R.id.user_profile_image);

        userName.setText(Prevalent.currentOnlineUser.getName());
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(userProfileImage);

        recyclerView = findViewById(R.id.product_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        userCartBtn=(FloatingActionButton)findViewById(R.id.product_cart_button);

        userCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent4);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef, Products.class)
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
                                Intent intent2 = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                                intent2.putExtra("pid", model.getPid());
                                intent2.putExtra("quantity", "1");
                                startActivity(intent2);

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
        inflater.inflate(R.menu.user_option_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_menu_search:
                Intent intent3 = new Intent(HomeActivity.this, SearchProductActivity.class);
                startActivity(intent3);
                return true;
            case R.id.user_menu_orders:
                Intent intent4 = new Intent(HomeActivity.this, BuyerOrdersActivity.class);
                startActivity(intent4);
                return true;
            case R.id.user_menu_settings:
                Intent intent1 = new Intent(HomeActivity.this, SettingsActivity.class);
                intent1.putExtra("parentDBname", "Users");
                startActivity(intent1);
                return true;

            case R.id.user_menu_logout:
                Paper.book().destroy();

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}