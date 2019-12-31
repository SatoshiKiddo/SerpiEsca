package controlador;

public class Trama {

	protected String preambulo = "01111110";
	protected byte[] direccion_final;
	protected byte[] direccion_inicial;
	protected String control_segmento="0000";
	protected String protocolo_interno="1111";
	
	public byte[] envio_trama() {
		byte[] trama_envio = new byte[14];
		trama_envio[0]= (byte)Short.parseShort(this.preambulo,2);
		for(int i=1; i<7; i++) {
			trama_envio[i]= direccion_final[i];
		}
		for(int i=7; i<13; i++) {
			trama_envio[i]= direccion_inicial[i-7];
		}
		trama_envio[13] = (byte) Short.parseShort(control_segmento + protocolo_interno,2);
		return trama_envio;
	}
	
	public byte[] envio_trama_token() {
		this.control_segmento= "0001";
		return envio_trama();
	}
	
	public String get_preambulo() {
		return this.preambulo;
	}
	
}
