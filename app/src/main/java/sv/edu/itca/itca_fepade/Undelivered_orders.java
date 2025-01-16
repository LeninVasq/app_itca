package sv.edu.itca.itca_fepade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import sv.edu.itca.itca_fepade.Item.Order_item;

public class Undelivered_orders extends AppCompatActivity {

    private static final String TAG = "UndeliveredOrders";
    private String url;
    private List<JSONObject> orderItemsList = new ArrayList<>();
    private Order_item adapter;
    private RecyclerView recyclerView;
    private LinearLayout messague;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undelivered_orders);

        recyclerView = findViewById(R.id.undelivered_orders); // RecyclerView en el layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Order_item(this, orderItemsList);
        recyclerView.setAdapter(adapter);

        messague = findViewById(R.id.messague);
        messague.setVisibility(View.GONE);
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);

        // Llamar a la función que carga los datos
        app_reservas();
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

                    if (jsonResponse.has("message")) {
                        JSONArray messageArray = jsonResponse.getJSONArray("message");
                        if (messageArray.length() > 0) {
                            messague.setVisibility(View.GONE);
                            for (int i = 0; i < messageArray.length(); i++) {
                                orderItemsList.add(messageArray.getJSONObject(i));
                            }
                            recyclerView.setVisibility(View.VISIBLE);
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
