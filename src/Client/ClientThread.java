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
	// ��ӡ��Ϣ����
	private LinkedList<String> linkedList = new LinkedList<>();
	// ������
	private boolean inputFlag = false;

	// �ͻ�����������
	public void launch() {
		try {
            //�������������
			this.socket = new Socket("127.0.0.1", 9000);
            //��ʽ������
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            //��ʽ�����
			this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            //����ǳƷ��͵�������
			System.out.println("����ǳƣ�");
			this.name = new Scanner(System.in).nextLine();
			out.println(this.name);
			out.flush();

            //��Ϣ�����߳�
			new Thread() {
				@Override
				public void run() {
					receive();
				}
			}.start();

            //��Ϣ�����߳�
			new Thread() {
				@Override
				public void run() {
					input();
				}
			}.start();

            //��Ϣ��ӡ�߳�
			new Thread() {
				@Override
				public void run() {
					mesPrint();
				}
			}.start();
		} catch (Exception e) {
			System.out.println("�޷����ӷ�����");
		}
	}

	private void mesPrint() {
		while (true) {
            //ͬ������飬�����Ϣ�����Լ������źţ�����Ϣ�����������ݣ������ź�Ϊfalseʱ��ӡ��Ϣ
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
		System.out.println("���س�����������");
		while (true) {
            //���س��������룬��ֹ��ѭ��һֱ�������źŴ�
			new Scanner(System.in).nextLine();
            //׼������ʱ���������ź�
			inputFlag = true;
			System.out.print("�����������ݣ�");
			String s = new Scanner(System.in).nextLine();
			out.println(s);
			out.flush();
			// �������󣬹ر������źŲ�֪ͨ�ȴ����߳�
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
				// ���յ���Ϣʱ������Ϣ���뼯���в�֪ͨ�ȴ����߳�
				synchronized (linkedList) {
					linkedList.add(s);
					linkedList.notifyAll();
				}
			}
		} catch (Exception e) {
		}
		System.out.println("�Ѿ���������Ͽ�����");
	}

}