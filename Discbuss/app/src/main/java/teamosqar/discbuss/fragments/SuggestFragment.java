package teamosqar.discbuss.fragments;


import android.app.DialogFragment;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import teamosqar.discbuss.activities.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuggestFragment extends DialogFragment {
    private EditText statement;
    public SuggestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_suggest, container, false);
    }


}