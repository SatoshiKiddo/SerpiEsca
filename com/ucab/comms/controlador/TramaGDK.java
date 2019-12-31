package controlador;

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
	}
	
	public String identificadorByte() {
		switch(this.identificador) {
			case "A":
				return "00";
			case "B":
				return "01";
			case "C":
				return "10";
			case "D":
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
		byte[] trama_envio_super= super.envio_trama();
		for(int i=0; i < 14; i++) 
			trama_envio[i]=trama_envio_super[i];
		String posicion_t = ByteConv.bitString(this.posicion,7);
		trama_envio[14] = (byte) Short.parseShort(this.identificadorByte() + posicion_t.substring(1, 6), 2);
		trama_envio[15] = (byte) Short.parseShort(posicion_t.substring(7) + ByteConv.bitString(this.d, 3) + ByteConv.bitString(this.se, 1) + ByteConv.bitString(this.tab,3), 2);
		return trama_envio;
	}

}
