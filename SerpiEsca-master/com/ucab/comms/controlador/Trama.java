package controlador;

import java.util.ArrayList;

public class Trama {

	protected String preambulo = "01111110";
	protected byte[] direccion_final;
	protected byte[] direccion_inicial;
	protected String control_segmento="0000";
	protected String protocolo_interno="1111";
	
	public byte[] envio_trama(String protocolo_interno) {
		byte[] trama_envio = new byte[14];
		this.protocolo_interno= protocolo_interno;
		trama_envio[0]= (byte)Short.parseShort(this.preambulo,2);
		for(int i=1; i<7; i++) {
			trama_envio[i]= direccion_final[i-1];
		}
		for(int i=7; i<13; i++) {
			trama_envio[i]= direccion_inicial[i-7];
		}
		trama_envio[13] = (byte) Short.parseShort(control_segmento + protocolo_interno,2);
		return trama_envio;
	}
	
	public byte[] envio_trama_token() {
		this.control_segmento= "0001";
		return envio_trama("1111");
	}
	
	public String get_preambulo() {
		return this.preambulo;
	}
	
	public static byte[] addBytesTrama(byte[] buffer, byte[] add) {
		byte[] retorno = new byte[buffer.length + add.length];
		for(int i=0; i<buffer.length; i++) {
			retorno[i]= buffer[i];
		}
		for(int i= buffer.length; i<buffer.length + add.length; i++) {
			retorno[i]= add[i];
		}
		return retorno;
	}
	
	public static byte[] getDireccionFinal(ArrayList<Byte> buffer) {
		byte[] retorno= new byte[6];
		for(int i=1; i<7; i++) {
			retorno[i-1]=buffer.get(i);
		}
		return retorno;
	}
	
	public static byte[] getDireccionFinal(byte[] buffer) {
		byte[] retorno= new byte[6];
		for(int i=1; i<7; i++) {
			retorno[i-1]=buffer[i];
		}
		return retorno;
	}
	
	public static byte[] getDireccionInicial(ArrayList<Byte> buffer) {
		byte[] retorno= new byte[6];
		for(int i=7; i<13; i++) {
			retorno[i-7]=buffer.get(i);
		}
		return retorno;
	}
	
	public static byte[] getDireccionInicial(byte[] buffer) {
		byte[] retorno= new byte[6];
		for(int i=7; i<13; i++) {
			retorno[i-7]=buffer[i];
		}
		return retorno;
	}
	
}
