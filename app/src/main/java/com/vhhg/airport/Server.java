package com.vhhg.airport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

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
    public String send(String req){
        FutureTask<String> future = new FutureTask<>(() -> {
            try(Socket socket = new Socket("vid16.online", 42000)){
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer.write(req + "\n");
                writer.flush();
                return reader.readLine();
            }catch(IOException e){
                return "IOException";
            }
        });
        new Thread(future).start();
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            return "Exception: " + e.getMessage();
        }
    }
    public void sendAsync(String req, Consumer<? super String> callback){
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try(Socket socket = new Socket("vid16.online", 42000)){
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer.write(req + "\n");
                writer.flush();
                return reader.readLine();
            }catch(IOException e){
                return "IOException";
            }
        });
        future.thenAccept(callback);
    }
    public String sendEncrypted(String req){
        if(serverPublicKey == null)
            serverPublicKey = AirportKeys.readPublicKey(send("getkey"));
        byte[] encrypedRequest = AirportKeys.cipher(serverPublicKey, AirportKeys.ENCRYPT, req);
        return send(new String(Base64.getEncoder().encode(encrypedRequest)));
    }
    public void sendEncryptedAsync(String req, Consumer<? super String> callback){
        if(serverPublicKey == null)
            serverPublicKey = AirportKeys.readPublicKey(send("getkey"));
        byte[] encrypedRequest = AirportKeys.cipher(serverPublicKey, AirportKeys.ENCRYPT, req);
        sendAsync(new String(Base64.getEncoder().encode(encrypedRequest)), callback);
    }

    public String login(String username, String password){
        String answer = sendEncrypted("login " + username + " " + password);
        String[] tokens = answer.split(" ");
        accessToken = tokens[0];
        refreshToken = tokens[1];
        return answer;
    }
    public void register(String username, String password){
        sendEncrypted("register " + username + " " + password);
    }

    public String sendWithJWT(String req){
        return send(accessToken + " " + req);
    }

}
