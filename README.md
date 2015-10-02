

Screens [September 24, 2015, 5:20 pm]

<b>Login Screen</b>

![snap 2015-09-24 at 21 13 56](https://cloud.githubusercontent.com/assets/5139030/10078181/36276e16-6301-11e5-987c-5ed6d842206c.png)

This is the first screen a person sees when they start the app, if they are already logged in, they wont see this screen.

<b>The Drawer</b>

![snap 2015-09-24 at 21 14 44](https://cloud.githubusercontent.com/assets/5139030/10078208/4c99bcd0-6301-11e5-916c-325150fd3f3d.png)

This is the navigation drawer that displays a list of all the groups a person owns or NONE if the person doesn't and provides options to logout and access settings.

<b>The Main Screen</b>

![snap 2015-09-24 at 21 15 24](https://cloud.githubusercontent.com/assets/5139030/10078230/6766d246-6301-11e5-9217-814abc854717.png)

This is the screen showing the posts and comments from the currently selected group along with important statistics for that group. Question to answer would be what statistics, how to display posts, how to display comments

![snap 2015-09-24 at 21 16 26](https://cloud.githubusercontent.com/assets/5139030/10078271/88c8ffb8-6301-11e5-9491-0d560703e5ea.png)

![snap 2015-09-24 at 21 17 03](https://cloud.githubusercontent.com/assets/5139030/10078293/a005acb2-6301-11e5-8546-f8c98dd0a65d.png)

![snap 2015-09-24 at 21 18 19](https://cloud.githubusercontent.com/assets/5139030/10078328/ccb9309e-6301-11e5-99d5-d36cea9e81ed.png)

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

The Database with posts from PoopScoop, Android Programming and IOS Programming

![snap 2015-09-25 at 21 49 20](https://cloud.githubusercontent.com/assets/5139030/10105978/a8fecfd2-63cf-11e5-986e-f53d29edcd16.png)

The Delete problem

Fpam as of September 26 8:30 am has a drawback where if a post is deleted using Fpam, it will be deleted from the underlying database after its successfully deleted from Facebook Graph API. However if a person deletes a post from Facebook directly, the deleted post is still present in the Fpam database. An upper limit of 100 posts per group also needs to be enforced on Fpam so that it deletes the oldest post and stores the newest one when it crosses the limit. 

Is the database empty, is the feed for this group being loaded the first time?
    If yes, store the JSON feed directly
    If no, find the list of common posts between the JSON feed and posts stored in the database.
    There is nothing common
      Dont process any further
    There is something common
      Find the list of all posts that are contained in both the JSON feed and the database. Lets call it List<C>
      Sort these posts in order of their updated time
      Find the updated time of the oldest common post and the newest common post
      Find all the posts stored in the database between this time range. Lets call it List<U>
      Compare the posts between List<C> and List<U>
        If a post is present in List<U> but not in List<C> delete the post
        If a post is present in List<U> and List<C> update the post
      

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

UPDATE 4 [September 26, 2015, 10+ pm]

As of now, admin, groups, posts and feed details are stored with manual JSON parsing. Each time the same group is loaded, it simply overrides or replaces what was already present.

UPDATE 5 [September 29, 2015, 10 am]

<ol>

<li><b>Gson Deserializer for Admin and Group Done</b></li>
<li><b>Eliminated Groups class to simplify model</b></li>
<li><b>Pagination for Groups implemented</b></li>
<li><b>SIMPLE DELETE for POSTS IMPLEMENTED</b></li>
<li><b>Eliminate Feed model class</b></li>
<li><b>Write Feed deserializer</b></li>
<li>Support pagination at the top and pagination at the bottom for posts</li>
<li>Enforce database limits for storing posts</li>

</ol>

<ol>

<li>Load feed by using since and until or a combination of them</li>
<li>Support pagination for feeds</li>
<li>Store comments and attachments</li>
<li>use a sliding panel to display posts and comments</li>
<li><b>Swipe to delete posts from facebook first and then from realm on success</b></li>
<li>Swipe to delete comments from facebook first and then from realm on success</li>
<li>Cascade deletion of comments for a related post<li>
<li>Commence analytics and spam processing work</li>

</ol>

UPDATE 6, Oct 1 , 2015 [9:30 am - 1:30 pm]
Hired Neel Raj by paying him 5000 bucks upfront for Fpam App design. 

UPDATE 7, Oct 2, 2015 [8:20pm]

What would complete Fpam?
<ol>
<li>Create the IntentService for API<21 and JobScheduler Service for API>21 [HIGH]</li>
<li>Make a viable approach to detect duplicate posts [MODERATE]</li>
<li>Determine if a spammer must be blocked instantly or after 3 tries in a viable way [MODERATE]</li>
<li>Show images of the post along with caption, links, description [HIGH]</li>
<li>Show comments in a sliding panel and let people swipe to delete comments [HIGH]</li>
<li>Add morphing animation to turn the facebook login button into something with a progress bar [LOW]</li>
<li>Show a tutorial for how to use the app at the login screen [LOW]</li>
<li>Add rate my app feature [MODERATE]</li>
<li>Make a mechanism to find all groups a person owns and message other admins in random intervals of time to promote the app [LOW]</li>
<li>Exempt yourself from spam processing [HIGH]</li>
<li>Create Analytics database [HIGH]</li>
<li>Collect analytics data [HIGH]</li>
<li>Sync analytics data from app to server [HIGH]</li>
<li>Enforce database limits for posts stored [HIGH]</li>
<li>Figure out scrap post deletion [post deleted from facebook but present in fpam database] [HIGH]</li>
</ol>

The Workflow

When the user logs in, get the list of groups and user details and store them in Realm. 
When the user clicks on a group, load the posts with that group postId and store it in Realm. If there are more than 100 posts that are already stored for that group, delete the oldest N entries and add the new ones, update all existing posts, comments, attachments and any other detail.

The Algorithm

The 4 pieces of information that we need to analyze are : the person who posted, the message that was posted if any, the link tag if any and the picture if any. The message that was posted may be of 4 types 1) no message or no text 2) only text 3) message with only a link inside its contents 4)message with text and one or more link in its contents.

<ul>

    <li>Read a post and scan its message, link from its link property, picture and person who posted it</li>
    <li>Is this person present in the spammers database?</li>
    <ul>
        <li>Regardless of whether the post is a spam or not, [for analytics purpose]
            <ul>
                <li>Which group is this post read from? Track the group id</li>
                <li>How many posts have been read so far? Increment the number of posts read so far.</li>
                <li>What are the properties of this post?
                    <ul>
                        <li>Language in which this post is written [Implement if possible now]</li>
                        <li>Does it have a link tag?</li>
                        <li>Does it have a picture tag?</li>
                        <li>Does it have a message tag?
                            <ul>
                                <li>Number of words</li>
                                <li>Number of characters</li>
                                <li>Percentage of capslock or capital to small letters [spam posts often have capital letters in them]</li>
                                <li>Number of emoticons detected [spam posts often use many emoticons]</li>
                                <li>Percentage of emoticons to actual content in the post</li>
                            </ul>
                        </li>
                    </ul>
                </li>
            </ul>
        </li>
        <li>If Yes, 
            <ul>
                <li>Delete the post</li>
                <li>Increment the number of spam posts made by the person</li>
                <li>What time was it created?</li>
                <li>What time was it updated?</li>
                <li>Are the update_time and create_time the same?</li>
            </ul>
        </li>
        <li> If No,
            <ul>
                <li>Are link tags allowed?</li>
                    <li>If No,
                        <ul>
                            <li>Delete the post</li>
                            <li>Add this person to the spammers database and increment the number of posts made by him/her
                                <ul>
                                    <li>Is the link present in the blacklist [for analytics purpose]
                                        <ul>
                                            <li>If Yes, increase the number of times this link was found in the blacklist</li>
                                            <li>Properties of this link [for analytics purpose]
                                                <ul>
                                                    <li>number of characters in the primary domain name [stackoverflow = 13]</li>
                                                    <li>number of characters in the domain extension [.com = 3]</li>
                                                    <li>type of domain extension [.com, .io, .org etc]</li>
                                                    <li>does the url have a path</li>
                                                </ul>
                                            </li>
                                            <li>If No, do nothing here</li>
                                        </ul>
                                    </li>
                                    <li>Is the link present in the whitelist [for analytics purpose]
                                        <ul>
                                            <li>If Yes, increase the number of times this link was found in the whitelist</li>
                                            <li>Properties of this link [for analytics purpose]
                                                <ul>
                                                    <li>number of characters in the primary domain name [stackoverflow = 13]</li>
                                                    <li>number of characters in the domain extension [.com = 3]</li>
                                                    <li>type of domain extension [.com, .io, .org etc]</li>
                                                    <li>does the url have a path</li>
                                                </ul>
                                            </li>
                                            <li>If No, do nothing here</li>
                                        </ul>
                                    </li>
                                </ul>
                            </li>
                        </ul>    
                    </li>
                    <li>If Yes, jump to picture allowed</li>
                <li>Are Pictures Allowed?</li>
                <li>If No,
                    <ul>
                        <li>Delete the post</li>
                        <li>Add this person to the spammers database and increment the number of posts made by him/her</li>
                    </ul>
                </li>
                <li>If Yes, jump to message processing</li>
                <li>What type of message is it?
                    <ul>
                        <li>Message with no text
                            <ul>
                                <li>Are empty messages allowed?
                                    <ul>
                                        <li>If No, 
                                            <ul>
                                                <li>Delete the post</li>
                                                <li>Add this person to the spammers database and increment the number of posts made by him/her</li>
                                            </ul>
                                        </li>
                                        <li>If Yes, <b>What to do if picture is found here?</b></li>   
                                    </ul>
                                </li>
                            </ul>
                        </li>
                        <li>Message with only text
                            <ul>
                                <li>Does it have spam phrases or words?
                                    <ul>
                                        <li>If No, jump to link processing</li>
                                        <li>If Yes, 
                                            <ul>
                                                <li>Delete the post</li>
                                                <li>Add this person to the spammers database</li>
                                                <li>Increment the number of posts made by him/her.</li>
                                                <li>Increment the number of times the spam word or words were found</li>
                                            </ul>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </li>
                        <li>Message with only link in its contents
                            <ul>
                                <li><b>jump to link processsing</b></li>
                            </ul>
                        </li>
                        <li>Message with text and link in its contents
                            <ul>
                                <li>Does it have spam phrases or words?
                                    <ul>
                                        <li>If No, jump to message link processing</li>
                                        <li>If Yes, 
                                            <ul>
                                                <li>Delete the post</li>
                                                <li>Add this person to the spammers database</li>
                                                <li>Increment the number of posts made by him/her.</li>
                                                <li>Increment the number of times the spam word or words were found</li>
                                            </ul>
                                        </li>
                                    </ul>
                                </li>
                                <li>jump to link processing</li>
                            </ul>
                        </li>
                    </ul>
                </li>
                <li>For link processing
                    <ul>
                        <li>Regardless of where the link is found, analyze properties of this link [for analytics purpose]
                            <ul>
                                <li>number of characters in the primary domain name [stackoverflow = 13]</li>
                                <li>number of characters in the domain extension [.com = 3]</li>
                                <li>type of domain extension [.com, .io, .org etc]</li>
                                <li>does the url have a path</li>
                            </ul>
                        </li>
                    </ul>
                    <ul>
                        <li>Is the link present in the blacklist?
                            <ul>
                                <li>Delete the post</li>
                                <li>Add this person to the spammers database and increment the number of posts made by him/her</li>
                            </ul>
                        </li>
                        <li>Is the link present in the whitelist?
                            <ul>
                                <li>Approve the post</li>
                            </ul>
                        </li>
                        <li>Is the link uncategorized?
                            <ul>
                                <li>Store this link in a data structure where the link is unique and a single link points to several post ids.</li>
                            </ul>
                        </li>
                    </ul>
                </li>
            </ul>
        </li>
    
    </ul>
    </li>

</ul>
