# About the project

This project is the first attempt of mine in developing a client-server application. During development a bunch of technologies were used including:
- Client-server architecture
- User authentication
- Data encryption
- Methods of password storage
- Relational databases with SQLite

The _client side_ was developed as a coursework on the discipline "Mobile App Development".

The _server side_ was developed as a part of a school project on the discipline "Technologies of Software Development" and later adapted for use in the aforementioned coursework.

# Installation and launch

## Server side

Run the following command to build an uberjar for the server side:
```bash
./gradlew shadowJar
```

To upload the uberjar to the server and to launch the appliaction use __ansible__:
```bash
ansible-playbook update-server.yml -i hosts
```

The `hosts` file contains the server's IP.

In order for the program to work the server needs to have _Java 11_ and the _jps_ utility installed.

## Mobile App

To build and install the mobile app use the standard Android Studio tools.

# Technologies used

## Server side

### Sockets

Client communicates with the server via __sockets__.

Client forms a request to the server in a bash-like command:
> login _username password_

After the command is formed it is:
- _Encrypted with RSA_
- Encoded with Base64
- Sent to server via a socket

In response, server sends data as XML.
Example response:
```xml
<response>
	<flight id="11031" from="Санкт-Петербург"
			to="Москва" date0="1686216647000"
			date1="1686217547000" price="6532" fav="0"/>
	<flight id="11032" from="Казань"
			to="Калининград" date0="1686216677000"
			date1="1686217577000" price="3717" fav="1"/>
</response>
```

### JWT and authorization

Every request (except for _login_ and _register_ requests) to the server is prefixed with a JWT-token to authenticate the user.

Tokens are generated on the server with [Auth0's library](https://github.com/auth0/java-jwt).

__Passwords__ are hashed with [Bcrypt](https://github.com/patrickfav/bcrypt) and stored in a local database.

### SQLite

All the data is stored in a single SQLite database file[^1]. The connection to the database is established using [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc).

### Encryption

The encryption keys are created at the first start of the server side application.
To make working with keys easier the [AirportKeys](keys/src/main/java/keys/AirportKeys.java) class was written.

This auxiliary class lets us create, save and read the encryption keys, as well as encrypt/decrypt data using the __RSA algorithm__.

##  Mobile App

The mobile app is a thin client that lets us get data from the server.

To make asynchronous network requests `java.util.concurrent` and its `CompletableFuture` class is used.

To represent different screens the Multiple Activity Architecture was used.

[^1]: [DB schema](https://github.com/Vhhg13/Airport/blob/9452f4bbbbd7701ca5f2a5bd4aaa4dd6c73addf9/db_schema.png)
