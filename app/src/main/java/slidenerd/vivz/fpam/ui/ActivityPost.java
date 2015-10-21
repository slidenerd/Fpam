package slidenerd.vivz.fpam.ui;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.widget.PostView;

/**
 * Created by vivz on 20/10/15.
 */
@EActivity(R.layout.sample_post_view)
public class ActivityPost extends AppCompatActivity {
    @ViewById(R.id.post_view_1)
    PostView mPost1;
    @ViewById(R.id.post_view_2)
    PostView mPost2;

    @ViewById(R.id.post_view_3)
    PostView mPost3;

    @AfterViews
    void onCreateView() {
        mPost1.setUserName("Vijai Chander");
        mPost1.setUpdatedTime("5 mins ago");
        mPost1.setMessage("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");

        mPost2.setUserName("Ankush Deshpande");
        mPost2.setUpdatedTime("20 mins ago");
        mPost2.setMessage("Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?");

        mPost3.setUserName("Sex Poster");
        mPost3.setUpdatedTime("2 hours ago");
        mPost3.setMessage("What the hell is going on guys?");
    }
}
