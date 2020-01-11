package controlador;

import java.util.ArrayList;

public class TramaADK extends TramaADD {
	
	private String tablero;
	
	public TramaADK(byte[] identificador, byte[] direccion_final, byte[] direccion_inicial, String tablero) {
		super(identificador,direccion_final,direccion_inicial);
		this.tablero= tablero;
		this.protocolo_interno="0001";
	}
	
	public TramaADK(byte[] direccion_final, byte[] direccion_inicial, String tablero) {
		super(direccion_final,direccion_inicial);
		this.tablero= tablero;
		this.protocolo_interno="0001";
	}
	
	public byte[] envio_trama() {
		int lengthTrama = 14 + this.identificadores.size() * 7 + 2;
		byte[] trama_envio = new byte[lengthTrama];
		byte[] trama_envio_super= super.envio_trama("0001");
		for(int i=0; i < trama_envio_super.length - 1; i++) 
			trama_envio[i]=trama_envio_super[i];
		trama_envio[lengthTrama - 2]= (byte) Short.parseShort("0000" + tablero, 2);
		trama_envio[lengthTrama - 1] = (byte) Short.parseShort(this.preambulo, 2);
		return trama_envio;
	}
	
}
