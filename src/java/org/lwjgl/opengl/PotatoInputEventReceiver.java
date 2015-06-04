package org.lwjgl.opengl;

import java.io.*;
import java.net.*;
import java.util.*;

public class PotatoInputEventReceiver implements Runnable {
	public Socket sock;
	public OutputStream os;
	public void init(int port) throws Exception {
		sock = new Socket("127.0.0.1", port);
		os = sock.getOutputStream();
		Thread t = new Thread(this, "Input event receiver");
		t.setDaemon(true);
		t.start();
	}

	public void run() {
		try {
			InputStream is = sock.getInputStream();
			byte[] msg = new byte[24];
			while (true) {
				is.read(msg, 0, 24);
				switch (msg[0]) {
					case 'p':
						putMouseEventWithCoords(msg);
						break;
					case 'c':
						setMouseCoords(msg);
						break;
					case 'w':
						setWindowSize(msg);
						break;
					case 'g':
						setMouseButtonInGrabMode(msg);
						break;
					case 'k':
						setKey(msg);
						break;
					default:
						break;
				}
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public void setGrab(boolean b) {
		if (os == null) return;
		try {
			os.write(b? (byte) 'G': (byte) 'g');
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	private void putMouseEventWithCoords(byte[] msg) {
		PotatoDisplay.putMouseEventWithCoords(msg[1], msg[2], readInt(msg, 3), readInt(msg, 7),
			readInt(msg, 11), ((readInt(msg, 15) & 0xffffffffL) << 32) | (readInt(msg, 19) & 0xffffffffL));
	}

	private void setMouseCoords(byte[] msg) {
		PotatoDisplay.mouseX = readInt(msg, 1);
		PotatoDisplay.mouseY = readInt(msg, 5);
	}

	private void setWindowSize(byte[] msg) {
		PotatoDisplay.windowWidth = readInt(msg, 1);
		PotatoDisplay.windowHeight = readInt(msg, 5);
	}

	private void setMouseButtonInGrabMode(byte[] msg) {
		PotatoDisplay.setMouseButtonInGrabMode(msg[1], msg[2]);
	}

	private void setKey(byte[] msg) {
		PotatoDisplay.setKey(readInt(msg, 1), (char) readInt(msg, 5), msg[9] == (byte) 1);
	}

	private static int readInt(byte[] m, int i) {
		return ((m[i] & 0xff) << 24) | ((m[i+1] & 0xff) << 16) | ((m[i+2] & 0xff) << 8) | (m[i+3] & 0xff);
	}
}
