package controlador;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;

import com.fazecast.jSerialComm.*;

import sample.Main;

/**
 * 
 * @author kiddo
 * @version 1.1
 * 
 * PController refiere a la clase principal que se encarga del manejo
 * de los dos puertos que tendran en la funcion cada computadora dentro del juego y ademas aplica los protocolos ADP y GDP.
 *
 */
public class PController implements IProtocoloADP, IProtocoloGDP, Runnable{
	
	public Main interfaz;
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
	private DataJugada jugada;
	
	public PController(boolean servidor, String puerto_especifico_1, String puerto_especifico_2) {
		this.servidor = servidor;
		try {
			this.mac_address= NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
		}
		catch(Exception e) {
			e.printStackTrace();
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
				}
			}
		}
		else {
			this.puertos = new SerialPort[2];
			this.puertos[0] = SerialPort.getCommPort(puerto_especifico_1);
			this.puertos[1] = SerialPort.getCommPort(puerto_especifico_2);
		}
		this.puertos[puerto1].setComPortParameters(2400, 8, 0, 1);
		this.puertos[puerto2].setComPortParameters(2400, 8, 0, 1);
		while(!this.puertos[this.puertoEntrada].isOpen() || !this.puertos[this.puertoSalida].isOpen()) {
			this.puertos[this.puerto1].openPort();
			this.puertos[this.puerto2].openPort();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(servidor) {
			this.token = true;
			this.identificador= "00000000";
		}
		else
			this.token = false;
	}
	
	public void set_interfaz(Main interfaz) {
		this.interfaz = interfaz;
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
		byte[] identificador_add= new byte[7];
		TramaADD trama_envio;
		for(int i=0; i<6; i++) {
			identificador_add[i] = "F".getBytes()[0];
			//Coloca "70" como string.
		}
		identificador_add[6] = (byte) Short.parseShort(this.identificador,2);
		trama_envio = new TramaADD(identificador_add, "FFFFFF".getBytes(), "FFFFFF".getBytes());
		byte[] data= trama_envio.envio_trama();
		this.puertos[this.puertoSalida].writeBytes(data, data.length);
	}

	public void envioADK(int puerto, String tablero) throws Exception {
		TramaADK trama_envio;
		//Coloca 65 si es A
		trama_envio = new TramaADK("FFFFFF".getBytes(), "FFFFFF".getBytes(), tablero);
		trama_envio.byteToIdentificador(this.buffer);
		System.out.println("TRAMA ADK ACÁ MISMO MIRALA:    " + ByteConv.printBytes(trama_envio.envio_trama()));
		byte[] data= trama_envio.envio_trama();
		this.puertos[this.puertoSalida].writeBytes(data, data.length);
	}
	
	public void envioGDK(int puerto) {
		System.out.println("Enviando trama GDK...");
		TramaGDK trama_envio;
		trama_envio = new TramaGDK("FFFFFF".getBytes(), "FFFFFF".getBytes(), this.jugada.identificador, 
				this.jugada.posicion, this.jugada.se, this.jugada.tab, this.jugada.d);
		byte[] data= trama_envio.envio_trama();
		this.puertos[this.puertoSalida].writeBytes(data, data.length);
	}
	
	public void envioTokenRing(int puerto) {
		this.token = false;
		Trama ceder_token = new Trama();
		this.puertos[this.puertoSalida].writeBytes(ceder_token.envio_trama_token(), ceder_token.envio_trama_token().length);
	}
	
	public void seteoPorts() throws Exception{
		while(this.puertos[this.puerto1].bytesAvailable() <= 0 && this.puertos[this.puerto2].bytesAvailable() <= 0) {
			Thread.sleep(500);
		}
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

		System.out.println("Estableciendo ADP Servidor");
		this.interfaz.jugador=1;
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
			if (puertoRandom == 1) {
				this.puertoEntrada= this.puerto1;
				this.puertoSalida= this.puerto2;
			}
			else {
				this.puertoEntrada= this.puerto2;
				this.puertoSalida= this.puerto1;
			}
			while(this.puertos[this.puertoEntrada].bytesAvailable() <= 0 && timeElapsed >= this.limitanteSeg * 1000 && envio_paquetes < limitante) {
				try {
					this.envioADD(this.puertoSalida);
					envio_paquetes++;
					System.out.println("No recibe datos");
					Thread.sleep(5000);
				}
				catch(Exception e) {
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
					this.ADPEstablished= true;
					this.inicioServidorADK();
				break;
			}
			System.out.println("Se estableció conexión - Servidor");
		}
	}

	@Override
	public void inicioServidorADK() throws Exception {
		int envio_paquetes=0;
		this.pastTime= System.currentTimeMillis();
		long timeElapsed;
		int puertoRandom;
		while(!this.ADKEstablished) {
			System.out.println("Estableciendo ADK Servidor");
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
			while(this.puertos[this.puertoEntrada].bytesAvailable() <= 0 && timeElapsed >= this.limitanteSeg * 1000 && envio_paquetes < limitante) {
				try {
					this.envioADK(this.puertoSalida, tablero);
					envio_paquetes++;
					System.out.println(this.puertos[this.puertoEntrada].bytesAvailable());
					Thread.sleep(5000);
				}
				catch(Exception e) {
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

				System.out.println("Se estableció conexión - CLIENTE");
		}
	}

	@Override
	public void inicioClienteADDK() throws Exception {
		System.out.println("Inicio ADP Cliente");
		int recepcion_paquetes = 0;
		while(!this.ADPEstablished) {
			//Se coloca aca el codigo para seleccionar el identificador del equipo.
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
					System.out.println("ADPEstablecido");
					this.ADPEstablished = true;
					break;
			}
		}
		while(!this.ADKEstablished) {
			System.out.println("Inicio ADK Cliente");
			this.seteoPorts();
			recepcion_paquetes++;
			this.RecepcionTrama();
			switch(this.desempaquetadoADK(this.buffer)) {
				case -1:
					System.out.println("Error en el desempaquetado");
					if(recepcion_paquetes < limitante)
						continue;
					else {
						throw new Exception("Tiempo agotado para establecer el protocolo ADP: error ADK");
					}
				case 0:
					System.out.println("ADK Establecido");
					this.ADKEstablished = true;
					break;
			}
		}
	}

	@Override
	public int desempaquetadoADK(ArrayList<Byte>  buffer) {
		if(desempaquetadoTrama(buffer) == 2 ) {
				if(this.servidor) {
					//El servidor al tener esta informacion, comienza el protocolo GDP
				}
				else {

					//Llenado de informacion sobre los jugadores y el tablero. El codigo debe ir aca.
					this.interfaz.llenado_Data_Cliente(buffer);
					byte[] reenvio= ByteConv.arrayListByteToArrayByte(buffer);
					System.out.println(ByteConv.printBytes(buffer));
					this.puertos[this.puertoSalida].writeBytes(reenvio, reenvio.length);
				}
				return 0;
		}
		return -1;
	}

	@Override
	public int desempaquetadoADD(ArrayList<Byte>  buffer) {
		int i = desempaquetadoTrama(buffer);
		if(i == 1 ) {
			if(this.servidor) {
				//Se formatea la informacion de los jugadores para la facilidad de la aplicacion.
				this.interfaz.llenado_Data_Servidor(buffer);
				//Codigo de llenado de informacion con los datos de los demas jugadores en conjunto con la determinacion del tablero.
			}
			else {
				//Agregado de identificador y envio al siguiente nodo, pasando la informacion ADD.
				this.identificador = DataJugador.DesignaciónIdentificador(buffer, this.interfaz);
				TramaADD trama_envio = new TramaADD(Trama.getDireccionFinal(buffer), Trama.getDireccionInicial(buffer));
				trama_envio.byteToIdentificador(buffer);
				trama_envio.agregar_identificador(this.identificador, "FFFFFF");
				//Problema acá respecto a los datos que se llenan
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
		if (buffer.size() != 0) {
			String direccion_final = ByteConv.getMacAddress(buffer, 1);
			String control_s_protocolo = ByteConv.byteToString(buffer.get(13));
			try {
				if (control_s_protocolo.substring(0, 4) == "0001") {
					System.out.println("Error no es acá");
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
					case "1111":
						return 5;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
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
	
	public void ejecución_Jugada(DataJugada jugada) {
		this.jugada= jugada;
	}

	//Debe ejecutarse constantemente este proceso en un while infinito dentro del juego posterior a la ejecución de ADP.
	@Override
	public void ProcesoGDP() throws Exception {
		this.interfaz.Turno(this.token);
		int recepcion_paquetes =0 ;
		if (this.token) {

			while(this.interfaz.jugadaHecha == false) {
				Thread.sleep(200);
			}

			//Codigo de ejecucion de alguna jugada y creacion de la data para transmitirlo por la trama
			//Llenado de la trama con la jugada. TramaGDK();
			//Se envia la trama al puerto de salida segun la logica de juego.
			System.out.println("Esto se enviará como jugador: " + this.interfaz.jugador);
			this.envioGDK(this.puertoSalida);
			do {
				recepcion_paquetes++;
				this.seteoPorts();
				this.RecepcionTrama();
				if(recepcion_paquetes == this.limitante) {
					throw new Exception("Tiempo agotado para establecer el protocolo GDP: error GDK");
				}
			} while(this.desempaquetadoTrama(this.buffer) != 4);
			this.token=false;
			this.interfaz.Turno(this.token);
			this.envioTokenRing(this.puertoSalida);
			this.interfaz.jugadaHecha = false;
		}
		else {
			while(!this.token) {
				this.seteoPorts();
				this.RecepcionTrama();
				recepcion_paquetes++;
				switch(this.desempaquetadoGDK(this.buffer)) {
					case 5:
						this.interfaz.Turno(this.token);
						break;
					case -1:
						if(recepcion_paquetes < limitante)
							continue;
						else {
							throw new Exception("Exceso de paquetes perdidos, se finaliza la sesión de juego...");
						}
					case 0:
						System.out.println("Esto se enviará como jugador: " + this.interfaz.jugador);
						TramaGDK trama_recibido = new TramaGDK(this.buffer);
						this.puertos[this.puertoSalida].writeBytes(trama_recibido.envio_trama(), trama_recibido.envio_trama().length);
						break;
				}
				Thread.sleep(2000);
			}
		}
	}

	@Override
	public int desempaquetadoGDK(ArrayList<Byte> buffer) {
		int i = desempaquetadoTrama(buffer);
		if (i == 5) {
			this.token=true;
			return 5;
		}
		if(i == 4) {
			DataJugada jugada= TramaGDK.desempaquetado_jugada(buffer);
			this.interfaz.jugada_externa(jugada);
			return 0;
		}
		return -1;
	}

	@Override
	public void run() {
		while(1==1)
			try {
				this.ProcesoGDP();
			} catch (Exception e) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
	}

}
