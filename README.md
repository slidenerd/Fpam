

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

Fpam as of September 26 8:30 am has a drawback where if a postId is deleted using Fpam, it will be deleted from the underlying database after its successfully deleted from Facebook Graph API. However if a person deletes a postId from Facebook directly, the deleted postId is still present in the Fpam database. An upper limit of 100 posts per group also needs to be enforced on Fpam so that it deletes the oldest postId and stores the newest one when it crosses the limit.

Is the database empty, is the groupMetaData for this group being loaded the first time?
    If yes, store the JSON groupMetaData directly
    If no, find the list of common posts between the JSON groupMetaData and posts stored in the database.
    There is nothing common
      Dont process any further
    There is something common
      Find the list of all posts that are contained in both the JSON groupMetaData and the database. Lets call it List<C>
      Sort these posts in order of their updated time
      Find the updated time of the oldest common postId and the newest common postId
      Find all the posts stored in the database between this time range. Lets call it List<U>
      Compare the posts between List<C> and List<U>
        If a postId is present in List<U> but not in List<C> delete the postId
        If a postId is present in List<U> and List<C> update the postId
      

# Fpam
You know what it does, don't you?
There can be posts made by people who don't exist
![people without names posting stuff](https://cloud.githubusercontent.com/assets/5139030/9966000/c7686352-5e57-11e5-89d4-47725ba6f76d.png)

This example postId below is made by a person who doesn't exist on Facebook anymore or was banned
![snap 2015-09-18 at 22 32 38](https://cloud.githubusercontent.com/assets/5139030/9966022/e13ecb40-5e57-11e5-9c21-49724fcdd84a.png)


UPDATE 1 [September 20, 2015 9+ pm writing after a nice BR ice cream and chat]

The next question is to determine how to load the feeds optimally. There are N groups that a person may own. Lets say we wanna load the groupMetaData for Android Programming , we have a database table called cachestack that knows the ID of a group for which it is loading the groupMetaData, if we have never loaded the groupMetaData before, then either the database is empty or does not return a result with the group ID for which we are loading currently, in such a case, load the group ID, load N items , store a row in the table that indicates the group ID, the last loaded time, the number of times the group was loaded so far, if we are loading the groupMetaData for that group the subsequent time, first get the last loaded time, use this paramter to fetch all the posts from that group that were updated since the last loaded time, if the request was successful, update the last loaded time in the database and the number of times the group was loaded so far. For every group, maintain a row in the table that indicates when its groupMetaData was last loaded.

UPDATE 2 [September 20, 2015 9+ pm]
Currently, only the list of groups on 1 page are being stored, gotta find a way to use the paging cursors and fetch all groups in one stroke or cache the paging cursors, the most important question here is how to store paging cursors, since Facebook doc says they are unstable and should not be stored permanently.

UPDATE 3 [September 20, 2015 9+ pm]

The current model for Feed processing takes separate model classes, one for GSON that implements a parcelable and one for Realm that doesn't . Need to keep one single class that directly stores stuff from JSON to Realm, and eliminates the need for any intermediate GSON, the idea is also to remember the fact that only a max of 500 posts should be stored per group and when a postId is deleted, all its attachments and comments also be deleted.

UPDATE 4 [September 26, 2015, 10+ pm]

As of now, admin, groups, posts and groupMetaData details are stored with manual JSON parsing. Each time the same group is loaded, it simply overrides or replaces what was already present.

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

<li>Load groupMetaData by using since and until or a combination of them</li>
<li>Support pagination for feeds</li>
<li>Store comments and attachments</li>
<li>use a sliding panel to display posts and comments</li>
<li><b>Swipe to delete posts from facebook first and then from realm on success</b></li>
<li>Swipe to delete comments from facebook first and then from realm on success</li>
<li>Cascade deletion of comments for a related postId<li>
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
<li>Show images of the postId along with caption, links, description [HIGH]</li>
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
<li>Figure out scrap postId deletion [postId deleted from facebook but present in fpam database] [HIGH]</li>
</ol>

UPDATE 8, Oct 5, 2015 [4:30pm]

Make the MVP of fpam capable of only blocking people in v1. This will ensure the app is released aptly, before 17th October 2015. 

<h3>Things needed for the MVP Fpam v1.0</h3>
<ul>
	<li>Process only spammers in v1.0</li>
	<li>let admin decide which groups to scan</li>
	<li>If a postId is deleted, mark the person as a spammer and block all further posts from that person for all groups.</li>
	<li>A spammer is a member of usually more than one group and as such, keep a track of the user groupId, user groupName of the spammer, list of group ids where he is a member of and the number of times he or she has spammed.</li>
	<li>Enable spam filtering for both posts and comments {show the dual pane with posts and comments} and delete the postId or comment on swipe and add the person to the blacklist if he/she is not already a member</li>
	<li>Send data about spammers to the server fpam.io with the same information, [userid, username, list of group ids where the spammer is a member of and the number of spam posts he or she has made] corresponding to a user groupId or the person using the app fpam.</li>
</ul>

UPDATE 9, Nov 1, 2015 [12:50 pm]

<h3> What finishes Fpam now? </h3>

<ol>
<li>Add the spam words screen</li>
<li>Add the spammers screen</li>
<li>Swipe to refresh at the top to refresh and add new posts</li>
<li>Load more at the bottom to load old posts older than the one which is loaded</li>
<li>Refresh option to completely erase all posts in a group and load fresh ones</li>
<li>Optimize delete algorithm</li>
<li>Background scan for lollipop and pre lollipop devices</li>
<li>Generate analytics data appropriately</li>
<li>Display analytics UI</li>
<li>Transport anayltics data from app to backend and vice versa</li>
<li>Allow import and export of data as needed</li>
</ol>



The Workflow

When the user logs in, get the list of groups and user details and store them in Realm. 
When the user clicks on a group, load the posts with that group postId and store it in Realm. If there are more than 100 posts that are already stored for that group, delete the oldest N entries and add the new ones, update all existing posts, comments, attachments and any other detail.

The Algorithm

There are some important considerations about how data is processed with Fpam, links may be from facebook either in the picture section or the link section or the message section. The question is on how to deal with links from Facebook.

A link/links found within the description or caption or message will be hence forth called LINK_CONTENT and a link found within the link tag in the json groupMetaData if it is non null will be hence forth called LINK_TAG.

The 4 pieces of information that we need to analyze are : the person who posted, the message that was posted if any, the LINK_TAG if any and the picture if any. The message that was posted may be of 4 types 1) no message or no text 2) only text 3) message with only LINK_CONTENT inside its contents 4)message with text and one or more LINK_CONTENT in its contents.

