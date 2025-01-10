package sv.edu.itca.itca_fepade.Fragmen;

import android.os.Bundle;


import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import sv.edu.itca.itca_fepade.Item.Category_cymbals;
import sv.edu.itca.itca_fepade.Item.Items_cymbals;
import sv.edu.itca.itca_fepade.MainActivity;
import sv.edu.itca.itca_fepade.R;
import sv.edu.itca.itca_fepade.URL_APIS;

public class Home extends Fragment {

    private String mParam1;
    private String mParam2;

    private String url;
    private View view;
    private List<JSONObject> Category_cymbals = new ArrayList<>();
    private Category_cymbals adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;


    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
            mParam2 = getArguments().getString("param2");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.Category_cymbals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Category_cymbals(getContext(), Category_cymbals);
        recyclerView.setAdapter(adapter);



        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);


        swipeRefreshLayout.setOnRefreshListener(() -> {
            category_menu();
        });
        category_menu();



        return view;
    }




    private void category_menu() {
        url = URL_APIS.BASE_URL + "app_category_menu";
        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        JSONArray miArregloJSON = json.getJSONArray("message");
                        Category_cymbals.clear();
                        for (int i = 0; i < miArregloJSON.length(); i++) {
                            JSONObject item = miArregloJSON.getJSONObject(i);
                            String categoryName = item.getString("nombre");
                            Category_cymbals.add(miArregloJSON.getJSONObject(i));

                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
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




}
