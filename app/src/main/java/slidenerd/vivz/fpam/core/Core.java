package slidenerd.vivz.fpam.core;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.Keyword;

/**
 * Created by vivz on 10/11/15.
 */
public class Core {
    public List<Keyword> getRelevantKeywords(Realm realm, String groupId) {
        RealmResults<Keyword> keywords = realm.where(Keyword.class).findAll();
        RealmList<Keyword> list = new RealmList<>();
        for (Keyword keyword : keywords) {

            //get the list of all groups applicable on this keyword
            RealmList<Group> groups = keyword.getGroups();

            //Since we haven't specified the list of groups to which this keyword applies, it means this keyword applies to all groups without any preference, hence add this keyword to the list of retrieved keywords
            if (groups == null || groups.isEmpty()) {
                list.add(keyword);
            } else {

                //for each applicable group of the current keyword, check if its group id matches with our target, if yes add this keyword to the list to be retrieved
                for (Group group : groups) {
                    if (group.getGroupId().equals(groupId)) {
                        list.add(keyword);
                        break;
                    }
                }
            }
        }
        return list;
    }
}
