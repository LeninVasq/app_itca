package sv.edu.itca.itca_fepade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;


import cz.msebera.android.httpclient.Header;
import sv.edu.itca.itca_fepade.Validation.Login;
import sv.edu.itca.itca_fepade.Validation.Sign_up;


public class cymbals extends AppCompatActivity {

    private String url;
    private TextView name, price, available, id, amount,description;
    private ImageView img;
    private int amounts = 1 ;
    private float pricesutils = 0, price_multiplication;
    private NestedScrollView nestedScrollView;
    private ShimmerFrameLayout shimmerLayout;
    private Button btn_resevar, btnMinus,btnPlus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cymbals);

        name = findViewById(R.id.name_cymbals);
        price = findViewById(R.id.price_cymbals);
        available = findViewById(R.id.available_cymbals);
        id = findViewById(R.id.id_cymbals);
        amount = findViewById(R.id.amount);
        img = findViewById(R.id.restaurantImg);
        btn_resevar = findViewById(R.id.btn_reservar);
        btnMinus = findViewById(R.id.btnMinus);
        description = findViewById(R.id.description);
        btnPlus = findViewById(R.id.btnPlus);


        shimmerLayout = findViewById(R.id.shimmer_layout);
        nestedScrollView = findViewById(R.id.content_layout);


        btn_resevar.setEnabled(false);
        btn_resevar.setBackgroundColor(getResources().getColor(R.color.gray));
        if (getIntent() != null && getIntent().hasExtra("id_cymbals")) {
            String id_cymbals = getIntent().getStringExtra("id_cymbals");
            select_cymbals(id_cymbals);
        }
    }

    private void select_cymbals(String id_cymbals) {
        url = URL_APIS.BASE_URL + "app_filter_id/" + id_cymbals;
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        JSONObject item = json.getJSONObject("message");
                        String names = item.optString("nombre", "Nombre no disponible");
                        String desc = item.optString("descripcion", "Nombre no disponible");
                        String prices = item.optString("precio", "Nombre no disponible");
                        String avalibles = item.optString("cantidad_platos", "Nombre no disponible");
                        String ids = item.optString("id_menu", "Nombre no disponible");
                        String imgBase64 = item.optString("img", "");


                        pricesutils = Float.parseFloat(prices);

                        name.setText(names);
                        price.setText(prices);
                        description.setText(desc);
                        available.setText("Disponibles: "+avalibles);
                        id.setText(ids);
                        if (!imgBase64.isEmpty()) {
                            try {
                                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                if (decodedByte != null) {
                                    img.setImageBitmap(decodedByte);
                                } else {
                                    img.setImageResource(R.drawable.hogar);
                                }
                            } catch (IllegalArgumentException e) {
                                img.setImageResource(R.drawable.cuenta);
                            }
                        } else {
                            img.setImageResource(R.drawable.cuenta);
                        }
                        price_multiplication = amounts * pricesutils ;
                        btn_resevar.setText("Agregar " + amounts +" a la reserva  $"+ price_multiplication );
                        shimmerLayout.setVisibility(View.GONE);
                        shimmerLayout.stopShimmer();
                        nestedScrollView.setVisibility(View.VISIBLE);
                        btn_resevar.setEnabled(true);
                        btn_resevar.setBackgroundResource(R.drawable.btn_square);



                    } catch (JSONException e) {
                        Toast.makeText(cymbals.this, ""+e, Toast.LENGTH_SHORT).show();
                        Log.e("consulta", "Error al procesar el JSON de los items", e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(cymbals.this, "Error cargando los datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void plus(View view) {
        amounts += 1;
        price_multiplication = amounts * pricesutils ;
        amount.setText(String.valueOf(amounts));
        if (amounts == 5) {
            btnPlus.setEnabled(false);
        }
        btnMinus.setEnabled(true);
        btn_resevar.setText("Agregar " + amounts +" a la reserva  $"+ price_multiplication );

    }


    public void minus(View view) {
        amounts -= 1;
        if (amounts == 1) {
            btnMinus.setEnabled(false);
        }
        btnPlus.setEnabled(true);

        price_multiplication = amounts * pricesutils ;
        amount.setText(String.valueOf(amounts));
        btn_resevar.setText("Agregar " + amounts +" a la reserva  $"+ price_multiplication );

    }

    public void booking(View view) {

        SharedPreferences sharedPreferencesusu = getSharedPreferences("id", MODE_PRIVATE);
        String usuarioId = sharedPreferencesusu.getString("usuario_id", "");
        String id_menu = id.getText().toString();



        url = URL_APIS.BASE_URL + "reservas";
        RequestParams parametros = new RequestParams();
        parametros.put("id_usuario", usuarioId);
        parametros.put("id_menu", id_menu);
        parametros.put("cantidad", amounts);
        parametros.put("precio", pricesutils);

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject MiJson = new JSONObject(respuesta);
                    if (MiJson.has("message")) {
                        Intent main = new Intent(cymbals.this, MainActivity.class);
                        startActivity(main);
                        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        finish();
                        Toast.makeText(cymbals.this, "Se ha reservado exitosamenente", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(cymbals.this, "Error en JSON", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    try {
                        String respuesta = new String(responseBody);
                        JSONObject MiJson = new JSONObject(respuesta);
                        if (MiJson.has("excess")) {
                            String mensajeError = MiJson.getString("excess");
                            Toast.makeText(cymbals.this, mensajeError, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (MiJson.has("mesague")) {
                            String mensajeError = MiJson.getString("mesague");
                            Toast.makeText(cymbals.this, mensajeError, Toast.LENGTH_SHORT).show();
                            return;
                        }


                    } catch (JSONException e) {
                        Toast.makeText(cymbals.this, "Error en el json", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
