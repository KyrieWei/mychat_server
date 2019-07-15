//package mychat1_db.fri_Info;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;  
import java.net.ServerSocket;
import java.net.Socket;
import mychat1_db.operation_db;
import java.util.ArrayList;
import mychat1_db.FriendInfo;
//import org.json.JSONObject;
//import org.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

public class loginAndRegServer {
    public static final int PORT = 9602;

    public static void main(String[] args) {  
        System.out.println("server is running...\n");  
        loginAndRegServer server = new loginAndRegServer();  
        server.init();  
    }  

    public void init() {  
        try {  
            ServerSocket serverSocket = new ServerSocket(PORT);  
            while (true) {  

                Socket client = serverSocket.accept();  

                new HandlerThread(client);  
            }  
        } catch (Exception e) {  
            System.out.println("server exception:" + e.getMessage());  
        }  
    }  

    private class HandlerThread implements Runnable {  
        private Socket socket;  
        public BufferedReader input;
        public BufferedWriter out;
        public HandlerThread(Socket client) {  
            socket = client;  
            new Thread(this).start();  
        }  

        public void run() {  
            try {  

                input = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
                String clientInput = input.readLine();   
		
                JSONObject json = JSONObject.fromObject(clientInput);
                String user_name = json.getString("username");
                String user_password = json.getString("password");
                //String user_IP_ADDR = json.getString("mIP_ADDR");
                String type = json.getString("type");
                //int user_PORT = json.getInt("mPORT");

                operation_db oper_db = new operation_db();		
                System.out.println("the info is: " + user_name + " and " + user_password  + " and " + type);

                String is_success = "0";
		String type_client = "n";
		ArrayList<String[]> friendList = new ArrayList<String[]>();
                if (type.equals("register")){
		   String avatar = json.getString("avatar");	
                   if (!(oper_db.userInfo_add(user_name,user_password,avatar))){
                    is_success = "2";
                    System.out.println("Insert failed!!!!!!");
                    }
                } else if (type.equals("login")){
                   if ((oper_db.userInfo_check(user_name,user_password)) == 1){
                        is_success = "3";
                        System.out.println("Username not exist!!!!!!!!!!!!!!!!!");
                   } else if ((oper_db.userInfo_check(user_name,user_password)) == 2){
                        is_success = "4";
                        System.out.println("Password not correct!!!!!!!");	
                    }else{
			System.out.println("to get friend list");
                        friendList = oper_db.getFriendList(user_name);
                    }
                }else if (type.equals("getfriendinfo")){
			if(!(oper_db.userexist_check(user_name))){
				System.out.println("user name not exist while adding new friend");
				is_success = "3";
			}else{
				System.out.println("user name exists while adding new friend");
				friendList = oper_db.getFriendToAddInfo(user_name);				   type_client = "friendInfo";
			}
		}else if(type.equals("addtolist")){
			oper_db.addToFriendList(user_name,user_password);
			is_success = "0";
		}

                JSONObject json_send = new JSONObject();
		JSONArray fri_arr = new JSONArray();
		for(int i = 0; i < friendList.size(); i ++)
		{
			//fri_arr.put(0,friendList.get(i));
			JSONObject item = new JSONObject();
			item.put("friend_name",friendList.get(i)[0]);
			item.put("friend_ava",friendList.get(i)[1]);
			fri_arr.add(item);
		}

		json_send.put("type", type_client);
                json_send.put("is_success", is_success);
                json_send.put("friendlist", fri_arr);
		//System.out.println("the friend list : " + friendList.get(0).friend_avatar);
		//System.out.println("the json to send is : " + json_send);
                out.write(json_send.toString() + "\n");
                out.flush();
    	        //oper_db.userInfo_check();
                out.close();
                input.close();  
            } catch (Exception e) {  
                System.out.println("server run exception:" + e.getMessage());  

            } finally {  
                if (socket != null) {  
                    try {  
                            //socket.close();  
                    } catch (Exception e) {  
                        socket = null;  
                        System.out.println("server exception:" + e.getMessage());  
                    }  
                }  
            } 
        }  
    }  
}  
