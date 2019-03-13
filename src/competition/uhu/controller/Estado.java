package competition.uhu.controller;

import java.util.ArrayList;
import java.util.Collections;

import org.hamcrest.core.IsSame;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.environments.Environment;

public class Estado {
	
	private int[] 	 estado;						//Vector de enteros que representa el estado del agente.
	private int 	 nAtributos       = 7;			//Número de atributos del estado.
	private int 	 ModoMario;						//Modo de Mario (0,1,2).
	private int      distanciaAlsuelo = 1;			//Distancia minima al suelo.
			
	private int      DistanciaRec 	  = 0;			//Distancia recorrida en el frame.
	private int      UltimaDist   	  = 0;			//Distancia recorrida en el frame anterior.
	private Boolean  bloqueado    	  = false;		//Mario permanece inmóvil.
	private int 	 NframeBloqueado  = 0;			//Contador de frames en los que Mario permanece bloqueado.
	private int 	 colsFrame 		  = 0;			//Colisiones en el frame actual.
	private int 	 colisiones       = 0;			//Colisiones en el frame anterior.
	private int      direccion		  = 0;			//Dirección de Mario 0-Izda 1-Parado 2-Dcha.
	private byte[][] cuadriculaO;					//Cuadrícula de obstaculos. 
	private byte[][] cuadriculaE;					//Cuadrícula de enemigos.
	private String 	 obstaculos;
	private String   enemigos;
	private int 	 MaxDistancia     = 10;			//Límite de distancia recorrida
	private int 	 MaxFrameBloq     = 10;			//Límite de frames bloqueado para poner en estado de bloqueo a Mario.

	
	public Estado(){
		
		estado = new int [nAtributos];
		
	}
	
	public int getnAtributos(){return nAtributos;}
	
	public void update(Environment environment){
		
		int[] posMario = environment.getMarioEgoPos();
		//System.out.println("####"+ posMario[0]+ "-" + posMario[1]);
	
		cuadriculaO = environment.getLevelSceneObservationZ(1);   									 //Objetos en las 19x19 casillas alrededor de Mario.
		cuadriculaE = environment.getEnemiesObservationZ(1);                                         //Enemigos en las 19x19 casillas alrededor de Mario.
		
		/*
		//Mostrar cuadrícula enemigos.
		for(int i=0;i<cuadriculaE.length;i++){
			for(int j=0;j<cuadriculaE.length;j++){
				if(j==9 && i==9 ) System.out.print("["+ (esEnemigo(cuadriculaE[i][j]) ? 1 : 0));
				else System.out.print(" " + (esEnemigo(cuadriculaE[i][j]) ? 1 : 0));
			}
			System.out.print("\n");
		}*/
		
		enemigos = new String();
		
		for(int i=7;i<11;i++){																          //Enemigos alrededor de Mario.
			for(int j=8;j<11;j++){
				//if(j==9 && i==9 ) System.out.print("["+ (esEnemigo(cuadriculaE[i][j])? 1 : 0));
				//else System.out.print(" " +(esEnemigo(cuadriculaE[i][j])? 1 : 0));
				enemigos+= (esEnemigo(cuadriculaE[i][j]) ? 1 : 0);
			}
			//System.out.print("\n");
		}
		
		//System.out.println("\n"+enemigos);
		
		/*
		//Mostrar cuadrícula obstaculos.
		for(int i=0;i<cuadriculaO.length;i++){
			for(int j=0;j<cuadriculaO.length;j++){
				if(j==9 && i==9 ) System.out.print("["+ (esObstaculo(cuadriculaO[i][j]) ? 1 : 0) );
				else System.out.print(" " + (esObstaculo(cuadriculaO[i][j]) ? 1 : 0) );
			}
			System.out.print("\n");
		}*/
		
		obstaculos = new String();
		
		obstaculos+= esObstaculo(cuadriculaO[10][10]) ? 1 : 0;							 //Obstaculos a la derecha de Mario.
		obstaculos+= esObstaculo(cuadriculaO[9][10])  ? 1 : 0;							     
		obstaculos+= esObstaculo(cuadriculaO[8][10])  ? 1 : 0;							     
		obstaculos+= esObstaculo(cuadriculaO[7][10])  ? 1 : 0;							     
		obstaculos+= esObstaculo(cuadriculaO[6][10])  ? 1 : 0;	
								     
			
		//System.out.println("\n"+obstaculos);
		
		boolean hayTierra = false;														         //Cálculo de distancia al suelo de Mario.
		int indice = 10;
		while(indice < 19 && hayTierra== false){
			//System.out.println(cuadricula[indice][9]);
			if(esObstaculo(cuadriculaO[indice][9])){
				hayTierra = true;
				distanciaAlsuelo = indice - 9;
				//System.out.println(distanciaAlsuelo);
			}	
			indice++;
		}
		if(indice == 19) distanciaAlsuelo = 10;											         //Si llega al límite de la cuadrícula se limita a 10.
		
		//System.out.println("DISTANCIA AL SUELO:"+ distanciaAlsuelo);
		//System.out.println("DISTANCIA RECORRIDA:"+ DistanciaRec);
		
		
		DistanciaRec = environment.getEvaluationInfo().distancePassedPhys - UltimaDist ;         //Distancia recorrida en el frame actual.
		
		if(environment.getEvaluationInfo().distancePassedPhys>UltimaDist) direccion = 2;         //Cálculo de la dirección de Mario.
		else if(environment.getEvaluationInfo().distancePassedPhys<UltimaDist) direccion = 0;
		else direccion =1;
		
		UltimaDist = environment.getEvaluationInfo().distancePassedPhys;						 //Distancia recorrida en el frame anterior.
		if(DistanciaRec>10) DistanciaRec = 10;													 //Ajustar a los límites.
		else if(DistanciaRec<-10) DistanciaRec = -10;
		
		colsFrame   = environment.getEvaluationInfo().collisionsWithCreatures - colisiones;      //Colisiones en el frame actual.
		colisiones  = environment.getEvaluationInfo().collisionsWithCreatures;					 //Colisiones en el frame anterior.
		if(colsFrame>1){
			colsFrame = 1;													 		             //Ajustar a los límites.
		} 
				
		if(direccion == 1 ) NframeBloqueado++;                           					     //Incremento de frames bloqueados.
		else NframeBloqueado = 0;																 //Reinicio del contador.
		
		if(NframeBloqueado > MaxFrameBloq) {									                 //Si el número de frames parado alcanza el limite BLOQUEADO.
			//System.out.println("BLOQUEADO");
			bloqueado = true;
		}
		else bloqueado = false;
		
		ModoMario = environment.getMarioMode();													 //Modo Mario 0=Pequeño, 1=Grande ,2=Fuego.
	
	    
	    //ACTUALIZACIÓN DEL ESTADO
	    
		estado[0] = ModoMario;								
		estado[1] = bloqueado ? 1 : 0;                                             
		estado[2] = environment.isMarioOnGround() ? 1:0;
		estado[3] = direccion;
		estado[4] = colsFrame;
		estado[5] = Integer.parseInt(obstaculos,2);
		estado[6] = Integer.parseInt(enemigos,2);
		
	}
	
