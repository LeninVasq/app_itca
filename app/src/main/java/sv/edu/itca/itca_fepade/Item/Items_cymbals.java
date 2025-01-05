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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sv.edu.itca.itca_fepade.R;

public class Items_cymbals extends RecyclerView.Adapter<Items_cymbals.PublicacionViewHolder> {

    private List<JSONObject> Items_cymbalsList;
    private Context context;

    public Items_cymbals(Context context, List<JSONObject> Items_cymbalsList) {
        this.context = context;
        this.Items_cymbalsList = Items_cymbalsList;
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.activity_items_cymbals, parent, false);
        return new PublicacionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        try {
            JSONObject Items_cymbals = Items_cymbalsList.get(position);
            String name = Items_cymbals.optString("nombre", "Nombre no disponible");
            String price = Items_cymbals.optString("precio", "Precio no disponible");
            String quantity = Items_cymbals.optString("cantidad_platos", "Cantidad no disponible");
            String img = Items_cymbals.optString("img", "");

            holder.name.setText(name);
            holder.price.setText(price);

            int quantityValue = Integer.parseInt(quantity);

            if (quantityValue >= 1) {
                holder.available.setText("Cantidad: " + quantity);
            }else{
                holder.available.setText("Agotado ");
            }

            if (!img.isEmpty()) {
                byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                if (decodedByte != null) {
                    //holder.img.setImageBitmap(decodedByte);
                } else {
                    Log.e("Img", "Error al decodificar la imagen");
                }
            } else {
                Log.e("img", "Imagen no disponible");
                //holder.img.setImageResource(R.drawable.hogar);
            }
        } catch (Exception e) {
            Log.e("PublicacionesAdapter", "Error al obtener datos de publicación", e);
        }
    }


    @Override
    public int getItemCount() {
        return Items_cymbalsList != null ? Items_cymbalsList.size() : 0;
    }

    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, id,available;
        ImageView img;

        public PublicacionViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            available = itemView.findViewById(R.id.available);
            img = itemView.findViewById(R.id.restaurantImage);
           // id = itemView.findViewById(R.id.ids);
        }
    }
}
