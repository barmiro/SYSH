# SYSH
SYSH (See Your Streaming History) is a self-hosted Spotify streaming history manager, focused on deliberate data collection rules and accuracy over feature count. The goal is to do one thing and do it well, and any additional features must not interfere with data collection.

At the time of writing, SYSH is composed of a dockerized server, which collects data from Spotify and direct JSON imports, and an android client. A web client is on the roadmap, but not very high up.
SYSH is under active development and has not yet been published on docker. Contact me for deployment details if you want to run it as-is.

Short demo of what the client looks like:
https://youtu.be/1_iDDmWinZM?si=Ui3jm5_HX4qQa-5o

## SETUP
1. Create a new app in Spotify's developer dashboard.
2. Find your app's Client ID and Client secret and add them to your .env file (detailed environment variable list coming soon).
3. Launch your application using Docker Compose (the only officially supported way to run SYSH at the moment).
4. Install the Android client and follow the instructions to create an account and authorize SYSH to make API calls to Spotify.
5. SYSH will automatically fetch and save your streaming data, but you'll need to get your streaming history from Spotify directly; these days it's a button in your Spotify account dashboard and the turnaround rarely exceeds 2-3 days, so it's a good idea to ask for the files after you've set up your SYSH server to avoid gaps in your streaming data.

Spotify imposes a limit of 25 users per app. With the current setup, the upper limit of reliability is probably closer to 15. 25 users would make for 1440 updates per day, each requiring at least one API call (more if new tracks/albums/artists are found). Reducing the rate of updates is trivial, but may in rare cases lead to missed streams. In internal testing, the 24-hour limit seems to be around 1500 calls, but this is undocumented and may vary day to day. It's also not recommended to onboard more than 10 users per day due to streaming history imports requiring many API calls.

## FAQ
> How often should I do a manual import?

As often as you'd like! Spotify's recent streams API is a great way to stay up-to-date on your streaming activity, but the data isn't as accurate as what's in the exported streaming history. Whenever you have a new file, just upload it to your server and it will merge seamlessly with your current dataset.

> What about duplicate streams? How will SYSH know which streams to keep and which to import?

It doesn't have to. On import, all streams for the detected timestamp range are wiped and replaced with what's in the JSON files, making duplicates and inconsistencies virtually impossible.

> How does SYSH deal with users in different timezones?

On the first in-app login, SYSH saves the user's timezone and saves it to the database. On every subsequent login, if the detected timezone differs from what's saved in the database, the user will be prompted whether they want to change their timezone. This change is completely non-destructive and only changes how the data is displayed. After a timezone change, some data might take longer to load until all caches are re-generated.

> Why does my server need to use HTTPS?

This is a change very recently implemented by Spotify. If you have an app created before April 2025 in your Spotify Developer Dashboard, you can use it for your SYSH instance to use plain HTTP until November 2025.
