package slidenerd.vivz.fpam.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import slidenerd.vivz.fpam.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentStats extends Fragment {


    public FragmentStats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }


}
