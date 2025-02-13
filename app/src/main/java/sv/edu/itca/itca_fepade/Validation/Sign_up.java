package sv.edu.itca.itca_fepade.Validation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class Sign_up extends AppCompatActivity {

    private EditText txt_email, txt_password, txt_name, txt_lastname;
    private TextView tvrequirements,tvrequirements_email;
    private Button btn_register;
    private String email, password, url, name, lastname, car, geners,  cars, gener;

    private boolean isGeneroSelected = false;
    private boolean isCarreraSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinnerGenero = findViewById(R.id.spinner_genero);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapter);




        Spinner spinnerCarrera = findViewById(R.id.spinner_carrera);
        ArrayAdapter<CharSequence> adapterCarrera = ArrayAdapter.createFromResource(this,
                R.array.Carrera, android.R.layout.simple_spinner_item);

        adapterCarrera.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarrera.setAdapter(adapterCarrera);



        spinnerGenero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                gener = parentView.getItemAtPosition(position).toString();

                if (position > 0) {
                    isGeneroSelected = true;
                } else {
                    isGeneroSelected = false;
                }

                updateButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isGeneroSelected = false;
                updateButtonState();
            }
        });

        spinnerCarrera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                cars = parentView.getItemAtPosition(position).toString();

                if (position > 0) {
                    isCarreraSelected = true;
                } else {
                    isCarreraSelected = false;
                }

                updateButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isCarreraSelected = false;
                updateButtonState();
            }
        });


        txt_email = findViewById(R.id.txt_email);
        txt_name = findViewById(R.id.txt_name);
        txt_lastname = findViewById(R.id.txt_lastname);
        txt_password = findViewById(R.id.txt_password);
        tvrequirements = findViewById(R.id.tvrequirements);
        btn_register = findViewById(R.id.btn_register);
        tvrequirements_email = findViewById(R.id.tvrequirements_email);

        btn_register.setEnabled(false);
        btn_register.setBackgroundColor(getResources().getColor(R.color.gray));

        txt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = txt_email.getText().toString();
                if (!Validation_email(email)) {
                    tvrequirements_email.setText("El correo ingresado no es válido");
                    tvrequirements_email.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    tvrequirements_email.setVisibility(View.VISIBLE);
                    btn_register.setEnabled(false);
                    btn_register.setBackgroundColor(getResources().getColor(R.color.black));

                }else {
                    tvrequirements_email.setText("✔ Correo válida");
                    tvrequirements_email.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                    tvrequirements_email.setVisibility(View.VISIBLE);
                    btn_register.setEnabled(true);
                    btn_register.setBackgroundColor(getResources().getColor(R.color.gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        InputFilter[] filters = new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        if (source.toString().matches("[a-zA-Z]*")) {
                            return null;
                        } else {
                            return " ";
                        }
                    }
                }
        };


        txt_name.setFilters(filters);
        txt_lastname.setFilters(filters);

        txt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                String mensaje = Validation_password(password);

                if (mensaje.isEmpty()) {
                    tvrequirements.setText("✔ Contraseña válida");
                    tvrequirements.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                    btn_register.setEnabled(true);
                    btn_register.setBackgroundResource(R.drawable.btn_square);

                } else {
                    tvrequirements.setText(mensaje);
                    tvrequirements.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    btn_register.setEnabled(false);
                    btn_register.setBackgroundColor(getResources().getColor(R.color.gray));
                }
                tvrequirements.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void updateButtonState() {
        if (isGeneroSelected && isCarreraSelected) {
            btn_register.setEnabled(true);
            btn_register.setBackgroundResource(R.drawable.btn_square);
        } else {
            btn_register.setEnabled(false);
            btn_register.setBackgroundColor(getResources().getColor(R.color.gray));
        }
    }

    public void register(View view) {
        email = txt_email.getText().toString();
        password = txt_password.getText().toString();
        name = txt_name.getText().toString();
        lastname = txt_lastname.getText().toString();
        car = cars;
        geners = gener;


        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || lastname.isEmpty()) {
            Toast.makeText(Sign_up.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }
        url = URL_APIS.BASE_URL + "registro";

        RequestParams parametros = new RequestParams();
        parametros.put("correo", email);
        parametros.put("clave", password);
        parametros.put("id_tipo_usuario", 3);
        parametros.put("nombre", name);
        parametros.put("apellido", lastname);
        parametros.put("carrera", car);
        parametros.put("genero", geners);
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.post(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject MiJson = new JSONObject(respuesta);
                    if (MiJson.has("message")) {
                        String mensajeBienvenida = "Se ha registrado exitosamente";
                        Toast.makeText(Sign_up.this, mensajeBienvenida, Toast.LENGTH_SHORT).show();
                        Intent login = new Intent(Sign_up.this, Login.class);
                        startActivity(login);
                        finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(Sign_up.this, "Error en JSON", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Sign_up.this, mensajeError, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (MiJson.has("details")) {
                            String mensajeError = "El correo ya esta registrado.";
                            // String mensajeError = MiJson.getString("details");

                            Toast.makeText(Sign_up.this, mensajeError, Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (JSONException e) {
                        Toast.makeText(Sign_up.this, "Error en els json", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    public void login(View view) {
        Intent login = new Intent(Sign_up.this, Login.class);
        startActivity(login);
        finish();
    }

    private boolean Validation_email(String correo) {
        // return Patterns.EMAIL_ADDRESS.matcher(correo).matches();
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return correo.matches(emailPattern);
    }

    private String Validation_password(String password) {
        StringBuilder errores = new StringBuilder();

        if (password.length() < 8) {
            errores.append("Debe tener al menos 8 caracteres.\n");
        }
        if (!password.matches(".*[A-Z].*")) {
            errores.append("Debe incluir al menos una letra mayúscula.\n");
        }
        if (!password.matches(".*[a-z].*")) {
            errores.append("Debe incluir al menos una letra minúscula.\n");
        }
        if (!password.matches(".*\\d.*")) {
            errores.append("Debe incluir al menos un número.\n");
        }
        if (!password.matches(".*[@#$%^&+=].*")) {
            errores.append("Debe incluir al menos un carácter especial.\n");
        }

        return errores.toString().trim();
    }
}