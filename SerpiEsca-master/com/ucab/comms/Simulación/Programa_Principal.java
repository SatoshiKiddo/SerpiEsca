package Simulación;

import controlador.PController;

public class Programa_Principal {
	
	public static void main (String[] args) {
		Thread cliente = new Thread(new SimuladorCliente());
		cliente.start();
		Thread servidor = new Thread(new SimuladorServidor());
		servidor.start();
	}

}
