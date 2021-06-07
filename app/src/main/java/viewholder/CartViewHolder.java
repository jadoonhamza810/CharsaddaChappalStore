package viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.charsaddachappalstore.R;

import org.jetbrains.annotations.NotNull;

import buyer.ItemClickListner;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private ItemClickListner itemClickListner;
    public TextView cartItemName,cartItemStock,cartItemPrice,cartItemDiscount,cartItemQuantity;
    public ImageView cartItemImage;


    public CartViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);


        cartItemName=itemView.findViewById(R.id.cart_item_name);
        cartItemStock=itemView.findViewById(R.id.cart_item_stock);
        cartItemPrice=itemView.findViewById(R.id.cart_item_price);
        cartItemDiscount=itemView.findViewById(R.id.cart_item_discount);
        cartItemQuantity=itemView.findViewById(R.id.cart_item_quantity);

        cartItemImage=itemView.findViewById(R.id.cart_item_image);

    }

    @Override
    public void onClick(View v) {

        itemClickListner.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}
