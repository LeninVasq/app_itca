package sv.edu.itca.itca_fepade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class Edit_my_count extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private CircleImageView profileImage;

    private String nom,ape,email,geners,car , url, cars, gener,clave;

    private TextView nombre,apellido, corre, txt_password;
    private Spinner spinnerCarrera,spinnerGenero;

    private boolean isGeneroSelected = false;
    private boolean isCarreraSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_my_count);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileImage = findViewById(R.id.profile_image);

        nombre = findViewById(R.id.txt_name);
        apellido = findViewById(R.id.txt_lastname);
        txt_password = findViewById(R.id.txt_password);
        spinnerCarrera = findViewById(R.id.spinner_carrera);
        spinnerGenero = findViewById(R.id.spinner_genero);
       // correo = findViewById(R.id.txt_email);
        get_user();


        spinnerGenero.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                gener = parentView.getItemAtPosition(position).toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isGeneroSelected = false;

            }
        });

        spinnerCarrera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                cars = parentView.getItemAtPosition(position).toString();




            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isCarreraSelected = false;

            }
        });

    }

    public void get_user(){

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapterCarrera = ArrayAdapter.createFromResource(this,
                R.array.Carreras, android.R.layout.simple_spinner_item);

        adapterCarrera.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarrera.setAdapter(adapterCarrera);


        SharedPreferences sharedPreferencesusu = getSharedPreferences("id", MODE_PRIVATE);
        String usuarioId = sharedPreferencesusu.getString("usuario_id", "");

        url = URL_APIS.BASE_URL + "user/" +usuarioId;
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject MiJson = new JSONObject(respuesta);
                    if (MiJson.has("message")) {
                        JSONObject usuarioObj = MiJson.getJSONObject("message");


                        String nom = usuarioObj.getString("nombre");
                        String ape = usuarioObj.getString("apellido");
                        String carrera = usuarioObj.getString("carrera");
                        String gene = usuarioObj.getString("genero");
                        String emial = usuarioObj.getString("correo");

                        String img = usuarioObj.getString("img");
                        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);

                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        profileImage.setImageBitmap(decodedByte);


                        int position = adapterCarrera.getPosition(carrera);
                        int position1 = adapter.getPosition(gene);


                        if (position != -1 && position < getResources().getStringArray(R.array.Carreras).length) {
                            spinnerCarrera.setSelection(position);
                        } else {
                            spinnerCarrera.setSelection(2);
                        }

                        if (position1 != -1 && position1 < getResources().getStringArray(R.array.gender).length) {
                            spinnerGenero.setSelection(position1);
                        } else {
                            spinnerGenero.setSelection(0);
                        }




                        nombre.setText(nom);
                        apellido.setText(ape);
                        //genero.setText(gene);
                        //spinnerGenero(gene);
                    }
                } catch (JSONException e) {
                    Toast.makeText(Edit_my_count.this, ""+ e, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(Edit_my_count.this, "Error en el json", Toast.LENGTH_SHORT).show();


            }
        });
    }


    public void img(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }


    public void actualizar(View view) {

        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 800); // Redimensiona a 800x800 píxeles, ajusta según tus necesidades

        byte[] imageBytes = getImageBytes(resizedBitmap);
        String imageString = convertImageBytesToString(imageBytes);

        SharedPreferences sharedPreferencesusu = getSharedPreferences("id", MODE_PRIVATE);
        String usuarioId = sharedPreferencesusu.getString("usuario_id", "");

        nom = nombre.getText().toString().trim();
        ape = apellido.getText().toString().trim();
        car = cars;
        geners = gener;
        clave = txt_password.getText().toString().trim();
        if (nom.isEmpty() || ape.isEmpty() || car.isEmpty() || geners.isEmpty() ) {
            Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }



        url = URL_APIS.BASE_URL + "user/" +usuarioId;



        RequestParams parametros = new RequestParams();
        parametros.put("img", imageString);
        parametros.put("nombre", nom);
        parametros.put("apellido", ape);
        parametros.put("genero", geners);
        parametros.put("carrera", car);

        if(!clave.isEmpty()){
            parametros.put("clave", clave);

        }

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.put(url, parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject MiJson = new JSONObject(respuesta);

                    String message = MiJson.getString("message");

                    Toast.makeText(Edit_my_count.this, message, Toast.LENGTH_SHORT).show();

                    Intent acount = new Intent(Edit_my_count.this, MainActivity.class);
                    startActivity(acount);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
            }
        });

    }

    public Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        float ratio = Math.min(
                (float) maxWidth / bitmap.getWidth(),
                (float) maxHeight / bitmap.getHeight()
        );
        int width = Math.round(ratio * bitmap.getWidth());
        int height = Math.round(ratio * bitmap.getHeight());
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public byte[] getImageBytes(Bitmap bitmap) {
        if (bitmap.getWidth() > 1024) {
            int newWidth = 1024;
            int newHeight = (bitmap.getHeight() * newWidth) / bitmap.getWidth();
            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public String convertImageBytesToString(byte[] imageBytes) {
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            profileImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();  // Si no se seleccionó ninguna imagen
        }
    }

}