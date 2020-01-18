package controlador;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;

public class TramaADD extends Trama implements ITrama {
	
	protected ArrayList<byte[]> identificadores = new ArrayList<byte[]>();
	
	public TramaADD(byte[] identificador, byte[] direccion_final, byte[] direccion_inicial) {
		this.identificadores.add(identificador);
		this.direccion_final= direccion_final;
		this.direccion_inicial= direccion_inicial;
		this.protocolo_interno="0000";
	}
	
	public TramaADD( byte[] direccion_final, byte[] direccion_inicial) {
		this.direccion_final= direccion_final;
		this.direccion_inicial= direccion_inicial;
		this.protocolo_interno="0000";
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
	
	public void agregar_identificador(String identificador, String mac) {
		byte[] nuevo_identificador = new byte[7];
		for(int i=0; i< 6; i++) {
			nuevo_identificador[i]= mac.getBytes()[i];
		}
		nuevo_identificador[6]= (byte) Short.parseShort(identificador,2);
		this.identificadores.add(nuevo_identificador);
	}
	
	public void agregar_identificador(String identificador, NetworkInterface mac_address) {
		byte[] nuevo_identificador = new byte[7];
		byte[] mac= null;
		try {
			mac= mac_address.getHardwareAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		for(int i=0; i< 6; i++) {
			nuevo_identificador[i]= mac[i];
		}
		nuevo_identificador[6]= (byte) Short.parseShort(identificador, 2);
		this.identificadores.add(nuevo_identificador);
	}
	
	public void agregar_identificador(byte[] identificador, NetworkInterface mac_address) {
		byte[] nuevo_identificador = new byte[7];
		byte[] mac= null;
		try {
			mac= mac_address.getHardwareAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		for(int i=0; i< 6; i++) {
			nuevo_identificador[i]= mac[i];
		}
		nuevo_identificador[6]= identificador[0];
		this.identificadores.add(nuevo_identificador);
	}

	public byte[] envio_trama() {
		byte[] trama_envio = new byte[14 + this.identificadores.size() * 7 + 1];
		byte[] trama_envio_super= super.envio_trama("0000");
		int traslado=0;
		for(int i=0; i < 14; i++) {
			trama_envio[i]=trama_envio_super[i];
		}
		for(int i=14; i < 14 + this.identificadores.size() * 7; i++) {
			for(int j=0; j<7; j++) {
				trama_envio[i + j ] = this.identificadores.get(traslado)[j];
			}
			i+=6;
			traslado++;
		}
		trama_envio[14 + this.identificadores.size() * 7] = (byte) Short.parseShort(this.preambulo, 2);
		return trama_envio;
	}
	
	public byte[] envio_trama(String protocolo_interno) {
		byte[] trama_envio = new byte[14 + this.identificadores.size() * 7 + 1];
		byte[] trama_envio_super= super.envio_trama(protocolo_interno);
		int traslado=0;
		for(int i=0; i < 14; i++) 
			trama_envio[i]=trama_envio_super[i];
		for(int i=14; i < 14 + this.identificadores.size() * 7; i++) {
			for(int j=0; j<7; j++) {
				trama_envio[i + j ] = this.identificadores.get(traslado)[j];
			}
			i+=6;
			traslado++;
		}
		trama_envio[14 + this.identificadores.size() * 7] = (byte) Short.parseShort(this.preambulo, 2);
		return trama_envio;
	}
	
	public void byteToIdentificador(byte[] buffer) {
		byte[] identificador = new byte[7];
		for(int i=14; i< buffer.length -1; i++) {
			for(int j=0; j<7; j++) {
				identificador[j] = buffer[i + j];
			}
			i+=6;
			this.identificadores.add(identificador);
		}
	}
	
	public void byteToIdentificador(ArrayList<Byte> buffer) {
		byte[] identificador = new byte[7];
		for(int i=14; i< buffer.size()-1; i++) {
			for(int j=0; j<7; j++) {
				identificador[j] = buffer.get(i + j);
			}
			this.identificadores.add(identificador);
			i+=6;
		}
	}
	
	
}
