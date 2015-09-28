package slidenerd.vivz.fpam.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.model.json.group.Group;

public class TestActivity1 extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutForActivity() {
        return R.layout.activity_test1;
    }

    @Override
    public int getRootViewId() {
        return R.id.main_root;
    }

    @NonNull
    @Override
    public int getViewPagerId() {
        return 0;
    }

    @NonNull
    @Override
    public PagerAdapter getPagerAdapter() {
        return null;
    }

}