Analytics [Overall for each postId]

<ul>
    <li>Which group is this postId read from? Track the group groupId</li>
    <li>How many posts have been read so far? Increment the number of posts read so far.</li>
    <li>What are the properties of this postId?
        <ul>
            <li>Language in which this postId is written [Implement if possible now]</li>
            <li>Does it have a link tag?</li>
            <li>Does it have a picture tag?</li>
            <li>Does it have a message tag?
                <ul>
                    <li>Number of words</li>
                    <li>Number of characters</li>
                    <li>Percentage of capslock or capital to small letters [spam posts often have capital letters in them]</li>
                    <li>Number of emoticons detected [spam posts often use many emoticons]</li>
                    <li>Percentage of emoticons to actual content in the postId</li>
                </ul>
            </li>
        </ul>
    </li>
</ul>

<ul>
	<dl>
		<dt>CONTENT</dt>
		<dd>A postId contains several optional fields such as groupName, message, caption, description, link and the person who posted it (person is optional as well) which will be referred to henceforth as CONTENT</dd>
		<dt>MESSAGE</dt>
		<dd>A postId may or may not contain data for the json tag message which will be referred to henceforth as MESSAGE</dd>
		<dt>LINK_CONTENT</dt>
		<dd>A postId may has 0 to many links inside the message json tag which will be referred to henceforth as LINK_CONTENT </dd>
		<dt>LINK_TAG</dt>
		<dd>A postId may have 0 or 1 link tag in its json groupMetaData which will be referred to henceforth as LINK_TAG</dd>
		<dt>LINK_SET</dt>
		<dd>A postId may contain 0 to many LINK_CONTENT or LINK_TAG items which will be combined referred to henceforth as LINK_SET</dd>
		<dt>NON EXISTING USER</dt>
		<dd>The data of a person making a postId may or may not be available depending on whether the person's facebook profile is intact on Facebook. If there is no data regarding the person who made a postId, that postId is considered rogue and attributed to a fictional user called NON EXISTING USER</dd>
	</dl>
	<li>Extract the set of all links from a postId which consitute the LINK_SET. The goal of the algorithm is to decide if a postId is spam as fast as possible.
	<ol>
		<li>Read a postId</li>
		<li>Is there a person available for this postId? {A person may be null if he/she was banned or doesnt exist on Facebook anymore}
		<ul>
			<li>If PERSON not available,
			<ol>
				<li>Delete the postId</li>
				<li>Find the group groupId where this postId was made.</li>
				<li>Increment the number of posts made by NON EXISTING USER for that group groupId</li>
				<li>Skip FURTHER PROCESSING</li>
			</ol>
			</li>
			<li>If PERSON is available, PROCESS THE PERSON</li>
		</ul>
		</li>
		<li>PROCESS THE PERSON
		<ul>
			<li>Is the person in the SPAMMERS database?
			<ol>
				<li>Delete the postId</li>
				<li>Note the group groupId where this postId was made</li>
				<li>Increment the number of spam posts made by this person under that group groupId</li>
				<li>Note the created time of this postId [analytics]</li>
				<li>Note the updated time of this postId [analytics]</li>
				<li>Note whether created time and updated time are same or different? [analytics]</li>
				<li>Skip FURTHER PROCESSING</li>
			</ol>
			</li>
			<li>If the person is NOT present in the SPAMMERS database, process that element first on the basis of which we can instantly classify this as a SPAM postId or GOOD postId. The postId can be eliminated very quickly as SPAM if PICTURES are not authorized by the admin or LINK_SET is not authorized by the admin. The postId will require significant amount of processing to check for spam words and even more processing if has to check LINK_SET since it requires admin approval. Jump to CHECK FOR PICTURE</li>
		</ul>
		</li>
		<li>CHECK FOR PICTURE, does the postId contain a picture?
			<ul>
				<li>If NO PICTURE is found, jump to PROCESS THE CONTENT</li>
				<li>If PICTURE is found, has the admin authorized posts to be made that contain a PICTURE?
				<ul>
					<li>If NOT ALLOWED,
					<ol>
						<li>Delete the postId</li>
						<li>Note the group groupId where this postId was made</li>
						<li>Add this person to the SPAMMERS database</li>
						<li>Increment the number of spam posts made by this person under that group groupId</li>
						<li>Note the created time of this postId [analytics]</li>
						<li>Note the updated time of this postId [analytics]</li>
						<li>Note whether created time and updated time are same or different [analytics]</li>
						<li>Skip FURTHER PROCESSING</li>
					</ol>
					</li>
					<li>If ALLOWED, it means our postId at this postId contains a PICTURE for certain and may include other optional entities which would be CONTENT or LINK_SET Jump to PROCESS THE CONTENT</li>
				</ul>
				</li>
			</ul>
		</li>
		<li>PROCESS THE CONTENT, What type of content is it? There are 4 types of posts 
		<dl>
			<dt>MESSAGE_EMPTY</dt>
			<dd>A postId with no message tag data but may contain data in the other fields such as groupName, caption, description which will referred to henceforth as MESSAGE_EMPTY</dd>
			<dl>MESSAGE_ONLY_LINK</dl>
			<dd>A postId with message tag that has only a link in it also known as LINK_CONTENT and may contain data in the other fields such as groupName, caption, description which will be referred to henceforth as MESSAGE_ONLY_LINK</dd>
			<dl>MESSAGE_TEXT</dl>
			<dd>A postId with message tag that has only text and may contain data in the other fields such as groupName, caption, description which will be referred to henceforth as MESSAGE_TEXT</dd>
			<dl>MESSAGE_TEXT_AND_LINK</dl>
			<dd>A postId with message that that has text and LINK_CONTENT and may contain data in the other fields such as groupName, caption, description which will be referred to henceforth as MESSAGE_TEXT_AND_LINK</dd>
		</dl>
		<ol>
			<li>For a postId of type MESSAGE_EMPTY
			<ul>
				<li>If MESSAGE is NOT found, does the ADMIN allow empty posts? or posts that do not have a MESSAGE in them?
					<ul>
						<li>If no which implies that postId is empty and the admin doesnt allow it,
						<ol>
							<li>Delete the postId</li>
							<li>Note the group groupId where this postId was made</li>
							<li>Add this person to the SPAMMERS database</li>
							<li>Increment the number of spam posts made by this person under that group groupId</li>
							<li>Note the created time of this postId [analytics]</li>
							<li>Note the updated time of this postId [analytics]</li>
							<li>Note whether created time and updated time are same or different [analytics]</li>
							<li>Skip FURTHER PROCESSING</li>
						</ol> 
						</li>
						<li>If yes which implies that the postId has a message which is empty and the admin allows it, but we need to process the other elements  such as {groupName, caption, description}, does this postId contain OPTIONAL elements?
						<ul>
							<li>When the postId contains only OPTIONAL elements, PROCESS OPTIONAL ELEMENTS</li>
							<li>When the postId contains only LINK_SET, PROCESS LINK SET</li>
							<li>When the postId has no OPTIONAL elements  and no LINK_SET delete the postId since there is nothing meaningful in the postId</li>
							<li>When the postId has both OPTIONAL elements and LINK_SET, first PROCESS OPTIONAL ELEMENTS and then based on the outcome {if its a spam or not} PROCESS LINK SET {if it was not spam from the previous step}</li>
						</ul>
						</li>
					</ul>
				</li>
			</ul>
			</li>
			<li>For a postId of type MESSAGE_TEXT, does the postId contain words or phrases that match with one or more words or phrases from the spam database?
			<ul>
				<li>If NO MATCH found, 
				<ul>
					<li>When the postId contains only OPTIONAL elements, PROCESS OPTIONAL ELEMENTS</li>
					<li>When the postId contains only LINK_SET, PROCESS LINK SET</li>
					<li>When the postId has no OPTIONAL_ELEMENTS and no LINK_SET approve the postId</li>
					<li>When the postId has both OPTIONAL elements and LINK_SET, first PROCESS OPTIONAL ELEMENTS and then based on the outcome {if its a spam or not} PROCESS LINK SET {if it was not spam from the previous step}</li>
				</ul>
				</li>
				<li>If one or more matches are found, 
				<ol>
					<li>Delete the postId</li>
					<li>Note the group groupId where this postId was made</li>
					<li>Add this person to the SPAMMERS database</li>
					<li>Increment the number of spam posts made by this person under that group groupId</li>
					<li>Increment the counter of each spam word that was a match [analytics]</li>
					<li>Note the created time of this postId [analytics]</li>
					<li>Note the updated time of this postId [analytics]</li>
					<li>Note whether created time and updated time are same or different [analytics]</li>
					<li>Skip FURTHER PROCESSING</li>
				</ol>
				</li>
			</ul>
			</li>
			<li>For a postId of type MESSAGE_ONLY_LINK, our job is to classify a postId as spam as fast as possible with minimal user input. Since the postId has only a LINK_CONTENT and optional elements, the LINK_CONTENT will require manual admin approval but the optional elements such as caption, groupName or description can be used to indicate if a postId must be classfied as spam or not
			<ul>
				<li>When the postId contains only OPTIONAL elements, PROCESS OPTIONAL ELEMENTS</li>
				<li>When the postId contains only LINK_SET, PROCESS LINK SET</li>
				<li>Since we already have a LINK_CONTENT, we dont encounter a case where the postId has neither OPTIONALS nor LINK_SET</li>
				<li>When the postId has both OPTIONAL elements and LINK_SET, first PROCESS OPTIONAL ELEMENTS and then based on the outcome {if its a spam or not} PROCESS LINK SET {if it was not spam from the previous step}</li>
			</ul>
			</li>
			<li>For a postId of type MESSAGE_TEXT_AND_LINK, lets first process the message part that may tell us if it is a spam by simply doing spam word comparisons, if the outcome of the spam word comparisons is not conclusive, then PROCESS OPTIONAL ELEMENTS and depending on the outcome there, make a further jump to PROCESS LINK SET
			<ul>
				<li>Does the postId contain words or phrases that match with one or more words or phrases from the spam database?
				<ul>
					<li>If NO MATCH found, 
					<ul>
						<li>When the postId contains only OPTIONAL elements, PROCESS OPTIONAL ELEMENTS and then based on the outcome {if its a spam or not}, PROCESS LINK SET{if it was not spam from the previous step}</li>
						<li>When the postId contains only LINK_SET, PROCESS LINK SET</li>
						<li>Since we already have a LINK_CONTENT, we dont encounter a case where the postId has neither OPTIONALS nor LINK_SET</li>
						<li>When the postId has both OPTIONAL elements and LINK_SET, PROCESS OPTIONAL ELEMENTS and then based on the outcome {if its a spam or not}, PROCESS LINK SET{if it was not spam from the previous step}</li>
					</ul>
					</li>
					<li>If one or more matches are found, 
					<ol>
						<li>Delete the postId</li>
						<li>Note the group groupId where this postId was made</li>
						<li>Add this person to the SPAMMERS database</li>
						<li>Increment the number of spam posts made by this person under that group groupId</li>
						<li>Increment the counter of each spam word that was a match [analytics]</li>
						<li>Note the created time of this postId [analytics]</li>
						<li>Note the updated time of this postId [analytics]</li>
						<li>Note whether created time and updated time are same or different [analytics]</li>
						<li>Skip FURTHER PROCESSING</li>
					</ol>
					</li>
				</ul>
				</li>
			</ul>
			</li>
		</ol>
		</li>
		<li>PROCESS OPTIONAL ELEMENTS
		<ul>
			<li>Does the combination of the optional elements groupName, description, caption contain words or phrases that match with one or more words or phrases from the spam database?
			<ul>
				<li>If NO MATCH found, then jump to PROCESS LINK SET</li>
				<li>If one or more matches are found, 
				<ol>
					<li>Delete the postId</li>
					<li>Note the group groupId where this postId was made</li>
					<li>Add this person to the SPAMMERS database</li>
					<li>Increment the number of spam posts made by this person under that group groupId</li>
					<li>Increment the counter of each spam word that was a match [analytics]</li>
					<li>Note the created time of this postId [analytics]</li>
					<li>Note the updated time of this postId [analytics]</li>
					<li>Note whether created time and updated time are same or different [analytics]</li>
					<li>Skip FURTHER PROCESSING</li>
				</ol>
				</li>
			</ul>
			</li>
		</ul>
		</li>
		<li>PROCESS LINK SET, the link can be either in the whitelist or in the blacklist or it can be uncategorized currently which means the admin must approve it
		<ol>
			<li>Is the link present in the blacklist?
			<ol>
				<li>Delete the postId</li>
				<li>Note the group groupId where this postId was made</li>
				<li>Add this person to the SPAMMERS database</li>
				<li>Increment the number of spam posts made by this person under that group groupId</li>
				<li>Increment the number of times this link was found in the blacklist for that group groupId</li>
				<li>Note the created time of this postId [analytics]</li>
				<li>Note the updated time of this postId [analytics]</li>
				<li>Note whether created time and updated time are same or different [analytics]</li>
				<li>Skip FURTHER PROCESSING</li>
			</ol>
			</li>
			<li>Is the link present in the whitelist?
			<ol>
				<li>Approve the postId</li>
				<li>Note the group groupId where this postId was made</li>
				<li>Increment the number of total posts made</li>
			</ol>
			</li>
			<li>Is the link uncategorized?
			<ol>
				<li>Extract the base url of this link</li>
				<li>Add this base url to the unique set of urls that need approval</li>
				<li>Associate this postId groupId along with the set of other postId ids that rely on the approval of the admin for this url</li>
			</ol>
			</li>
		</ol>
		</li>
	</ol>
	</li>
</ul>
