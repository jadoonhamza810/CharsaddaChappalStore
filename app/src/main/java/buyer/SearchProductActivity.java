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
import android.widget.EditText;

import com.example.charsaddachappalstore.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Products;
import viewholder.ProductViewHolder;

public class SearchProductActivity extends AppCompatActivity {

    private EditText searchProductName;
    private Button seacrhBtn;

    private RecyclerView searchProductRecyclerView;

    private String inputtedProductName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        searchProductName=(EditText)findViewById(R.id.search_edit_text);
        seacrhBtn=(Button)findViewById(R.id.search_product_btn);
        searchProductRecyclerView=(RecyclerView) findViewById(R.id.search_product_recycler_view);
        searchProductRecyclerView.setLayoutManager(new LinearLayoutManager(SearchProductActivity.this));

        seacrhBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inputtedProductName=searchProductName.getText().toString();
                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference productsRef= FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(productsRef.orderByChild("pname")
                                .startAt(inputtedProductName), Products.class)
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
                                Intent intent2 = new Intent(SearchProductActivity.this, ProductDetailsActivity.class);
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
        searchProductRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}