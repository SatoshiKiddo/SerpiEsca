package controlador;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;

import com.fazecast.jSerialComm.*;

/**
 * 
 * @author kiddo
 * @version 1.1
 * 
 * PController refiere a la clase principal que se encarga del manejo
 * de los dos puertos que tendran en la funcion cada computadora dentro del juego y ademas aplica los protocolos ADP y GDP.
 *
 */
public class PController implements IProtocoloADP, IProtocoloGDP{
	
	private boolean servidor;
	private boolean token;
	private long pastTime;
	//ADP completo y establecido
	private boolean ADPEstablished = false;
	private boolean ADKEstablished = false;
	private SerialPort[] puertos;
	private String identificador;
	private NetworkInterface mac_address;
	private int puertoSalida;
	private int puertoEntrada;
	final private int puerto1 = 0;
	final private int puerto2 = 1;
	final private int limitante = 20;
	//Cantidad de tiempo en segundos para intentar realizar los protocolos.
	final private int limitanteSeg = 15;
	private ArrayList<Byte> buffer;
	
	public PController(boolean servidor, String puerto_especifico_1, String puerto_especifico_2) {
		this.servidor = servidor;
		try {
			this.mac_address= NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error al intentar obtener la MAC del dispositivo.");
		}
		if (puerto_especifico_1 == null && puerto_especifico_2 == null) {
		this.puertos = SerialPort.getCommPorts();
			while (this.puertos.length < 2) {
				//Busqueda de puertos disponibles y su asignacion.
				try {
				
					wait(1000);
					this.puertos = SerialPort.getCommPorts();
				}
				catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error al intentar realizar intervalo de espera.");
				}
				System.out.println("Intentando leer los puertos disponibles ...");
			}
		}
		else {
			this.puertos = new SerialPort[2];
			this.puertos[0] = SerialPort.getCommPort(puerto_especifico_1);
			this.puertos[1] = SerialPort.getCommPort(puerto_especifico_2);
		}
		this.puertos[puerto1].setComPortParameters(2400, 8, 0, 1);
		this.puertos[puerto2].setComPortParameters(2400, 8, 0, 1);
		System.out.println("Abriendo los puertos de uso del equipo");
		while(!this.puertos[this.puertoEntrada].isOpen() || !this.puertos[this.puertoSalida].isOpen()) {
			System.out.println("Intentando nuevamente abrir los puertos de uso del equipo... " + this.servidor);
			this.puertos[this.puerto1].openPort();
			this.puertos[this.puerto2].openPort();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(this.puertos[this.puerto1].getSystemPortName() + " Abierto...");
		System.out.println(this.puertos[this.puerto2].getSystemPortName() + " Abierto...");
		if(servidor)
			this.token = true;
		else
			this.token = false;
	}
	
	public void set_identificador(String id) {
		this.identificador = id;
	}
	
	public void seteo_puertos(String puerto_1, String puerto_2) throws Exception {
		if( SerialPort.getCommPort(puerto_1) != null && SerialPort.getCommPort(puerto_2) != null ) {
			this.puertos = new SerialPort[2];
			this.puertos[0]=SerialPort.getCommPort(puerto_1);
			this.puertos[1]=SerialPort.getCommPort(puerto_2);
		}
		else {
			throw new Exception("Error, alguno de los puertos es inexistente");
		}
	}
	
	public void envioADD(int puerto) throws Exception {
		System.out.println("Enviando trama ADD... " + this.servidor);
		byte[] identificador_add= new byte[7];
		TramaADD trama_envio;
		for(int i=0; i<6; i++) {
			identificador_add[i] = "F".getBytes()[0];
			//Coloca "70" como string.
		}
		identificador_add[6] = (byte) Short.parseShort(this.identificador,2);
		//Coloca 65 si es A
		trama_envio = new TramaADD(identificador_add, "FFFFFF".getBytes(), "FFFFFF".getBytes());
		byte[] data= trama_envio.envio_trama();
		System.out.println("-------------------Envio de trama por el puerto: " + this.puertos[this.puertoSalida].getSystemPortName() + " " + data.length);
		this.puertos[this.puertoSalida].writeBytes(data, data.length);
	}

	public void envioADK(int puerto, String tablero) throws Exception {
		System.out.println("Enviando trama ADK... " + this.servidor);
		TramaADK trama_envio;
		//Coloca 65 si es A
		trama_envio = new TramaADK("FFFFFF".getBytes(), "FFFFFF".getBytes(), tablero);
		trama_envio.byteToIdentificador(this.buffer);
		byte[] data= trama_envio.envio_trama();
		this.puertos[this.puertoSalida].writeBytes(data, data.length);
	}
	
