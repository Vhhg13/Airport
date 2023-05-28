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
                flightsExist = false;
        try(ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master")){
            while(rs.next()){
                if(rs.getString("name").equals("user")){
                    usersExist = true;
                }else if(rs.getString("name").equals("flight")){
                    flightsExist = true;
                }
            }
        }
        if(!usersExist){
            st.execute("CREATE TABLE user ( " +
                    "username text PRIMARY KEY, " +
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
        try(ResultSet rs = st.executeQuery("SELECT ID FROM flight ORDER BY ID DESC")){
            if(rs.next())
                lastId = rs.getInt("ID");
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
