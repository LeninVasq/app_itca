package sv.edu.itca.itca_fepade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import sv.edu.itca.itca_fepade.Fragmen.Cymbals;
import sv.edu.itca.itca_fepade.Fragmen.Orders;
import sv.edu.itca.itca_fepade.Fragmen.Search;
import sv.edu.itca.itca_fepade.Fragmen.Home;
import sv.edu.itca.itca_fepade.Fragmen.Account;
import sv.edu.itca.itca_fepade.Fragmen.Undelivered_orders;

public class MainActivity extends AppCompatActivity {

    private String url;

    Home home =new Home();
    Account account = new Account();
    Search search = new Search();
    Cymbals cymbals = new Cymbals();
    Orders orders = new Orders();
    Undelivered_orders undelivered_orders = new Undelivered_orders();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (savedInstanceState == null) {
            loadFragment(home);
        }

        img_email();
    }


    public void img_email(){
        SharedPreferences sharedPreferencesusu = getSharedPreferences("id", MODE_PRIVATE);
        String usuarioId = sharedPreferencesusu.getString("usuario_id", "");

        url = URL_APIS.BASE_URL + "image_and_email/" +usuarioId;
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject MiJson = new JSONObject(respuesta);
                    if (MiJson.has("correo")) {
                        String correo = MiJson.getString("correo");
                        String img = MiJson.getString("img");

                        SharedPreferences correoobtenido = getSharedPreferences("correo", MODE_PRIVATE);
                        SharedPreferences.Editor correoedtitor = correoobtenido.edit();
                        correoedtitor.putString("correo", correo);
                        correoedtitor.apply();

                        SharedPreferences imgobtenido = getSharedPreferences("img", MODE_PRIVATE);
                        SharedPreferences.Editor imgeditor = imgobtenido.edit();
                        imgeditor.putString("img", img);
                        imgeditor.apply();
                    }
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this,  "Error en JSON", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(MainActivity.this, "Error en el json", Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getTitle().toString()) {
                case "Inicio":
                    loadFragment(home);
                    return true;
                case "Pedidos":
                    loadFragment(orders);
                    return true;

                case "Cuenta":
                    loadFragment(account);
                    return true;
                default:
                    return false;
            }
        }

    };

    public void search(View view) {
        loadFragment(search);
    }

    public void home(View view) {
        loadFragment(home);
    }

    public void filter_by_category(View view) {
        TextView id = view.findViewById(R.id.id_category);
        String id_Category = id.getText().toString().trim();


        Bundle args = new Bundle();
        args.putString("id_category", id_Category);
        cymbals.setArguments(args);



        loadFragment(cymbals);
    }

    public void selected_cymbals(View view) {
        TextView id = view.findViewById(R.id.id_cymbals);
        String id_cymbals = id.getText().toString().trim();


        Intent intent = new Intent(MainActivity.this, cymbals.class);
        intent.putExtra("id_cymbals", id_cymbals);
        startActivity(intent);


    }


    public void undelivered_orders(View view) {
        loadFragment(undelivered_orders);
    }


}