	public void envioGDK(int puerto) {
		System.out.println("Enviando trama GDK...");
		//Llenado de los datos de la jugada
	}
	
	public void envioTokenRing(int puerto) {
		System.out.println("Enviando trama TokenRing...");
		this.token = false;
		Trama ceder_token = new Trama();
		this.puertos[this.puertoSalida].writeBytes(ceder_token.envio_trama_token(), ceder_token.envio_trama_token().length);
	}
	
	public void seteoPorts() throws Exception{
		System.out.println("Comprobando bytes disponibles");
		while(this.puertos[this.puerto1].bytesAvailable() <= 0 && this.puertos[this.puerto2].bytesAvailable() <= 0) {
			Thread.sleep(500);
			System.out.println("Se tiene esta cantidad de bytes en el puerto " + this.puertos[this.puerto1].getSystemPortName() + " " + this.puertos[this.puerto1].bytesAvailable());
			System.out.println("Se tiene esta cantidad de bytes en el puerto " + this.puertos[this.puerto2].getSystemPortName() + " " + this.puertos[this.puerto2].bytesAvailable());
		}
		System.out.println("Puertos con bytes disponibles");
		if (this.puertos[this.puerto1].bytesAvailable() > 0) {
			this.puertoEntrada= this.puerto1;
			this.puertoSalida= this.puerto2;
		}
		else {
			this.puertoEntrada= this.puerto2;
			this.puertoSalida= this.puerto1;
		}
	}

	@Override
	public void inicioServidorADD() throws Exception {
		System.out.println("--------------------------------ESTABLECIENDO ADD------------------------ SERVER");
		int envio_paquetes=0;
		this.pastTime= System.currentTimeMillis();
		long timeElapsed;
		int puertoRandom;
		// Se genera el inicio del protocolo ADP cumpliendo el papel del servidor.
		//Se puede agregar como condicion dentro del while un raise event para poder salir y seleccionar cliente.
		while (!this.ADPEstablished) {
			//Se coloca aca el codigo para seleccionar el identificador del equipo.
			timeElapsed= System.currentTimeMillis();
			puertoRandom = (int) Math.round(Math.random());
			System.out.println("Seteo de puerto aleatorio: " + puertoRandom);
			if (puertoRandom == 1) {
				System.out.println(this.puertos[this.puerto1].getSystemPortName() + " puerto de entrada");
				System.out.println(this.puertos[this.puerto2].getSystemPortName() + " puerto de salida");
				this.puertoEntrada= this.puerto1;
				this.puertoSalida= this.puerto2;
			}
			else {
				System.out.println(this.puertos[this.puerto2].getSystemPortName() + " puerto de entrada");
				System.out.println(this.puertos[this.puerto1].getSystemPortName() + " puerto de salida");
				this.puertoEntrada= this.puerto2;
				this.puertoSalida= this.puerto1;
			}
			while(this.puertos[this.puertoEntrada].bytesAvailable() <= 0 && timeElapsed >= this.limitanteSeg * 1000 && envio_paquetes < limitante) {
				try {
					this.envioADD(this.puertoSalida);
					System.out.println("!!!!!!!!Se tiene esta cantidad de bytes en el puerto " + this.puertos[this.puerto1].getSystemPortName() + " " + this.puertos[this.puerto1].bytesAvailable());
					System.out.println("!!!!!!!!Se tiene esta cantidad de bytes en el puerto " + this.puertos[this.puerto2].getSystemPortName() + " " + this.puertos[this.puerto2].bytesAvailable());
					envio_paquetes++;
					System.out.println("Puerto entrada del servidor:                      " + this.puertos[this.puertoEntrada].getSystemPortName());
					Thread.sleep(2000);
				}
				catch(Exception e) {
					System.out.println("Error al intentar esperar la llegada del paquete");
					e.printStackTrace();
				}
			}
			this.seteoPorts();
			this.RecepcionTrama();
			switch(this.desempaquetadoADD(this.buffer)) {
				case -1:
					if(envio_paquetes < limitante && timeElapsed >= this.limitanteSeg * 1000)
						continue;
					else {
						throw new Exception("Tiempo agotado para establecer el protocolo ADP: error ADD");
					}
				case 0:
					System.out.println("Se recibió la trama por parte del cliente, inicio de ADK por parte del servidor -----------!!!!!");
					this.ADPEstablished= true;
					this.inicioServidorADK();
				break;
			}
		}
	}

