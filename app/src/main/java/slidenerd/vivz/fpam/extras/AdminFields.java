package slidenerd.vivz.fpam.extras;

/**
 * The feed shown below is admin JSON object
 * {
 * "id": "867531740000500",
 * "email": "vivek.officialr@gmail.com",
 * "first_name": "Vladimir",
 * "last_name": "Makarov",
 * "picture":
 * {
 * "data":
 * {
 * "height": 200,
 * "is_silhouette": false,
 * "url": "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-xfa1/v/t1.0-1/p200x200/11949472_882200231866984_5133040776625725290_n.jpg?oh=dd1440ca1c282007531a55262e4720b9&oe=5699F731&__gda__=1453635768_835099a37947e7b2bc08713c52cecbdd",
 * "width": 200
 * }
 * }
 * }
 */
public interface AdminFields {
    String ID = "id";
    String EMAIL = "email";
    String PICTURE = "picture";
    String DATA = "data";
    String WIDTH = "width";
    String HEIGHT = "height";
    String URL = "url";
    String IS_SILHOUETTE = "is_silhouette";
    String NAME = "name";
}