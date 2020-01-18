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
		this.control_segmento = control_s_protocolo.substring(0, 4);
		this.protocolo_interno = control_s_protocolo.substring(4, 8);
		String segmento_1 = ByteConv.byteToString(buffer.get(14));
		String segmento_2 = ByteConv.byteToString(buffer.get(15));
		this.identificador = segmento_1.substring(0, 2);
		System.out.println(this.identificador);
		this.posicion = Integer.parseInt(segmento_1.substring(3,8) + segmento_2.substring(0, 1),2);
		System.out.println(this.posicion);
		this.se = Integer.parseInt(segmento_2.substring(4,5),2);
		System.out.println(this.se);
		this.tab = Integer.parseInt(segmento_2.substring(5,8),2);
		System.out.println(this.tab);
		this.d = Integer.parseInt(segmento_2.substring(1,4),2);
		System.out.println(this.d);
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
	
	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	@Override
	public byte[] envio_trama() {
		byte[] trama_envio = new byte[17];
		byte[] trama_envio_super= super.envio_trama("0011");
		for(int i=0; i < 14; i++) 
			trama_envio[i]=trama_envio_super[i];
		String posicion_t = ByteConv.bitString(this.posicion,7);
		trama_envio[14] = (byte) Short.parseShort(this.identificador.substring(0,2) + posicion_t.substring(0, 6), 2);
		trama_envio[15] = (byte) Short.parseShort(posicion_t.substring(7) + ByteConv.bitString(this.d, 3) + ByteConv.bitString(this.se, 1) + ByteConv.bitString(this.tab,3), 2);
		trama_envio[16] = (byte) Short.parseShort(this.preambulo,2);
		return trama_envio;
	}
	
	public static DataJugada desempaquetado_jugada(ArrayList<Byte> buffer) {
		TramaGDK trama = new TramaGDK(buffer);
		DataJugada jugada = new DataJugada(trama.identificador,trama.getPosicion(),trama.getSe(),trama.getTab(),trama.getD());
		return jugada;
	}

}
