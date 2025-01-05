package sv.edu.itca.itca_fepade.Fragmen_menu;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import sv.edu.itca.itca_fepade.Item.Items_cymbals;
import sv.edu.itca.itca_fepade.R;
import sv.edu.itca.itca_fepade.URL_APIS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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
    private String url;
    private SearchView searchView;
    private View view;


    private List<JSONObject> publicacionesList = new ArrayList<>();
    private Items_cymbals adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.items_cymbals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Items_cymbals(getContext(), publicacionesList);
        recyclerView.setAdapter(adapter);

        // Consultas de datos
        consulta_item_cymbals(); // Carga textos
        consulta_item_cymbals_img(); // Carga imágenes
        return view;
    }

    private void consulta_item_cymbals() {
        url = URL_APIS.BASE_URL + "app_menu";

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        JSONArray miArregloJSON = json.getJSONArray("message");

                        // Limpia la lista antes de agregar nuevos datos
                        publicacionesList.clear();

                        for (int i = 0; i < miArregloJSON.length(); i++) {
                            publicacionesList.add(miArregloJSON.getJSONObject(i));
                        }

                        // Notifica al adaptador después de actualizar los datos
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("consulta", "Error al procesar el JSON", e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error cargando textos: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void consulta_item_cymbals_img() {
        url = URL_APIS.BASE_URL + "app_menu_img";

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        JSONArray miArregloJSON = json.getJSONArray("message");

                        // No limpies la lista aquí porque consulta_item_cymbals ya la limpió
                        for (int i = 0; i < miArregloJSON.length(); i++) {
                            JSONObject imgItem = miArregloJSON.getJSONObject(i);

                            // Encuentra el objeto correspondiente en la lista actual y agrega la imagen
                            for (JSONObject item : publicacionesList) {
                                if (item.optString("id").equals(imgItem.optString("id"))) {
                                    item.put("img", imgItem.optString("img"));
                                    break;
                                }
                            }
                        }

                        // Notifica al adaptador después de actualizar los datos
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("consulta", "Error al procesar el JSON de imágenes", e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error cargando imágenes: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



}