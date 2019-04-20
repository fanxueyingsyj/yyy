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
	//���д��ʲô
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
		ServerSocket listener = new ServerSocket(59090);//����һ������59090�˿ڵġ���
		System.out.println("The IM server is running...");
		while (true) {
            Socket socket = listener.accept();//�ȴ����Ӵ��룬���û����ô��һֱͣ������ ���������������
            ClientThread ct = new ClientThread(socket);
            ct.start();
            cThreads.add(ct);
            System.out.println("["+socket.getInetAddress()+"] ����������,��ǰ��������["+cThreads.size()+"]��");
		}
	}
	
	//�����������ͬ����Ϣ�������ͻ���
	private void SyncMsg(String Sender,String msg) {
		for(ClientThread ct:cThreads) {
			ct.SendMsg("["+Sender+"]: "+msg);//�ո�����д���Ǵ����
		}
	}
	
//	private void RemoveDisconnectedClients() {
//		cThreads.removeIf(cs -> (cs.disconnected));
//		System.out.println("�����ѶϿ�");
//	}
	
	//�����ר�Ŵ���ͻ��˵�����
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
				BufferedReader br = new BufferedReader(new InputStreamReader(ClientSocket.getInputStream()));//���ַ��������ж�ȡ�ı�
		         while ((msg = br.readLine()) != null) {//���ӶϿ��� ���ѭ�����˳� �Ҳµ�
		        	 PrintMsg(msg);//�ѻ�õ���Ϣ���
		        	 SyncMsg(myIP,msg);//����Ϣͬ���������ͻ���
		             }
		         } 
			catch (Exception ex) {
				cThreads.remove(this);
				SyncMsg(myIP,"�˳�������");
				//disconnected = true;
				//RemoveDisconnectedClients();
			}
		}
		//Ϊʲô��Ҫ����дһ��Print������
		//��Ϊ������Ժ���Ҫ���������ʽ������˵�ٶ������ʱ��
		//ֻ��Ҫ������һ�������Ϳ���
		public void PrintMsg(String m) {
			System.out.println("��"+ClientSocket.getInetAddress()+"��: "+m);
		}
		
		public void SendMsg(String m) {
			try {
				PrintWriter out = new PrintWriter(ClientSocket.getOutputStream(), true);//��ô������ӵġ��������
	            out.println(m);//���������д������
			}
			catch(Exception ex) {
				//TODO:������� �رյ�ǰ�߳�
			}
		}
	}
}
