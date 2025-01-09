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

import sv.edu.itca.itca_fepade.R;

public class header_category_name  extends RecyclerView.Adapter<header_category_name.Category_name> {

    private List<JSONObject> Items_cymbalsList;



    private Context context;

    public header_category_name(Context context, List<JSONObject> Items_cymbalsList) {
        this.context = context;
        this.Items_cymbalsList = Items_cymbalsList;
    }

    @NonNull
    @Override
    public header_category_name.Category_name onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.header_category_name, parent, false);
        return new header_category_name.Category_name(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull header_category_name.Category_name holder, int position) {
        try {
            JSONObject Items_cymbals = Items_cymbalsList.get(position);
            String category_name = Items_cymbals.optString("nombre_categoria", "Nombre");


            holder.category_name.setText(category_name);

        } catch (Exception e) {
            Log.e("Items_cymbals", "Error al procesar los datos del adaptador", e);
        }
    }


    @Override
    public int getItemCount() {
        return Items_cymbalsList != null ? Items_cymbalsList.size() : 0;
    }

    public static class Category_name extends RecyclerView.ViewHolder {
        TextView category_name;

        public Category_name(View itemView) {
            super(itemView);

            category_name = itemView.findViewById(R.id.category_name);

        }
    }
}
