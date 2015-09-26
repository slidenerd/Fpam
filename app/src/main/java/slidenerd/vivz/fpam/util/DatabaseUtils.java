package slidenerd.vivz.fpam.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

public class DatabaseUtils {
    public static void exportDatabase(Context context) {

        // init realm
        Realm realm = Realm.getInstance(context.getApplicationContext());
        Date date = new Date();
        File exportRealmFile = null;
        try {
            // get or create an "export.realm" file

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss");
            String formattedDate = dateFormat.format(date);
            exportRealmFile = new File(context.getExternalCacheDir(), "export_" + formattedDate + ".realm");

            // if "export.realm" already exists, delete
            exportRealmFile.delete();

            // copy current realm to "export.realm"
            realm.writeCopyTo(exportRealmFile);

            // init email intent and add export.realm as attachment
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            String[] addresses = {"slidenerd@gmail.com", "ankush1158@gmail.com"};
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Fpam Database Exported");
            String extraText = "This is an automated email containing your Fpam local database. Your Fpam Database here is exported as of " + date + ". Download Realm Browser on your mac to view the database.";
            intent.putExtra(Intent.EXTRA_TEXT, extraText);
            Uri u = Uri.fromFile(exportRealmFile);
            intent.putExtra(Intent.EXTRA_STREAM, u);

            // start email intent
            context.startActivity(Intent.createChooser(intent, "Export Realm Database"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }
}
