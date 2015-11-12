package slidenerd.vivz.fpam;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

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
import slidenerd.vivz.fpam.model.deserializer.AdminDeserializer;
import slidenerd.vivz.fpam.model.deserializer.GroupDeserializer;
import slidenerd.vivz.fpam.model.deserializer.PostDeserializer;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.feed.Post;
import slidenerd.vivz.fpam.model.json.group.Group;

/**
 * Created by vivz on 28/07/15.
 */
@EApplication
public class Fpam extends Application {

    private AccessToken mToken;

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

    /**
     * Needed to support multidex since Fpam has more than 65536 methods that can be invoked inside a single dex thereby causing a DexOverflowException if multidex is not enabled in the build.gradle file
     *
     * @param base
     */
    @Override
    public void attachBaseContext(Context base) {
        MultiDex.install(base);
        super.attachBaseContext(base);
    }

    public AccessToken getToken() {
        return AccessToken.getCurrentAccessToken();
    }

    public void setToken(AccessToken accessToken) {
        mToken = accessToken;
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