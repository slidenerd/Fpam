package slidenerd.vivz.fpam.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import io.realm.Realm;

public class FileUtils {
    public static void exportDatabase(Context context) {

        // init realm
        Realm realm = Realm.getInstance(context.getApplicationContext());

        File exportRealmFile = null;
        try {
            // get or create an "export.realm" file
            exportRealmFile = new File(context.getExternalCacheDir(), "export.realm");

            // if "export.realm" already exists, delete
            exportRealmFile.delete();

            // copy current realm to "export.realm"
            realm.writeCopyTo(exportRealmFile);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }

        // init email intent and add export.realm as attachment
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        String[] addresses = {"slidenerd@gmail.com", "ankush1158@gmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.setType("plain/text");
        Date date = new Date();
        intent.putExtra(Intent.EXTRA_SUBJECT, "Fpam Database Exported");
        intent.putExtra(Intent.EXTRA_TEXT, "This is an automated email containing your Fpam local database. Your Fpam Database here is exported as of " + date + ". Download Realm Browser on your mac to view the database.");
        Uri u = Uri.fromFile(exportRealmFile);
        intent.putExtra(Intent.EXTRA_STREAM, u);

        // start email intent
        context.startActivity(Intent.createChooser(intent, "Export Realm Database"));
    }
}
