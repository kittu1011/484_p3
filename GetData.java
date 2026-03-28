import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import org.json.JSONObject;

import netscape.javascript.JSObject;

import org.json.JSONArray;

public class GetData {

    static String prefix = "project3.";

    // You must use the following variable as the JDBC connection
    Connection oracleConnection = null;

    // You must refer to the following variables for the corresponding 
    // tables in your database
    String userTableName = null;
    String friendsTableName = null;
    String cityTableName = null;
    String currentCityTableName = null;
    String hometownCityTableName = null;

    // DO NOT modify this constructor
    public GetData(String u, Connection c) {
        super();
        String dataType = u;
        oracleConnection = c;
        userTableName = prefix + dataType + "_USERS";
        friendsTableName = prefix + dataType + "_FRIENDS";
        cityTableName = prefix + dataType + "_CITIES";
        currentCityTableName = prefix + dataType + "_USER_CURRENT_CITIES";
        hometownCityTableName = prefix + dataType + "_USER_HOMETOWN_CITIES";
    }

    // TODO: Implement this function
    @SuppressWarnings("unchecked")
    public JSONArray toJSON() throws SQLException {

        // This is the data structure to store all users' information
        JSONArray users_info = new JSONArray();
        
        try (Statement stmt = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            // Your implementation goes here....
            ResultSet rst = stmt.executeQuery(
                "SELECT USER_ID, FIRST_NAME, LAST_NAME, YEAR_OF_BIRTH, MONTH_OF_BIRTH, DAY_OF_BIRTH, GENDER FROM "
                + userTableName
            );
            Map<Integer, JSONObject> users_map = new HashMap<>();
            while (rst.next()) {
                    JSONObject single_user = new JSONObject();
                    single_user.put("user_id", rst.getInt(1));
                    single_user.put("first_name", rst.getString(2));
                    single_user.put("last_name", rst.getString(3));
                    single_user.put("YOB", rst.getInt(4));
                    single_user.put("MOB", rst.getInt(5));
                    single_user.put("DOB", rst.getInt(6));
                    single_user.put("gender", rst.getString(7));

                    single_user.put("friends", new JSONArray());
                    single_user.put("current", new JSONObject());
                    single_user.put("hometown", new JSONObject());

                    users_info.put(single_user);
                    users_map.put(rst.getInt(1), single_user);
            }
            Statement friends_stmt = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet friends_rst = friends_stmt.executeQuery(
                "SELECT USER1_ID, USER2_ID FROM "
                + friendsTableName
            );

            while (friends_rst.next()) {
                int user_id = friends_rst.getInt(1);
                int friend_id = friends_rst.getInt(2);

                JSONObject user = users_map.get(user_id);
                user.getJSONArray("friends").put(friend_id);
            }

            Statement current_stmt = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet current_rst = current_stmt.executeQuery(
                "SELECT cc.USER_ID, city.CITY_NAME, city.STATE_NAME, city.Country_Name FROM " + 
                currentCityTableName + " cc JOIN " + cityTableName + " city ON cc.CURRENT_CITY_ID = city.CITY_ID "
            );

            while (current_rst.next()) {
                int user_id = current_rst.getInt(1);
                String city = current_rst.getString(2);
                String state = current_rst.getString(3);
                String country = current_rst.getString(4);

                JSONObject user = users_map.get(user_id);
                user.getJSONObject("current").put("city",city);
                user.getJSONObject("current").put("state",state);
                user.getJSONObject("current").put("country",country);
            }
            
            Statement home_stmt = oracleConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet home_rst = home_stmt.executeQuery(
                "SELECT h.USER_ID, city.CITY_NAME, city.STATE_NAME, city.Country_Name FROM " + 
                hometownCityTableName + " h JOIN " + cityTableName + " city ON h.HOMETOWN_CITY_ID = city.CITY_ID "
            );

            while (home_rst.next()) {
                int user_id = home_rst.getInt(1);
                String city = home_rst.getString(2);
                String state = home_rst.getString(3);
                String country = home_rst.getString(4);

                JSONObject user = users_map.get(user_id);
                user.getJSONObject("hometown").put("city",city);
                user.getJSONObject("hometown").put("state",state);
                user.getJSONObject("hometown").put("country",country);
            }

            friends_rst.close();
            current_rst.close();
            home_rst.close();

            friends_stmt.close();
            current_stmt.close();
            home_stmt.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return users_info;
    }

    // This outputs to a file "output.json"
    // DO NOT MODIFY this function
    public void writeJSON(JSONArray users_info) {
        try {
            FileWriter file = new FileWriter(System.getProperty("user.dir") + "/output.json");
            file.write(users_info.toString());
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
