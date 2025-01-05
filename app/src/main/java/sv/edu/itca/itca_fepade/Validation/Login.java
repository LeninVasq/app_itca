package sv.edu.itca.itca_fepade.Validation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import sv.edu.itca.itca_fepade.MainActivity;
import sv.edu.itca.itca_fepade.R;
import sv.edu.itca.itca_fepade.URL_APIS;

public class Login extends AppCompatActivity {

    private EditText txt_email, txt_password;
    private String email, password, url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
    }

    public void login(View view) {
        email = txt_email.getText().toString();
        password = txt_password.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        url = URL_APIS.BASE_URL + "login";

        RequestParams parametros = new RequestParams();
        parametros.put("correo", email);
        parametros.put("clave", password);


        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject MiJson = new JSONObject(respuesta);
                    if (MiJson.has("message")) {
                        int tipo_usuario = MiJson.getInt("tipo_usuario");
                        if (tipo_usuario == 3) {


                            String mensajeBienvenida = "Bienvenido";
                            String usuario = MiJson.getString("id");
                            Toast.makeText(Login.this, mensajeBienvenida, Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("id", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("usuario_id", usuario);
                            editor.apply();
                            Intent regitrarse = new Intent(Login.this, MainActivity.class);
                            startActivity(regitrarse);

                        }
                        else{
                            Toast.makeText(Login.this, "Credenciales Incorrectas", Toast.LENGTH_SHORT).show();

                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(Login.this, "Error en JSON", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {
                    try {
                        String respuesta = new String(responseBody);
                        JSONObject MiJson = new JSONObject(respuesta);
                        if (MiJson.has("error")) {
                            String mensajeError = MiJson.getString("error");
                            Toast.makeText(Login.this, mensajeError, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (MiJson.has("details")) {
                            String mensajeError = "El correo no es valido.";
                            Toast.makeText(Login.this, mensajeError, Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (JSONException e) {
                        Toast.makeText(Login.this, "Error en el json", Toast.LENGTH_SHORT).show();
                    }
                }
                Toast.makeText(Login.this, "Error en la conexión a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void register(View view) {
        Intent regitrarse = new Intent(Login.this, Sign_up.class);
        startActivity(regitrarse);
    }
}