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
	final private int limitante = 10;
	//Cantidad de tiempo en segundos para intentar realizar los protocolos.
	final private int limitanteSeg = 15;
	final private ArrayList<Byte> buffer= null;
	
	public PController(boolean servidor, String puerto_especifico_1) {
		this.servidor = servidor;
		try {
			this.mac_address= NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error al intentar obtener la MAC del dispositivo.");
		}
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
		this.puertos[puerto1].setComPortParameters(2400, 8, 0, 1);
		this.puertos[puerto2].setComPortParameters(2400, 8, 0, 1);
		while(!this.puertos[puerto1].openPort(500) && !this.puertos[puerto2].openPort(500)) {
			System.out.println("Abriendo puertos de uso del equipo...");
		}
		System.out.println(this.puertos[this.puerto1].getDescriptivePortName() + "Abierto...");
		System.out.println(this.puertos[this.puerto2].getDescriptivePortName() + "Abierto...");
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
		System.out.println("Enviando trama ADD...");
		byte[] identificador_add= new byte[7];
		TramaADD trama_envio;
		for(int i=0; i<6; i++) {
			identificador_add[i] = "F".getBytes()[0];
		}
		identificador_add[6] = this.identificador.getBytes()[0];
		trama_envio = new TramaADD(identificador_add, "FFFFFF".getBytes(), "FFFFFF".getBytes());
		byte[] data= trama_envio.envio_trama();
		this.puertos[this.puerto1].writeBytes(data, data.length);
	}

	public void envioADK(int puerto, String tablero) throws Exception {
		System.out.println("Enviando trama ADK...");
		byte[] identificador_add= new byte[7];
		TramaADK trama_envio;
		for(int i=0; i<6; i++) {
			identificador_add[i] = this.mac_address.getHardwareAddress()[i];
		}
		identificador_add[6] = this.identificador.getBytes()[0];
		trama_envio = new TramaADK(identificador_add, "FFFFFF".getBytes(), this.mac_address.getHardwareAddress(), tablero);
		byte[] data= trama_envio.envio_trama();
		this.puertos[this.puerto1].writeBytes(data, data.length);
		
	}
	
	public void envioGDK(int puerto) {
		System.out.println("Enviando trama GDK...");
		
	}
	
	public void envioTokenRing(int puerto) {
		System.out.println("Enviando trama ADK...");
		
	}
	
	public void seteoPorts() {
		while(this.puertos[this.puerto1].bytesAvailable() == 0 && this.puertos[this.puerto2].bytesAvailable() == 0) {
		}
		if (this.puertos[this.puerto1].bytesAvailable() != 0) {
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
		int envio_paquetes=0;
		this.pastTime= System.currentTimeMillis();
		long timeElapsed;
		int puertoRandom;
		// Se genera el inicio del protocolo ADP cumpliendo el papel del servidor.
		//Se puede agregar como condicion dentro del while un raise event para poder salir y seleccionar cliente.
		while (!this.ADPEstablished) {
			//Se coloca aca el codigo para seleccionar el identificador del equipo.
			//Se coloca aca el codigo para seleccionar el puerto aleatorio de envio.
			//Colocar un cronometro de espera en este punto.
			//Espera de llegada del paquete.
			//Se coloca cual es el puerto de entrada  y el puerto de salida
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
			while(this.puertos[this.puertoEntrada].bytesAvailable() == 0 && timeElapsed >= this.limitanteSeg * 1000 && envio_paquetes < limitante) {
				try {
					wait(1000);
					this.envioADD(this.puertoSalida);
					envio_paquetes++;
				}
				catch(Exception e) {
					System.out.println("Error al intentar esperar la llegada del paquete");
					e.printStackTrace();
				}
			}
			this.RecepcionTrama();
			switch(this.desempaquetadoADD(this.buffer)) {
				case -1:
					if(envio_paquetes < limitante && timeElapsed >= this.limitanteSeg * 1000)
						continue;
					else {
						throw new Exception("Tiempo agotado para establecer el protocolo ADP: error ADD");
					}
				case 0:
					this.ADPEstablished= true;
			}
		}
	}

	@Override
	public void inicioServidorADK() throws Exception {
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
			while(this.puertos[this.puertoEntrada].bytesAvailable() == 0 && timeElapsed >= this.limitanteSeg * 1000 && envio_paquetes < limitante) {
				try {
					this.envioADK(this.puertoSalida, tablero);
					wait(1000);
					envio_paquetes++;
				}
				catch(Exception e) {
					System.out.println("Error al intentar esperar la llegada del paquete");
					e.printStackTrace();
				}
			}
			if(this.puertos[this.puertoEntrada].bytesAvailable() != 0) {
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
				}
			}
		}
		
	}

	@Override
	public void inicioClienteADDK() throws Exception {
		int recepcion_paquetes = 0;
		while(!this.ADPEstablished) {
			//Se coloca aca el codigo para seleccionar el identificador del equipo.
			//Se coloca aca el codigo para seleccionar el puerto aleatorio de envio.
			//Colocar un cronometro de espera en este punto.
			//Espera de llegada del paquete.
			//Se coloca cual es el puerto de entrada  y el puerto de salida
			//Espera de llegada del paquete final
			System.out.println("Esperando Aknowledgemente de todos los equipos involucrados...");
			this.seteoPorts();
			recepcion_paquetes++;
			this.RecepcionTrama();
			switch(this.desempaquetadoADD(this.buffer)) {
				case -1:
					if(recepcion_paquetes < limitante)
						continue;
					else {
						throw new Exception("Tiempo agotado para establecer el protocolo ADP: error ADK");
					}
				case 0:
					this.ADPEstablished = true;
			}
		}
		while(!this.ADKEstablished) {
			System.out.println("Esperando Aknowledgemente de todos los equipos involucrados...");
			recepcion_paquetes++;
			this.RecepcionTrama();
			switch(this.desempaquetadoADK(this.buffer)) {
				case -1:
					if(recepcion_paquetes < limitante)
						continue;
					else {
						throw new Exception("Tiempo agotado para establecer el protocolo ADP: error ADK");
					}
				case 0:
					this.ADKEstablished = true;
			}
		}
	}

	@Override
	public int desempaquetadoADK(ArrayList<Byte>  buffer) {
		if(desempaquetadoTrama(buffer) == 1 ) {
			if(desempaquetadoTrama(buffer) == 2 ) {
				if(this.servidor) {
					//El servidor al tener esta informacion, comienza el protocolo GDP
				}
				else {
					//Llenado de informacion sobre los jugadores y el tablero. El codigo debe ir aca.
					byte[] reenvio= ByteConv.arrayListByteToArrayByte(buffer);
					this.puertos[this.puertoSalida].writeBytes(reenvio, reenvio.length);
				}
				return 0;
			}
		}
		return -1;
	}

	@Override
	public int desempaquetadoADD(ArrayList<Byte>  buffer) {
		if(desempaquetadoTrama(buffer) == 2 ) {
			if(this.servidor) {
				//Se formatea la informacion de los jugadores para la facilidad de la aplicacion.
				ArrayList<DataJugador> datos_jugadores = new ArrayList<DataJugador>();
				DataJugador.llenadoData(buffer, datos_jugadores);
				//Codigo de llenado de informacion con los datos de los demas jugadores en conjunto con la determinacion del tablero.
			}
			else {
				//Agregado de identificador y envio al siguiente nodo, pasando la informacion ADD.
				TramaADD trama_envio = new TramaADD(Trama.getDireccionFinal(buffer), Trama.getDireccionInicial(buffer));
				trama_envio.byteToIdentificador(buffer);
				trama_envio.agregar_identificador(this.identificador, this.mac_address);
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
		String direccion_final = ByteConv.getMacAddress(buffer, 1);
		String control_s_protocolo = ByteConv.byteToString(buffer.get(13));
		byte[] buffer_s;
		try {
			if (direccion_final != "FFFFFF") {
					//La direccion final no coincide con el equipo que la recibe, por lo tanto se debe reenviar al siguiente nodo.
					this.puertos[this.puertoSalida].writeBytes(ByteConv.arrayListByteToArrayByte(buffer), buffer.size());
					return -1;
			}
			if (control_s_protocolo.substring(1, 4) == "0001") {
				return 5;
			}
			switch(control_s_protocolo.substring(5, 8)) {
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
		//Protocolo desconocido
		return -1;
	}
	
	public void RecepcionTrama() {
		byte[] buffer= new byte[1];
		while(this.puertos[this.puertoEntrada].bytesAvailable() > 0) {
			this.puertos[this.puertoEntrada].readBytes(buffer, 1);
			if(ByteConv.byteToString(buffer[0]) == "01111110") {
				do {
					this.buffer.add(buffer[0]);
					this.puertos[this.puertoEntrada].readBytes(buffer, 1);
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
					throw new Exception("Tiempo agotado para establecer el protocolo ADP: error ADK");
				}
			} while(this.desempaquetadoGDK(this.buffer) != 0);
			this.token = false;
			Trama ceder_token = new Trama();
			this.puertos[this.puertoSalida].writeBytes(ceder_token.envio_trama_token(), ceder_token.envio_trama_token().length);
		}
		else {
			while(!this.token) {
				this.seteoPorts();
				this.RecepcionTrama();
				recepcion_paquetes++;
				switch(this.desempaquetadoGDK(this.buffer)) {
					case 1:
						this.token = true;
						this.ProcesoGDP();
					break;
					case -1:
						if(recepcion_paquetes < limitante)
							continue;
						else {
							throw new Exception("Tiempo agotado para establecer el protocolo ADP: error ADK");
						}
					case 0:
						//Llenado de la informacion con los datos de la jugada por parte del paquete.
						TramaGDK trama_recibido = new TramaGDK(this.buffer);
						//Codigo de llenado en la interfaz.
					break;
				}
			}
		}
	}

	@Override
	public int desempaquetadoGDK(ArrayList<Byte> buffer) {
		// TODO Auto-generated method stub
		return 0;
	}

}
