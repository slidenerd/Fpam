package slidenerd.vivz.fpam.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.group.Group;


public class TestActivity2 extends ActivityBase {
    private EditText mInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInput = (EditText) findViewById(R.id.test_input);
    }

    @Override
    public int getLayoutForActivity() {
        return R.layout.activity_test2;
    }

    @Override
    public int getRootViewId() {
        return R.id.main_root;
    }

    @Nullable
    @Override
    public int getViewPagerId() {
        return 0;
    }

    @Nullable
    @Override
    public PagerAdapter getPagerAdapter() {
        return null;
    }

    @Override
    public void onGroupSelected(Group group) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public void doSomething(View view) {
        L.t(this, "" + mInput.getText().toString());
    }
}
