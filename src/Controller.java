import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Controller {

    private final Database db = new Database();

    // user doesn't exist yet. Just pass in individual values
    public Boolean registerUser(String username, String password, String role) throws SQLException {
        Connection conn = db.getConnection();
        String query = "INSERT INTO `users` (`username`, `password`, `role`) VALUES(?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);
        statement.setString(3, role);

        int rowsAffected = statement.executeUpdate();
        if(rowsAffected > 0) {
            statement.close();
            return true;
        }
        return false;
    }

    // user details are recorded. Pass in a user object instead of individual values
    // that's the OOP way of doing things
    public Boolean updateUser(User user) throws SQLException {
        Connection conn = db.getConnection();
        String query = "UPDATE `users` SET `username` = ?, `password` = ?, `role` = ? WHERE `user_id` = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getRole());
        statement.setInt(4, user.getUserId());

        int rowsAffected = statement.executeUpdate();
        if(rowsAffected > 0) {
            statement.close();
            return true;
        }
        return false;
    }

    public Boolean deleteUser(int userId) throws SQLException {
        Connection conn = db.getConnection();
        String query = "DELETE FROM `users` WHERE `user_id` = ?";
        PreparedStatement statement = conn.prepareStatement(query);
        statement.setInt(1, userId);

        int rowsAffected = statement.executeUpdate();
        if(rowsAffected > 0) {
            statement.close();
            return true;
        }
        return false;
    }

    public ArrayList<User> queryUsers() throws SQLException {
        Connection conn = db.getConnection();
        String query = "SELECT * FROM `users`";
        PreparedStatement statement = conn.prepareStatement(query);
        ResultSet res = statement.executeQuery();
        statement.closeOnCompletion();

        ArrayList<User> userList = new ArrayList<>();

        while (res.next()) {
            final int userId = res.getInt("user_id");
            final String username = res.getString("username");
            final String password = res.getString("password");
            final String role =  res.getString("role");
            userList.add(new User(userId, username, password, role));
        }
        return userList;
    }

    public void setTimeout(int delay, Runnable runnable){
        Timer timer = new Timer(delay, e -> {
            runnable.run();
        });
        timer.setRepeats(false);
        timer.start();
    }











}