	@Override
	public void inicioServidorADK() throws Exception {
		System.out.println("--------------------------------ESTABLECIENDO ADK------------------------ SERVER");
		int envio_paquetes=0;
		this.pastTime= System.currentTimeMillis();
		long timeElapsed;
		int puertoRandom;
		while(!this.ADKEstablished) {
			//Se coloca aca el codigo para llenar la info necesaria sobre los jugadores.
			String tablero="0000";
			//Se coloca aca el codigo para seleccionar el valor del tablero.
			timeElapsed= System.currentTimeMillis();
			puertoRandom = (int) Math.round(Math.random());
			if (puertoRandom == 1) {
				this.puertoEntrada= this.puerto1;
				this.puertoSalida= this.puerto2;
			}
			else {
				this.puertoEntrada= this.puerto2;
				this.puertoSalida= this.puerto1;
			}
			//Espera de llegada del paquete final
			System.out.println("Esperando Aknowledgemente de todos los equipos involucrados...");
			while(this.puertos[this.puertoEntrada].bytesAvailable() <= 0 && timeElapsed >= this.limitanteSeg * 1000 && envio_paquetes < limitante) {
				try {
					this.envioADK(this.puertoSalida, tablero);
					envio_paquetes++;
					Thread.sleep(5000);
				}
				catch(Exception e) {
					System.out.println("Error al intentar esperar la llegada del paquete");
					e.printStackTrace();
				}
			}
			this.seteoPorts();
				this.RecepcionTrama();
				switch(this.desempaquetadoADK(this.buffer)) {
					case -1:
						if(envio_paquetes < limitante && timeElapsed >= this.limitanteSeg * 1000)
							continue;
						else {
							throw new Exception("Tiempo agotado para establecer el protocolo ADP: error ADK");
						}
					case 0:
						this.ADKEstablished = true;
						break;
				}
		}
		while(1==1)
			this.ProcesoGDP();
	}

	@Override
	public void inicioClienteADDK() throws Exception {
		System.out.println("--------------------------------ESTABLECIENDO ADD------------------------ CLIENTE");
		int recepcion_paquetes = 0;
		while(!this.ADPEstablished) {
			//Se coloca aca el codigo para seleccionar el identificador del equipo.
			System.out.println("Esperando Aknowledgemente de todos los equipos involucrados...");
			System.out.println("Seteando los puertos");
			this.seteoPorts();
			recepcion_paquetes++;
			this.RecepcionTrama();
			System.out.println("------------Recepción de trama----------\n\n\n");
			switch(this.desempaquetadoADD(this.buffer)) {
				case -1:
					if(recepcion_paquetes < limitante)
						continue;
					else {
						System.out.println("Error de espera al intentar recibir paquete.");
						throw new Exception("Tiempo agotado para establecer el protocolo ADP: error ADK");
					}
				case 0:
					this.ADPEstablished = true;
					break;
			}
		}
		System.out.println("--------------------------------ESTABLECIENDO ADK------------------------ CLIENTE");
		while(!this.ADKEstablished) {
			ByteConv.pausas_De_prueba();
			this.seteoPorts();
			recepcion_paquetes++;
			this.RecepcionTrama();
			System.out.println("Se recibió trama ADK por parte del servidor");
			switch(this.desempaquetadoADK(this.buffer)) {
				case -1:
					if(recepcion_paquetes < limitante)
						continue;
					else {
						throw new Exception("Tiempo agotado para establecer el protocolo ADP: error ADK");
					}
				case 0:
					this.ADKEstablished = true;
					break;
			}
		}
		while(1==1)
			this.ProcesoGDP();
	}

	@Override
	public int desempaquetadoADK(ArrayList<Byte>  buffer) {
		if(desempaquetadoTrama(buffer) == 2 ) {
				if(this.servidor) {
					//El servidor al tener esta informacion, comienza el protocolo GDP
					System.out.println("Comienza el proceso GDP Servidor");
				}
				else {
					//Llenado de informacion sobre los jugadores y el tablero. El codigo debe ir aca.
					byte[] reenvio= ByteConv.arrayListByteToArrayByte(buffer);
					this.puertos[this.puertoSalida].writeBytes(reenvio, reenvio.length);
				}
				return 0;
		}
		return -1;
	}

