package competition.uhu.controller;


import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.MarioAIOptions;

public class MainPrueba {
	
	public static void main(String[] args){

  	  final MarioAIOptions marioAIOptions  = new MarioAIOptions(args);
  	  final Control  agent           = new Control("Control");
      BasicTask basicTask;
      marioAIOptions.setAgent(agent);                                            //Carga del agente.
             
	  basicTask = new BasicTask(marioAIOptions);
	  marioAIOptions.setLevelRandSeed(1);										 //Selección de semilla.
	  marioAIOptions.setVisualization(true);									 //Activar visualización.
	  basicTask.doEpisodes(1,true,1);											 //Iniciar partida.
      System.exit(0);
	}

}
