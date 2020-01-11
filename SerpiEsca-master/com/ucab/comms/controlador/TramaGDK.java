package controlador;

import java.util.ArrayList;

public class TramaGDK extends Trama implements ITrama {
	
	protected String identificador;
	protected Integer posicion;
	protected Integer d;
	protected Integer se;
	protected Integer tab;
	
	public TramaGDK(byte[] direccion_final, byte[] direccion_inicial, String identificador, int posicion, int se, int tab, int d) {
		this.direccion_final= direccion_final;
		this.direccion_inicial= direccion_inicial;
		this.identificador = identificador;
		this.posicion = posicion;
		this.se = se;
		this.tab = tab;
		this.d = d;
		this.protocolo_interno = "0011";
		this.control_segmento = "0000";
	}
	
	public TramaGDK(ArrayList<Byte> buffer) {
		this.direccion_final= ByteConv.getMacAddress(buffer, 1).getBytes();
		this.direccion_inicial= ByteConv.getMacAddress(buffer, 7).getBytes();
		String control_s_protocolo = ByteConv.byteToString(buffer.get(13));
		this.control_segmento = control_s_protocolo.substring(1, 4);
		this.protocolo_interno = control_s_protocolo.substring(5, 8);
		String segmento_1 = ByteConv.byteToString(buffer.get(14));
		String segmento_2 = ByteConv.byteToString(buffer.get(15));
		this.identificador = segmento_1.substring(1, 2);
		this.posicion = Integer.parseInt(segmento_1.substring(3,8) + segmento_2.substring(1, 1));
		this.se = Integer.parseInt(segmento_2.substring(5,5));
		this.tab = Integer.parseInt(segmento_2.substring(6,8));
		this.d = Integer.parseInt(segmento_2.substring(2,4));
	}
	
	public String identificadorByte() {
		switch(this.identificador) {
			case "00000000":
				return "00";
			case "00000001":
				return "01";
			case "00000010":
				return "10";
			case "00000011":
				return "11";
		}
		return null;
	}
	
	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public int getPosicion() {
		return posicion;
	}

	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}

	public int getSe() {
		return se;
	}

	public void setSe(int se) {
		this.se = se;
	}

	public int getTab() {
		return tab;
	}

	public void setTab(int tab) {
		this.tab = tab;
	}

	@Override
	public byte[] envio_trama() {
		byte[] trama_envio = new byte[16];
		byte[] trama_envio_super= super.envio_trama("0011");
		for(int i=0; i < 15; i++) 
			trama_envio[i]=trama_envio_super[i];
		String posicion_t = ByteConv.bitString(this.posicion,7);
		trama_envio[15] = (byte) Short.parseShort(this.identificadorByte() + posicion_t.substring(1, 6), 2);
		trama_envio[16] = (byte) Short.parseShort(posicion_t.substring(7) + ByteConv.bitString(this.d, 3) + ByteConv.bitString(this.se, 1) + ByteConv.bitString(this.tab,3), 2);
		trama_envio[17] = (byte) Short.parseShort(this.preambulo,2);
		return trama_envio;
	}

}
