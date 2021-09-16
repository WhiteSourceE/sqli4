package tryFix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import testcasesupport.IO;

public class TryFix {

  private Random random;

  void example1(HttpServletRequest req) throws SQLException {

    Connection dbConnection = null;
    PreparedStatement sqlStatement = null;
    dbConnection = IO.getDBConnection();

    sqlStatement = dbConnection.prepareStatement(
        "insert into users (status) values ('updated') where name='" +
            (Integer.parseInt(req.getParameter("index")) == 1 ? req.getParameter("one") : "two") +
            "'");

    sqlStatement.execute();
  }

  void example2(HttpServletRequest req) throws SQLException {

    Statement statement = getJDBCConnection().createStatement();
    String query = "insert into users (status) values ('updated') where name='" +
        (Integer.parseInt(req.getParameter("index")) == 1 ? req.getParameter("one") : "two") +
        "'";

    statement.execute(query);
  }

  void example3(HttpServletRequest req) throws SQLException {

    String sql;
    String name = req.getParameter("name");
    String userid = req.getParameter("userid");
    String acctNum = req.getParameter("acctNum");
    try (Statement statement = getJDBCConnection().createStatement()) {
      if (System.getProperty("os.name").contains("Win")) {
        sql = "SELECT TOP 1 first_name FROM user_data WHERE name = '"
            + name + "'";
      } else {
        sql = "SELECT first_name FROM user_data WHERE userid = '"
            + userid + "'";
      }
      sql += " AND account = '" + acctNum + "'";
      statement.executeQuery(sql);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  void example4(HttpServletRequest req) throws SQLException {

    String sql;
    String userid = req.getParameter("userid");
    Statement statement1 = getStatement();
    Statement statement2 = getJDBCConnection().createStatement();
    sql = "SELECT TOP 1 first_name FROM user_data WHERE name = '"
        + "John" + "'";
    statement1.executeQuery(sql);
  }

  public ResultSet example5(HttpServletRequest req)
      throws SQLException {
    List<String> users = new ArrayList<>();
    users.add(req.getParameter("User_1"));
    users.add(req.getParameter("User_2"));
    Statement statement = getJDBCConnection().createStatement();
    String previousUser = "id_" + users.remove(users.size() - 1);
    users.add(req.getParameter("User_3"));
    return statement.executeQuery("SELECT first_name FROM user_data WHERE userid = '" + previousUser + "'");
  }

  public ResultSet example6(HttpServletRequest req)
      throws SQLException {
    List<String> users = new ArrayList<>();
    users.add(req.getParameter("User_1"));
    users.add(req.getParameter("User_2"));
    Statement statement = getJDBCConnection().createStatement();
    String previousUser = "id_" + users.remove(users.size() - 1);
    users.clear();
    return statement.executeQuery("SELECT first_name FROM user_data WHERE userid = '" + previousUser + "'");
  }

  public ResultSet example7(HttpServletRequest req)
      throws SQLException {
    String prefix = "a_";
    Statement statement = getJDBCConnection().createStatement();
    String previousUser = "id_" + getUser(req, prefix);
    prefix = "b_";
    return statement.executeQuery("SELECT first_name FROM user_data WHERE userid = '" + previousUser + "'");
  }

  public ResultSet example8(HttpServletRequest req)
      throws SQLException {
    Statement statement = getJDBCConnection().createStatement();
    UserData userData = new UserData(req.getParameter("userid"), "guest");
    String userId = "id_" + userData.getUserId();
    userData.setUserId("new_id");
    return statement.executeQuery("SELECT first_name FROM user_data WHERE userid = '" + userId + "'");
  }

  private String getUser(HttpServletRequest req, String prefix) {
    return req.getParameter("User_1");
  }

  private Statement getStatement() {
    return null;
  }

  private Connection getJDBCConnection() {
    try {
      return DriverManager.getConnection("jdbc:sqlserver://myserver.database.windows.net:1433;");
    } catch (SQLException exception) {
      return null;
    }
  }

  private class UserData {
    private String userId;
    private String name;

    public UserData(String userId, String name) {
      this.userId = userId;
      this.name = name;
    }

    public UserData() {
    }

    public String getUserId() {
      return userId;
    }

    public String getName() {
      return name;
    }

    public UserData setUserId(String userId) {
      this.userId = userId;
      return this;
    }

    public UserData setName(String name) {
      this.name = name;
      return this;
    }

    public String getNormalizedUserId(String userId) {
      return "id_" + userId;
    }
  }
}
