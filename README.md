# Fpam
You know what it does, don't you?
There can be posts made by people who don't exist
![people without names posting stuff](https://cloud.githubusercontent.com/assets/5139030/9966000/c7686352-5e57-11e5-89d4-47725ba6f76d.png)

This example post below is made by a person who doesn't exist on Facebook anymore or was banned
![snap 2015-09-18 at 22 32 38](https://cloud.githubusercontent.com/assets/5139030/9966022/e13ecb40-5e57-11e5-9c21-49724fcdd84a.png)

UPDATE 1 [September 20, 2015 9+ pm writing after a nice BR ice cream and chat]

The next question is to determine how to load the feeds optimally. There are N groups that a person may own. Lets say we wanna load the feed for Android Programming , we have a database table called cachestack that knows the ID of a group for which it is loading the feed, if we have never loaded the feed before, then either the database is empty or does not return a result with the group ID for which we are loading currently, in such a case, load the group ID, load N items , store a row in the table that indicates the group ID, the last loaded time, the number of times the group was loaded so far, if we are loading the feed for that group the subsequent time, first get the last loaded time, use this paramter to fetch all the posts from that group that were updated since the last loaded time, if the request was successful, update the last loaded time in the database and the number of times the group was loaded so far. For every group, maintain a row in the table that indicates when its feed was last loaded.

UPDATE 2 [September 20, 2015 9+ pm]
Currently, only the list of groups on 1 page are being stored, gotta find a way to use the paging cursors and fetch all groups in one stroke or cache the paging cursors, the most important question here is how to store paging cursors, since Facebook doc says they are unstable and should not be stored permanently.

UPDATE 3 [September 20, 2015 9+ pm]

The current model for Feed processing takes separate model classes, one for GSON that implements a parcelable and one for Realm that doesn't . Need to keep one single class that directly stores stuff from JSON to Realm, and eliminates the need for any intermediate GSON, the idea is also to remember the fact that only a max of 500 posts should be stored per group and when a post is deleted, all its attachments and comments also be deleted.

UPDATE 4[September 24, 2015, 5:20 pm]
<b>Login Screen</b>
![snap 2015-09-24 at 17 19 17](https://cloud.githubusercontent.com/assets/5139030/10072586/7bf45402-62e0-11e5-8de6-2a17a1a8ad07.png)

This is the first screen a person sees when they start the app, if they are already logged in, they wont see this screen
