package competition.uhu.controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

public class Qtabla {
	
	Random rnd;
	Hashtable<String, Double> qtabla;				//Tabla hash que almacena el q valor de los distintos estado-acción.
	Hashtable<String, Double> Mejorqtabla;          //Mejor tabla obtenida.
	Acciones acciones;
	boolean[] accion;
	int maxAccion;
	FileWriter fichero;
    PrintWriter pw;

	public Qtabla(){
		
		rnd 	    = new Random();
		rnd.setSeed(System.currentTimeMillis());
		qtabla      = new Hashtable<String, Double>();
		Mejorqtabla = new Hashtable<String, Double>();
		acciones    = new Acciones();
		accion      = new boolean[acciones.nAcciones];
		
	}
	
	public boolean[] getSigAccion(Estado estadoActual, boolean puedeSaltar){
		
		maxAccion = -1;
		boolean [] accion;
		//System.out.print("\nAccion escogida->");
		
		if(rnd.nextDouble() > Constantes.Aleatoriedad){         //Si aleatoriamente el valor obtenido es mayor que la constante de exploración.
			maxAccion = getMaxQ(estadoActual,puedeSaltar);      //Para el estado actual obtiene la acción de q valor máximo en la qtabla.
			if(maxAccion != -1){								//Si ha encontrado una acción
				accion = acciones.getAccion(maxAccion);
				return accion;	 
			}else{												//Si no es así.
				//System.out.print("Para el estado "+ estadoActual.toString() + " no hay ninguna accion en Qtabla(Escogemos accion aleatoria).");
				accion = acciones.getAccionAlet(puedeSaltar);	//Obtiene una acción aleatoria.
				//System.out.print(acciones.getNAccion(accion));
				return accion;
			}
		}else{													//Si la acción es aleatoria.
			//System.out .print("Aleatoriamente");
			accion = acciones.getAccionAlet(puedeSaltar);
			//System.out.println(acciones.getNAccion(accion));
			return accion;
		}
	}
	
	public boolean[] getSigAccionEvaluacion(Estado estadoActual, boolean puedeSaltar){
		
		maxAccion = -1;
		boolean [] accion;
		//System.out.print("\nAccion escogida->");
		
		maxAccion = getMaxQ(estadoActual,puedeSaltar);						//Para el estado actual obtiene la acción de q valor máximo en la qtabla.
		if(maxAccion != -1){
			accion = acciones.getAccion(maxAccion);
			return accion;	 
		}else return new boolean[] {false,false,false,false,false,false};   //Si no es así la acción escogida es parar.
	}
	
	private int getMaxQ(Estado estado,boolean puedeSaltar) {
		
		double qMax = 0;																 //Q valor máximo para el estado-acción escogida.
		int indiceAccion = -1;															 //Acción escogida.
		
		int limiteAcciones = puedeSaltar ? acciones.nAcciones : acciones.nAccionesS ;    //Índice de acciones a elegir en función de si puede saltar.
		
		//System.out.println("\nOpciones en Tabla q:");
		
		for(int i = 0;i<limiteAcciones;i++){
			String nombreEA = getNombreEA(estado, acciones.getAccion(i));
			Object q = qtabla.get(nombreEA);                                             //Obtener q valor de la tabla.
			if(q != null && ((Double)q > qMax || indiceAccion==-1)){                     //Si existe el valor y es mayor que el actual o es el primer q valor encontrado.
				qMax = (Double)q;														 //Actualizar qMax y el indice de acción escogida.
				indiceAccion = i;
				//System.out.print(nombreEA + " " + qMax+"\n");
			}
		}
		
		if(indiceAccion!=-1){
			//System.out.println("Accion con qMax en la qTabla.");
			//System.out.print(acciones.getNAccion(indiceAccion) + " = " + qMax);
		}
		
		return indiceAccion;
	}



	public void ActualizarQvalor(String estadoAnterior, Estado estadoActual, String nombreEA, boolean puedeSaltar) {
		
		double MaxQ = 0;
		double qValorEstadoAnterior;
		double recompensa = estadoActual.getRecompensa();
		
		
		//System.out.print("\nRecompensa estado actual="+recompensa);
		
		if(qtabla.containsKey(estadoAnterior)){                                                                                       //Si se encuentra el estado anterior en la qtabla.
			
			//System.out.print("\nExistiendo el estado anterior " + estadoAnterior + " en qTabla.");
			qValorEstadoAnterior = qtabla.get(estadoAnterior);                                                                        //Obtiene el q valor del estado anterior.
			
			if(maxAccion==-1){																										  //Si la acción escogida fue aleatoria.
				
				//System.out.print("\nObtenemos el mejor qValor para "+estadoActual.toString());
				maxAccion = getMaxQ(estadoActual,puedeSaltar);																		  //Buscar la acción con mmayor q valor para el estado actual.
			}
			
			if(maxAccion!=-1){																										  //Si se ha encontrado una acción.
				MaxQ = qtabla.get(getNombreEA(estadoActual, acciones.getAccion(maxAccion)));										  //Actualizar QMax.
			}//else System.out.print("No se ha encontrado ninguna accion en q tabla. MaxQ = "+ MaxQ);
			
			
			double qValor = (1-Constantes.alpha)*qValorEstadoAnterior + Constantes.alpha *(recompensa + Constantes.fdescuento * MaxQ); //Actulizar q valor del estado anterior.
			qtabla.put(estadoAnterior,qValor);
			
			//System.out.print("\n\nqValor estado anterior = " + "(1-"+Constantes.alpha+")"+"*"+qValorEstadoAnterior + " + " + Constantes.alpha + "* (" + recompensa
			//		+ "+" + Constantes.fdescuento +"*"+ MaxQ + ")" +" --->"+ qValor);
			
		}else{
			//System.out.print("\nNo existe estado anterior en qTabla el valor del estado anterior es 0.0");
			qtabla.put(estadoAnterior,0.0);
		}
		
		//System.out.println("\n");
	}
	
	public String getNombreEA(Estado estado, boolean[] accion ) {
		
		return new String(Arrays.toString(estado.getEstado()) + Arrays.toString(accion));
	}
	
	public void setMejorQtabla(){
		Mejorqtabla = (Hashtable<String, Double>) qtabla.clone();
	}
	
	public void guardarQtabla() throws Exception{
		try {
			FileOutputStream fos = new FileOutputStream("qtabla.txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(Mejorqtabla);
			oos.close();
		} catch (Exception e) {
			System.out.println("Error al guardar la tabla q.");
		}	
	}
	
	public void leerQtabla() throws Exception {
		try {
			FileInputStream fin = new FileInputStream("qtabla.txt");
			ObjectInputStream inn = new ObjectInputStream(fin);
			qtabla = (Hashtable<String, Double>)inn.readObject();
			fin.close();
		} catch (Exception e) {
			throw new Exception("Error al abrir el fichero qtabla.");
		}
	}
	
	public void guardarQtablaTexto() throws Exception{
		
		try {
			fichero = new FileWriter("hash.txt");
			pw =  new PrintWriter(fichero);
			Enumeration e = Mejorqtabla.keys();
			Object clave;
			while( e.hasMoreElements() ){
			  clave = e.nextElement();
			  pw.println(clave);
			}
			fichero.close();
		} catch (Exception e) {
			throw new Exception("Error al guardar la qtabla en modo texto.");
		}
		
	}
	
}
