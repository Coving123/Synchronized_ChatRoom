package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 
 */
public class ServerThread {
	// 聊天对象集合
	private ArrayList<Chat> chatList = new ArrayList<>();

	// 聊天室启动方法
	public void launch() {
		new Thread() {
			@Override
			public void run() {
				try {
					// 创建服务端socket线程
					ServerSocket serverSocket = new ServerSocket(9000);
					System.out.println("服务在9000端口启动成功");
					while (true) {
						// 等待客户端连接
						Socket socket = serverSocket.accept();
						System.out.println("一个客户端上线");
						// 创建一个聊天处理线程
						Chat chat = new Chat(socket);
						// 聊天线程启动
						chat.start();
					}
				} catch (IOException e) {
					System.out.println("服务端口被占用，启动失败");
				}
			}
		}.start();

	}

	// 聊天通讯类,服务端负责处理聊天相关的类
	class Chat extends Thread {
		Socket socket;
		BufferedReader in;
		PrintWriter out;
		private String name;

		public Chat(Socket socket) {
			this.socket = socket;
		}

		// 聊天室消息发送
		public void send(String msg) {
			// 消息发送到缓冲区
			out.println(msg);
			// 输出流直接将缓冲区内容发出，不等待缓冲区满
			out.flush();
		}

		//群发消息
		public void sendAll(String msg) {
			for (Chat c : chatList) {
				c.send(msg);
			}
		}

		//聊天处理
		@Override
		public void run() {
			try {
                //格式化输入流
				this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                //格式化输出流
				this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
                //获取客户端名称
				this.name = in.readLine();
				send(this.name + "欢迎进入聊天室！");
                //当前聊天对象加入集合中
				chatList.add(this);
                //群发上线消息
				sendAll(this.name + "已经进入聊天室");
				String chatMessage;
				while ((chatMessage = in.readLine()) != null) {
					sendAll(this.name + ":" + chatMessage);
				}
			} catch (IOException e) {
			}
            //当前聊天对象断开时，将其移出集合
			chatList.remove(this);
			sendAll(this.name + "已经离开聊天室");
		}
	}

    //服务端主函数
	public static void main(String[] args) {
		ServerThread chatServer = new ServerThread();
		chatServer.launch();
	}
}