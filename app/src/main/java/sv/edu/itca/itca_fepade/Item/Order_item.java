package sv.edu.itca.itca_fepade.Item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

import sv.edu.itca.itca_fepade.R;

public class Order_item extends RecyclerView.Adapter<Order_item.OrderViewHolder> {

    private List<JSONObject> itemsList;
    private Context context;

    public Order_item(Context context, List<JSONObject> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.activity_order_item, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        try {
            JSONObject item = itemsList.get(position);

            // Obtener datos del JSON
            String id = item.optString("id_reservas", "ID no disponible");
            String id_item = item.optString("id_reserva_item", "ID no disponible");
            String name = item.optString("nombre", "Nombre no disponible");
            int amount = item.optInt("cantidad", 0);
            Double price = item.optDouble("precio", 0);
            String img = item.optString("img", "");

            holder.id.setText(id_item);
            holder.details.setText( amount+" articulos • $"+amount * price);
            holder.name.setText(name);



            // Decodificar la imagen desde Base64
            if (!img.isEmpty()) {
                byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedByte != null) {
                    holder.img.setImageBitmap(decodedByte);
                } else {
                    Log.e("Img", "Error al decodificar la imagen");
                    holder.img.setImageResource(R.drawable.hogar); // Cambia por un recurso predeterminado
                }
            } else {
                holder.img.setImageResource(R.drawable.cuenta);
            }
        } catch (Exception e) {
            Log.e("Order_item", "Error al procesar los datos del adaptador", e);
        }
    }

    @Override
    public int getItemCount() {
        return itemsList != null ? itemsList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView id, name, details;
        ImageView img;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id_reserve_item);
            details = itemView.findViewById(R.id.orderDetails);
            name = itemView.findViewById(R.id.restaurantName);
            img = itemView.findViewById(R.id.restaurantLogo);
        }
    }
}
