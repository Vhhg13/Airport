package server;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FavoritesMarker {
    private static FavoritesMarker instance;
    public static FavoritesMarker get(){
        if(instance == null)
            instance = new FavoritesMarker();
        return instance;
    }
    private FavoritesMarker(){}
    public String mark(DecodedJWT jwt, int ID) throws SQLException {
        try(ResultSet rs = DB.get().executeQuery("SELECT ID FROM flight WHERE ID=%d", ID)){
            if(!rs.next())
                return "No such flight";
        }
        int userId;
        try(ResultSet rs = DB.get().executeQuery("SELECT id FROM user WHERE username=\"%s\"", jwt.getClaim("usr").asString())){
            if(!rs.next())
                return "No user " + jwt.getClaim("usr").asString();
            userId = rs.getInt("id");
        }
        try(ResultSet rs = DB.get().executeQuery("SELECT * FROM favs WHERE user=%d AND flight=%d", userId, ID)){
            if(rs.next()) {
                DB.get().executeUpdate("DELETE FROM favs WHERE user=%d AND flight=%d", userId, ID);
                return "Fav removed";
            }else {
                DB.get().executeUpdate("INSERT INTO favs VALUES(%d, %d)", userId, ID);
                return "Fav added";
            }
        }
    }
}
