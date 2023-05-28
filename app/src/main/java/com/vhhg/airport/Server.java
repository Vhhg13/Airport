package com.vhhg.airport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Server {
    private static Server instance;
    public static Server get(){
        if(instance == null)
            instance = new Server();
        return instance;
    }
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
}
