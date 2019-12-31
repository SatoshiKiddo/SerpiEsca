package controlador;

import java.util.ArrayList;

public class TramaADD extends Trama implements ITrama {
	
	protected ArrayList<byte[]> identificadores;
	
	public TramaADD(byte[] identificador, byte[] direccion_final, byte[] direccion_inicial) {
		this.identificadores.add(identificador);
		this.direccion_final= direccion_final;
		this.direccion_inicial= direccion_inicial;
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
		byte[] trama_envio_super= super.envio_trama();
		for(int i=0; i < 14; i++) 
			trama_envio[i]=trama_envio_super[i];
		for(int i=14; i< 16 + this.identificadores.size(); i++) {
			for(int j=0; j<7; j++) {
				trama_envio[i + j + ((i - 15) * 7)] = this.identificadores.get(i)[j];
			}
		}
		return trama_envio;
	}
	
}
