package sv.edu.itca.itca_fepade.Fragmen;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;

import sv.edu.itca.itca_fepade.R;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        view= inflater.inflate(R.layout.fragment_orders, container, false);
        tabHost= view.findViewById(R.id.tabHost);
        tabHost.setup();



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



        return view;
    }
}