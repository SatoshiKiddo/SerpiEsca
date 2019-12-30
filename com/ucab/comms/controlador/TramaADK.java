package controlador;

import java.util.ArrayList;

public class TramaADK extends TramaADD {
	
	private String tablero;
	
	public TramaADK(byte[] identificador, byte[] direccion_final, byte[] direccion_inicial, String protocolo_interno, String tablero) {
		super(identificador,direccion_final,direccion_inicial,protocolo_interno);
		this.tablero= tablero;
	}
	
	public byte[] envio_trama() {
		int lengthTrama = 14 + this.identificadores.size() * 7 + 1;
		byte[] trama_envio = new byte[lengthTrama];
		byte[] trama_envio_super= super.envio_trama();
		for(int i=0; i < lengthTrama - 1; i++) 
			trama_envio[i]=trama_envio_super[i];
		trama_envio[lengthTrama + 1]= (byte) Short.parseShort("0000" + tablero, 2);
		return trama_envio;
	}
	
}
