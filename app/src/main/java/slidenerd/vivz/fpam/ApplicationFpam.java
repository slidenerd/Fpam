package slidenerd.vivz.fpam;

import android.app.Application;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.androidannotations.annotations.EApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import slidenerd.vivz.fpam.model.gson.AdminDeserializer;
import slidenerd.vivz.fpam.model.gson.GroupDeserializer;
import slidenerd.vivz.fpam.model.gson.PostDeserializer;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;

/**
 * Created by vivz on 28/07/15.
 */
@EApplication
public class ApplicationFpam extends Application {

    public static Gson getGson() {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(Admin.class, new AdminDeserializer())
                .registerTypeAdapter(Group.class, new GroupDeserializer())
                .registerTypeAdapter(Post.class, new PostDeserializer())
                .create();
        return gson;
    }

    public static AccessToken getFacebookAccessToken() {
        return AccessToken.getCurrentAccessToken();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        FacebookSdk.sdkInitialize(this);
        FacebookSdk.setIsDebugEnabled(true);
    }
}