	@Override
	public int desempaquetadoADD(ArrayList<Byte>  buffer) {
		int i = desempaquetadoTrama(buffer);
		System.out.println("----------------Desempaquetado ADD : "+ i);
		System.out.println(ByteConv.printBytes(buffer));
		if(i == 1 ) {
			System.out.println("--------Entro al desempaquetado ADD \n\n");
			if(this.servidor) {
				//Se formatea la informacion de los jugadores para la facilidad de la aplicacion.
				ArrayList<DataJugador> datos_jugadores = new ArrayList<DataJugador>();
				DataJugador.llenadoData(buffer, datos_jugadores);
				System.out.println("Soy el servidor, desempaquetando el paquete ADD del cliente ---------!!!!!");
				//Codigo de llenado de informacion con los datos de los demas jugadores en conjunto con la determinacion del tablero.
			}
			else {
				//Agregado de identificador y envio al siguiente nodo, pasando la informacion ADD.
				TramaADD trama_envio = new TramaADD(Trama.getDireccionFinal(buffer), Trama.getDireccionInicial(buffer));
				trama_envio.byteToIdentificador(buffer);
				trama_envio.agregar_identificador(this.identificador, "FFFFFF");
				System.out.println("ENVIO DE TRAMA DESDE EL CLIENTE AL SERVIDOR trama length: " + trama_envio.envio_trama().length);
				System.out.println(ByteConv.printBytes(trama_envio.envio_trama()));
				this.puertos[this.puertoSalida].writeBytes(trama_envio.envio_trama(),trama_envio.envio_trama().length);
			}
			return 0;
		}
		return -1;
	}
	
	// 5: token ring
	// 1-4: procedimientos de los protocolos ADP y GDP.
	@Override
	public int desempaquetadoTrama(ArrayList<Byte>  buffer) {
		System.out.println("Tamaño del buffer: " + buffer.size());
		if (buffer.size() != 0) {
			String direccion_final = ByteConv.getMacAddress(buffer, 1);
			System.out.println("---------------------MAC RECIBIDA: " + direccion_final);
			String control_s_protocolo = ByteConv.byteToString(buffer.get(13));
			System.out.println("---------------------Protocolo y control de segmento: " + control_s_protocolo);
			try {
				if (control_s_protocolo.substring(0, 4) == "0001") {
					return 5;
				}
				switch(control_s_protocolo.substring(4, 8)) {
					case "0000":
						return 1;
					case "0001":
						return 2;
					case "0010":
						return 3;
					case "0011":
						return 4;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Desempaquetado malo");
		//Protocolo desconocido
		return -1;
	}
	
	public void RecepcionTrama() {
		byte[] buffer= new byte[1];
		this.buffer= new ArrayList<Byte>();
		while(this.puertos[this.puertoEntrada].bytesAvailable() > 0) {
			this.puertos[this.puertoEntrada].readBytes(buffer, 1);
			this.buffer.add(buffer[0]);
			if(ByteConv.byteToString(buffer[0]) == "01111110") {
				do {
					this.buffer.add(buffer[0]);
					this.puertos[this.puertoEntrada].readBytes(buffer, 1);
					this.buffer.add(buffer[0]);
				}while (ByteConv.byteToString(buffer[0]) == "01111110");
			}
			else
				continue;
		}
	}
	
	public void cambio_sentido() {
		int cambio = this.puertoEntrada;
		this.puertoEntrada = this.puertoSalida;
		this.puertoSalida = cambio;
	}

	@Override
	public void ProcesoGDP() throws Exception {
		int recepcion_paquetes =0 ;
		if (this.token) {
			//Codigo de ejecucion de alguna jugada y creacion de la data para transmitirlo por la trama
			//Llenado de la trama con la jugada. TramaGDK();
			//Se envia la trama al puerto de salida segun la logica de juego.
			do {
				recepcion_paquetes++;
				this.seteoPorts();
				this.RecepcionTrama();
				if(recepcion_paquetes == this.limitante) {
					throw new Exception("Tiempo agotado para establecer el protocolo GDP: error GDK");
				}
			} while(this.desempaquetadoGDK(this.buffer) != 0);
			this.envioGDK(this.puertoSalida);
			this.token=false;
			this.envioTokenRing(this.puertoSalida);
		}
		else {
			while(!this.token) {
				this.seteoPorts();
				this.RecepcionTrama();
				recepcion_paquetes++;
				switch(this.desempaquetadoGDK(this.buffer)) {
					case 5:
						this.token = true;
						this.ProcesoGDP();
					break;
					case -1:
						if(recepcion_paquetes < limitante)
							continue;
						else {
							throw new Exception("Exceso de paquetes perdidos, se finaliza la sesión de juego...");
						}
					case 0:
						TramaGDK trama_recibido = new TramaGDK(this.buffer);
						this.puertos[this.puertoSalida].writeBytes(trama_recibido.envio_trama_token(), trama_recibido.envio_trama_token().length);
						break;
				}
			}
		}
	}

	@Override
	public int desempaquetadoGDK(ArrayList<Byte> buffer) {
		int i = desempaquetadoTrama(buffer);
		if (i == 5) {
			return 5;
		}
		if(i == 4) {
			System.out.println("--------Entro al desempaquetado GDK \n\n");
			//Llenado de información con los datos de la jugada por parte del paquete
			return 0;
		}
		return -1;
	}

}
