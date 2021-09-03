package simulator.launcher;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;


import simulator.control.Controller;
import simulator.control.StateComparator;
import simulator.factories.BasicBodyBuilder;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.EpsilonEqualStateBuilder;
import simulator.factories.Factory;
import simulator.factories.MassEqualStateBuilder;
import simulator.factories.MassLosingBodyBuilder;
import simulator.factories.MovingTowardsFixedPointBuilder;
import simulator.factories.NewtonUniversalGravityBuilder;
import simulator.factories.NoForceBuilder;
import simulator.model.Body;
import simulator.model.ForceLaws;
import simulator.model.PhysicsSimulator;
import simulator.view.MainWindow;

public class Main {

	// default values for some parameters
	//
	private final static Double _dtimeDefaultValue = 2500.0;
	private final static String _forceLawsDefaultValue = "nlug";
	private final static String _stateComparatorDefaultValue = "epseq";
	private final static int _numberOfStepsDefaultValue = 150;
	private final static String _defaultMode = "batch";

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _dtime = null;
	private static String _inFile = null;
	private static String _outFile = null;
	private static String _eoFile = null;
	private static JSONObject _forceLawsInfo = null;
	private static JSONObject _stateComparatorInfo = null;
	private static int _numberOfSteps = _numberOfStepsDefaultValue;
	private static String _mode = null;
	
	
	// factories
	private static Factory<Body> _bodyFactory;
	private static Factory<ForceLaws> _forceLawsFactory;
	private static Factory<StateComparator> _stateComparatorFactory;
	
	//Physics Simulator
	private static PhysicsSimulator py = null;
	
	//Controller
	private static Controller control = null;
	
