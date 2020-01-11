package Simulación;

import com.fazecast.jSerialComm.SerialPort;

public class PruebasSeriales {
	
	public static void main (String[] args) {
		SerialPort[] puertos = new SerialPort[4];
		puertos[0] = SerialPort.getCommPort("COM3");
		puertos[1] = SerialPort.getCommPort("COM4");
		puertos[2] = SerialPort.getCommPort("COM1");
		puertos[3] = SerialPort.getCommPort("COM2");
		puertos[0].openPort();
		puertos[1].openPort();
		puertos[2].openPort();
		puertos[3].openPort();
		byte[] buffer = new byte[5];

		puertos[0].setComPortParameters(2400, 8, 0, 1);
		puertos[1].setComPortParameters(2400, 8, 0, 1);
		while(1==1) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			puertos[0].writeBytes("EPALE".getBytes(), "EPALE".length());
			System.out.println(puertos[1].bytesAvailable());
			puertos[1].readBytes(buffer, 5);
			puertos[3].writeBytes("EPALE".getBytes(), "EPALE".length());
			System.out.println(puertos[2].bytesAvailable());
			puertos[2].readBytes(buffer, 5);
		}
	}

}
