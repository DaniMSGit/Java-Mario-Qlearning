package competition.uhu.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

public class Constantes {
	
	public static double alpha = 0.1;
	public static double fdescuento = 0.6;
	public static double Aleatoriedad = 0.2;

	
	//Constante para evaluar la recompensa
	
	public static double   bloqueado 		  = -500;
	public static double   colision 		  = -500;
	public static double   distanciaPos       = 15;
	public static double   distanciaEve       = 5;
	public static double   distanciaNeg       = 5;
	
	
	public static void guardarConstantes(){
		String constantes = alpha+"#"+fdescuento+"#"+Aleatoriedad+"#";
		try {
			FileOutputStream fos = new FileOutputStream("constantes.txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(constantes);
			oos.close();
		} catch (Exception e) {
			System.out.println("Error al guardar las constantes.");
		}	
	}
	
	public static void leerConstantes() throws Exception {
		String constantes = "";
		
		try {
			FileInputStream fin = new FileInputStream("constantes.txt");
			ObjectInputStream inn = new ObjectInputStream(fin);
			constantes = (String)inn.readObject();
			fin.close();
				
			String[] tmp = constantes.split("#");
			
			alpha = Double.parseDouble(tmp[0]);
			fdescuento = Double.parseDouble(tmp[1]);
			Aleatoriedad = Double.parseDouble(tmp[2]);
			
		} catch (Exception e) {
			throw new Exception("Error al abrir el fichero constantes.");
		}
		
	}
	
}
