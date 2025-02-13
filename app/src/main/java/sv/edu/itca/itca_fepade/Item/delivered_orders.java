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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import sv.edu.itca.itca_fepade.R;

public class delivered_orders extends RecyclerView.Adapter<delivered_orders.PublicacionViewHolder> {

    private List<JSONObject> Items_cymbalsList;


    private Context context;

    public delivered_orders(Context context, List<JSONObject> Items_cymbalsList) {
        this.context = context;
        this.Items_cymbalsList = Items_cymbalsList;
    }

    @NonNull
    @Override
    public delivered_orders.PublicacionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.activity_delivered_orders, parent, false);
        return new delivered_orders.PublicacionViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull delivered_orders.PublicacionViewHolder holder, int position) {
        try {
            JSONObject Items_cymbals = Items_cymbalsList.get(position);
            String name = Items_cymbals.optString("fecha_entrega", "Nombre no disponible");
            String price = Items_cymbals.optString("precio", "");
            String amount = Items_cymbals.optString("cantidad", "");
            String id = Items_cymbals.optString("id_reservas", "");


            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = inputFormat.parse(name);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a"); 

            String formattedTime = outputFormat.format(date);


            holder.name.setText("Fecha Entrega " + formattedTime);
            holder.details.setText(amount + " articulos • $"+price);
            holder.id.setText(id);



        } catch (Exception e) {
            Log.e("Items_cymbals", "Error al procesar los datos del adaptador", e);
        }
    }


    @Override
    public int getItemCount() {
        return Items_cymbalsList != null ? Items_cymbalsList.size() : 0;
    }

    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        TextView name, details, id, available;


        public PublicacionViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.delivered_orders_date);
            details = itemView.findViewById(R.id.delivered_orders_Details);
            id = itemView.findViewById(R.id.delivered_orders_id);

        }
    }
}
