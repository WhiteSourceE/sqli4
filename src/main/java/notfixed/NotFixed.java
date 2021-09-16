package notfixed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import testcasesupport.IO;

public class NotFixed {
  private Random random;

  void example1(HttpServletRequest req) throws SQLException {

    String data = null;
    Connection dbConnection = null;
    PreparedStatement sqlStatement = null;
    try {
      if (random.nextBoolean()) {
        data = req.getParameter("one");
      } else if (random.nextBoolean()) {
        data = req.getParameter("two");
      }
      dbConnection = IO.getDBConnection();
    } catch (Exception e) {
      data = "dfgdfg";
    }

    sqlStatement = dbConnection.prepareStatement(
        "insert into users (status) values ('updated') where name='" + data + "'");

    sqlStatement.execute();
  }

  void example2(HttpServletRequest req) throws SQLException {

    Connection dbConnection = null;
    PreparedStatement sqlStatement = null;
    String data = Integer.parseInt(req.getParameter("index")) == 1 ?
        "id_" + req.getParameter("one") : "id_" + req.getParameter("two");
    dbConnection = IO.getDBConnection();

    sqlStatement = dbConnection.prepareStatement(
        "insert into users (status) values ('updated') where name='" + data + "'");

    sqlStatement.execute();
  }

}
