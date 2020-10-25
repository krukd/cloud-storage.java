package ru.kruk.dmitry.cloud.storage.server;

import java.sql.*;
import java.util.concurrent.*;

public class DataBaseAuthService {

    private static Connection connection;
    private static Statement statement;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);


    public void stop(){
        executorService.shutdown();
    }


    public String getDirectoryByLoginPassword(String loginEntry, String passEntry){
        String directory = null;
        Future<String> future = executorService.submit(new DirectoryOfUser(loginEntry, passEntry));
        try {
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return directory;
    }

    public static void connection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:server\\mainDB.db");
        statement = connection.createStatement();
    }
private static void disconnect() throws SQLException {
        connection.close();
}

private static class DirectoryOfUser implements Callable<String> {
   String loginEntry;
   String passEntry;

   DirectoryOfUser(String loginEntry, String passEntry){
       this.loginEntry = loginEntry;
       this.passEntry = passEntry;
   }
    @Override
    public String call() throws Exception {

       String directory = null;

       connection();
        ResultSet rs = statement.executeQuery("SELECT * FROM users_info " +
                "WHERE login = '" + loginEntry + "' AND password = '" + passEntry + "' LIMIT 1");
        while (rs.next()){
            directory = rs.getString("login");
            rs.close();
        }
        disconnect();
        return directory;

    }
}
}
