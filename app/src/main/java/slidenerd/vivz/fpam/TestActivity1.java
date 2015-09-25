package slidenerd.vivz.fpam;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;

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

    @Override
    public void addTabs(TabLayout tabLayout) {
        tabLayout.setVisibility(View.GONE);
    }
}
