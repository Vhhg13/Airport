package server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import at.favre.lib.crypto.bcrypt.BCrypt;
import keys.AirportKeys;

public class Authenticator {
    private static Authenticator instance;
    public static Authenticator get(){
        if(instance == null)
            instance = new Authenticator();
        return instance;
    }
    private Authenticator(){}
    public String register(String username, String password) throws SQLException{
        try(ResultSet rs = DB.get().executeQuery("SELECT * FROM user WHERE UPPER(username)=UPPER(\"%s\")", username)){
            if(!rs.next()){
                String passwdHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                DB.get().executeUpdate("INSERT INTO user VALUES (%d, \"%s\", \"%s\", \"\", \"\", \"\", \"\", \"\")", DB.get().newId(), username, passwdHash);
                return "Successfully registered";
            }
            return "Already registered";
        }
    }
    public String login(String login, String passwd) throws SQLException{
        try(ResultSet rs = DB.get().executeQuery("SELECT password FROM user WHERE UPPER(username)=UPPER(\"%s\")", login)){
            if(!rs.next()){
                return "No such user";
            }else{
                if (BCrypt.verifyer().verify(passwd.toCharArray(), rs.getString("password").toCharArray()).verified){
                    long date = new Date().getTime();
                    DB.get().executeUpdate(String.format(Locale.getDefault(),
                            "UPDATE user SET refresh_date = %d WHERE username = \"%s\"", date, login));
                    Thread.sleep(1000);
                    String jwt = createJWT(login);
                    String refresh = createRefresh(jwt);
                    DB.get().executeUpdate("UPDATE user SET refresh = \"%s\" WHERE username = \"%s\"", refresh, login);
                    return jwt + " " + refresh;
                } else {
                    return "Invalid password";
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String refresh(String token, String refresh) throws SQLException{
        String refreshToBe = createRefresh(token);
        if(refreshToBe.equals(refresh)){
            try(ResultSet rs = DB.get().executeQuery("SELECT username FROM user WHERE refresh = \"%s\"", refresh)){
                if(rs.next()) {
                    String username = rs.getString("username");
                    DB.get().executeUpdate("UPDATE user SET refresh_date = %d WHERE username = \"%s\"", new Date().getTime(), username);
                    Thread.sleep(1000);
                    String new_jwt = createJWT(username);
                    String new_refresh = createRefresh(new_jwt);
                    DB.get().executeUpdate("UPDATE user SET refresh = \"%s\" WHERE username = \"%s\"", new_refresh, username);
                    return new_jwt + " " + new_refresh;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("E: Refresh denied");
        return "Refresh denied";
    }

    private String createRefresh(String jwt) {
        StringBuilder refresh = new StringBuilder(10);
        long seed = 0;
        for(byte b : Airport.getServerPrivateKey().getEncoded())
            seed += b;
        Random rnd = new Random(seed);
        for(int i=0;i<10;++i)
            refresh.append(jwt.charAt(rnd.nextInt(jwt.length())));
        return refresh.toString();
    }

    public String createJWT(String user){
        File pri = new File(Airport.privKeyFile);
        Algorithm algorithm = Algorithm.HMAC512(AirportKeys.readPrivateKey(pri).getEncoded());
        String token = JWT.create()
                .withIssuer("AirportInfoSystem")
                .withExpiresAt(new Date(new Date().getTime()+60*1000))
                .withClaim("usr", user)
                .withIssuedAt(new Date())
                .sign(algorithm);
        return token;
    }
    public DecodedJWT validateJWT(String token) throws SQLException {
        File pri = new File(Airport.privKeyFile);
        Algorithm algorithm = Algorithm.HMAC512(AirportKeys.readPrivateKey(pri).getEncoded());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("AirportInfoSystem")
                .build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            String usr = jwt.getClaim("usr").asString();
            String query = String.format("SELECT refresh_date FROM user WHERE username = \"%s\"", usr);
            long refreshDate;
            try(ResultSet rs = DB.get().executeQuery(query)) {
                if (!rs.next())
                    throw new JWTVerificationException("no refresh in db");
                refreshDate = rs.getLong("refresh_date");
            }
            System.out.println(new Date(jwt.getIssuedAt().getTime()));
            System.out.println(new Date(refreshDate));
            if(jwt.getIssuedAt().before(new Date(refreshDate)))
                throw new JWTVerificationException("jwt.getIssuedAt().before(new Date(refreshDate))");
            return jwt;
        }catch(JWTDecodeException e){
            throw new JWTVerificationException("Unable to decode JWT");
        }
    }
}
