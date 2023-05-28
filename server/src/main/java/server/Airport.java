package server;

import com.auth0.jwt.interfaces.DecodedJWT;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;

import javax.crypto.BadPaddingException;

import keys.AirportKeys;

public class Airport {
    private static PublicKey serverPublicKey;
    private static PrivateKey serverPrivateKey;
    public static final String pubKeyFile = "airportserverkey.pub";
    public static final String privKeyFile = "airportserverkey";
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
                        String answer;
                        try {
                            answer = processCommand(command);
                        }catch (SQLException sql){
                            sql.printStackTrace();
                            answer = "SQLException";
                        }
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
        File pub = new File(pubKeyFile);
        File pri = new File(privKeyFile);
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

    private static String processCommand(String command) throws SQLException{
        LinkedList<String> cmdlets = Parser.parse(command);
        String cmd = cmdlets.get(0);

        if(cmd.equalsIgnoreCase("getKey"))
            return new String(Base64.getEncoder().encode(serverPublicKey.getEncoded()));

        if(cmd.equalsIgnoreCase("register")){
            if(cmdlets.size()!=3) return "Not 3 args";
            return Authenticator.get().register(cmdlets.get(1), cmdlets.get(2));
        }
        if(cmd.equalsIgnoreCase("login")){
            if(cmdlets.size()<3) return "Not 3 args";
            return Authenticator.get().login(cmdlets.get(1), cmdlets.get(2));
        }

        //String jwtString = cmdlets.pollFirst();
        //DecodedJWT jwt = Authenticator.get().validateJWT(jwtString);
        //cmd = cmdlets.get(0);

        if(cmd.equalsIgnoreCase("getall"))
            try(ResultSet rs = DB.get().executeQuery("SELECT * FROM flight")) {
                return ResponseGenerator.getall(rs);
            }

        if(cmd.equalsIgnoreCase("addflight")){
            DB.get().executeUpdate("INSERT INTO flight VALUES(%d, \"%s\", \"%s\", %d)",
                    DB.get().newId(), cmdlets.get(1), cmdlets.get(2), new Date().getTime());
            return "Added";
        }




        return "Command does not exist";
    }

}
