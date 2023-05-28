package server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class ResponseGenerator {
    public static String getall(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("<response>\n");
        while(rs.next()){
            sb.append(String.format(Locale.getDefault(), "<flight id=\"%d\" from=\"%s\" to=\"%s\" date=\"%d\"/>\n",
                    rs.getInt("ID"), rs.getString("startpoint"), rs.getString("dest"),
                    rs.getInt("date")));
        }
        sb.append("</response>");
        return sb.toString();
    }
}
