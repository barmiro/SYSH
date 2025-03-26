# SYSH
SYSH (See Your Streaming History) is a self-hosted Spotify streaming history manager, focused on deliberate data collection rules and accuracy over feature count. The goal is to do one thing and do it well, and any additional features must not interfere with data collection.

At the time of writing, SYSH is composed of a dockerized server, which collects data from Spotify and direct JSON imports, and an android client. A web client is on the roadmap, but not very high up.
SYSH is under active development and has not yet been published on docker. Contact me for deployment details if you want to run it as-is.
Short demo of what the client looks like:
https://youtu.be/1_iDDmWinZM?si=Ui3jm5_HX4qQa-5o

## SETUP (outdated)
1. Create a new app in Spotify's developer dashboard.
2. Find your app's Client ID and Client secret and add them to your .env file.
3. Launch your application using Docker Compose (the only officially supported way to run SYSH at the moment).
4. Authenticate with Spotify by opening your-server-address/authorize in a browser.
5. SYSH will automatically fetch and save your streaming data, but you'll need to get your streaming history from Spotify directly; these days it's a button in your Spotify account dashboard and the turnaround rarely exceeds 2-3 days, so it's a good idea to ask for the files after you've set up your SYSH server to avoid gaps in your streaming data.
6. Import your streaming history and enjoy!
