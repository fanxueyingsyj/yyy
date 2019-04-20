package FXY;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FXY_IM {
	List<ClientThread> cThreads;
	//随便写点什么
	public static void main(String[] args) {
		try {
			new FXY_IM();
		}
		catch(Exception ex) {
			System.out.println(ex.toString());
		}
	}
	
	public FXY_IM() throws IOException {
		cThreads = new ArrayList<ClientThread>();
		ServerSocket listener = new ServerSocket(59090);//创建一个监听59090端口的……
		System.out.println("The IM server is running...");
		while (true) {
            Socket socket = listener.accept();//等待连接传入，如果没有那么会一直停在这里 这个叫做“阻塞”
            ClientThread ct = new ClientThread(socket);
            ct.start();
            cThreads.add(ct);
            System.out.println("["+socket.getInetAddress()+"] 进入聊天室,当前聊天室有["+cThreads.size()+"]人");
		}
	}
	
	//这个方法用于同步消息给其他客户端
	private void SyncMsg(String Sender,String msg) {
		for(ClientThread ct:cThreads) {
			ct.SendMsg("["+Sender+"]: "+msg);//刚刚那种写法是错误的
		}
	}
	
//	private void RemoveDisconnectedClients() {
//		cThreads.removeIf(cs -> (cs.disconnected));
//		System.out.println("连接已断开");
//	}
	
	//这个类专门处理客户端的事务
	class ClientThread extends Thread {
		Socket ClientSocket;
//		boolean disconnected;
		String myIP;
		public ClientThread(Socket s) {
			ClientSocket = s;
			myIP = s.getInetAddress().toString();
//			disconnected = false;
		}
		public void run() {
			try {
				String msg;
				BufferedReader br = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));//从字符输入流中读取文本
		         while ((msg = br.readLine()) != null) {//连接断开后 这个循环会退出 我猜的
		        	 PrintMsg(msg);//把获得的消息输出
		        	 SyncMsg(myIP,msg);//把消息同步给其他客户端
		             }
		         } 
			catch (Exception ex) {
				cThreads.remove(this);
				SyncMsg(myIP,"退出聊天室");
				//disconnected = true;
				//RemoveDisconnectedClients();
			}
		}
		//为什么我要单独写一个Print方法？
		//因为如果我以后想要更改输出格式，比如说再多输出个时间
		//只需要更改这一个方法就可以
		public void PrintMsg(String m) {
			System.out.println("【"+ClientSocket.getInetAddress()+"】: "+m);
		}
		
		public void SendMsg(String m) {
			try {
				PrintWriter out = new PrintWriter(ClientSocket.getOutputStream(), true);//获得传入连接的“输出流”
	            out.println(m);//向“输出流”写入内容
			}
			catch(Exception ex) {
				//TODO:输出错误 关闭当前线程
			}
		}
	}
}
