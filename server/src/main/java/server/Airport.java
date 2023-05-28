package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;

import javax.crypto.BadPaddingException;

import keys.AirportKeys;

public class Airport {
    private static PublicKey serverPublicKey;
    private static PrivateKey serverPrivateKey;
    public static void main(String[] args) {
        ensureKeys();
        Socket clientSocket = null;
        ServerSocket server = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        for (; ; ) {
            try {
                try {
                    server = new ServerSocket(42000);
                    System.out.printf("Socket opened [%s]\n", new Date());
                    for (; ; ) {
                        System.out.printf("! waiting for input ! [%s]\n", new Date());
                        clientSocket = server.accept();
                        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String command = decode(in.readLine());
                        System.out.printf("Received command: `%s` [%s]\n", command, new Date());
                        String answer = processCommand(command);
                        out.write(answer + "\n");
                        System.out.println("Sent to client: " + answer);
                        out.flush();
                    }
                } finally {
                    if (clientSocket != null) clientSocket.close();
                    if (in != null) in.close();
                    if (out != null) out.close();
                    System.out.printf("Server closed [%s]\n", new Date());
                    if (server != null) server.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void ensureKeys() {
        File pub = new File("airportserverkey.pub");
        File pri = new File("airportserverkey");
        if(pub.exists() && pri.exists()) {
            serverPrivateKey = AirportKeys.readPrivateKey(pri);
            serverPublicKey = AirportKeys.readPublicKey(pub);
        }else{
            KeyPair pair = AirportKeys.generatePair();
            serverPrivateKey = pair.getPrivate();
            serverPublicKey = pair.getPublic();
            AirportKeys.writeKey(pub, serverPublicKey);
            AirportKeys.writeKey(pri, serverPrivateKey);
        }
    }

    private static String decode(String str){
        try{
            byte[] encoded = Base64.getDecoder().decode(str);
            byte[] decoded = AirportKeys.cipher(serverPrivateKey, AirportKeys.DECRYPT, encoded);
            return new String(decoded);
        } catch(RuntimeException ignored){}
        return str;
    }

    private static String processCommand(String command) {
        LinkedList<String> cmdlets = Parser.parse(command);
        String cmd = cmdlets.get(0);
        if(cmd.equalsIgnoreCase("getKey")){
            return new String(Base64.getEncoder().encode(serverPublicKey.getEncoded()));
        }else if(cmd.equalsIgnoreCase("register")){
            return "rEgister";
        }else if(cmd.equalsIgnoreCase("login")){
            return "lOgin";
        }
        return "Command does not exist";
    }

}
