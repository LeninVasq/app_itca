package sv.edu.itca.itca_fepade;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import sv.edu.itca.itca_fepade.Item.See_Reservation;

public class Undelivered_orders extends AppCompatActivity {

    private static final String TAG = "UndeliveredOrders";
    private String url;
    private List<JSONObject> orderItemsList = new ArrayList<>();
    private See_Reservation adapter;
    private RecyclerView recyclerView;
    private LinearLayout messague;
    private LinearLayout total_sub;
    private TextView total;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undelivered_orders);

        recyclerView = findViewById(R.id.undelivered_orders); // RecyclerView en el layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new See_Reservation(this, orderItemsList);
        recyclerView.setAdapter(adapter);

        messague = findViewById(R.id.messague);
        total_sub = findViewById(R.id.total_sub);
        total = findViewById(R.id.total);
        messague.setVisibility(View.GONE);
        total_sub.setVisibility(View.GONE);
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);


        // Llamar a la función que carga los datos
        app_reservas();
    }



    public void see_reserve(View view) {

        TextView textView = findViewById(R.id.id_reserve_item);
        String id_reserve_iten = textView.getText().toString();


        Intent intent = new Intent(Undelivered_orders.this, see_reservation.class);
        intent.putExtra("id_reserve_item", id_reserve_iten);
        startActivity(intent);
    }




    public void delete_product(View view) {

        View parent = (View) view.getParent();
        TextView textView = parent.findViewById(R.id.id_reserve_item);
        String id_reserve_iten = textView.getText().toString();



        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_product);
        LinearLayout delete = dialog.findViewById(R.id.delete);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                url = URL_APIS.BASE_URL + "reservas_item/"+id_reserve_iten ;
                AsyncHttpClient cliente = new AsyncHttpClient();
                cliente.delete(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String respuesta = new String(responseBody);
                        try {
                            JSONObject MiJson = new JSONObject(respuesta);
                            if (MiJson.has("message")) {
                                Toast.makeText(Undelivered_orders.this, "lo elimino", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(Undelivered_orders.this,  "Error sdksdjb en JSON", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        Toast.makeText(Undelivered_orders.this, "Error en el json", Toast.LENGTH_SHORT).show();


                    }
                });
            }
        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    public void app_reservas() {
        SharedPreferences sharedPreferences = getSharedPreferences("id", MODE_PRIVATE);
        String usuarioId = sharedPreferences.getString("usuario_id", "");

        if (usuarioId.isEmpty()) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }
        orderItemsList.clear();
        url = URL_APIS.BASE_URL + "app_reservas/" + usuarioId;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (responseBody == null) {
                        Toast.makeText(Undelivered_orders.this, "Respuesta vacía del servidor", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String responseString = new String(responseBody, StandardCharsets.UTF_8);
                    JSONObject jsonResponse = new JSONObject(responseString);

                    double tota_sub = 0;
                    if (jsonResponse.has("message")) {
                        JSONArray messageArray = jsonResponse.getJSONArray("message");
                        if (messageArray.length() > 0) {
                            messague.setVisibility(View.GONE);
                            for (int i = 0; i < messageArray.length(); i++) {
                                JSONObject item = messageArray.getJSONObject(i);
                                double amount = item.getDouble("cantidad");
                                double price = item.getDouble("precio");
                                tota_sub += amount * price;
                                orderItemsList.add(messageArray.getJSONObject(i));
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            total_sub.setVisibility(View.VISIBLE);
                            total.setText("$ " + tota_sub);
                            adapter.notifyDataSetChanged();

                        } else {
                            messague.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Error procesando el JSON: ", e);
                    Toast.makeText(Undelivered_orders.this, "Error procesando datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = responseBody != null ? new String(responseBody, StandardCharsets.UTF_8) : error.getMessage();
                Log.e(TAG, "Error en la solicitud: " + statusCode + " - " + errorMessage, error);

                Toast.makeText(Undelivered_orders.this, "No se pudo conectar al servidor. Intente de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void returns(View view) {
        Intent intent = new Intent(Undelivered_orders.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
