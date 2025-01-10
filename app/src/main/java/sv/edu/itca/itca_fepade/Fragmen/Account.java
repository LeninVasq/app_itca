package sv.edu.itca.itca_fepade.Fragmen;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

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
import sv.edu.itca.itca_fepade.R;
import sv.edu.itca.itca_fepade.URL_APIS;
import sv.edu.itca.itca_fepade.Validation.Login;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Account#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Account extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Account() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment account.
     */
    // TODO: Rename and change types and number of parameters
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

    private  View view;
    private Button close;
    private String url;
    private ImageView imgprofile;
    private TextView email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        view =  inflater.inflate(R.layout.fragment_account, container, false);
        close = view.findViewById(R.id.log_out);

        close.setOnClickListener(v -> log_out());


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


    public void log_out(){
        SharedPreferences sharedPreferencesusu = getContext().getSharedPreferences("id", MODE_PRIVATE);
        String usuarioId = sharedPreferencesusu.getString("usuario_id", "");


        url = URL_APIS.BASE_URL + "logout/" +usuarioId;



        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject MiJson = new JSONObject(respuesta);
                    if (MiJson.has("message")) {
                        Toast.makeText(getContext(), "Se ha cerrado su session exitosamente", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(getContext(), "Error en els json", Toast.LENGTH_SHORT).show();


            }
        });
    }

}