# SYSH
SYSH (See Your Streaming History) is a self-hosted Spotify streaming history dashboard, accessed and managed using an Android client, with the main focus being accurate data collection and representation. 

The Android app is available for download on the [Google Play Store](https://play.google.com/store/apps/details?id=com.github.barmiro.syshclient) or on the [Releases](https://github.com/barmiro/SYSH/releases) page. If you're not sure whether SYSH is right for you, the app includes access to a demo server, allowing you to explore its features without the need to set up your own instance.

SYSH was created as a FOSS alternative to existing, commercial services. While they have an impressive user base, they seem to prioritize user engagement and monetization over improving the service or fixing data accuracy issues.

The project was inspired in part by [Yooooomi/your-spotify](https://github.com/Yooooomi/your_spotify). I wanted to bring similar functionality to a mobile app, accessible on the go, and rethink some design decisions - including the way streaming statistics were calculated. If you're looking for a more established solution or prefer a web app interface, I highly recommend checking out their repo as well!

## Setup

1. Create a new app in Spotify's developer dashboard and add sysh://open/callback as a redirect URI. If accounts other than the app owner will use your instance, add them in the User Management section.
2. Find your app's Client ID and Client secret and add them to your .env file.
3. Launch your application using Docker Compose (the only officially supported way to run SYSH at the moment).
4. Install the Android client and follow the instructions to create an account and authorize SYSH to make API calls to Spotify.
5. SYSH will automatically fetch and save your streaming data, but you'll need to get your streaming history from Spotify directly; these days it's a button in your Spotify account's privacy settings and the turnaround rarely exceeds 2-3 days, so it's a good idea to ask for the files after you've set up your SYSH account to avoid gaps in your streaming data.

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
      - SYSH_SERVER_PORT=${SYSH_SERVER_PORT:-5754}
    ports:
      - '${SYSH_SERVER_PORT:-5754}:${SYSH_SERVER_PORT:-5754}'
    volumes:
      - syshkeys:/keys
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
  syshkeys:

```

### .env

```env
# required
POSTGRES_DB=sysh_db
POSTGRES_USER=yourusername
POSTGRES_PASSWORD=yourpassword
SPOTIFY_CLIENT_ID=yourclientid
SPOTIFY_CLIENT_SECRET=yourclientsecret

# When true, only the first user (with admin privileges by default) can self-register.
# After that, the /register endpoint will be disabled and all subsequent accounts must be created by an admin.
# Strongly recommended if your server is publicly accessible.
SYSH_RESTRICTED_MODE=false


# optional
# SYSH_SERVER_PORT=0000 # if you want to override the default 5754 port
# SYSH_TZ=Your/Timezone # mainly for server logs, each user has their own streaming data timezone
```

## Recommended nginx config
If you're using nginx: SYSH uses server-sent events. To make sure they work correctly, a separate config for one of the endpoints is required:

```nginx
# SYSH SSE Endpoint
# Replace /your-path/ with your app's base path
location /your-path/zipStatusStream {
	proxy_pass http://localhost:5754/zipStatusStream; # or your custom port
	proxy_http_version 1.1;
	proxy_set_header Connection '';
	proxy_buffering off;
	proxy_read_timeout 3600s;
	proxy_send_timeout 3600s;
	add_header Cache-Control no-cache;
}

# SYSH General endpoint
# Replace /your-path/ with your app's base path
location /your-path/ {
	proxy_pass http://localhost:5754/; # or your custom port
	proxy_set_header Host $host;
	proxy_set_header X-Real-IP $remote_addr;
	proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
	proxy_set_header X-Forwarded-Proto $scheme;
	client_max_body_size 128M;
	proxy_connect_timeout 180s;
	proxy_send_timeout 180s;
	proxy_read_timeout 180s;

	# Strip your app's base path (not needed if set to '/')
	rewrite ^/your-path(/.*)$ $1 break;

	# Optional rate limiting setting
	# limit_req zone=api_limit burst=10 nodelay;
	# also requires in nginx.conf: limit_req_zone $binary_remote_addr zone=api_limit:10m rate=5r/s;
}

```

## FAQ
> How often should I do a manual import?

As often as you'd like! Spotify's recent streams API is a great way to stay up-to-date on your streaming activity, but the data isn't as accurate as what's in the exported streaming history. Whenever you have a new file, just upload it to your server and it will merge seamlessly with your current dataset.

> What about duplicate streams? How will SYSH know which streams to keep and which to import?

On import, all streams for the detected timestamp range are wiped and replaced with what's in the JSON files, making duplicates and inconsistencies virtually impossible.

> How does SYSH deal with users in different timezones?

When an account is created, SYSH saves the user's timezone and saves it to the database. Every time a user logs in, if the detected timezone differs from what's saved in the database, the user will have an option to update their timezone in the app's settings. This change is completely non-destructive and only changes how the data is displayed.

> Does SYSH need to use HTTPS?

No. Even though Spotify introduced an HTTPS requirement for redirect URIs in April 2025, you can run SYSH over plain HTTP. The Android client uses sysh://open/callback - a local deep link which, as a loopback address, is exempt from the requirement.

> Is using HTTP secure?

All communication with Spotify takes place over HTTPS, regardless of your setup. Even though the redirect URI uses HTTP, it only sends data from your browser to the local proxy. The credentials are then, however, sent from your device to your server - it's up to you to secure all traffic between the server and the Android client. Traffic over HTTP can be secure if you're using a mesh VPN like Tailscale or connect on a trusted private network (e.g. your home Wi-Fi).

> How many users does SYSH support?

Spotify imposes a limit of 25 users per app. With the current setup, the upper limit of reliability is probably closer to 15. 25 users would make for 1440 updates per day, each requiring at least one API call (more if new tracks/albums/artists are found). In internal testing, the 24-hour limit seems to be around 1500 calls, but this is undocumented and may vary day to day. An option to reduce the rate of updates is coming, but may in rare cases lead to missed streams.

When onboarding users, keep in mind a single user's history import may use up to 400 API calls at once (typically 100-200). 

The API call limit is per developer account - it's possible to run multiple instances of SYSH on one machine and register them with separate developer accounts if it's an issue.
