package test;

import java.util.ArrayList;
import test.Commands.Command;
import test.Commands.DefaultIO;


public class CLI {	
	ArrayList<Command> commands;
	DefaultIO dio;
	Commands c;
	
	public CLI(DefaultIO dio) {
		this.dio=dio;
		c=new Commands(dio); 
		commands=new ArrayList<>();
		// implement
	}
	
	public void start() {
		boolean run = true;
		while(run) {
		printMenu();
		int x = (int)dio.readVal();
		switch (x) {
		case 1:
			Command uploadFileCommand =  c.new UploadFileCommand();
			uploadFileCommand.execute();
			commands.add(uploadFileCommand);
			break;
		case 2:
			Command algorithemSettingsCommand  =  c.new AlgorithemSettingsCommand();
			algorithemSettingsCommand.execute();
			commands.add(algorithemSettingsCommand);
			break;
		case 3:
			Command detectAnomaliesCommand  =  c.new DetectAnomaliesCommand();
			detectAnomaliesCommand.execute();
			commands.add(detectAnomaliesCommand);
			break;
		case 4:
			Command displayResultsCommand  =  c.new DisplayResultsCommand();
			displayResultsCommand.execute();
			commands.add(displayResultsCommand);
			break;
		case 5:
			Command uploadAnomaliesCommand  =  c.new UploadAnomaliesCommand();
			uploadAnomaliesCommand.execute();
			commands.add(uploadAnomaliesCommand);
			break;
		case 6:
			return;
		default:
			break;
		}	
		}
	}
	
	private void printMenu() {
		dio.write("Welcome to the Anomaly Detection Server.\n"
				+ "Please choose an option:\n"
				+ "1. upload a time series csv file\n"
				+ "2. algorithm settings\n"
				+ "3. detect anomalies\n"
				+ "4. display results\n"
				+ "5. upload anomalies and analyze results\n"
				+ "6. exit\n");
	}
}
