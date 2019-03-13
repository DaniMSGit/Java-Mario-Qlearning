package competition.uhu.controller;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

public class MainEntrenamiento {
	
	public static void main(String[] args){

		final MarioAIOptions marioAIOptions  = new MarioAIOptions(args);
		final Control  agent           = new Control("Control");
		ArrayList<Integer>   resultados      = new ArrayList<>();					//Lista de distancias para cada nivel 0-99.
		ArrayList<Double>    medias	         = new ArrayList<>();					//Lista de medias para cada evaluación de 100 niveles.
		double			     mejorMedia      = 0;									//Mejor evaluación.
		BasicTask basicTask;
		FileWriter fichero;
	    PrintWriter pw;

        marioAIOptions.setAgent(agent);												//Carga del agente.
        
	    Scanner Sc               			 = new Scanner (System.in);     
	    double  alpha;                                                              //Factor de aprendizaje.
	    double  gamma;																//Factor de descuento.
	    double  aleatoriedad;														//Factor de exploración.
	    int 	niteraciones = 10000;
	   
        System.out.println("ENTRENAMIENTO:");
          	 
		do{																			//Carga de parámetros de entrenamiento.
			System.out.println("Introducir alpha(0-1):");
			alpha = Double.parseDouble(Sc.nextLine());
		}while(alpha < 0 || alpha > 1);
		 
		do{
			System.out.println("Introducir gamma(0-1):");
			gamma = Double.parseDouble(Sc.nextLine());
		}while(gamma < 0 || gamma > 1);
		 
		do{
			System.out.println("Introducir aleatoriedad(0-1):");
			aleatoriedad = Double.parseDouble(Sc.nextLine());
		}while(aleatoriedad < 0 || aleatoriedad > 1);
          	 
        Constantes.alpha = alpha;
      	Constantes.fdescuento = gamma;
      	Constantes.Aleatoriedad = aleatoriedad;
          	
  	    int contadorModoMario = 2;													//Se usa para cambiar el modo de Mario cada 10 iteraciones.
  	    int contadorCambioScene = 0;												//Se usa para cambiar el escenario cada 30 iteraciones.
        
  	    agent.resetQtabla();														//Limpieza de la tabla q.
    	agent.setModo(0);															//Modo entrenamiento.
      	
    	basicTask = new BasicTask(marioAIOptions);
      	marioAIOptions.setLevelRandSeed(100);										//Se entrena desde la semilla 100 en adelante.									
      	marioAIOptions.setMarioMode(2);												//Se empieza el entrenamiento en modo Mario fuego.
      	marioAIOptions.setVisualization(false);
                     
 	            
	    for (int i = 1; i <= niteraciones ; i++) {											//Realiza 6000 niveles entrenando.
	            		
	    	basicTask.doEpisodes(1,true,1);											//Inicia partida.
	            		
    		if(i % 10 == 0){														//Cada 10 repeticiones del nivel se cambia el modo de Mario.
    			contadorModoMario--;
    			marioAIOptions.setMarioMode(contadorModoMario);
    		}
	            		
	        if(i % 30 == 0){														//Cada 30 repeticiones se pasa al modo evaluación.
	            			
    			agent.setModo(1);													//Modo evaluación.												
	            			
    			for (int j = 0; j < 100; j++) {										//Evaluación del comportamiento de Mario en los niveles 0-99.
	        		marioAIOptions.setLevelRandSeed(j);
	        		marioAIOptions.setMarioMode(2);
	        		basicTask.doEpisodes(1,true,1);
	        		resultados.add(basicTask.getEnvironment().getEvaluationInfo().distancePassedCells);	 //Distancia alcanzada en el nivel.
	   		   	}
	            			
    			double sum = 0;														
    			for(Integer d : resultados) sum += d;
    			double media = ((sum/resultados.size())*100)/256;                   //Media de distancia en porcentaje de los 0-99 niveles.
    			
    			if(media > mejorMedia){												//Actualización de la mejor media.
    				mejorMedia = media;
    				agent.setMejorqtabla();
    			}
    			
    			medias.add(media);													//Registro de media obtenida.
    			resultados.clear();													//Limpiar resultados.
	            agent.setModo(0);													//Reanudamos entremiento.
	            contadorCambioScene++;												//Siguiente semilla para el nivel.
  				contadorModoMario = 2;												//Modo Mario Fuego.
  				marioAIOptions.setMarioMode(contadorModoMario);
  				marioAIOptions.setLevelRandSeed(contadorCambioScene);        					
	        }
	            		
	    }
	            		
	            	 
	    try {
  			
			agent.guardarParametros();															//Guardar qtabla y constantes.
			fichero = new FileWriter("resultados.txt");								//Guardar resultados.
	        pw =  new PrintWriter(fichero);
	        pw.println("["+Constantes.alpha+","+Constantes.fdescuento+","+Constantes.Aleatoriedad+"]");
	  		pw.println("Mejor media: " + mejorMedia);
	        	
	  		for (int k = 0; k <medias.size(); k++) {
	  			pw.println(medias.get(k));
			}
	  		
	  		fichero.close();
				
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
          	            	  
	   System.out.println("\nEntreamiento completado con los parámetros:"+" alfa:"+Constantes.alpha+" gamma:"+Constantes.fdescuento+" exploración:"+Constantes.Aleatoriedad);
	   System.out.println("Mejor distancia media conseguida: " + mejorMedia + ".");
	   System.out.println("Ejecute MainPrueba.java para probar el agente.");
       System.exit(0);
	}

}
