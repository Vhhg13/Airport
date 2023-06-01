package com.vhhg.airport;

import com.auth0.android.jwt.JWT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import keys.AirportKeys;

public class Server {
    private static Server instance;
    public static Server get(){
        if(instance == null)
            instance = new Server();
        return instance;
    }
    private String accessToken;
    private String refreshToken;
    private PublicKey serverPublicKey;
    public CompletableFuture<StringHolder> sendAsync(String req, Consumer<? super StringHolder> callback){
        CompletableFuture<StringHolder> future = CompletableFuture.supplyAsync(() -> {
            try(Socket socket = new Socket("vid16.online", 42000)){
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer.write(req + "\n");
                writer.flush();
                return new StringHolder(reader.readLine());
            }catch(IOException e){
                return new StringHolder("IOException");
            }
        });
        future.thenAccept(callback);
        return future;
    }
    public CompletableFuture<StringHolder> sendEncryptedAsync(String req, Consumer<StringHolder> callback){
        if(serverPublicKey == null)
            return sendAsync("getkey", keyholder -> {
                serverPublicKey = AirportKeys.readPublicKey(keyholder.string);
                byte[] encrypedRequest = AirportKeys.cipher(serverPublicKey, AirportKeys.ENCRYPT, req);
                sendAsync(new String(Base64.getEncoder().encode(encrypedRequest)), callback);
            });
        else {
            byte[] encrypedRequest = AirportKeys.cipher(serverPublicKey, AirportKeys.ENCRYPT, req);
            return sendAsync(new String(Base64.getEncoder().encode(encrypedRequest)), callback);
        }
    }

    public CompletableFuture<StringHolder> login(String username, String password, Consumer<StringHolder> callback){
        return sendEncryptedAsync("login " + username + " " + password, ((Consumer<StringHolder>) answer -> {
            String[] tokens = answer.string.split(" ");
            accessToken = tokens[0];
            refreshToken = tokens[1];
        }).andThen(callback));
    }
    public CompletableFuture<StringHolder> register(String username, String password, Consumer<StringHolder> callback){
        return sendEncryptedAsync("register " + username + " " + password, callback);
    }

    public CompletableFuture<StringHolder> getall(Consumer<StringHolder> callback){
        JWT jwt = new JWT(accessToken);
        if(jwt.isExpired(10))
            return refresh(res -> sendAsync(accessToken + " getall", callback));
        else
            return sendAsync(accessToken + " getall", callback);
    }
    private CompletableFuture<StringHolder> refresh(Consumer<StringHolder> callback){
        return sendAsync("refresh " + accessToken + " " + refreshToken, ((Consumer<StringHolder>) answer -> {
            String[] tokens = answer.string.split(" ");
            accessToken = tokens[0];
            refreshToken = tokens[1];
        }).andThen(callback));
    }

    public CompletableFuture<StringHolder> mark(int flightID, Consumer<StringHolder> callback){
        return sendAsync(accessToken + " mark " + flightID, callback);
    }

    public CompletableFuture<StringHolder> getFavs(Consumer<StringHolder> callback){
        return sendAsync(accessToken + " getfavs", callback);
    }

    public static class StringHolder{
        private String string;
        public StringHolder(String string) { this.string = string; }
        public String getString() { return string; }
        public void setString(String string) { this.string = string; }
    }

}
