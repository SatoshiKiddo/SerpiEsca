package controlador;

import java.util.ArrayList;

public class TramaADD extends Trama implements ITrama {
	
	private byte[] direccion_final;
	private byte[] direccion_inicial;
	private String control_segmento="0000";
	private String protocolo_interno;
	private ArrayList<byte[]> identificadores;
	
	public TramaADD(byte[] identificador, byte[] direccion_final, byte[] direccion_inicial, String protocolo_interno) {
		this.identificadores.add(identificador);
		this.direccion_final= direccion_final;
		this.direccion_inicial= direccion_inicial;
		this.protocolo_interno= protocolo_interno;
	}
	
	public void set_protocolo_interno(String protocolo_interno) {
		this.protocolo_interno= protocolo_interno;
	}
	
	public void set_control_segmento(String control_segmento) {
		this.protocolo_interno= control_segmento;
	}
	
	public void set_direccion_final(byte[] direccion_final) {
		this.direccion_final= direccion_final;
	}
	
	public void set_direccion_inicial(byte[] direccion_inicial) {
		this.direccion_final= direccion_inicial;
	}
	
	public void agregar_identificador(byte[] identificador) {
		this.identificadores.add(identificador);
	}

	public byte[] envio_trama() {
		byte[] trama_envio = new byte[14 + this.identificadores.size() * 7];
		trama_envio[0]= (byte)Short.parseShort(this.get_preambulo(),2);
		for(int i=1; i<7; i++) {
			trama_envio[i]= direccion_final[i];
		}
		for(int i=7; i<113; i++) {
			trama_envio[i]= direccion_inicial[i-7];
		}
		trama_envio[14] = (byte) Short.parseShort(control_segmento + protocolo_interno,2);
		for(int i=15; i< 15 + this.identificadores.size(); i++) {
			for(int j=0; j<7; j++) {
				trama_envio[i + j + ((i - 15) * 7)] = this.identificadores.get(i)[j];
			}
		}
		return trama_envio;
	}
	
}
