package slidenerd.vivz.fpam;

import android.os.Bundle;

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
}
