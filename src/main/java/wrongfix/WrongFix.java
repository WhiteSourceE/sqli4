package wrongfix;

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

public class WrongFix {
  private Random random;

  void example1(HttpServletRequest req) throws SQLException {

    Connection dbConnection = null;
    PreparedStatement sqlStatement = null;
    dbConnection = IO.getDBConnection();

    sqlStatement = dbConnection.prepareStatement(
        "insert into users (status) values ('updated') where name='" +
            (Integer.parseInt(req.getParameter("index")) == 1 ? req.getParameter("one") : req.getParameter("two")) +
            "'");

    sqlStatement.execute();
  }

  void example2(HttpServletRequest req) throws SQLException {

    String sql;
    String name = req.getParameter("name");
    String userid = req.getParameter("userid");
    String acctNum = req.getParameter("acctNum");
    Connection connection = getJDBCConnection();
    try (Statement statement = connection.createStatement()) {
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

  String example3(HttpServletRequest req) throws SQLException {

    String sql;
    String name = req.getParameter("name");
    String userid = req.getParameter("userid");
    String acctNum = req.getParameter("acctNum");
    Connection connection = getJDBCConnection();
    try (Statement statement = connection.createStatement()) {
      if (System.getProperty("os.name").contains("Win")) {
        sql = "SELECT TOP 1 first_name FROM user_data WHERE name = '"
            + name + "'";
      } else {
        sql = "SELECT first_name FROM user_data WHERE userid = '"
            + userid + "'";
      }
      sql += " AND account = '" + acctNum + "'";
      return statement.executeQuery(sql).getString(0);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    } finally {
      return "Done";
    }
  }

  public ResultSet example4(HttpServletRequest req)
      throws SQLException {
    String sql;
    Connection connection = getJDBCConnection();
    Statement statement;
    String acctNum = req.getParameter("accNum");
    String userid = req.getParameter("userid");
    String name = req.getParameter("name");
    if (userid.equals("admin")) {
      statement = connection.createStatement();
    } else {
      statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }
    userid = userid.toLowerCase();
    if (System.getProperty("os.name").contains("Win")) {
      sql = "SELECT TOP 1 first_name FROM user_data WHERE name = '"
          + name + "'";
    } else {
      sql = "SELECT first_name FROM user_data WHERE userid = '"
          + userid + "'";
    }
    sql += " AND account = '" + acctNum + "'";
    return statement.executeQuery(sql);
  }

  public ResultSet example5(HttpServletRequest req)
      throws SQLException {
    List<String> users = new ArrayList<>();
    users.add("One");
    users.add("Two");
    Statement statement = getJDBCConnection().createStatement();
    String previousUser = "id_" + users.remove(users.size() - 1);
    users.add(req.getParameter("userId"));
    return statement.executeQuery("SELECT first_name FROM user_data WHERE userid = '" + previousUser + "'");
  }

  public void example6(HttpServletRequest req) throws SQLException {
    final String input = req.getParameter("user_id");
    Sql sql = new Sql() {
      public String getSql() {
        return "safe_" + input;
      }

      @Override
      public String getNameQuery(String name) {
        return " AND name = '" + name + "'";
      }

      @Override
      public String getUserIdOrDefault(String userId) {
        return null;
      }
    };
    Statement statement = getJDBCConnection().createStatement();
    statement.executeQuery("SELECT first_name FROM user_data WHERE userid = '" + sql.getSql() + "'");
  }

  void example7(HttpServletRequest req) throws SQLException {
    Statement statement = getJDBCConnection().createStatement();
    statement.executeQuery("SELECT first_name FROM user_data WHERE userid = '" +
        req.getParameterValues("userIds")[0] + "'");
  }

  private Connection getJDBCConnection() {
    try {
      return DriverManager.getConnection("jdbc:sqlserver://myserver.database.windows.net:1433;");
    } catch (SQLException exception) {
      return null;
    }
  }

  interface Sql {

    String getSql();

    String getNameQuery(String name);

    String getUserIdOrDefault(String userId);
  }

}
