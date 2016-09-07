package com.thecherno.rainserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.thecherno.raincloud.serialization.RCDatabase;

public class Server {

	private int port;
	private Thread listenThread;
	private boolean listening = false;
	private DatagramSocket socket;

	private final int MAX_PACKET_SIZE = 1024;
	private byte[] receivedDataBuffer = new byte[MAX_PACKET_SIZE * 10];

	public Server(int port) {
		this.port = port;
	}

	public void start() {

		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}

		listening = true;

		listenThread = new Thread(() -> listen(), "RainCloudServer-ListenThread");
		listenThread.start();
	}

	private void listen() {
		while (listening) {
			DatagramPacket packet = new DatagramPacket(receivedDataBuffer, MAX_PACKET_SIZE);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			process(packet);
		}
	}

	private void process(DatagramPacket packet) {
		byte[] data = packet.getData();
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		dumpPacket(packet);

		if (true) return;

		if (new String(data, 0, 4).equals("RCDB")) {
			RCDatabase database = RCDatabase.Deserialize(data);
			String username = database.findObject("root").findString("username").getString();
			process(database);
		} else {
			switch (data[0]) {
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;

			}

		}
	}

	private void process(RCDatabase database) {

	}

	public void send(byte[] data, InetAddress address, int port) {
		assert (socket.isConnected());
		DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void dumpPacket(DatagramPacket packet) {
		byte[] data = packet.getData();
		InetAddress address = packet.getAddress();
		int port = packet.getPort();

		System.out.println("----------------------");
		System.out.println("PACKET:");
		System.out.println("\t" + address.getHostAddress() + ":" + port);
		System.out.println();
		System.out.println("\tContents:");
		System.out.println("\t\t" + new String(data));
		System.out.println("-----------------------");
	}
}
