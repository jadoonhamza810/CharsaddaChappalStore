package sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.charsaddachappalstore.MainActivity;
import com.example.charsaddachappalstore.R;
import com.example.charsaddachappalstore.SettingsActivity;
import com.squareup.picasso.Picasso;

import Prevalent.Prevalent;
import io.paperdb.Paper;

public class SellerCategoryActivity extends AppCompatActivity {

    private ImageView peshawariChappal, charsaddaChappal, kaptaanChappal, quettaChappal;
    private  ImageView noroziChappal, leatherJacket,handMade, quettaShikariChappal;
   private Toolbar myToolbar;

    private  TextView sellerName;
    private ImageView sellerProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_category);


        myToolbar = (Toolbar) findViewById(R.id.category_seller_toolbar);
        setSupportActionBar(myToolbar);

        peshawariChappal = (ImageView) findViewById(R.id.category_peshawari);
        charsaddaChappal = (ImageView) findViewById(R.id.category_charsadda);
        kaptaanChappal = (ImageView) findViewById(R.id.category_kaptaan);
        quettaChappal = (ImageView) findViewById(R.id.category_quetta);
        noroziChappal = (ImageView) findViewById(R.id.category_norozi);
        leatherJacket = (ImageView) findViewById(R.id.category_leather);
        handMade = (ImageView) findViewById(R.id.category_handmade);
        quettaShikariChappal = (ImageView) findViewById(R.id.category_shikari);

        Paper.init(this);

        TextView sellerName = (TextView) findViewById(R.id.category_seller_name);
        sellerProfileImage=(ImageView)findViewById(R.id.category_seller_profile_image);

        sellerName.setText(Prevalent.currentOnlineUser.getName());
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(sellerProfileImage);

        charsaddaChappal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SellerCategoryActivity.this, AddNewProductactivity.class);
                intent.putExtra("category", "Charsadda Chappal");
                intent.putExtra("pid", "");
                startActivity(intent);
            }
        });


        peshawariChappal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SellerCategoryActivity.this, AddNewProductactivity.class);
                intent.putExtra("category", "Peshawari Chappal");
                intent.putExtra("pid", "");
                startActivity(intent);
            }
        });


        quettaChappal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SellerCategoryActivity.this, AddNewProductactivity.class);
                intent.putExtra("category", "Quetta Chappal");
                intent.putExtra("pid", "");
                startActivity(intent);
            }
        });


        noroziChappal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SellerCategoryActivity.this, AddNewProductactivity.class);
                intent.putExtra("category", "Norozi Chappal");
                intent.putExtra("pid", "");
                startActivity(intent);
            }
        });


        kaptaanChappal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SellerCategoryActivity.this, AddNewProductactivity.class);
                intent.putExtra("category", "Kaptaan Chappal");
                intent.putExtra("pid", "");
                startActivity(intent);
            }
        });

        quettaShikariChappal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SellerCategoryActivity.this, AddNewProductactivity.class);
                intent.putExtra("category", "Shikari Chappal");
                intent.putExtra("pid", "");
                startActivity(intent);
            }
        });


        leatherJacket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SellerCategoryActivity.this, AddNewProductactivity.class);
                intent.putExtra("category", "Leather jacket");
                intent.putExtra("pid", "");
                startActivity(intent);
            }
        });


        handMade.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(SellerCategoryActivity.this, AddNewProductactivity.class);
                intent.putExtra("category", "Hand made");
                intent.putExtra("pid", "");
                startActivity(intent);
            }
        });


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
                Intent intent3 = new Intent(SellerCategoryActivity.this, SellerOrdersActivity.class);
                startActivity(intent3);
                return true;
            case R.id.seller_menu_settings:
                Intent intent1 = new Intent(SellerCategoryActivity.this, SettingsActivity.class);
                intent1.putExtra("parentDBname", "Sellers");
                startActivity(intent1);
                return true;
            case R.id.seller_menu_logout:
                Paper.book().destroy();

                Intent intent = new Intent(SellerCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}