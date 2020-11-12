package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 
 */
public class ServerThread {
	// ������󼯺�
	private ArrayList<Chat> chatList = new ArrayList<>();

	// ��������������
	public void launch() {
		new Thread() {
			@Override
			public void run() {
				try {
					// ���������socket�߳�
					ServerSocket serverSocket = new ServerSocket(9000);
					System.out.println("������9000�˿������ɹ�");
					while (true) {
						// �ȴ��ͻ�������
						Socket socket = serverSocket.accept();
						System.out.println("һ���ͻ�������");
						// ����һ�����촦���߳�
						Chat chat = new Chat(socket);
						// �����߳�����
						chat.start();
					}
				} catch (IOException e) {
					System.out.println("����˿ڱ�ռ�ã�����ʧ��");
				}
			}
		}.start();

	}

	// ����ͨѶ��,����˸�����������ص���
	class Chat extends Thread {
		Socket socket;
		BufferedReader in;
		PrintWriter out;
		private String name;

		public Chat(Socket socket) {
			this.socket = socket;
		}

		// ��������Ϣ����
		public void send(String msg) {
			// ��Ϣ���͵�������
			out.println(msg);
			// �����ֱ�ӽ����������ݷ��������ȴ���������
			out.flush();
		}

		//Ⱥ����Ϣ
		public void sendAll(String msg) {
			for (Chat c : chatList) {
				c.send(msg);
			}
		}

		//���촦��
		@Override
		public void run() {
			try {
                //��ʽ��������
				this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                //��ʽ�������
				this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                //��ȡ�ͻ�������
				this.name = in.readLine();
				send(this.name + "��ӭ���������ң�");
                //��ǰ���������뼯����
				chatList.add(this);
                //Ⱥ��������Ϣ
				sendAll(this.name + "�Ѿ�����������");
				String chatMessage;
				while ((chatMessage = in.readLine()) != null) {
					sendAll(this.name + ":" + chatMessage);
				}
			} catch (IOException e) {
			}
            //��ǰ�������Ͽ�ʱ�������Ƴ�����
			chatList.remove(this);
			sendAll(this.name + "�Ѿ��뿪������");
		}
	}

    //�����������
	public static void main(String[] args) {
		ServerThread chatServer = new ServerThread();
		chatServer.launch();
	}
}