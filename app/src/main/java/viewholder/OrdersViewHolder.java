package viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.charsaddachappalstore.R;

import org.jetbrains.annotations.NotNull;

public class OrdersViewHolder extends RecyclerView.ViewHolder
{
    public TextView orderCustomerName,orderCustomerPhoneNumber,orderPrice,orderCustomerAddress,orderDateTime;
    public Button orderShowProducts;

    public OrdersViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        orderCustomerName=itemView.findViewById(R.id.orders_user_name);
        orderCustomerPhoneNumber=itemView.findViewById(R.id.orders_phone_number);
        orderPrice=itemView.findViewById(R.id.orders_total_price);
        orderCustomerAddress=itemView.findViewById(R.id.orders_address_city);
        orderDateTime=itemView.findViewById(R.id.orders_date_time);
        orderShowProducts=itemView.findViewById(R.id.orders_show_products);
    }

}
