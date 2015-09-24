

<h1>SCREENS</h1> [September 24, 2015, 5:20 pm]

<b>Login Screen</b>

![snap 2015-09-24 at 21 13 56](https://cloud.githubusercontent.com/assets/5139030/10078181/36276e16-6301-11e5-987c-5ed6d842206c.png)

This is the first screen a person sees when they start the app, if they are already logged in, they wont see this screen

<b>The Drawer</b>

![snap 2015-09-24 at 21 14 44](https://cloud.githubusercontent.com/assets/5139030/10078208/4c99bcd0-6301-11e5-916c-325150fd3f3d.png)

This is the navigation drawer that displays a list of all the groups a person owns or NONE if the person doesn't and provides options to logout and access settings.

<b>The Main Screen</b>

![snap 2015-09-24 at 21 15 24](https://cloud.githubusercontent.com/assets/5139030/10078230/6766d246-6301-11e5-9217-814abc854717.png)

This is the screen showing the posts and comments from the currently selected group along with important statistics for that group. Question to answer would be what statistics, how to display posts, how to display comments

![snap 2015-09-24 at 21 16 26](https://cloud.githubusercontent.com/assets/5139030/10078271/88c8ffb8-6301-11e5-9491-0d560703e5ea.png)

![snap 2015-09-24 at 21 17 03](https://cloud.githubusercontent.com/assets/5139030/10078293/a005acb2-6301-11e5-8546-f8c98dd0a65d.png)

![snap 2015-09-24 at 21 18 19](https://cloud.githubusercontent.com/assets/5139030/10078328/ccb9309e-6301-11e5-99d5-d36cea9e81ed.png)



# Fpam
You know what it does, don't you?
There can be posts made by people who don't exist
![people without names posting stuff](https://cloud.githubusercontent.com/assets/5139030/9966000/c7686352-5e57-11e5-89d4-47725ba6f76d.png)

This example post below is made by a person who doesn't exist on Facebook anymore or was banned
![snap 2015-09-18 at 22 32 38](https://cloud.githubusercontent.com/assets/5139030/9966022/e13ecb40-5e57-11e5-9c21-49724fcdd84a.png)

<h2>What statistics?</h2>
<ol>
<li>% spam posts</li>
<li>% spam comments</li>
<li>Number of posts read so far</li>
<li>Number of comments read so far</li>
<li>Top 10 Spammers</li>
<li>Top 10 spam links</li>
<li>Top 10 spam words found</li>
<li>What about timelines?</li>

<li>Last 24 hours</li>
<li>Past 1 week</li>
</ol>
The visual representation for 
Each?

UPDATE 1 [September 20, 2015 9+ pm writing after a nice BR ice cream and chat]

The next question is to determine how to load the feeds optimally. There are N groups that a person may own. Lets say we wanna load the feed for Android Programming , we have a database table called cachestack that knows the ID of a group for which it is loading the feed, if we have never loaded the feed before, then either the database is empty or does not return a result with the group ID for which we are loading currently, in such a case, load the group ID, load N items , store a row in the table that indicates the group ID, the last loaded time, the number of times the group was loaded so far, if we are loading the feed for that group the subsequent time, first get the last loaded time, use this paramter to fetch all the posts from that group that were updated since the last loaded time, if the request was successful, update the last loaded time in the database and the number of times the group was loaded so far. For every group, maintain a row in the table that indicates when its feed was last loaded.

UPDATE 2 [September 20, 2015 9+ pm]
Currently, only the list of groups on 1 page are being stored, gotta find a way to use the paging cursors and fetch all groups in one stroke or cache the paging cursors, the most important question here is how to store paging cursors, since Facebook doc says they are unstable and should not be stored permanently.

UPDATE 3 [September 20, 2015 9+ pm]

The current model for Feed processing takes separate model classes, one for GSON that implements a parcelable and one for Realm that doesn't . Need to keep one single class that directly stores stuff from JSON to Realm, and eliminates the need for any intermediate GSON, the idea is also to remember the fact that only a max of 500 posts should be stored per group and when a post is deleted, all its attachments and comments also be deleted.
