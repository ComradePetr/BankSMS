package ru.spbau.banksms;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private CardsOpenHelper dbHelper;
    private SMSProvider smsProvider;
    private Activity activity;
    int id = -1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        dbHelper = new CardsOpenHelper(activity);
        smsProvider = new SMSProvider(activity);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            int id = getArguments().getInt(ARG_ITEM_ID);
            if (id == -1) {
                try {
                    Card card = new Card();
                    dbHelper.addCard(card);
                    id = card.id;
                } catch (SQLException e) {
                    id = -1;
                }
            }

            this.id = id;

            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null)
                appBarLayout.setTitle("Card #" + id);
        }
    }

    private List<String> makeSpinnerList(List<SMSProvider.SMSThread> threadList) {
        ArrayList<String> list = new ArrayList<>();
        for (SMSProvider.SMSThread smsThread : threadList) {
            list.add(smsThread.address);
        }
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        TextView textView = ((TextView) rootView.findViewById(R.id.item_detail));

        if (id != -1) {
            textView.setText("Card information " + id);
            Card card = dbHelper.getCardById(id);
            ArrayList<SMSProvider.SMSThread> threadList = smsProvider.getSMSThreadList();
            List<String> spinnerList = makeSpinnerList(threadList);

            Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                    android.R.layout.simple_spinner_item, spinnerList.toArray(new String[spinnerList.size()]));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } else
            textView.setText("Unable to create new card (problems with database)");

        return rootView;
    }
}
