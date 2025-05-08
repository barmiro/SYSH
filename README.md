# SYSH
SYSH (See Your Streaming History) is a self-hosted Spotify streaming history manager, focused on deliberate data collection rules and accuracy over feature count. The goal is to do one thing and do it well, and any additional features must not interfere with data collection.

At the time of writing, SYSH is composed of a dockerized server, which collects data from Spotify and direct JSON imports, and an android client. A web client is on the roadmap, but not very high up.

Short demo of what the client looks like:
https://youtu.be/1_iDDmWinZM?si=Ui3jm5_HX4qQa-5o

## Setup
1. Create a new app in Spotify's developer dashboard and add http://127.0.0.1:5754/callback as a redirect URI.
2. Find your app's Client ID and Client secret and add them to your .env file (detailed environment variable list coming soon).
3. Launch your application using Docker Compose (the only officially supported way to run SYSH at the moment).
4. Install the Android client and follow the instructions to create an account and authorize SYSH to make API calls to Spotify.
5. SYSH will automatically fetch and save your streaming data, but you'll need to get your streaming history from Spotify directly; these days it's a button in your Spotify account dashboard and the turnaround rarely exceeds 2-3 days, so it's a good idea to ask for the files after you've set up your SYSH server to avoid gaps in your streaming data.

## Installation
To launch the server, run the command 'docker compose up' in a directory with compose.yaml and .env files created using these templates:

### compose.yaml

```yml
services:
  app:
    container_name: sysh-server
    image: barmiro/sysh-server:latest
    env_file:
      - .env
    environment:
      - POSTGRES_DB:${POSTGRES_DB}
      - POSTGRES_USER:${POSTGRES_USER}
      - POSTGRES_PASSWORD:${POSTGRES_PASSWORD}
      - SPOTIFY_CLIENT_ID:${SPOTIFY_CLIENT_ID}
      - SPOTIFY_CLIENT_SECRET=${SPOTIFY_CLIENT_SECRET}
      - TZ=${SYSH_TZ:-UTC}
    ports:
      - '${SYSH_SERVER_PORT:-5754}:${SYSH_SERVER_PORT:-5754}'
    depends_on:
      - postgres
    restart: always

  postgres:
    container_name: sysh-postgres
    image: barmiro/sysh-postgres:latest
    env_file:
      - .env
    environment:
      - POSTGRES_DB:${POSTGRES_DB}
      - POSTGRES_USER:${POSTGRES_USER}
      - POSTGRES_PASSWORD:${POSTGRES_PASSWORD}
    volumes:
      - syshdb:/var/lib/postgresql/data
    ports: []
    restart: always

volumes:
  syshdb:

```

### .env

```env
# required
POSTGRES_DB=sysh_db
POSTGRES_USER=yourusername
POSTGRES_PASSWORD=yourpassword
SPOTIFY_CLIENT_ID=yourclientid
SPOTIFY_CLIENT_SECRET=yourclientsecret
SYSH_SERVER_URL=yoururl.com # soon to be deprecated

# optional
# SYSH_SERVER_PORT=0000 # if you want to override the default 5754 port
# SYSH_TZ=Your/Timezone # mainly for server logs, each user has their own streaming data timezone
```

## FAQ
> How often should I do a manual import?

As often as you'd like! Spotify's recent streams API is a great way to stay up-to-date on your streaming activity, but the data isn't as accurate as what's in the exported streaming history. Whenever you have a new file, just upload it to your server and it will merge seamlessly with your current dataset.

> What about duplicate streams? How will SYSH know which streams to keep and which to import?

It doesn't have to. On import, all streams for the detected timestamp range are wiped and replaced with what's in the JSON files, making duplicates and inconsistencies virtually impossible.

> How does SYSH deal with users in different timezones?

When an account is created, SYSH saves the user's timezone and saves it to the database. Every time a user logs in, if the detected timezone differs from what's saved in the database, the user will be prompted whether they want to change their timezone. This change is completely non-destructive and only changes how the data is displayed. After a timezone change, some data might take longer to load until all caches are re-generated.

> Does SYSH need to use HTTPS?

No. Even though Spotify introduced an HTTPS requirement for redirect URIs in April of 2025, you can run SYSH over plain HTTP. The Android client uses a built-in transparent proxy to handle Spotify's redirects using 127.0.0.1 - a loopback address which is exempt from the requirement. That's why, regardless of your server's protocol, address or port number, you have to set http://127.0.0.1:5754/callback as the redirect URI in your Spotify Developer Dashboard.

> Is using HTTP secure?

All communication with Spotify takes place over HTTPS, regardless of your setup. Even though the redirect URI uses HTTP, it only sends data from your browser to the local proxy. The credentials are then, however, sent from your device to your server - it's up to you to secure all traffic between the server and the Android client. Traffic over HTTP can be secure if you're using a mesh VPN like Tailscale or connect on a trusted private network (e.g. your home Wi-Fi).

> How many users does SYSH support?

Spotify imposes a limit of 25 users per app. With the current setup, the upper limit of reliability is probably closer to 15. 25 users would make for 1440 updates per day, each requiring at least one API call (more if new tracks/albums/artists are found). Reducing the rate of updates is trivial, but may in rare cases lead to missed streams. In internal testing, the 24-hour limit seems to be around 1500 calls, but this is undocumented and may vary day to day. It's also not recommended to onboard more than 10 users per day due to streaming history imports requiring many API calls.
