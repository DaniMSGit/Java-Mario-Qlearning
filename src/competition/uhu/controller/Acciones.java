package competition.uhu.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

public class Acciones {
	
	Random rnd;

	public static final boolean[] Adelante = {false,true,false,false,false,false};				//Todas las acciones que usará el agente.
	public static final boolean[] AdelanteRapido = {false,true,false,false,true,false};
	public static final boolean[] AdelanteSaltando = {false,true,false,true,true,false};
	public static final boolean[] Atras = {true,false,false,false,false,false};
	public static final boolean[] AtrasRapido = {true,false,false,false,true,false};
	public static final boolean[] AtrasSaltando = {true,false,false,true,true,false};
	public static final boolean[] Saltar = {false,false,false,true,false,false};
	
	public ArrayList<boolean[]> acciones;														//Conjunto de acciones que puede realizar cuando puede saltar.
	public ArrayList<boolean[]> accionesSinSaltar;												//Conjunto de acciones que puede realizar cuando no puede saltar.
	
	public int nAcciones = 7;
	public int nAccionesS = 4;
	
	public Acciones(){
		
		
		acciones 		  = new  ArrayList<boolean[]>();										
		accionesSinSaltar = new  ArrayList<boolean[]>();
		rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		
		
		acciones.add(Adelante);
		acciones.add(AdelanteRapido);
		acciones.add(Atras);
		acciones.add(AtrasRapido);
		acciones.add(AtrasSaltando);
		acciones.add(AdelanteSaltando);
		acciones.add(Saltar);
		
		
		accionesSinSaltar.add(Adelante);
		accionesSinSaltar.add(AdelanteRapido);
		accionesSinSaltar.add(Atras);
		accionesSinSaltar.add(AtrasRapido);

		
		
	}
	
	public boolean[] getAccionAlet(boolean puedesaltar){
		
		if(puedesaltar) return acciones.get(rnd.nextInt(nAcciones));							//Elección de acción aleatoria en función de si puede saltar o no.
		return accionesSinSaltar.get(rnd.nextInt(nAccionesS));
	}
	
	public boolean[] getAccion(int i){															//Devuelve la acción i en formato vector de boolean.
		return acciones.get(i);
	}
	
	public String getNAccion(boolean[] accion){								                    //Convierte vector de booleanos a String.
		return new String(Arrays.toString(accion));
	}
	
	public String getNAccion(int i){															//Devuelve la acción i en formato string.
		return new String(Arrays.toString(acciones.get(i)));
	}
}
