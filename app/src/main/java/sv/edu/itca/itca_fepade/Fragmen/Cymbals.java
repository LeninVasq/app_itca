package sv.edu.itca.itca_fepade.Fragmen;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import sv.edu.itca.itca_fepade.Item.Items_cymbals;
import sv.edu.itca.itca_fepade.R;
import sv.edu.itca.itca_fepade.URL_APIS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Cymbals#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Cymbals extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static Cymbals instance;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Cymbals() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Cymbals.
     */
    // TODO: Rename and change types and number of parameters
    public static Cymbals newInstance(String param1, String param2) {
        Cymbals fragment = new Cymbals();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private String url;
    private View view;
    private List<JSONObject> Item_cymbals = new ArrayList<>();
    private Items_cymbals adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String id_Category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cymbals, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.items_cymbals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Items_cymbals(getContext(), Item_cymbals);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);



        if (getArguments() != null) {
             id_Category = getArguments().getString("id_category");
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            consulta_item_cymbals(id_Category);
            consulta_item_cymbals_img(id_Category);
        });
        consulta_item_cymbals(id_Category);
        consulta_item_cymbals_img(id_Category);
        return view;
    }

    public void recargar_img(){
        consulta_item_cymbals_img(id_Category);
        Toast.makeText(getContext(), "Recargando", Toast.LENGTH_SHORT).show();

    }



   private void consulta_item_cymbals(String id_Category) {

       Item_cymbals.clear();
       url = URL_APIS.BASE_URL + "app_menu/"+id_Category;
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        JSONArray miArregloJSON = json.getJSONArray("message");
                        for (int i = 0; i < miArregloJSON.length(); i++) {
                            Item_cymbals.add(miArregloJSON.getJSONObject(i));
                        }


                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("consulta", "Error al procesar el JSON de los items", e);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error cargando textos: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void consulta_item_cymbals_img(String id_Category) {

        url = URL_APIS.BASE_URL + "app_menu_img/"+id_Category;
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        JSONArray miArregloJSON = json.getJSONArray("message");

                        Map<String, String> imgMap = new HashMap<>();
                        for (int i = 0; i < miArregloJSON.length(); i++) {
                            JSONObject imgItem = miArregloJSON.getJSONObject(i);
                            String id = imgItem.optString("id_menu");
                            String imgUrl = imgItem.optString("img");
                            imgMap.put(id, imgUrl);
                        }

                        for (JSONObject item : Item_cymbals) {
                            String id = item.optString("id_menu");
                            if (imgMap.containsKey(id)) {
                                item.put("img", imgMap.get(id));
                            }
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("consulta", "Error al procesar el JSON de imágenes", e);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error cargando imágenes: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}