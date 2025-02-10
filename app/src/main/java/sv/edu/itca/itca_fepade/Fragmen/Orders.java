package sv.edu.itca.itca_fepade.Fragmen;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import sv.edu.itca.itca_fepade.Item.Category_cymbals;
import sv.edu.itca.itca_fepade.Item.Order_item;
import sv.edu.itca.itca_fepade.Item.delivered_orders;
import sv.edu.itca.itca_fepade.R;
import sv.edu.itca.itca_fepade.URL_APIS;
import sv.edu.itca.itca_fepade.Undelivered_orders;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Orders#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Orders extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Orders() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Orders.
     */
    // TODO: Rename and change types and number of parameters
    public static Orders newInstance(String param1, String param2) {
        Orders fragment = new Orders();
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
    private TabHost tabHost;
    private RecyclerView recyclerView,recyclerView_orders;
    private LinearLayout messague, messague_orders;
    private List<JSONObject> orderItemsList = new ArrayList<>();
    private List<JSONObject> ItemsList = new ArrayList<>();
    private Order_item adapter;
    private delivered_orders adapterorders;
    private String url;
    private LinearLayout total_sub;
    private TextView total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        view= inflater.inflate(R.layout.fragment_orders, container, false);
        tabHost= view.findViewById(R.id.tabHost);
        tabHost.setup();

        messague = view.findViewById(R.id.messague_fragment);
        messague_orders = view.findViewById(R.id.messague_fragment_orders);

        recyclerView_orders = view.findViewById(R.id.undelivered_orders_fragment_orders);
        recyclerView_orders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterorders = new delivered_orders(getContext(), ItemsList);
        recyclerView_orders.setAdapter(adapterorders);
        recyclerView_orders.setVerticalScrollBarEnabled(false);
        recyclerView_orders.setHorizontalScrollBarEnabled(false);

        recyclerView = view.findViewById(R.id.undelivered_orders_fragment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Order_item(getContext(), orderItemsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);
        total_sub = view.findViewById(R.id.total_sub);
        total = view.findViewById(R.id.total);


        TabHost.TabSpec tab1 = tabHost.newTabSpec("Carrito");
        tab1.setIndicator("Carrito");
        tab1.setContent(R.id.tab1);
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2 = tabHost.newTabSpec("Pedidos");
        tab2.setIndicator("Pedidos");
        tab2.setContent(R.id.tab2);
        tabHost.addTab(tab2);


        tabHost.setCurrentTab(0);

        tabHost.setOnTabChangedListener(tabId -> {
            int selectedIndex = tabHost.getCurrentTab();
            TabWidget tabWidget = tabHost.getTabWidget();

            for (int i = 0; i < tabWidget.getChildCount(); i++) {
                View tab = tabWidget.getChildAt(i);

                if (i == selectedIndex) {
                    tab.setBackgroundResource(R.drawable.tab_indicator);
                } else {
                    tab.setBackgroundResource(0);
                }
            }
        });

        int defaultTab = 0;
        tabHost.setCurrentTab(defaultTab);

        TabWidget tabWidget = tabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            View tab = tabWidget.getChildAt(i);

            if (i == defaultTab) {
                tab.setBackgroundResource(R.drawable.tab_indicator);
            } else {
                tab.setBackgroundResource(0);
            }
        }


        app_reservas();
        app_orders();



        return view;




    }

    public void app_orders() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("id", Context.MODE_PRIVATE);
        String usuarioId = sharedPreferences.getString("usuario_id", "");

        if (usuarioId.isEmpty()) {
            Toast.makeText(getContext(), "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }
        ItemsList.clear();
        url = URL_APIS.BASE_URL + "app_pedidos/" + usuarioId;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {


                    String responseString = new String(responseBody, StandardCharsets.UTF_8);
                    JSONObject jsonResponse = new JSONObject(responseString);


                    if (jsonResponse.has("message")) {
                        JSONArray messageArray = jsonResponse.getJSONArray("message");
                        if (messageArray.length() > 0) {
                            messague_orders.setVisibility(View.GONE);
                            for (int i = 0; i < messageArray.length(); i++) {
                                JSONObject item = messageArray.getJSONObject(i);

                                ItemsList.add(messageArray.getJSONObject(i));
                            }
                            recyclerView_orders.setVisibility(View.VISIBLE);
                            adapterorders.notifyDataSetChanged();

                        } else {
                            messague_orders.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error procesando datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = responseBody != null ? new String(responseBody, StandardCharsets.UTF_8) : error.getMessage();

                Toast.makeText(getContext(), "No se pudo conectar al servidor. Intente de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void delete_product_fragment() {

        View parent = (View) view.getParent();
        TextView textView = parent.findViewById(R.id.id_reserve_item);
        String id_reserve_iten = textView.getText().toString();



        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_product);
        LinearLayout delete = dialog.findViewById(R.id.delete);


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                url = URL_APIS.BASE_URL + "reservas_item/"+id_reserve_iten ;
                AsyncHttpClient cliente = new AsyncHttpClient();
                cliente.delete(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String respuesta = new String(responseBody);
                        try {
                            JSONObject MiJson = new JSONObject(respuesta);
                            if (MiJson.has("message")) {
                                Toast.makeText(getContext(), "lo elimino", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(),  "Error sdksdjb en JSON", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        Toast.makeText(getContext(), "Error en el json", Toast.LENGTH_SHORT).show();


                    }
                });
            }
        });



        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    public void app_reservas() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("id", Context.MODE_PRIVATE);
        String usuarioId = sharedPreferences.getString("usuario_id", "");

        if (usuarioId.isEmpty()) {
            Toast.makeText(getContext(), "Usuario no válido", Toast.LENGTH_SHORT).show();
            return;
        }
        orderItemsList.clear();
        url = URL_APIS.BASE_URL + "app_reservas/" + usuarioId;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    if (responseBody == null) {
                        Toast.makeText(getContext(), "Respuesta vacía del servidor", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String responseString = new String(responseBody, StandardCharsets.UTF_8);
                    JSONObject jsonResponse = new JSONObject(responseString);

                    double tota_sub = 0;
                    if (jsonResponse.has("message")) {
                        JSONArray messageArray = jsonResponse.getJSONArray("message");
                        if (messageArray.length() > 0) {
                            messague.setVisibility(View.GONE);
                            for (int i = 0; i < messageArray.length(); i++) {
                                JSONObject item = messageArray.getJSONObject(i);
                                double amount = item.getDouble("cantidad");
                                double price = item.getDouble("precio");
                                tota_sub += amount * price;
                                orderItemsList.add(messageArray.getJSONObject(i));
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            total_sub.setVisibility(View.VISIBLE);
                            total.setText("$ " + tota_sub);
                            adapter.notifyDataSetChanged();

                        } else {
                            messague.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error procesando datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = responseBody != null ? new String(responseBody, StandardCharsets.UTF_8) : error.getMessage();

                Toast.makeText(getContext(), "No se pudo conectar al servidor. Intente de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}