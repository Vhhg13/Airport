package server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class ResponseGenerator {
    public static String getall(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("<response>");
        while(rs.next()){
            sb.append(String.format(Locale.getDefault(), "<flight id=\"%d\" from=\"%s\" to=\"%s\" date0=\"%d\" date1=\"%s\" price=\"%s\" fav=\"%d\"/>",
                    rs.getInt("ID"), rs.getString("startpoint"), rs.getString("dest"),
                    rs.getLong("date0"), rs.getLong("date1"), rs.getInt("price"), rs.getInt(7)));
        }
        sb.append("</response>");
        return sb.toString();
    }

    public static String userInfo(ResultSet rs) throws SQLException{
        StringBuilder sb = new StringBuilder();
        sb.append("<response>");
        while(rs.next()){
            sb.append(String.format(Locale.getDefault(), "<user id=\"%s\" firstname=\"%s\" lastname=\"%s\" thirdname=\"%s\" profile_picture=\"%s\"/>",
                    rs.getInt("ID"), rs.getString("first_name"),
                    rs.getString("last_name"), rs.getString("third_name"),
                    rs.getString("profile_picture")));
        }
        sb.append("</response>");
        return sb.toString();
    }
}