	private static void init() {
		//initialize the bodies factory
		ArrayList<Builder<Body>> bodyBuilders = new ArrayList<>(); //declaramos un array de bodies
		bodyBuilders.add(new BasicBodyBuilder());//inicializamos con los posibles bodies
		bodyBuilders.add(new MassLosingBodyBuilder());
		_bodyFactory = new BuilderBasedFactory<Body>(bodyBuilders); //creamos la factoria mediante el BuilderBasedFactory pasando la lista de bodies

		//initialize the force laws factory
		ArrayList<Builder<ForceLaws>> forceLawsBuilders = new ArrayList<>(); //declaramos un array de fuerzas
		forceLawsBuilders.add(new NewtonUniversalGravityBuilder());//inicializamos con las posibles leyes de fuerza
		forceLawsBuilders.add(new MovingTowardsFixedPointBuilder());
		forceLawsBuilders.add(new NoForceBuilder());
		_forceLawsFactory = new BuilderBasedFactory<ForceLaws>(forceLawsBuilders); //creamos la factoria mediante el BuilderBasedFactory pasando la lista de fuerzas
		
		//initialize the state comparator
		ArrayList<Builder<StateComparator>> stateComparatorBuilders = new ArrayList<>();//declaramos un arrya de comparadores
		stateComparatorBuilders.add(new MassEqualStateBuilder());//incializamos con los posibles comparadores
		stateComparatorBuilders.add(new EpsilonEqualStateBuilder());
		_stateComparatorFactory = new BuilderBasedFactory<StateComparator>(stateComparatorBuilders);//creamos la factoria median el BuilderBasedFactory pasando la lista de comparadores
	}

	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);

			parseHelpOption(line, cmdLineOptions);
			parseInFileOption(line);
			parseOutFileOption(line);
			parseEoOption(line);
			parseStepsOption(line);
			parseDeltaTimeOption(line);
			parseForceLawsOption(line);
			parseStateComparatorOption(line);
			parseMode(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Bodies JSON input file.").build());
	
		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written. Default value: the standar output").build());
		
		//expected output
		cmdLineOptions.addOption(Option.builder("eo").longOpt("expected-output").hasArg().desc("The expected output file. If not provided no comparison is applied").build());
		
		//simulation steps
		cmdLineOptions.addOption(Option.builder("s").longOpt("steps").hasArg().desc("An integer representing the number of simulation steps. Default value: " + _numberOfStepsDefaultValue).build());
		
		// delta-time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _dtimeDefaultValue + ".")
				.build());

		// force laws
		cmdLineOptions.addOption(Option.builder("fl").longOpt("force-laws").hasArg()
				.desc("Force laws to be used in the simulator. Possible values: "
						+ factoryPossibleValues(_forceLawsFactory) + ". Default value: '" + _forceLawsDefaultValue
						+ "'.")
				.build());

		// state comparator
		cmdLineOptions.addOption(Option.builder("cmp").longOpt("comparator").hasArg()
				.desc("State comparator to be used when comparing states. Possible values: "
						+ factoryPossibleValues(_stateComparatorFactory) + ". Default value: '"
						+ _stateComparatorDefaultValue + "'.")
				.build());
		//mode
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc("Execution mode. Possible values: 'batch' (Batch mode), 'gui' (Graphical User Interface mode). Default value: 'batch'").build());

		return cmdLineOptions;
	}

	public static String factoryPossibleValues(Factory<?> factory) {
		if (factory == null)
			return "No values found (the factory is null)";

		String s = "";

		for (JSONObject fe : factory.getInfo()) {
			if (s.length() > 0) {
				s = s + ", ";
			}
			s = s + "'" + fe.getString("type") + "' (" + fe.getString("desc") + ")";
		}

		s = s + ". You can provide the 'data' json attaching :{...} to the tag, but without spaces.";
		return s;
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
	}
	
	private static void parseOutFileOption(CommandLine line) {
		_outFile = line.getOptionValue("o");
		
	}
	
	private static void parseEoOption(CommandLine line) {
		_eoFile = line.getOptionValue("eo");
	}
	
	private static void parseStepsOption(CommandLine line) {
		String steps = line.getOptionValue("s");
		if(steps != null){
			_numberOfSteps = Integer.parseInt(steps);
		}
				
	}

	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _dtimeDefaultValue.toString());
		try {
			_dtime = Double.parseDouble(dt);
			assert (_dtime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + dt);
		}
	}

	private static JSONObject parseWRTFactory(String v, Factory<?> factory) {

		// the value of v is either a tag for the type, or a tag:data where data is a
		// JSON structure corresponding to the data of that type. We split this
		// information
		// into variables 'type' and 'data'
		//
		int i = v.indexOf(":");
		String type = null;
		String data = null;
		if (i != -1) {
			type = v.substring(0, i);
			data = v.substring(i + 1);
		} else {
			type = v;
			data = "{}";
		}

		// look if the type is supported by the factory
		boolean found = false;
		for (JSONObject fe : factory.getInfo()) {
			if (type.equals(fe.getString("type"))) {
				found = true;
				break;
			}
		}

		// build a corresponding JSON for that data, if found
		JSONObject jo = null;
		if (found) {
			jo = new JSONObject();
			jo.put("type", type);
			jo.put("data", new JSONObject(data));
		}
		return jo;

	}

	private static void parseForceLawsOption(CommandLine line) throws ParseException {
		String fl = line.getOptionValue("fl", _forceLawsDefaultValue);
		_forceLawsInfo = parseWRTFactory(fl, _forceLawsFactory);
		if (_forceLawsInfo == null) {
			throw new ParseException("Invalid force laws: " + fl);
		}
	}

	private static void parseStateComparatorOption(CommandLine line) throws ParseException {
		String scmp = line.getOptionValue("cmp", _stateComparatorDefaultValue);
		_stateComparatorInfo = parseWRTFactory(scmp, _stateComparatorFactory);
		if (_stateComparatorInfo == null) {
			throw new ParseException("Invalid state comparator: " + scmp);
		}
	}
	
	private static void parseMode(CommandLine line) {
		String mode = line.getOptionValue("m", _defaultMode);
		if(mode == null) {
			_mode = _defaultMode;
		}else {
			_mode = mode;
		}
	}

	private static void startBatchMode() throws Exception {
		py = new PhysicsSimulator(_forceLawsFactory.createInstance(_forceLawsInfo), _dtime); //inicializamos el simulador
		FileInputStream in = null;
		FileOutputStream out = null;
		FileInputStream eo = null;
		StateComparator cmp = null;
		
		if(_inFile == null) {
			throw new ParseException("In batch mode an input file of bodies is required");
		}else {
			in = new FileInputStream(new File(_inFile));

		}
		
		if(_outFile != null) {
			out = new FileOutputStream(new File(_outFile));
			
		}
		if(_eoFile != null) {
			eo = new FileInputStream(new File(_eoFile));
			cmp = _stateComparatorFactory.createInstance(_stateComparatorInfo); //inicializamos el comparador
		}
		
		
		control = new Controller(py, _bodyFactory,_forceLawsFactory); //construimos el comparador
		control.loadBodies(in); //cargamos los cuerpos en el comparador
		in.close();
		control.run(_numberOfSteps, out, eo, cmp);//iniciamos la simulación
		
		if(_outFile != null) {//cerramos el archivo para evitar errores
			out.close();
		}
		if(_eoFile != null) {//cerramos el archivo para evitar errores
			eo.close();
		}
	}
	
	private static void startGUIMode(){
		py = new PhysicsSimulator(_forceLawsFactory.createInstance(_forceLawsInfo), _dtime); //inicializamos el simulador
		control = new Controller(py, _bodyFactory, _forceLawsFactory); //construimos el comparador
		if(_inFile != null) {
			FileInputStream in;
			try {
				in = new FileInputStream(new File(_inFile));
				control.loadBodies(in); //cargamos los cuerpos en el comparador
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					new MainWindow(control);
				}
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void start(String[] args) throws Exception {
		parseArgs(args);
		if(_mode.equals("batch")) {
			startBatchMode();
		}else {
			startGUIMode();
		}
		
	}

	public static void main(String[] args) {
		try {
			init();
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
