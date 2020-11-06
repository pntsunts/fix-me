package com.fixme.market.Sever;

import com.fixme.market.Models.TextColors;

import java.sql.*;
import java.util.ArrayList;

public class Db {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement pre_stmt = null;
    // private static  ResultSet resultSet = null;

    public  Db() {
        try
        {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:FIXME.db");
            System.out.println("Connected to the SQLITE DB");
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        createTable();
    }

    public static Connection properConnection()
    {
        if (connection != null)
        {
            try
            {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:FIXME.db");
            }
            catch (SQLException | ClassNotFoundException e)
            {
                System.out.println("error: " + e.getMessage());
            }
        }

        return connection;
    }
    public Connection getConnection()
    {
        return connection;
    }

    public static void closeConnection()
    {
        if (connection != null)
        {
            try
            {
                connection.close();
            }
            catch (SQLException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    public void createTable()
    {
        if (this.getConnection() != null)
        {
            String sql = "CREATE TABLE IF NOT EXISTS `Market` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "`instrType` TEXT UNIQUE NOT NULL," +
                    "`price` INT NOT NULL," +
                    "`quantity` INT NOT NULL)";
            try
            {
                statement = properConnection().createStatement();
                statement.executeUpdate(sql);
                statement.close();
               closeConnection();
            }
            catch (SQLException e)
            {
                System.err.println( e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
        }
    }

    public static ArrayList<String> getInstruments()
    {
        String sqlQuery = "SELECT * FROM `Market`";
        ArrayList<String> arrayList = new ArrayList<>();

        try (Statement stmt = properConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery))
        {
            arrayList.add("\n"+ TextColors.GREEN +"*********************************" + TextColors.BLUE);
            arrayList.add(String.format("%s\t%s\t%s\t%s\t%s", " ",
                    "Instr.",
                    "Price",
                    "Qty", " "));
            arrayList.add(TextColors.GREEN + "*********************************" + TextColors.BLUE);
            for (int i = 0; rs.next(); i++)
            {
                arrayList.add(String.format("%s\t%s\t%d\t%d\t%s", "|",
                        rs.getString("instrType"),
                        rs.getInt("price"),
                        rs.getInt("quantity"), "|"));
            }
            arrayList.add(TextColors.GREEN + "*********************************\n" + TextColors.RESET);
            stmt.close();
            closeConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return arrayList;
    }

    public boolean checkAvailability(String instrType_, int quantity_, int price_) {
        if (properConnection() != null) {
            String query = "SELECT * FROM `Market` WHERE instrType = ?";
            try {
                PreparedStatement pre_stmt = properConnection().prepareStatement(query);
                pre_stmt.setString(1, instrType_);
                ResultSet resultSet = pre_stmt.executeQuery();
                if (resultSet.getString("instrType").equalsIgnoreCase(instrType_)) {
                    if (resultSet.getInt("quantity") >= quantity_) {
                        if (resultSet.getInt("price") <= price_) {
                            pre_stmt.close();
                            closeConnection();
                            return true;
                        } else {
                            System.out.println("The instrument " + resultSet.getString("instrType") + "goes for the price of " + resultSet.getInt("price"));
                        }
                    } else {
                        System.out.println("We only have " + resultSet.getInt("quantity") + " quantity of " + resultSet.getString("instrType"));
                    }
                }
                pre_stmt.close();
                closeConnection();
            } catch (SQLException e) {
                System.out.println("We do not have that instrument in stock");
            }
        }
        return false;
    }

    public boolean buyInstrument(String instrType_, int quantity_, int price_) {
        if (checkAvailability(instrType_, quantity_, price_)) {
            String query = "UPDATE `Market` SET quantity = quantity - ? WHERE instrType = ?";
            try {
                PreparedStatement pre_stmt = properConnection().prepareStatement(query);
                pre_stmt.setInt(1, quantity_);
                pre_stmt.setString(2, instrType_);
                pre_stmt.executeUpdate();
                pre_stmt.close();
                closeConnection();
                System.out.println("Bought " + quantity_ + " shares of " + instrType_ + " at a price of " + price_);
            } catch (SQLException e) {
                System.out.println("problem with buying" + e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean sellInstrument(String instrType_, int quantity_, int price_)
    {
        boolean checkAvailability = false;
        if (properConnection() != null)
        {
            String query = "SELECT * FROM `Market` WHERE instrType = ?";
            try
            {
                PreparedStatement pre_stmt = properConnection().prepareStatement(query);
                pre_stmt.setString(1, instrType_);
                ResultSet resultSet = pre_stmt.executeQuery();

                if (resultSet.next())
                {
                    String query_ = "UPDATE `Market` SET quantity = quantity + ? WHERE instrType = ?";
                    PreparedStatement pre_stmt_ = connection.prepareStatement(query_);
                    pre_stmt_.setInt(1, quantity_);
                    pre_stmt_.setString(2, instrType_);
                    pre_stmt_.executeUpdate();
                    pre_stmt_.close();
                    checkAvailability = true;
                    System.out.println("Sold " + quantity_ + " shares of " + instrType_ + " at a price of " + price_);
                }
                resultSet.close();
                pre_stmt.close();
                closeConnection();

            }
            catch (SQLException e)
            {
               // System.out.println("We do not have that instrument in stock");
               // int x = InsertValues(instrType_, price_, quantity_);
                System.out.println(e.getStackTrace() + "\n" + e.getMessage());
            }
        }
        return checkAvailability;
    }

    public int InsertValues(String instrType, int price, int quantity)
    {
        int exe = 0;
        if (properConnection() != null)
        {
            String query = "INSERT INTO `Market` (`instrType`, `price`, `quantity`) " +
                    "VALUES (?, ?, ?) ";
            try
            {
                PreparedStatement pre_stmt = connection.prepareStatement(query);
                pre_stmt.setString(1, instrType);
                pre_stmt.setInt(2, price);
                pre_stmt.setInt(3, quantity);
                pre_stmt.executeUpdate();

                query = "SELECT seq FROM sqlite_sequence WHERE `name` = \"Market\"";
                statement = properConnection().createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                if(resultSet.next())
                {
                    exe = resultSet.getInt("seq");
                }
                statement.close();
                closeConnection();
            }
            catch (SQLException e)
            {
                if (e.getErrorCode() != 19)
                    System.out.println(e.getMessage());
            }
        }
        return exe;
    }

}

