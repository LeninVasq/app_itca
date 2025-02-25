package sv.edu.itca.itca_fepade.Validation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import sv.edu.itca.itca_fepade.R;
import sv.edu.itca.itca_fepade.URL_APIS;

public class recuperacion extends AppCompatActivity {

    private Button btn_register;
    private String email, mensaje, url, name, lastname, car, geners,  cars, gener;
    private EditText txt_mensaje, txt_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperacion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txt_email = findViewById(R.id.txt_email);
        txt_mensaje = findViewById(R.id.txt_mensa);
    }

    public void mensaje(View view) {
        email = txt_email.getText().toString();
        mensaje = txt_mensaje.getText().toString();

        if (email.isEmpty() || mensaje.isEmpty()) {
            Toast.makeText(recuperacion.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }
        url = URL_APIS.BASE_URL + "mensajes";
        RequestParams parametros = new RequestParams();
        parametros.put("correo", email);
        parametros.put("Mensaje", mensaje);
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject MiJson = new JSONObject(respuesta);
                    if (MiJson.has("message")) {
                        String mensajeBienvenida = "Se ha enviado el mensaje";
                        Toast.makeText(recuperacion.this, mensajeBienvenida, Toast.LENGTH_SHORT).show();
                        Intent login = new Intent(recuperacion.this, Login.class);
                        startActivity(login);
                        finish();
                    }
                    Toast.makeText(recuperacion.this, "hola", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(recuperacion.this, "Error en JSON", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    try {
                        String respuesta = new String(responseBody);
                        JSONObject MiJson = new JSONObject(respuesta);
                        if (MiJson.has("message")) {
                            String mensajeError = MiJson.getString("message");
                            Toast.makeText(recuperacion.this, mensajeError, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (MiJson.has("details")) {
                            String mensajeError = "El correo ya esta registrado.";
                            // String mensajeError = MiJson.getString("details");

                            Toast.makeText(recuperacion.this, mensajeError, Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (JSONException e) {
                        Toast.makeText(recuperacion.this, "Error en els json", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}