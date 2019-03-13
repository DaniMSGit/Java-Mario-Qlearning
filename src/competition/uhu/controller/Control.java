package competition.uhu.controller;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;


public class Control implements Agent{

	private   Estado 	  estadoActual;
	private   Acciones    acciones;
	private   Qtabla      qtabla;
	private   boolean[]   accion;
	protected boolean     action[];
	protected String      name;
	private   String      estadoAnterior;
	private   Environment environment;
	private   int 		  modo;

	
	public Control(String s){
	    setName(s);
	    estadoActual   = new Estado();
		acciones 	   = new Acciones();
		qtabla         = new Qtabla();
		accion  	   = new boolean[acciones.nAcciones];
		action         = new boolean[Environment.numberOfKeys];
		name           = "DanielMorueta";
		estadoAnterior = null;
		modo		   = 1;
		leerParametros();										//Qtabla y constantes.
	}
	
	public boolean[] getAction() {
		
		boolean puedeSaltar = environment.isMarioAbleToJump() || !environment.isMarioOnGround(); //Se usará para elegir la acción adecuada.
	
		//System.out.print("\nPara el estado actual: "+ estadoActual.toString()); 

		if (modo == 0){																			//Si el modo es entrenamiento.
			accion = qtabla.getSigAccion(estadoActual,puedeSaltar);								//Se obtiene la acción a tomar de la qtabla.
			//System.out.print("\nPara el estado anterior:" + estadoAnterior); 
			String nombreEA = qtabla.getNombreEA(estadoActual, accion);							//Estado-Accion a cadena.
			if(estadoAnterior != null){															//Si existe estado anterior.
				//System.out.print("\nActualizar Qtabla:");
				qtabla.ActualizarQvalor(estadoAnterior,estadoActual,nombreEA,puedeSaltar);		//Actualizar Valor q del estado anterior.
				estadoAnterior = new String(nombreEA);											//Estado actual pasa a ser estado anterior.
			}else{
				//System.out.print("\nNo actualizar Qtabla por no existir estado anterior.\n"); //Si no existe estado anterior.
				estadoAnterior = new String(nombreEA);											//Estado actual pasa a ser estado anterior.
			}
		}else{
			accion = qtabla.getSigAccionEvaluacion(estadoActual,puedeSaltar);					//Si el modo es evaluación.
		}
		return accion;
	}

	public void integrateObservation(Environment _environment) {	
		environment = _environment;																//Actualización del entorno y estado.
		estadoActual.update(environment);
	}

	public void giveIntermediateReward(float intermediateReward){}

	public void reset()
	{
		estadoAnterior = null;
		accion  	   = new boolean[acciones.nAcciones];
		estadoActual.reset();
	}

	public void setObservationDetails(final int rfWidth, final int rfHeight, final int egoRow, final int egoCol){}


	public String getName() { return name; }
	
	public void setName(String Name) { this.name = Name; }
	
	public void guardarParametros() throws Exception{
		qtabla.guardarQtabla();
		//qtabla.guardarQtablaTexto();
		//Constantes.guardarConstantes();
	}
	
	public void setMejorqtabla() { qtabla.setMejorQtabla(); }
	

	public void setModo(int i){
		modo = i;
	}

	public void leerParametros() {
		try {
	   		 //Constantes.leerConstantes();
	   		 qtabla.leerQtabla();
		} catch (Exception e) {
			 System.out.println(e.getMessage());
		}			
	}
	
	public void resetQtabla() {
		qtabla = new Qtabla();		
	}
	
}
