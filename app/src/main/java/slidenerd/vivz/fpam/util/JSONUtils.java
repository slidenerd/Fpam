package slidenerd.vivz.fpam.util;

/**
 * TODO Each time the JSON feed is loaded, only those posts are saved and the rest are deleted, maintain a 100 posts of each group in the database. Also add comments and attachments.
 * Created by vivz on 24/09/15.
 */
public class JSONUtils {

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
        String FIRST_NAME = "first_name";
        String LAST_NAME = "last_name";
        String PICTURE = "picture";
        String DATA = "data";
        String WIDTH = "width";
        String HEIGHT = "height";
        String URL = "url";
        String IS_SILHOUETTE = "is_silhouette";
    }

    /**
     * {
     * "data": [
     * {
     * "name": "The Fantastic Four",
     * "id": "368380276678573",
     * "icon": "https://fbstatic-a.akamaihd.net/rsrc.php/v2/yQ/r/QSvrimiDFPQ.png",
     * "unread": 1
     * },
     * {
     * "name": "C/C++ and Native Development",
     * "id": "1704641256427590",
     * "icon": "https://fbstatic-a.akamaihd.net/rsrc.php/v2/yB/r/odyq1oFT40B.png",
     * "unread": 0
     * }
     * ],
     * "paging": {
     * "cursors": {
     * "before": "MzY4MzgwMjc2Njc4NTcz",
     * "after": "MTcwNDY0MTI1NjQyNzU5MAZDZD"
     * },
     * "next": "https://graph.facebook.com/v2.4/867531740000500/admined_groups?access_token=CAAXv1PzFiNABACJiweVkycJ1ANIZCIZBZA3qhuLqViq4YxdeQDuQQeZA0pBfbbg3FxE46nsZA9ZCFkAw3JqzhhZBNpSApozbynBg26SAjpWZB3pazCyRkJoz4YX2qZBYAthBt8SrDh9BVcfrdowdhgPHcKxjBrlKagYas6rEgmuoOn2JBspJuFY5FZB4Te5jJd8ZBZCKPxYQHB8dtv2DuFZAZCfZATx&pretty=0&fields=name%2Cid%2Cicon%2Cunread&limit=2&after=MTcwNDY0MTI1NjQyNzU5MAZDZD",
     * "previous": "https://graph.facebook.com/v2.4/867531740000500/admined_groups?access_token=CAAXv1PzFiNABACJiweVkycJ1ANIZCIZBZA3qhuLqViq4YxdeQDuQQeZA0pBfbbg3FxE46nsZA9ZCFkAw3JqzhhZBNpSApozbynBg26SAjpWZB3pazCyRkJoz4YX2qZBYAthBt8SrDh9BVcfrdowdhgPHcKxjBrlKagYas6rEgmuoOn2JBspJuFY5FZB4Te5jJd8ZBZCKPxYQHB8dtv2DuFZAZCfZATx&pretty=0&fields=name%2Cid%2Cicon%2Cunread&limit=2&before=MzY4MzgwMjc2Njc4NTcz"
     * }
     * }
     */
    public interface GroupFields {
        String DATA = "data";
        String ID = "id";
        String NAME = "name";
        String ICON = "icon";
        String UNREAD = "unread";
        String PAGING = "paging";
        String CURSORS = "cursors";
        String BEFORE = "before";
        String AFTER = "after";
        String NEXT = "next";
        String PREVIOUS = "previous";
    }

    /*
    {
  "data": [
    {
      "from": {
        "name": "Vladimir Makarov",
        "id": "867531740000500"
      },
      "message": "This is a test post with multiple images",
      "name": "Photos from Vladimir Makarov's post",
      "picture": "https://fbcdn-photos-h-a.akamaihd.net/hphotos-ak-xta1/v/t1.0-0/p130x130/12036401_895444140542593_1597687779375581847_n.jpg?oh=824ee9912f8b51e06d7492cb03a1cb8e&oe=569CA8E4&__gda__=1453350801_db38c6668499528f818fef09a653529f",
      "type": "photo",
      "updated_time": "2015-09-25T12:33:39+0000",
      "link": "https://www.facebook.com/photo.php?fbid=895444140542593&set=gm.1035450203154182&type=3",
      "id": "961642513868285_1035450203154182",
      "comments": {
        "data": [
          {
            "from": {
              "name": "Chaitanya Jampani",
              "id": "1001846013201041"
            },
            "message": "Testing your new android app bro? ;)",
            "id": "1035450649820804"
          }
        ],
        "paging": {
          "cursors": {
            "after": "WTI5dGJXVnVkRjlqZFhKemIzSTZNVEF6TlRRMU1EWTBPVGd5TURnd05Eb3hORFF6TVRnME1qVTQ=",
            "before": "WTI5dGJXVnVkRjlqZFhKemIzSTZNVEF6TlRRMU1EWTBPVGd5TURnd05Eb3hORFF6TVRnME1qVTQ="
          }
        }
      },
      "attachments": {
        "data": [
          {
            "type": "album",
            "url": "https://www.facebook.com/media/set/?set=pcb.1035450203154182&type=1",
            "title": "Photos from Vladimir Makarov's post"
          }
        ]
      }
    },
    {
      "from": {
        "name": "Vladimir Makarov",
        "id": "867531740000500"
      },
      "message": "This is a test post with a YouTube video",
      "caption": "youtube.com",
      "description": "http://nicepeter.com for tickets. The Epic Rap Battles of History tour is underway, it is 3:40 pm, in Texas, as I write these words. My name is Nice Peter, e...",
      "name": "Epic Rap Battles of History News - 2015 Concert Tour",
      "picture": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQDtIoZ4Fxx42v_s&w=130&h=130&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2Fx23a-777UyQ%2Fmaxresdefault.jpg&cfs=1&sx=771&sy=0&sw=1231&sh=1231",
      "type": "video",
      "updated_time": "2015-09-25T12:32:53+0000",
      "link": "https://www.youtube.com/watch?v=x23a-777UyQ",
      "id": "961642513868285_1035451106487425",
      "attachments": {
        "data": [
          {
            "media": {
              "image": {
                "height": 720,
                "src": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQAD-1_XFLpFvo42&w=720&h=720&url=http%3A%2F%2Fi.ytimg.com%2Fvi%2Fx23a-777UyQ%2Fmaxresdefault.jpg&cfs=1&sx=771&sy=0&sw=1231&sh=1231",
                "width": 720
              }
            },
            "type": "video_share_youtube",
            "url": "https://www.facebook.com/l.php?u=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3Dx23a-777UyQ&h=eAQFfjj6y&s=1&enc=AZNz15XY02iNb9rgAi1VCy2CQR8pNB43Qq5wqlmf26Vbs3mHt1dri0lwkEl_MTLpIH8HVBDtObo1QgFUDavF_D7WkMJjVBLL5HTpOP8jwVV1Jw",
            "title": "Epic Rap Battles of History News - 2015 Concert Tour"
          }
        ]
      }
    },
    {
      "from": {
        "name": "Vladimir Makarov",
        "id": "867531740000500"
      },
      "message": "This is a test post with, it ll be removed",
      "caption": "9gag.com",
      "description": "Description",
      "name": "Name",
      "picture": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQB05jWENkgq0jc0&w=130&h=130&url=http%3A%2F%2Fimages-cdn.9gag.com%2Fphoto%2FanBEqP5_700b_v1.jpg&cfs=1",
      "type": "link",
      "updated_time": "2015-09-25T12:26:47+0000",
      "link": "http://9gag.com/gag/anBEqP5",
      "id": "961642513868285_1035449376487598",
      "attachments": {
        "data": [
          {
            "media": {
              "image": {
                "height": 600,
                "src": "https://fbexternal-a.akamaihd.net/safe_image.php?d=AQAlKN35ph6WhWqd&w=720&h=720&url=http%3A%2F%2Fimages-cdn.9gag.com%2Fphoto%2FanBEqP5_700b_v1.jpg&cfs=1",
                "width": 600
              }
            },
            "type": "share",
            "url": "http://www.facebook.com/l.php?u=http%3A%2F%2F9gag.com%2Fgag%2FanBEqP5&h=NAQHUpZ7x&s=1&enc=AZOak2yIjrsbKUFNjwcC8xk16i12-7N8ICNelZZIPma-MnD3p7WhSSZKMDfDTPW3G8BDjeyIdG52uxT1N5m2wU-UyOIxU8qsp4W0wDChxJmlVQ",
            "title": "Name"
          }
        ]
      }
    },
    {
      "from": {
        "name": "Andrew Magdy Kamal Nassief",
        "id": "950893981633609"
      },
      "message": "Now this might be Vladimir Makarov's favorite holiday, lol :P",
      "picture": "https://fbcdn-photos-e-a.akamaihd.net/hphotos-ak-xta1/v/t1.0-0/s130x130/11947582_937891949600479_26924043406517596_n.jpg?oh=c8139f23bd8856cd2fb94324ebdeb625&oe=569D2D28&__gda__=1453618474_098c381e997aaabb7aae7bd298160c42",
      "type": "photo",
      "updated_time": "2015-08-24T22:49:14+0000",
      "link": "https://www.facebook.com/photo.php?fbid=937891949600479&set=gm.1015042701861599&type=3",
      "id": "961642513868285_1015042701861599",
      "attachments": {
        "data": [
          {
            "media": {
              "image": {
                "height": 64,
                "src": "https://fbcdn-sphotos-e-a.akamaihd.net/hphotos-ak-xta1/v/t1.0-9/11947582_937891949600479_26924043406517596_n.jpg?oh=76b5a750eb99453c3a0edfc5e48e26fb&oe=56980713&__gda__=1452224300_1f386e06435f07232870ce7b1e4a3de0",
                "width": 250
              }
            },
            "type": "photo",
            "url": "https://www.facebook.com/photo.php?fbid=937891949600479&set=gm.1015042701861599&type=3"
          }
        ]
      }
    },
    {
      "from": {
        "name": "Vladimir Makarov",
        "id": "867531740000500"
      },
      "picture": "https://fbcdn-photos-b-a.akamaihd.net/hphotos-ak-xtp1/v/t1.0-0/s130x130/11261691_832285083525166_5497967526534581591_n.jpg?oh=c36e47205a4a9261dbd81c76fa878804&oe=56653E59&__gda__=1452604366_c7a63a1eb48dfef8cbad06c204ace210",
      "type": "photo",
      "updated_time": "2015-05-20T17:25:00+0000",
      "link": "https://www.facebook.com/photo.php?fbid=832285083525166&set=gm.961643317201538&type=3",
      "id": "961642513868285_961643317201538",
      "comments": {
        "data": [
          {
            "from": {
              "name": "Amit Nain",
              "id": "959691484094897"
            },
            "message": "(y) (y)",
            "id": "961720263860510"
          }
        ],
        "paging": {
          "cursors": {
            "after": "WTI5dGJXVnVkRjlqZFhKemIzSTZPVFl4TnpJd01qWXpPRFl3TlRFd09qRTBNekl4TkRJM01EQT0=",
            "before": "WTI5dGJXVnVkRjlqZFhKemIzSTZPVFl4TnpJd01qWXpPRFl3TlRFd09qRTBNekl4TkRJM01EQT0="
          }
        }
      },
      "attachments": {
        "data": [
          {
            "media": {
              "image": {
                "height": 265,
                "src": "https://fbcdn-sphotos-b-a.akamaihd.net/hphotos-ak-xtp1/v/t1.0-9/s720x720/11261691_832285083525166_5497967526534581591_n.jpg?oh=24f5545aa8bb9f87391e73e094d1d931&oe=56AAC9A6&__gda__=1449461809_2f4a0efada95475c15d87888ceb2bef9",
                "width": 720
              }
            },
            "type": "photo",
            "url": "https://www.facebook.com/photo.php?fbid=832285083525166&set=gm.961643317201538&type=3"
          }
        ]
      }
    },
    {
      "from": {
        "name": "Vijai Chander",
        "id": "439526892885517"
      },
      "message": "Yay! Finally an official group that allows spam! :D",
      "type": "status",
      "updated_time": "2015-05-20T13:00:39+0000",
      "id": "961642513868285_961661990533004"
    },
    {
      "from": {
        "name": "Kumaresh Chakraborty",
        "id": "914745011933507"
      },
      "message": "hahaha poopscoop xD",
      "type": "status",
      "updated_time": "2015-05-20T12:42:41+0000",
      "id": "961642513868285_961658597200010"
    },
    {
      "from": {
        "name": "Vladimir Makarov",
        "id": "867531740000500"
      },
      "type": "status",
      "updated_time": "2015-05-20T12:27:39+0000",
      "id": "961642513868285_961654853867051"
    },
    {
      "from": {
        "name": "Vladimir Makarov",
        "id": "867531740000500"
      },
      "message": "Share your shit, whatever you like :D",
      "picture": "https://fbcdn-photos-h-a.akamaihd.net/hphotos-ak-xfa1/v/t1.0-0/s130x130/11165296_832288683524806_6266362965911628394_n.jpg?oh=9c6aa5209de9b3daa213607463305974&oe=56A9A2F8&__gda__=1453751594_0a536e0ff5dc008d967ce12b528a7805",
      "type": "photo",
      "updated_time": "2015-05-20T12:19:09+0000",
      "link": "https://www.facebook.com/photo.php?fbid=832288683524806&set=gm.961653380533865&type=3",
      "id": "961642513868285_961653380533865",
      "attachments": {
        "data": [
          {
            "media": {
              "image": {
                "height": 265,
                "src": "https://fbcdn-sphotos-h-a.akamaihd.net/hphotos-ak-xfa1/v/t1.0-9/s720x720/11165296_832288683524806_6266362965911628394_n.jpg?oh=2f78cb82bdacadc92883145d50b26851&oe=56937507&__gda__=1454076373_878b658c6d65e461fad57a50d7d3d7ef",
                "width": 720
              }
            },
            "type": "photo",
            "url": "https://www.facebook.com/photo.php?fbid=832288683524806&set=gm.961653380533865&type=3"
          }
        ]
      }
    },
    {
      "from": {
        "name": "Vladimir Makarov",
        "id": "867531740000500"
      },
      "message": "Talk about the shit you love, others love, me loves and she loves",
      "type": "status",
      "updated_time": "2015-05-20T12:00:05+0000",
      "id": "961642513868285_961642710534932"
    },
    {
      "from": {
        "name": "Vladimir Makarov",
        "id": "867531740000500"
      },
      "type": "status",
      "updated_time": "2015-05-20T11:58:59+0000",
      "id": "961642513868285_961642517201618"
    }
  ],
  "paging": {
    "previous": "https://graph.facebook.com/v2.4/961642513868285/feed?fields=from,message,caption,comments%7Bfrom,message%7D,description,name,picture,type,updated_time,attachments%7Bmedia,type,url,title%7D,link&format=json&since=1443184419&access_token=CAAXv1PzFiNABAJlXSrRZCmYlH7UgZCssOmiASYlkmLofMjsGbKIZBy22Qpz05bqOMJV6MlrPxrBZCXJ7dLMn2uerOvSZCGscDubpfLXuqnL4FZB449xRBhP5rWFG7s261PQR6vUeGEAZAaDQkfBTSOQetsUy2CTFwu7ZCwVOVPMeADci7BFByIEFIrEEAHLp1HMflvR21hxNMahd1DDOJHa1&limit=25&__paging_token=enc_AdCRa0CsZAKbZCqzRZCsnhFC3OZAuOqz5hqaX1G5UzpZBRM611QfrFcCftViAmpBtpPms1zTwc1Kpn8dkOynHxWzE9i2WbZC8X2UoFZBxTGqPgZC3ZBYe6gZDZD&__previous=1",
    "next": "https://graph.facebook.com/v2.4/961642513868285/feed?fields=from,message,caption,comments%7Bfrom,message%7D,description,name,picture,type,updated_time,attachments%7Bmedia,type,url,title%7D,link&format=json&access_token=CAAXv1PzFiNABAJlXSrRZCmYlH7UgZCssOmiASYlkmLofMjsGbKIZBy22Qpz05bqOMJV6MlrPxrBZCXJ7dLMn2uerOvSZCGscDubpfLXuqnL4FZB449xRBhP5rWFG7s261PQR6vUeGEAZAaDQkfBTSOQetsUy2CTFwu7ZCwVOVPMeADci7BFByIEFIrEEAHLp1HMflvR21hxNMahd1DDOJHa1&limit=25&until=1432123139&__paging_token=enc_AdAAxRlzLiRMuaqYCDixZA47w6a4gl43oOBbidPbZAmnedHeMtvqWGPMIqMWojyKr5WUtfQZAqcE8yqGEdZACax5pxXhBUMpa6var7f2SbFWFaB8RAZDZD"
  }
}
     */
    public interface FeedFields {
        String DATA = "data";
        String FROM = "from";
        String ID = "id";
        String NAME = "name";
        String MESSAGE = "message";
        String CAPTION = "caption";
        String DESCRIPTION = "description";
        String PICTURE = "picture";
        String TYPE = "type";
        String UPDATED_TIME = "updated_time";
        String LINK = "link";
        String COMMENTS = "comments";
        String ATTACHMENTS = "attachments";
        String MEDIA = "media";
        String IMAGE = "image";
        String HEIGHT = "height";
        String WIDTH = "width";
        String SRC = "src";
        String TITLE = "title";
        String URL = "url";
        String PAGING = "paging";
        String CURSORS = "cursors";
        String BEFORE = "before";
        String AFTER = "after";
        String PREVIOUS = "previous";
        String NEXT = "next";
        String CREATED_TIME = "created_time";
    }
}
