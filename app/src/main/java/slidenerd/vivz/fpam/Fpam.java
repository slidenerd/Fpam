package slidenerd.vivz.fpam;

import android.app.Application;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.androidannotations.annotations.EApplication;

import java.util.Set;

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
public class Fpam extends Application {

    private static final String PUBLISH_ACTIONS = "publish_actions";

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

    public boolean hasToken() {
        AccessToken token = getToken();
        return token != null && !token.isExpired();
    }

    public AccessToken getToken() {
        return AccessToken.getCurrentAccessToken();
    }

    public boolean hasPermissionsPublishActions() {
        if (hasToken()) {
            AccessToken token = getToken();
            Set<String> permissions = token.getPermissions();
            return !permissions.isEmpty() && permissions.contains(PUBLISH_ACTIONS);
        }
        return false;
    }

    public boolean shouldRedirectToLogin() {
        return !hasToken();
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