package Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class ClientThread {
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String name;
	// 打印消息集合
	private LinkedList<String> linkedList = new LinkedList<>();
	// 输入标记
	private boolean inputFlag = false;

	// 客户端启动方法
	public void launch() {
		try {
            //连接聊天服务器
			this.socket = new Socket("127.0.0.1", 9000);
            //格式化输入
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            //格式化输出
			this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            //起个昵称发送到服务器
			System.out.println("起个昵称：");
			this.name = new Scanner(System.in).nextLine();
			out.println(this.name);
			out.flush();

            //消息接收线程
			new Thread() {
				@Override
				public void run() {
					receive();
				}
			}.start();

            //消息输入线程
			new Thread() {
				@Override
				public void run() {
					input();
				}
			}.start();

            //消息打印线程
			new Thread() {
				@Override
				public void run() {
					mesPrint();
				}
			}.start();
		} catch (Exception e) {
			System.out.println("无法连接服务器");
		}
	}

	private void mesPrint() {
		while (true) {
            //同步代码块，检查消息集合以及输入信号，当消息集合中有数据，输入信号为false时打印消息
			synchronized (linkedList) {
				while (linkedList.size() == 0 || inputFlag) {
					try {
						linkedList.wait();
					} catch (InterruptedException e) {
					}
				}
				String msg = linkedList.removeFirst();
				System.out.println(msg);
			}
		}
	}

	private void input() {
		System.out.println("按回车键输入内容");
		while (true) {
            //按回车才能输入，防止该循环一直将输入信号打开
			new Scanner(System.in).nextLine();
            //准备输入时，打开输入信号
			inputFlag = true;
			System.out.print("输入聊天内容：");
			String s = new Scanner(System.in).nextLine();
			out.println(s);
			out.flush();
			// 完成输入后，关闭输入信号并通知等待的线程
			inputFlag = false;
			synchronized (linkedList) {
				linkedList.notifyAll();
			}
		}
	}

	private void receive() {
		try {
			String s;
			while ((s = in.readLine()) != null) {
				// 接收到消息时，将消息放入集合中并通知等待的线程
				synchronized (linkedList) {
					linkedList.add(s);
					linkedList.notifyAll();
				}
			}
		} catch (Exception e) {
		}
		System.out.println("已经与服务器断开连接");
	}

}