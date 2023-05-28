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

import at.favre.lib.crypto.bcrypt.BCrypt;
import keys.AirportKeys;

public class Authenticator {
    private static Authenticator instance;
    public static Authenticator get(){
        if(instance == null)
            instance = new Authenticator();
        return instance;
    }
    private Authenticator(){

    }
    public String register(String username, String password) throws SQLException{
        try(ResultSet rs = DB.get().executeQuery("SELECT * FROM user WHERE UPPER(username)=\"%s\"", username)){
            if(!rs.next()){
                String passwdHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                DB.get().executeUpdate("INSERT INTO user VALUES (\"%s\", \"%s\", \"\", \"\")", username, passwdHash);
                return "Successfully registered";
            }
            return "Already registered";
        }
    }
    public String login(String username, String password){
        return "Logged in " + username + " with password " + password;
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
