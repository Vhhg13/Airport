package server;

import com.auth0.jwt.exceptions.JWTVerificationException;
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
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import javax.crypto.BadPaddingException;

import keys.AirportKeys;

public class Airport {
    private static PublicKey serverPublicKey;
    private static PrivateKey serverPrivateKey;
    public static PrivateKey getServerPrivateKey(){
        return serverPrivateKey;
    }
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
                            System.out.println("SQL Queries:");
                            DB.get().printQueries();
                            answer = "SQLException";
                        }catch(JWTVerificationException e){
                            e.printStackTrace();
                            answer = "Invalid JWT";
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
        if(cmd.equalsIgnoreCase("refresh")){
            return Authenticator.get().refresh(cmdlets.get(1), cmdlets.get(2));
        }
        if(cmd.equals("log"))
            return "";

        String jwtString = cmdlets.pollFirst();
        DecodedJWT jwt = Authenticator.get().validateJWT(jwtString);
        cmd = cmdlets.get(0);

        if(cmd.equalsIgnoreCase("getall"))
            try(ResultSet rs = DB.get().executeQuery("SELECT flight.*, SUM(favs.user=(SELECT id FROM user WHERE username=\"%s\")) " +
                    "FROM flight LEFT JOIN favs ON favs.flight=flight.ID GROUP BY ID", jwt.getClaim("usr").asString())) {
                return ResponseGenerator.getall(rs);
            }

        if(cmd.equalsIgnoreCase("getfavs"))
            try(ResultSet rs = DB.get().executeQuery("SELECT *, 1 FROM flight WHERE ID in " +
                    "(SELECT flight FROM favs WHERE user = (SELECT id FROM user WHERE username = \"%s\"))", jwt.getClaim("usr").asString())){
                return ResponseGenerator.getall(rs);
            }

        if(cmd.equalsIgnoreCase("addflight")){
            if(cmdlets.size() == 3)
                DB.get().executeUpdate("INSERT INTO flight VALUES(%d, \"%s\", \"%s\", %d, %d, %d)",
                        DB.get().newId(), cmdlets.get(1), cmdlets.get(2), new Date().getTime(), new Date().getTime(), (int)(Math.random()*10000));
            else
                DB.get().executeUpdate("INSERT INTO flight VALUES(%d, \"%s\", \"%s\", %d, %d, %d)",
                        DB.get().newId(), cmdlets.get(1), cmdlets.get(2),
                        Long.parseLong(cmdlets.get(3)),
                        Long.parseLong(cmdlets.get(4)),
                        Long.parseLong(cmdlets.get(5)));
            return "Added";
        }

        if(cmd.equalsIgnoreCase("removeflight")){ // removeflight ID
            DB.get().executeUpdate("DELETE FROM flight WHERE ID = %d", Integer.parseInt(cmdlets.get(1)));
            return "Removed";
        }

        if(cmd.equalsIgnoreCase("mark"))
            return FavoritesMarker.get().mark(jwt, Integer.parseInt(cmdlets.get(1)));

        if(cmd.equalsIgnoreCase("getUserInfo")){
            if(cmdlets.size()==2 && jwt.getClaim("usr").asString().equals("root")){
                try(ResultSet rs = DB.get().executeQuery("SELECT * FROM user WHERE ID = %s", cmdlets.get(1))){
                    return ResponseGenerator.userInfo(rs);
                }
            }else
                try(ResultSet rs = DB.get().executeQuery("SELECT * FROM user WHERE username = \"%s\"", jwt.getClaim("usr").asString())){
                    return ResponseGenerator.userInfo(rs);
                }
        }

        if(cmd.equalsIgnoreCase("setUserInfo")){
            if(cmdlets.size()!=5){
                DB.get().executeUpdate("UPDATE user SET first_name = \"%s\", last_name=\"%s\", third_name=\"%s\" WHERE username = \"%s\"",
                        cmdlets.get(1), cmdlets.get(2), cmdlets.get(3),
                        jwt.getClaim("usr").asString()
                );
            } else if(jwt.getClaim("usr").asString().equals("root")) {
                DB.get().executeUpdate("UPDATE user SET first_name = \"%s\", last_name=\"%s\", third_name=\"%s\"WHERE ID = %d",
                        cmdlets.get(1), cmdlets.get(2), cmdlets.get(3), cmdlets.get(4)
                );
            } else
                return "Not set";
            return "Set";
        }

        if(cmd.equalsIgnoreCase("findFlight")){
            String from = cmdlets.get(1);
            String to = cmdlets.get(2);
            String date0 = cmdlets.get(3);
            String date1 = cmdlets.get(4);
            String price1 = cmdlets.get(5);
            String price2 = cmdlets.get(6);
            StringBuilder query = new StringBuilder().append("SELECT flight.*, SUM(favs.user=(SELECT id FROM user WHERE username=\"%s\")) FROM flight LEFT JOIN favs ON favs.flight=flight.ID WHERE ");
            boolean lastWasAnd = true;
            if(!from.isBlank()) {
                query.append("UPPER(startpoint) = UPPER('").append(from).append("') ");
                lastWasAnd = false;
            }

            if(!to.isBlank()) {
                if(!lastWasAnd) query.append(" AND ");
                query.append("UPPER(dest) = UPPER('").append(to).append("') ");
                lastWasAnd = false;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("'yyyy-MM-dd'", Locale.getDefault());
            if(!date0.isBlank()){
                if(!lastWasAnd) query.append(" AND ");
                query.append("date0 > '").append(date0).append("'");
                lastWasAnd = false;
            }
            if(!date1.isBlank()){
                if(!lastWasAnd) query.append(" AND ");
                query.append("'").append(date1).append("' > date1");
                lastWasAnd = false;
            }
            if(!price1.isBlank()){
                if(!lastWasAnd) query.append(" AND ");
                query.append("price > ").append(price1);
                lastWasAnd = false;
            }
            if(!price2.isBlank()){
                if(!lastWasAnd) query.append(" AND ");
                query.append("price < ").append(price2);
                lastWasAnd = false;
            }
            query.append(" GROUP BY ID ");
            System.out.println(query);

            try(ResultSet rs = DB.get().executeQuery(query.toString(), jwt.getClaim("usr").asString())){
                return ResponseGenerator.getall(rs);
            }
        }

        if(cmd.equalsIgnoreCase("getusers")){
            try(ResultSet rs = DB.get().executeQuery("SELECT * FROM user")){
                return ResponseGenerator.userInfo(rs);
            }
        }
        if(cmd.equalsIgnoreCase("deleteuser")){
            DB.get().executeUpdate("DELETE FROM user WHERE ID = %d", Integer.parseInt(cmdlets.get(1)));
        }





        return "Command does not exist";
    }

}
