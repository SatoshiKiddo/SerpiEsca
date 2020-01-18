package controlador;

import java.util.ArrayList;

import sample.Main;

public class DataJugador {
	
	private String mac_address;
	private String identificador;
	
	public DataJugador(String mac, String identificador) {
		this.mac_address= mac;
		this.identificador = identificador;
	}
	
	public static void llenadoDataADD(byte[] buffer, ArrayList<DataJugador> arreglo_data) {
		String identificador="";
		for(int i= 14; i< buffer.length; i++) {
			switch(ByteConv.byteToString(buffer[i+6])) {
				case "00000000":
					identificador = "A";
					break;
				case "00000001":
					identificador = "B";
					break;
				case "00000010":
					identificador = "C";
					break;
				case "00000011":
					identificador = "D";
					break;
			}
			i+=6;
			arreglo_data.add(new DataJugador(ByteConv.getMacAddress(buffer, i),identificador));
			System.out.println(identificador);
		}
	}
	
	public static void llenadoDataADD(ArrayList<Byte> buffer, ArrayList<DataJugador> arreglo_data) {
		String identificador="";
		System.out.println(buffer.size());
		for(int i= 14; i< buffer.size()-1; i++) {
			switch(ByteConv.byteToString(buffer.get(i + 6))) {
				case "00000000":
					identificador = "A";
					break;
				case "00000001":
					identificador = "B";
					break;
				case "00000010":
					identificador = "C";
					break;
				case "00000011":
					identificador = "D";
					break;
			}
			arreglo_data.add(new DataJugador(ByteConv.getMacAddress(buffer, i),identificador));
			i+=6;
			System.out.println(identificador);
		}
	}
	
	public static void llenadoDataADK(ArrayList<Byte> buffer, ArrayList<DataJugador> arreglo_data) {
		String identificador="";
		for(int i= 14; i< buffer.size()-2; i++) {
			switch(ByteConv.byteToString(buffer.get(i + 6))) {
				case "00000000":
					identificador = "A";
					break;
				case "00000001":
					identificador = "B";
					break;
				case "00000010":
					identificador = "C";
					break;
				case "00000011":
					identificador = "D";
					break;
			}
			arreglo_data.add(new DataJugador(ByteConv.getMacAddress(buffer, i),identificador));
			i+=6;
			System.out.println(identificador);
		}
	}
	
	public static String DesignaciónIdentificador(ArrayList<Byte> buffer, Main interfaz) {
		switch(ByteConv.byteToString(buffer.get(buffer.size()-2))) {
			case "00000000":
				interfaz.jugador= 2;
				return "00000001";
			case "00000001":
				interfaz.jugador= 3;
				return "00000010";
			case "00000010":
				interfaz.jugador= 4;
				return "00000011";
		}
		return null;
	}
	
	public String getMac_address() {
		return mac_address;
	}
	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

}
