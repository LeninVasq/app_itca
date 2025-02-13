package sv.edu.itca.itca_fepade.Fragmen;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import sv.edu.itca.itca_fepade.Edit_my_count;
import sv.edu.itca.itca_fepade.R;
import sv.edu.itca.itca_fepade.URL_APIS;
import sv.edu.itca.itca_fepade.Validation.Login;

public class Account extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Account() {
        // Required empty public constructor
    }

    public static Account newInstance(String param1, String param2) {
        Account fragment = new Account();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View view;
    private Button close, my_count;
    private String url;
    private ImageView imgprofile;
    private TextView email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        view = inflater.inflate(R.layout.fragment_account, container, false);
        close = view.findViewById(R.id.log_out);
        my_count = view.findViewById(R.id.my_count);

        close.setOnClickListener(v -> showLogoutConfirmationDialog());
        my_count.setOnClickListener(v -> count());

        imgprofile = view.findViewById(R.id.imageView);
        email = view.findViewById(R.id.textView);

        SharedPreferences correo = getContext().getSharedPreferences("correo", MODE_PRIVATE);
        String usuariocorreo = correo.getString("correo", "");

        SharedPreferences img = getContext().getSharedPreferences("img", MODE_PRIVATE);
        String usuarioimg = img.getString("img", "");

        byte[] decodedString = Base64.decode(usuarioimg, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        imgprofile.setImageBitmap(decodedByte);
        email.setText(usuariocorreo);

        return view;
    }

    public void count() {
        Intent login = new Intent(requireActivity(), Edit_my_count.class);
        startActivity(login);
    }

    public void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        log_out();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void log_out() {
        SharedPreferences sharedPreferencesusu = getContext().getSharedPreferences("id", MODE_PRIVATE);
        String usuarioId = sharedPreferencesusu.getString("usuario_id", "");

        url = URL_APIS.BASE_URL + "logout/" + usuarioId;

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject MiJson = new JSONObject(respuesta);
                    if (MiJson.has("message")) {
                        Toast.makeText(getContext(), "Se ha cerrado su sesión exitosamente", Toast.LENGTH_SHORT).show();
                        SharedPreferences preferences = getContext().getSharedPreferences("token", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent login = new Intent(requireActivity(), Login.class);
                        startActivity(login);
                        requireActivity().finish();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error en JSON", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error en el JSON", Toast.LENGTH_SHORT).show();
            }
        });
    }
}