package sv.edu.itca.itca_fepade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
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
import sv.edu.itca.itca_fepade.Item.item_order_p;

public class see_reservation extends AppCompatActivity {

    private String url;
    private List<JSONObject> item_orderp = new ArrayList<>();
    private item_order_p adapter;
    private RecyclerView recyclerView;
    private TextView no_order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_see_reservation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null && getIntent().hasExtra("delivered_orders_id")) {
            String delivered_orders_id = getIntent().getStringExtra("delivered_orders_id");
            select_id_reserva_item(delivered_orders_id);

        }

        recyclerView = findViewById(R.id.no_orders); // RecyclerView en el layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new item_order_p(this, item_orderp);
        recyclerView.setAdapter(adapter);

        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);

        no_order = findViewById(R.id.id_o);


    }


    public void orders(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void select_id_reserva_item(String delivered_orders_id) {
        url = URL_APIS.BASE_URL + "app_reservas_item/" + delivered_orders_id;
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        if (responseBody == null) {
                            Toast.makeText(see_reservation.this, "Respuesta vacía del servidor", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String responseString = new String(responseBody, StandardCharsets.UTF_8);
                        JSONObject jsonResponse = new JSONObject(responseString);

                        double tota_sub = 0;
                        if (jsonResponse.has("message")) {
                            JSONArray messageArray = jsonResponse.getJSONArray("message");
                            String id_order =  "";
                            if (messageArray.length() > 0) {
                                for (int i = 0; i < messageArray.length(); i++) {
                                    JSONObject item = messageArray.getJSONObject(i);

                                    id_order = item.optString("id_reservas");

                                    item_orderp.add(messageArray.getJSONObject(i));
                                }

                                no_order.setText("N.o de pedido: "+ id_order);

                                recyclerView.setVisibility(View.VISIBLE);

                                adapter.notifyDataSetChanged();

                            }
                        }


                    } catch (JSONException e) {
                        Toast.makeText(see_reservation.this, ""+e, Toast.LENGTH_SHORT).show();
                        Log.e("consulta", "Error al procesar el JSON de los items", e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(see_reservation.this, "" + statusCode, Toast.LENGTH_LONG).show();
            }
        });
    }





}