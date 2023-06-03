package com.vhhg.airport;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import keys.AirportKeys;

public class Server {
    private static Server instance;
    public static Server get(Context context){
        if(instance == null)
            instance = new Server();
        return instance.withContext(context);
    }
    private String accessToken;
    private String refreshToken;
    private PublicKey serverPublicKey;
    private Context context;
    private boolean root;

    public boolean isRoot() {
        return root;
    }

    private Server withContext(Context context){
        this.context = context;
        return this;
    }
    private void setTokens(String accessToken, String refreshToken){
        Log.d("MYTAG", "Old tokens: " + this.accessToken + " " + this.refreshToken);
        String usrname = new JWT(accessToken).getClaim("usr").asString();
        root = usrname.equals("root");

        SharedPreferences sp = context.getSharedPreferences("TOKENS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("ACCESS", accessToken);
        editor.putString("REFRESH", refreshToken);
        editor.apply();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        Log.d("MYTAG", "New tokens: " + this.accessToken + " " + this.refreshToken);
    }
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
            setTokens(tokens[0], tokens[1]);
        }).andThen(callback));
    }
    public CompletableFuture<StringHolder> register(String username, String password, Consumer<StringHolder> callback){
        return sendEncryptedAsync("register " + username + " " + password, callback);
    }

    public CompletableFuture<StringHolder> getall(Consumer<StringHolder> callback){
        return sendAsync(withAccessToken("getall"), callback);
    }
    private CompletableFuture<StringHolder> refresh(Consumer<StringHolder> callback){
        return sendAsync("refresh " + accessToken + " " + refreshToken, ((Consumer<StringHolder>) answer -> {
            Log.i("MYTAG", "REFRESH: " + answer.string);
            String[] tokens = answer.string.split(" ");
            setTokens(tokens[0], tokens[1]);
        }).andThen(callback));
    }

    public CompletableFuture<StringHolder> mark(int flightID, Consumer<StringHolder> callback){
        return sendAsync(withAccessToken("mark " + flightID), callback);
    }

    public CompletableFuture<StringHolder> getFavs(Consumer<StringHolder> callback){
        //return sendAsync(accessToken + " getfavs", callback);
        return sendAsync(withAccessToken("getfavs"), callback);
    }

    private String withAccessToken(String command) {
        JWT jwt = new JWT(accessToken);
        Log.e("MYTAG", "Expires at: " + jwt.getExpiresAt());
        Log.e("MYTAG", "Now: " + new Date());
        Log.e("MYTAG", "isExpired? " + jwt.isExpired(10));
        if(jwt.isExpired(0)){
            try {
                return refresh(response -> {
                    response.string = accessToken;
                    Log.i("MYTAG", "refresh callback: " + response.string);
                }).get().getString().split(" ")[0] + " " + command;
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return accessToken + " " + command;
    }

    public static class StringHolder{
        private String string;
        public StringHolder(String string) { this.string = string; }
        public String getString() { return string; }
        public void setString(String string) { this.string = string; }
    }

    public boolean checkWhetherSignedIn(){
        SharedPreferences sp = context.getSharedPreferences("TOKENS", Context.MODE_PRIVATE);
        accessToken = sp.getString("ACCESS", null);
        root = new JWT(accessToken).getClaim("usr").asString().equals("root");
        refreshToken = sp.getString("REFRESH", null);
        if(accessToken == null || refreshToken == null)
            return false;
        try {
            String result = sendAsync("refresh " + accessToken + " " + refreshToken, response -> {
                if(response.getString().charAt(0) == 'e'){
                    String[] allTokens = response.getString().split(" ");
                    setTokens(allTokens[0], allTokens[1]);
                }
            }).get().getString();
            return result.charAt(0) == 'e';
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void logout() {
        SharedPreferences sp = context.getSharedPreferences("TOKENS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("ACCESS");
        editor.remove("REFRESH");
        editor.apply();
        accessToken = null;
        refreshToken = null;
    }
    public CompletableFuture<StringHolder> getUserInfo(Consumer<StringHolder> callback){
        return sendAsync(withAccessToken("getUserInfo"), callback);
    }

    public CompletableFuture<StringHolder> setUserInfo(User user){
        String request = String.format("setUserInfo \"%s\" \"%s\" \"%s\"", user.getFirstName(), user.getLastName(), user.getThirdName());
        return sendAsync(withAccessToken(request), res -> {});
    }


}
