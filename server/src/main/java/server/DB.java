package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    private static DB instance;
    public static DB get() throws SQLException{
        if(instance == null)
            instance = new DB();
        return instance;
    }
    private final Connection connection;
    private final Statement st;
    private int lastId;
    private DB() throws SQLException{
        connection = DriverManager.getConnection("jdbc:sqlite:__airport.db");
        st = connection.createStatement();
        st.setQueryTimeout(30);
        boolean usersExist = false,
                flightsExist = false,
                favsExist = false;
        try(ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master")){
            while(rs.next()){
                String name = rs.getString("name");
                if(name.equals("user"))
                    usersExist = true;
                else if(name.equals("flight"))
                    flightsExist = true;
                else if(name.equals("favs"))
                    favsExist = true;
            }
        }
        if(!usersExist){
            st.execute("CREATE TABLE user ( " +
                    "id integer PRIMARY KEY, " +
                    "username text, " +
                    "password text, " +
                    "refresh text, " +
                    "refresh_date integer)");
        }
        if(!flightsExist){
            st.execute("CREATE TABLE flight (" +
                    "ID integer PRIMARY KEY," +
                    "startpoint VARCHAR(20)," +
                    "dest VARCHAR(20)," +
                    "date integer)");
        }
        if(!favsExist){
            st.execute("CREATE TABLE favs ( " +
                    "user integer," +
                    "flight integer," +
                    "FOREIGN KEY (user) REFERENCES user (id)," +
                    "FOREIGN KEY (flight) REFERENCES flight (ID))");
        }
        try(ResultSet rs = st.executeQuery("SELECT ID FROM flight ORDER BY ID DESC")){
            if(rs.next())
                lastId = rs.getInt("ID");
            else
                lastId = 100;
        }
        try(ResultSet rs = st.executeQuery("SELECT id FROM user ORDER BY ID DESC")){
            if(rs.next())
                lastId = Math.max(lastId, rs.getInt("ID"));
            else
                lastId = 100;
        }
    }
    public void executeUpdate(String update) throws SQLException{
        st.executeUpdate(update);
    }
    public ResultSet executeQuery(String query) throws SQLException{
        return st.executeQuery(query);
    }
    public ResultSet executeQuery(String query, Object...args) throws SQLException{
        return executeQuery(String.format(query, args));
    }
    public void executeUpdate(String update, Object...args) throws SQLException{
        executeUpdate(String.format(update, args));
    }
    public int newId(){
        return ++lastId;
    }
}
