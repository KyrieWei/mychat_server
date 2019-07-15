import java.io.BufferedReader;  
import java.io.BufferedWriter;  
import java.io.InputStreamReader;  
import java.io.OutputStreamWriter;  
import java.net.ServerSocket;  
import java.net.Socket;  
import java.text.SimpleDateFormat;  
import java.util.ArrayList;  
import java.util.Date;  
  
//import org.json.JSONObject;
import net.sf.json.JSONObject;

import sendMes.Socket_SendMes;

public class SendMes_Server {
	private boolean isStartServer;
	private boolean duplicate;
	private ServerSocket mServer;

	private ArrayList<Socket_SendMes> mMsgList = new ArrayList<Socket_SendMes>();
	//message array list
	private ArrayList<SocketThread> mThreadList = new ArrayList<SocketThread>();
	//thread array list
	private ArrayList<String> onLineFriendList = new ArrayList<String>();

	private void startSocket(){
		try {
			isStartServer = true;
			int port = 5000;
			mServer = new ServerSocket(port);
			System.out.println("start server, port: " + port);
			Socket socket = null;
			String socketID;
			BufferedReader ireader;
			Socket_SendMes newMsg = new Socket_SendMes();

			//startSendMessageThread();

			while(isStartServer){
				duplicate = false;
				socket = mServer.accept();

				ireader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
				String data = ireader.readLine();
				JSONObject json = JSONObject.fromObject(data);
				newMsg.socketType = json.getString("socketType");
				newMsg.from = json.getString("from");
				newMsg.to = json.getString("to");
				newMsg.msg = json.getString("msg");		
				System.out.println("the newMsg is : socketType " + newMsg.socketType + " from " + newMsg.from + " to " + newMsg.to + " msg ");

			
				//SocketThread thread = new SocketThread(socket);
				//thread.start();
				//Thread.sleep(1000);
				//System.out.println("the thread start, and thread.socketType is :" + thread.socketType);
				if(newMsg.socketType.equals("login")){
					SocketThread thread = new SocketThread(socket,newMsg.from);
					thread.start();
					Thread.sleep(1000);
					for(SocketThread item : mThreadList){
						System.out.println("we have the thread with socketID: " + item.socketID);
						if(item.socketID.equals(thread.socketID))
							duplicate = true;
					}
					if(!duplicate){
						System.out.println("the thread is added to the list " + thread.socketID);
						mThreadList.add(thread);
					}
				} //else {
					//mMsgList.add(newMsg);	
				//}
/*				else if(newMsg.socketType.equals("startchat")){
					//SocketThread thread = new SocketThread(socket,newMsg.from);
					for(SocketThread item : mThreadList){
						if(item.socketID.equals(newMsg.from)){
							SocketThread thread = item;
							getOnlineFriendList(thread);
						}
					}
					//getOnlineFriendList(thread);
				}else{
						
				}*/
				startSendMessageThread();
				
			}

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/*public void getOnlineFriendList(SocketThread thread){
				try{
					for(SocketThread item : mThreadList){
						if(!(item.socketID.equals(thread.socketID)))
							onLineFriendList.add(item.socketID);
					}
					for(SocketThread item : mThreadList){
						if(item.socketID.equals(thread.socketID)){
							BufferedWriter writer = item.writer;
							JSONObject json = new JSONObject();
							json.put("socketType","online_friList");
							json.put("online_friendlist",onLineFriendList);
							writer.write(json.toString() + "\n");
							writer.flush();
						}
					}
				}catch(Exception e){
					e.printStackTrace();
				}
		
	}*/
	
	public void startSendMessageThread(){
		new Thread(){
			@Override
			public void run(){
				super.run();
				try{
					while(isStartServer){
						//System.out.println("the MsgList.size() is " + mMsgList.size());
						if (mMsgList.size() > 0) {
							System.out.println("we are gonna send Msg!!!!!!!");
							Socket_SendMes from = mMsgList.get(0);
							mMsgList.remove(0);
							for(SocketThread to : mThreadList){
								if(to.socketID.equals(from.to)){
									BufferedWriter writer = to.writer;
									JSONObject json = new JSONObject();
									json.put("socketType",from.socketType);
									json.put("from", from.from);
									json.put("msg", from.msg);
									json.put("to", from.to);
									writer.write(json.toString()+"w\n");
									writer.flush();
									//System.out.println("send message successfully: " + from.msg + " >> to socketID: " + from.to);
									break;
								}
							}	
							//mMsgList.remove(0);
							System.out.println("the MsgList.size() is " + mMsgList.size());						
						}
						Thread.sleep(1000);
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}.start();
	}

	public class SocketThread extends Thread {
		public String socketID;
		public Socket socket;
		public String socketType;
		public BufferedWriter writer;
		public BufferedReader reader;

		public SocketThread(Socket socket, String socketID) {
			this.socket = socket;
			this.socketID = socketID;
			System.out.println("add a new client");
		}

		@Override
		public void run() {
			super.run();
			try{
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
				writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
	
				while(isStartServer){
				
						String data = reader.readLine();
						//System.out.println("we read the data from client" + data);
						JSONObject json = JSONObject.fromObject(data);
						Socket_SendMes msg = new Socket_SendMes();
						msg.to = json.getString("to");
						msg.msg = json.getString("msg");
						msg.from = json.getString("from");
						msg.socketType = json.getString("socketType");		
	
						//socketID = msg.from;
						//socketType = msg.socketType;
							
						mMsgList.add(msg);

						System.out.println("receive a message and add it to the MsgList : " + msg.socketType);
					

					//Thread.sleep(100);
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args){
		SendMes_Server server = new SendMes_Server();
		server.startSocket();
	}
}