	public void verEstado(){
		
		for(int i = 0;i < estado.length; i++){
			System.out.print(estado[i] + " ");
		}
	}
	
	public String toString() { 
        String estadoS = "[";
        for(int i = 0;i < estado.length-1; i++){
        	estadoS += estado[i] + ",";
        }
        estadoS += estado[estado.length-1];
        estadoS += "]";
        return estadoS;
    } 
	
	public int getElemEstado(int i){
		return estado[i];
	}
	
	public int[] getEstado(){
		return estado;
	}
	
	public double getRecompensa() {
		
		double recompensa = 0;
		
		recompensa = recompensa  + estado[1]*Constantes.bloqueado;											
		
		if (DistanciaRec>=2)recompensa = recompensa + Constantes.distanciaPos*(DistanciaRec);				
		else if (DistanciaRec==0) recompensa = recompensa - Constantes.distanciaNeg;
		else if (estado[3]<=-2) recompensa = recompensa - Constantes.distanciaNeg*(DistanciaRec);
		
		if (distanciaAlsuelo>3)recompensa = recompensa + Constantes.distanciaEve*(distanciaAlsuelo)*2;     //Premiamos el doble si alcaza cierta altura.
		else if (distanciaAlsuelo>1)recompensa = recompensa + Constantes.distanciaEve*(distanciaAlsuelo);
		
		if(estado[4]==1){
			recompensa = recompensa + Constantes.colision;
		}
					
		return recompensa;
	}
	
	
	public boolean esObstaculo(int valor){
		
		return valor != 5   &&
			   valor != 2   &&
			   valor != -62 &&
			   valor != 0;
	}
	
	public boolean esEnemigo(int valor){
		
		return valor == 93 || valor == 80;
	}

	public void reset() {
		
		DistanciaRec 	  = 0;
		UltimaDist   	  = 0;
		bloqueado    	  = false;
		NframeBloqueado   = 0;
		colsFrame 		  = 0;
		colisiones        = 0;
		distanciaAlsuelo  = 1;
		
	}
	

	
}
