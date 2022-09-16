<h1 align="center">MaehAntiBot - Smart AntiBot Detection for Minecraft Server.</h1>

<br>

<h1 align="center"> -How to build?- </h1>

```sh
git clone https://github.com/DedInc/MaehAntiBot
cd MaehAntiBot
```

<h2 align="center"> -Windows- </h2>

```sh
gradlew buildFatJar
```

<h2 align="center"> -Linux- </h2>

```sh
./gradlew buildFatJar
```


<h1 align="center"> -Default config- </h1>

```yml
messages:
  proxy: '&cProxy is not allowed!'
  firewall: '&cYour country is not allowed on this server!'

iphub: #Please register in iphub.info, enter login and password (for AntiVPN)
  enabled: false
  login: ''
  password: ''

firewall:
  enabled: false
  blocked: false #If true black country list, If false - allowed country list
  list: #Default countries from CIS
    - AM
    - AZ
    - BY
    - KZ
    - KG
    - MD
    - RU
    - TJ
    - UZ
    - UA

blacklist: #Addition IP analyzer (when player in game)
  enabled: true
  detects: 2 #Min detects to punish
  actions: #Actions with console syntax | Placeholders: %player% - name %ip% - ip address
    - 'ban-ip %ip% Blacklist!'
    - 'ban %player% Started using vpn? It is forbidden here...'

passwords: #Passwords analyzer
  enabled: true
  matches: 3 #Matches to punish
  storage: 15 #Max passwords to save
  handleList:
    - /reg
    - /register
  actions:
    - 'ban-ip %ip%'

chat: #Chat analyzer
  enabled: true
  coefficient: 0.9 #The percentage of similarity of the last two messages (90%)
  seconds: 180 #The time that the check will be valid from the moment player log in to the server (3 minutes)
  flags: 3 #Flags to punish
  handleList:
    - /m
    - /msg
    - /r
    - /t
    - /tell
  actions:
    - 'kick %player% ChatBot!'

nicks: #Nicks analyzer
  enabled: true
  coefficient: 0.8 #Percentage of similarity of nicknames (80%)
  storage: 10 #Max nicks to save
  actions:
    - 'ban-ip %ip%'
```