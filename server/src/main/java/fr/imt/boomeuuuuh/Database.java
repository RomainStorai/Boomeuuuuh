/*
 * Copyright (c) 2022.
 * Authors : Storaï R, Faure B, Mathieu A, Garry A, Nicolau T, Bregier M.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package fr.imt.boomeuuuuh;

import fr.imt.boomeuuuuh.players.PlayerData;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

public class Database {

    private Connection connection;
    private MessageDigest messageDigest;

    /**
     * Create an instance of Database. One instance = one connection
     */
    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            checkDatabase();
            Boomeuuuuh.logger.info("Connection to SQLite has been established.");

            messageDigest = MessageDigest.getInstance("MD5");
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to connect to the database : " + e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Boomeuuuuh.logger.severe("Impossible to find MD5 algorithm : " + e.getMessage());
        }
    }

    /**
     * Create an account that is not already existing in the database
     *
     * @param username of the account
     * @param MdP      password encrypted in MD5
     */
    public void createAccount(String username, String MdP) {
        String request = "INSERT INTO bomberman(username,MdP,level,currentSkin,skin1,skin2,skin3,skin4,skin5,skin6,skin7,skin8,skin9,skin10,gold,kills,maxkillstreak,wins) VALUES(?,?,1,'skin1',1,0,0,0,0,0,0,0,0,0,0,0,0,0)";

        // Encrypt password
        messageDigest.update(MdP.getBytes());
        byte[] bytes = messageDigest.digest();
        StringBuilder s = new StringBuilder();
        for (byte aByte : bytes) {
            s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        String encryptedPassword = s.toString();

        try {
            PreparedStatement pstmt = connection.prepareStatement(request);
            pstmt.setString(1, username);
            pstmt.setString(2, encryptedPassword);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
        }
    }

    /**
     * Attempt to log into an account.
     *
     * @param username to log
     * @param password of the account
     * @return true if the password is correct and the account existing
     */
    public boolean login(String username, String password) {
        if (!usernameAlreadyExists(username))
            return false;

        String resultPassword = null;
        // Encrypt password
        messageDigest.update(password.getBytes());
        byte[] bytes = messageDigest.digest();
        StringBuilder s = new StringBuilder();
        for (byte aByte : bytes) {
            s.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        String encryptedPassword = s.toString();

        String request = "SELECT MdP FROM bomberman WHERE username= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(request)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            resultPassword = rs.getString("MdP");
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
            e.printStackTrace();
        }

        return encryptedPassword.equals(resultPassword);
    }

    /**
     * Checks if a username already exists in the database
     *
     * @param username to check
     * @return true if an account already exists
     */
    public boolean usernameAlreadyExists(String username) {
        String sql = "SELECT COUNT(*) AS number FROM bomberman WHERE username= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // set the value
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            int result = rs.getInt("number");
            return result >= 1;
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
        }

        return false;
    }

    /**
     * Returns the PlayerData of a user
     *
     * @param username to fetch
     * @return PlayerData
     */
    public PlayerData getPlayerData(String username) {
        int gold = 0;
        int level = 0;
        int kills = 0;
        int maxkillstreak = 0;
        int wins = 0;
        String currentSkin = null;
        ArrayList<String> unlockedSkins = new ArrayList<>();
        String sql = "SELECT level,currentSkin,skin1,skin2,skin3,skin4,skin5,skin6,skin7,skin8,skin9,skin10,gold,kills,maxkillstreak,wins FROM bomberman WHERE username= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // set the value
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            // loop through the result set
            gold = rs.getInt("gold");
            level = rs.getInt("level");
            kills = rs.getInt("kills");
            maxkillstreak = rs.getInt("maxkillstreak");
            wins = rs.getInt("wins");
            currentSkin = rs.getString("currentSkin");
            for (int i = 1; i < 10; i++)
                if (rs.getInt("skin" + i) == 1)
                    unlockedSkins.add("skin" + i);
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
        }

        return new PlayerData(gold, level, currentSkin, unlockedSkins, kills, maxkillstreak, wins);
    }

    /**
     * Sets the player level in the database
     *
     * @param username username
     * @param level    to save
     */
    public void setLevel(String username, int level) {
        String request = "UPDATE bomberman SET level = ? "
                + "WHERE username= ?";

        try (PreparedStatement pstmt = connection.prepareStatement(request)) {

            // set the corresponding param
            pstmt.setInt(1, level);
            pstmt.setString(2, username);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
        }
    }

    /**
     * Sets the player kills in the database
     *
     * @param username username
     * @param kills    kills
     */
    public void setKills(String username, int kills) {
        String request = "UPDATE bomberman SET kills = ? "
                + "WHERE username= ?";

        try (PreparedStatement pstmt = connection.prepareStatement(request)) {

            // set the corresponding param
            pstmt.setInt(1, kills);
            pstmt.setString(2, username);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
        }
    }

    /**
     * Sets the player maxkillstreak in the database
     *
     * @param username      user
     * @param maxkillstreak maxkillstreak
     */
    public void setMaxKillstreak(String username, int maxkillstreak) {
        String request = "UPDATE bomberman SET maxkillstreak = ? "
                + "WHERE username= ?";

        try (PreparedStatement pstmt = connection.prepareStatement(request)) {

            // set the corresponding param
            pstmt.setInt(1, maxkillstreak);
            pstmt.setString(2, username);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
        }
    }

    /**
     * Sets the player wins in the database
     *
     * @param username user
     * @param wins     wins
     */
    public void setWins(String username, int wins) {
        String request = "UPDATE bomberman SET wins = ? "
                + "WHERE username= ?";

        try (PreparedStatement pstmt = connection.prepareStatement(request)) {

            // set the corresponding param
            pstmt.setInt(1, wins);
            pstmt.setString(2, username);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
        }
    }


    /**
     * Sets the player golds in the database
     *
     * @param username user
     * @param gold     amount
     */
    public void setGold(String username, int gold) {
        String request = "UPDATE bomberman SET gold = ? " + "WHERE username= ?";

        try (PreparedStatement pstmt = connection.prepareStatement(request)) {

            // set the corresponding param
            pstmt.setInt(1, gold);
            pstmt.setString(2, username);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
        }
    }

    /**
     * Unlock a skin to a specific player
     *
     * @param username player
     * @param idSkin   skin number from 1 to 10
     */
    public void unlockSkin(String username, String idSkin) {
        String request = "UPDATE bomberman SET " + idSkin + " = 1 WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(request)) {

            // set the corresponding param
            pstmt.setString(1, username);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
        }
    }

    /**
     * Sets the player current skin
     *
     * @param username user
     * @param idSkin   skin id
     */
    public void setCurrentSkin(String username, String idSkin) {

        String request = "UPDATE bomberman SET currentSkin = ? WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(request)) {

            // set the corresponding param
            pstmt.setString(1, idSkin);
            pstmt.setString(2, username);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
        }
    }

    /**
     * Check if the database exists, if not it initializes it
     *
     * @throws SQLException if there is an exception
     */
    private void checkDatabase() throws SQLException {
        String createRequest = "CREATE TABLE \"bomberman\" (" +
                "    \"username\"    varchar," +
                "    \"MdP\"    varchar," +
                "    \"level\"    integer," +
                "    \"currentSkin\"    varchar," +
                "    \"skin1\"    integer," +
                "    \"skin2\"    integer," +
                "    \"skin3\"    integer," +
                "    \"skin4\"    integer," +
                "    \"skin5\"    integer," +
                "    \"skin6\"    integer," +
                "    \"skin7\"    integer," +
                "    \"skin8\"    integer," +
                "    \"skin9\"    integer," +
                "    \"skin10\"    integer," +
                "    \"gold\"    integer," +
                "    \"kills\"    integer," +
                "    \"maxkillstreak\"    integer," +
                "    \"wins\"    integer," +
                "    CONSTRAINT \"bomberman_pk\" PRIMARY KEY(\"username\")" +
                ")";

        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(null, null, "bomberman", null);
        rs.next();
        if (rs.getRow() <= 0) {
            try (PreparedStatement pstmt = connection.prepareStatement(createRequest)) {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                Boomeuuuuh.logger.severe("Impossible to prepare the statement to the database : " + e.getMessage());
            }
        }
    }
}
