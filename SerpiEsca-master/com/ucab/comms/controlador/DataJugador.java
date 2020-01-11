package controlador;

import java.util.ArrayList;

public class DataJugador {
	
	private String mac_address;
	private String identificador;
	
	public DataJugador(String mac, String identificador) {
		this.mac_address= mac;
		this.identificador = identificador;
	}
	
	public static void llenadoData(byte[] buffer, ArrayList<DataJugador> arreglo_data) {
		String identificador="";
		for(int i= 14; i< buffer.length; i++) {
			switch(ByteConv.byteToString(buffer[i+6])) {
				case "00000000":
					identificador = "A";
				case "00000001":
					identificador = "B";
				case "00000010":
					identificador = "C";
				case "00000011":
					identificador = "D";
			}
			arreglo_data.add(new DataJugador(ByteConv.getMacAddress(buffer, i),identificador));
		}
	}
	
	public static void llenadoData(ArrayList<Byte> buffer, ArrayList<DataJugador> arreglo_data) {
		String identificador="";
		for(int i= 14; i< buffer.size()-1; i++) {
			switch(ByteConv.byteToString(buffer.get(i + 6))) {
				case "00000000":
					identificador = "A";
				case "00000001":
					identificador = "B";
				case "00000010":
					identificador = "C";
				case "00000011":
					identificador = "D";
			}
			System.out.println(buffer.size());
			System.out.println(i);
			ByteConv.pausas_De_prueba();
			arreglo_data.add(new DataJugador(ByteConv.getMacAddress(buffer, i),identificador));
			i+=6;
		}
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
