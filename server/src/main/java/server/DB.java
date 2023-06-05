package server;

import com.github.javafaker.Faker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DB {
    private static DB instance;
    public static DB get() throws SQLException{
        if(instance == null)
            instance = new DB();
        return instance;
    }
    private final Connection connection;
    private final Statement st;
    private final Statement st2;
    private int lastId;
    private ReentrantLock idLock = new ReentrantLock();
    private final LinkedList<String> queries = new LinkedList<>();
    public void printQueries(){
        for(String s : queries)
            System.out.println(s);
    }
    private DB() throws SQLException{
        connection = DriverManager.getConnection("jdbc:sqlite:__airport.db");
        st = connection.createStatement();
        st2 = connection.createStatement();
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
                    "refresh_date integer, " +
                    "first_name VARCHAR(20)," +
                    "last_name VARCHAR(20)," +
                    "third_name VARCHAR(20)," +
                    "profile_picture VARCHAR(10))");
            st.execute("INSERT INTO user VALUES (1, \"root\", \"$2a$12$1T4eqsqdxnMYDNNmJtmEme1oM4Kyvgf1J2oyXCKmuP/Quao9nxYGC\", " +
                    "\"\", 0, \"Администратор\", \"\", \"\", \"root.png\")");
        }
        if(!flightsExist){
            st.execute("CREATE TABLE flight (" +
                    "ID integer PRIMARY KEY," +
                    "startpoint VARCHAR(20)," +
                    "dest VARCHAR(20)," +
                    "date0 date," +
                    "date1 date," +
                    "price integer)");
        }
        if(!favsExist){
            st.execute("CREATE TABLE favs ( " +
                    "user integer," +
                    "flight integer," +
                    "FOREIGN KEY (user) REFERENCES user (id) ON DELETE CASCADE," +
                    "FOREIGN KEY (flight) REFERENCES flight (ID) ON DELETE CASCADE)");
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

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            for(;;){
                try{
                    generateFlights(1);
                    clearOldFlights();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                try{
                    Thread.sleep(20000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
    }
    private void generateFlights(int q) throws SQLException{
        for(int i=0;i<q;++i){
            String query = String.format(Locale.getDefault(), "INSERT INTO flight VALUES " +
                    "(%d, \"%s\", \"%s\", DATETIME(%d, 'unixepoch'), DATETIME(%d, 'unixepoch'), %d)",
                    newId(),
                    Faker.instance().address().city(),
                    Faker.instance().address().city(),
                    new Date().getTime()/1000,
                    new Date().getTime()/1000,
                    (int)(Math.random()*10000));
            st2.execute(query);
        }
    }
    private void clearOldFlights() throws SQLException{
        st2.executeUpdate("DELETE FROM flight WHERE date1 < DATETIME('now', '-2 minutes')");
        st2.executeUpdate("DELETE FROM favs WHERE flight < (SELECT MIN(ID) FROM flight GROUP BY ID)");
    }
    public void executeUpdate(String update) throws SQLException{
        queries.add(update);
        st.executeUpdate(update);
    }
    public ResultSet executeQuery(String query) throws SQLException{
        queries.add(query);
        return st.executeQuery(query);
    }
    public ResultSet executeQuery(String query, Object...args) throws SQLException{
        return executeQuery(String.format(query, args));
    }
    public void executeUpdate(String update, Object...args) throws SQLException{
        executeUpdate(String.format(update, args));
    }
    public int newId(){
        try {
            if (idLock.tryLock(10, TimeUnit.SECONDS)) {
                return ++lastId;
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }finally {
            idLock.unlock();
        }
        return ++lastId;
    }
}
