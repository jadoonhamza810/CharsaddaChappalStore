package viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.charsaddachappalstore.R;

import buyer.ItemClickListner;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView txtProductName, txtProductCode, txtProductDescription, txtProductPrice, txtProductDiscount;
    public ImageView imageView;
    public ItemClickListner listner;


    public ProductViewHolder(View itemView)
    {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.product_item_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_item_name);
        txtProductCode = (TextView) itemView.findViewById(R.id.product_item_code);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_item_description);
        txtProductPrice = (TextView) itemView.findViewById(R.id.product_item_price);
        txtProductDiscount = (TextView) itemView.findViewById(R.id.product_item_discount);
    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
