package sv.edu.itca.itca_fepade.Fragmen;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import sv.edu.itca.itca_fepade.Item.Items_cymbals;
import sv.edu.itca.itca_fepade.Item.Items_cymbals_filter;
import sv.edu.itca.itca_fepade.MainActivity;
import sv.edu.itca.itca_fepade.R;
import sv.edu.itca.itca_fepade.URL_APIS;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
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

    private String url, filter;
    private View view;
    private EditText search;
    private ImageView clean;
    private List<JSONObject> Item_cymbals_filter = new ArrayList<>();
    private Items_cymbals_filter adapter;
    private RecyclerView recyclerView;



    private boolean puedesRetroceder() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (puedesRetroceder()) {
                            if (getActivity() != null) {
                                ((MainActivity) getActivity()).home(view);
                            }
                        } else {
                            requireActivity().finish();
                        }
                    }
                });

        // Inflate the layout for this fragment
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        view = inflater.inflate(R.layout.fragment_search, container, false);
        search = view.findViewById(R.id.search);
        clean = view.findViewById(R.id.clean);


        clean.setOnClickListener(v -> clean());




        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() > 0) {
                    clean.setVisibility(View.VISIBLE);
                } else {
                    clean.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    filter();
                    return true;
                }
                return false;
            }
        });


        recyclerView = view.findViewById(R.id.items_cymbals_filter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Items_cymbals_filter(getContext(), Item_cymbals_filter);
        recyclerView.setAdapter(adapter);


        recyclerView.setVerticalScrollBarEnabled(false);
        recyclerView.setHorizontalScrollBarEnabled(false);






        return view;

    }

    public void filter(){
        String query = search.getText().toString();
        consulta_item_cymbals_filter(query);
    }


    private void consulta_item_cymbals_filter(String param) {
        url = URL_APIS.BASE_URL + "app_menu_filter";
        AsyncHttpClient cliente = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("nombre", param);

        cliente.post(url,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    try {
                        JSONObject json = new JSONObject(new String(responseBody));
                        JSONArray miArregloJSON = json.getJSONArray("message");
                        Item_cymbals_filter.clear();
                        for (int i = 0; i < miArregloJSON.length(); i++) {
                            Item_cymbals_filter.add(miArregloJSON.getJSONObject(i));
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
    public void clean() {
        search.setText("");
    }




}