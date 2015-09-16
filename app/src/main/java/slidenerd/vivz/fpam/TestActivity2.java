package slidenerd.vivz.fpam;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import slidenerd.vivz.fpam.log.L;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public void doSomething(View view) {
        L.t(this, "" + mInput.getText().toString());
    }
}
