package slidenerd.vivz.fpam.util;

import java.util.ArrayList;

import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.model.json.admin.Admin;
import slidenerd.vivz.fpam.model.json.admin.Picture;
import slidenerd.vivz.fpam.model.json.admin.PictureData;
import slidenerd.vivz.fpam.model.json.group.Group;
import slidenerd.vivz.fpam.model.realm.RealmAdmin;
import slidenerd.vivz.fpam.model.realm.RealmGroup;

/**
 * Created by vivz on 18/09/15.
 */
public class CopyUtils {
    public static final RealmAdmin createFrom(Admin admin) {
        RealmAdmin realmAdmin = new RealmAdmin();
        realmAdmin.setId(admin.getId());
        realmAdmin.setEmail(admin.getEmail());
        realmAdmin.setFirstName(admin.getFirstName());
        realmAdmin.setLastName(admin.getLastName());
        Picture picture = admin.getPicture();
        if (picture != null && picture.getPictureData() != null) {
            PictureData pictureData = picture.getPictureData();
            realmAdmin.setHeight(pictureData.getHeight());
            realmAdmin.setWidth(pictureData.getWidth());
            realmAdmin.setIsSilhouette(pictureData.getIsSilhouette());
            realmAdmin.setUrl(pictureData.getUrl());
        } else {
            L.m("The admin's profile image information was unavailable for saving");
        }
        return realmAdmin;
    }

    public static final Admin createFrom(RealmAdmin realmAdmin) {
        Admin admin = new Admin();
        admin.setId(realmAdmin.getId());
        admin.setFirstName(realmAdmin.getFirstName());
        admin.setLastName(realmAdmin.getLastName());
        admin.setEmail(realmAdmin.getEmail());
        PictureData pictureData = new PictureData();
        pictureData.setWidth(realmAdmin.getWidth());
        pictureData.setHeight(realmAdmin.getHeight());
        pictureData.setUrl(realmAdmin.getUrl());
        pictureData.setIsSilhouette(realmAdmin.isSilhouette());
        Picture picture = new Picture();
        picture.setPictureData(pictureData);
        admin.setPicture(picture);
        return admin;
    }

    public static final RealmGroup createFrom(Group group) {
        RealmGroup realmGroup = new RealmGroup();
        realmGroup.setId(group.getId());
        realmGroup.setName(group.getName());
        realmGroup.setIcon(group.getIcon());
        realmGroup.setUnread(group.getUnread());
        return realmGroup;
    }

    public static final ArrayList<RealmGroup> createFrom(ArrayList<Group> listGroups) {
        ArrayList<RealmGroup> listRealmGroups = new ArrayList<>(listGroups.size());
        for (Group group : listGroups) {
            RealmGroup realmGroup = createFrom(group);
            listRealmGroups.add(realmGroup);
        }
        return listRealmGroups;
    }
}
