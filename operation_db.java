package mychat1_db;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
//import fri_Info.FriendInfo;
//import FriendInfo;

public class operation_db {

	// final public String username;
	// final public String password;
	// final public String send_Message;		

	final public String driver = "com.mysql.jdbc.Driver";
	final public String dbName = "mychatDB";
	final public String userName = "root";
	final public String passWord = "kyriewei";
	final public String url = "jdbc:mysql://localhost:3306/"+dbName;

	//FriendInfo fri_info = new FriendInfo();	

	public boolean userInfo_add(String username_add, String password_add, String avatar_add){
		
		String sql_insert = "insert into UserInfo (username, password, avatar)" + "values(?,?,?)";

		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, userName, passWord);
			PreparedStatement ins_ps = conn.prepareStatement(sql_insert);
			System.out.println("username to add: " + username_add + " password to add " + password_add);
			ins_ps.setString(1,username_add);
			ins_ps.setString(2,password_add);
			ins_ps.setBytes(3,avatar_add.getBytes());
			//ins_ps.setString(3,ip_addr);
			//ins_ps.setInt(4,port);
			ins_ps.executeUpdate();

			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public boolean userexist_check(String username){
		String sql_check = "select username from UserInfo where username = '" + username + "';";
		try{
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, userName, passWord);
			PreparedStatement che_ps = conn.prepareStatement(sql_check);
			ResultSet rs = che_ps.executeQuery();
			if(!(rs.next())){
				return false;
			}

			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}
	
	public int userInfo_check(String username_check, String password_check){

		String sql_check = "select *from UserInfo where username = '" + username_check + "';" ;
		//String sql_change_ip = "update UserInfo set ipaddr = '" + ip_addr + "' where username = 'username_check';";

		try {

			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, userName, passWord);
			PreparedStatement che_ps = conn.prepareStatement(sql_check);
			ResultSet rs = che_ps.executeQuery();
			
			if(rs.next()){
				System.out.println("username: " + rs.getString(1) + " password: " + rs.getString(2));
				if(!(rs.getString(2).equals(password_check))){
					System.out.println("password not correct!");
					return 2;
				}
			}else{
				System.out.println("the user does not exist!");
				return 1;
			}

			//PreparedStatement cha_ps = conn.prepareStatement(sql_change_ip);

			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if(che_ps != null) {
				try {
					che_ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public ArrayList<String[]> getFriendList(String username){
		
		String sql_getlist = "select FriendList.friendname, UserInfo.avatar from FriendList join UserInfo on UserInfo.username = FriendList.friendname where FriendList.username ='" + username + "';";
		
		ArrayList<String[]> friendList = new ArrayList<String[]>();

		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, userName, passWord);
			PreparedStatement ins_ps = conn.prepareStatement(sql_getlist);
			//ins_ps.setString(1,username);
		
			ResultSet rs = ins_ps.executeQuery();
			
			//String[] friend_item = new String[2];

			while (rs.next()){
				//System.out.println("he has friend: " + rs.getString(1));
				String[] friend_item = new String[2];
				friend_item[0] = rs.getString(1);
				System.out.println("he has friend: " + friend_item[0]);
				Blob blob = rs.getBlob(2);
				byte[] avaByte = blob.getBytes(1, (int)blob.length());
				String avaStr = new String(avaByte);
				//System.out.println("the avatar is : " + avaStr);
				friend_item[1] = avaStr;

				friendList.add(friend_item);
			}

			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return friendList;
	}

	public ArrayList<String[]> getFriendToAddInfo(String username){
		String sql_getlist = "select username, avatar from UserInfo where username = '" + username + "';";
		ArrayList<String[]> friendInfo = new ArrayList<String[]>();
		try{
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, userName, passWord);
			PreparedStatement add_ps = conn.prepareStatement(sql_getlist);
			ResultSet rs = add_ps.executeQuery();
			while(rs.next()){
				String[] item = new String[2];
				item[0] = rs.getString(1);
				System.out.println("getfriendinfo : " + item[0]);
				Blob blob = rs.getBlob(2);
				byte[] avaByte = blob.getBytes(1,(int)blob.length());
				String avaStr = new String(avaByte);
				item[1] = avaStr;
				//System.out.println("getfriendava : " + item[1]);
				friendInfo.add(item);
			}
			
			if(conn != null){
				try{
					conn.close();	
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return friendInfo;
	}

	public void addToFriendList(String username, String friend_name){
		String sql_addFriend = "insert into FriendList (username, friendname)" + "values(?,?)";
		try{
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(url, userName, passWord);
		PreparedStatement add_ps = conn.prepareStatement(sql_addFriend);
		add_ps.executeUpdate();

		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
			 	e.printStackTrace();
			}
		}

	} catch (Exception e) {
		e.printStackTrace();
	}
	}
}	
