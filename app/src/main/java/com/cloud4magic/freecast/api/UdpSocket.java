package com.cloud4magic.freecast.api;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;


class UdpSocket {
	//region members
	DatagramSocket _socket;
	//endregion

	//region ctor
	UdpSocket(int port) {
		try {
			_socket = new DatagramSocket(port);
		} catch (SocketException ignored) {}
	}
	//endregion

	//region firendly functions
	boolean connect(String ip, int port) {
		boolean ret = false;
		try {
			SocketAddress socketadd = new InetSocketAddress(
					ip, port);
			_socket.connect(socketadd);
			ret = true;
		} catch (SocketException ignored) {}

		return ret;
	}

	boolean send(String ip, byte[] data, int port) {
		boolean ret = false;
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			packet.setAddress(InetAddress.getByName(ip));
			packet.setPort(port);
			_socket.send(packet);
			ret = true;
		} catch (IOException ignored) {}

		return ret;

	}

	boolean send(byte[] data) {
		boolean ret = false;
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			_socket.send(packet);
			ret = true;
		} catch (IOException ignored) {}

		return ret;
	}

	boolean send(byte[] data, int size) {
		boolean ret = false;
		DatagramPacket packet = new DatagramPacket(data, size);
		try {
			_socket.send(packet);
			ret = true;
		} catch (IOException ignored) {}

		return ret;
	}

	boolean setTimeout(int ms) {
		boolean ret = false;
		try {
			_socket.setSoTimeout(ms);
			ret = true;
		} catch (SocketException ignored) {}

		return ret;
	}

	byte[] read() {
		byte[] data = null;
		DatagramPacket packet = new DatagramPacket(new byte[1200], 1200);
		try {
			_socket.receive(packet);
			if (packet.getLength() > 0) {
				data = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), 0, data, 0,
						data.length);
			}
		} catch (IOException ignored) {}

		return data;
	}

	void close() {
		if (_socket != null && !_socket.isClosed())
			_socket.close();
	}
	//endregion
}
