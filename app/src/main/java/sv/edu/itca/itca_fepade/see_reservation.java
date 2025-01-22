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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class see_reservation extends AppCompatActivity {

    private String url;
    private ImageView img_item_reserve;
    private TextView menu_name, amount, total, id;
    private int amounts = 1 ;
    private float pricesutils = 0, price_multiplication;
    private NestedScrollView nestedScrollView;
    private ShimmerFrameLayout shimmerLayout;
    private Button btn_resevar, btnMinus,btnPlus;

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

        if (getIntent() != null && getIntent().hasExtra("id_reserve_item")) {
            String id_reserve_item = getIntent().getStringExtra("id_reserve_item");
            select_id_reserva_item(id_reserve_item);
        }

        img_item_reserve = findViewById(R.id.img_item_reserve);
        menu_name = findViewById(R.id.menu_name);
        amount = findViewById(R.id.amount);
        total = findViewById(R.id.total);
        id = findViewById(R.id.id_reserve_item);

    }



    private void select_id_reserva_item(String id_reserve_item) {
        url = URL_APIS.BASE_URL + "reservas_item/" + id_reserve_item;
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        JSONArray items = json.getJSONArray("message");
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);

                            String id_reserva_item = item.optString("id_reserva_item", "Nombre no disponible");
                            String names = item.optString("nombre", "Nombre no disponible");
                            String imgBase64 = item.optString("img", "");
                            String amounts = item.optString("cantidad", "Nombre no disponible");
                            String prices = item.optString("precio", "Nombre no disponible");

                            int calamount = item.getInt("cantidad");
                            double calprices =  item.getDouble("precio");

                            double  subtotal= calamount * calprices;



                            total.setText("$" + subtotal );
                            menu_name.setText(names);
                            amount.setText(amounts);
                            id.setText(id_reserva_item);

                            if (!imgBase64.isEmpty()) {
                                try {
                                    byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                    if (decodedByte != null) {
                                        img_item_reserve.setImageBitmap(decodedByte);
                                    } else {
                                        img_item_reserve.setImageResource(R.drawable.hogar);
                                    }
                                } catch (IllegalArgumentException e) {
                                    img_item_reserve.setImageResource(R.drawable.cuenta);
                                }
                            } else {
                                img_item_reserve.setImageResource(R.drawable.cuenta);
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
                Toast.makeText(see_reservation.this, "Error cargando los datos", Toast.LENGTH_LONG).show();
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
        total.setText("$"+price_multiplication );

    }


    public void minus(View view) {
        amounts -= 1;
        if (amounts == 1) {
            btnMinus.setEnabled(false);
        }
        btnPlus.setEnabled(true);

        price_multiplication = amounts * pricesutils ;
        amount.setText(String.valueOf(amounts));
        total.setText("$"+price_multiplication );

    }


}