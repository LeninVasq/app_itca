package sv.edu.itca.itca_fepade.Item;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

import sv.edu.itca.itca_fepade.R;

public class Category_cymbals extends RecyclerView.Adapter<Category_cymbals.PublicacionViewHolder> {

    private List<JSONObject> Items_cymbalsList;


    private Context context;

    public Category_cymbals(Context context, List<JSONObject> Items_cymbalsList) {
        this.context = context;
        this.Items_cymbalsList = Items_cymbalsList;
    }

    @NonNull
    @Override
    public Category_cymbals.PublicacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.activity_category_cymbals, parent, false);
        return new Category_cymbals.PublicacionViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull Category_cymbals.PublicacionViewHolder holder, int position) {
        try {
            JSONObject Items_cymbals = Items_cymbalsList.get(position);
            String name = Items_cymbals.optString("nombre", "Nombre no disponible");
            String img = Items_cymbals.optString("foto", "");
            String id = Items_cymbals.optString("id_categoria_menu", "");

            holder.name.setText(name);
            holder.id.setText(id);


            if (!img.isEmpty()) {
                byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (decodedByte != null) {
                    holder.img.setImageBitmap(decodedByte);
                } else {
                    Log.e("Img", "Error al decodificar la imagen");
                    holder.img.setImageResource(R.drawable.hogar);
                }
            } else {
                holder.img.setImageResource(R.drawable.cuenta);
            }
        } catch (Exception e) {
            Log.e("Items_cymbals", "Error al procesar los datos del adaptador", e);
        }
    }


    @Override
    public int getItemCount() {
        return Items_cymbalsList != null ? Items_cymbalsList.size() : 0;
    }

    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, id, available;
        ImageView img;

        public PublicacionViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_category);
            img = itemView.findViewById(R.id.category_menu_img);
            id = itemView.findViewById(R.id.id_category);

        }
    }
}