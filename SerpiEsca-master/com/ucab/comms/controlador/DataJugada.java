package controlador;

import sample.Main;

public class DataJugada {
	
	public String identificador;
	public int posicion;
	public int se;
	public int tab;
	public int d;
	
	public DataJugada(String identificador, int posicion, int se, int tab, int d) {
		this.identificador= "000000" + identificador;
		this.posicion= posicion;
		this.se= se;
		this.tab= tab;
		this.d= d;
	}
}
