package test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Commands {

	// Default IO interface
	public interface DefaultIO {
		public String readText();

		public void write(String text);

		public float readVal();

		public void write(float val);
	}

	// the default IO to be used in all commands
	DefaultIO dio;

	public Commands(DefaultIO dio) {
		this.dio = dio;
	}

	// the shared state of all commands
	private class SharedState {
		TimeSeries trainTimeSeries;
		TimeSeries testTimeSeries;
		SimpleAnomalyDetector simpleAnomalyDetector = new SimpleAnomalyDetector();
		List<AnomalyReport> anomalyReports = new ArrayList<AnomalyReport>();
		double falsePositive;
		double truePostive;

		public TimeSeries getTestTimeSeries() {
			return testTimeSeries;
		}

		public TimeSeries getTrainTimeSeries() {
			return trainTimeSeries;
		}

		public List<AnomalyReport> getAnomalyReports() {
			return anomalyReports;
		}

		public SimpleAnomalyDetector getSimpleAnomalyDetector() {
			return simpleAnomalyDetector;
		}

		public void setAnomalyReports(List<AnomalyReport> anomalyReports) {
			this.anomalyReports = anomalyReports;
		}
	}

	private SharedState sharedState = new SharedState();

	// Command abstract class
	public abstract class Command {
		protected String description;

		public Command(String description) {
			this.description = description;
		}

		public abstract void execute();
	}

	// Command class for Exit:
	public class ExitCommand extends Command {

		public ExitCommand() {
			super("Exit Command");
		}

		@Override
		public void execute() {
			dio.write("bye");
		}
	}

	// Command class for Upload File:
	public class UploadFileCommand extends Command {
		PrintWriter printWriter;
		String str;

		public UploadFileCommand() {
			super("Upload File command");
		}

		@Override
		public void execute() {
			dio.write("Please upload your local train CSV file.\n");
			try {
				printWriter = new PrintWriter(new FileWriter("anomalyTrain.txt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			str = dio.readText();
			while (true) {
				str = dio.readText();
				if (str.equals("done")) {
					break;
				}
				printWriter.write(str + "\n");
			}
			printWriter.close();

			sharedState.trainTimeSeries = new TimeSeries("anomalyTrain.txt");
			dio.write("Upload complete.\n");
			dio.write("Please upload your local test CSV file.\n");
			try {
				printWriter = new PrintWriter(new FileWriter("anomalyTest.txt"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (true) {
				str = dio.readText();
				if (str.equals("done")) {
					break;
				}
				printWriter.write(str + "\n");
			}
			printWriter.close();
			sharedState.testTimeSeries = new TimeSeries("anomalyTest.txt");
			dio.write("Upload complete.\n");
		}
	}

	public class AlgorithemSettingsCommand extends Command {

		public AlgorithemSettingsCommand() {
			super("Correlation threshold command");
		}

		@Override
		public void execute() {
			dio.write("The current correlation threshold is "
					+ sharedState.simpleAnomalyDetector.getCorrelationThreshold() + "\n");
			dio.write("Type a new threshold\n");
			float newCorrelationThreshold = dio.readVal();
			if (checkCorrelationThreshold(newCorrelationThreshold)) {
				sharedState.simpleAnomalyDetector.setCorrelationThreshold(newCorrelationThreshold);
			} else {
				dio.write("please choose a value between 0 and 1.\n");
			}
		}

		private Boolean checkCorrelationThreshold(float threshold) {
			return (threshold > 0 && threshold < 1);
		}
	}

	public class DetectAnomaliesCommand extends Command {
		private TimeSeries trainSeries;
		private TimeSeries testSeries;
		private SimpleAnomalyDetector simpleAnomalyDetector;
		private List<AnomalyReport> anomalyReports = new ArrayList<AnomalyReport>();

		public DetectAnomaliesCommand() {
			super("Detect anomalies command");
			trainSeries = sharedState.getTrainTimeSeries();
			testSeries = sharedState.getTestTimeSeries();
			simpleAnomalyDetector = sharedState.getSimpleAnomalyDetector();
		}

		@Override
		public void execute() {
			simpleAnomalyDetector.learnNormal(trainSeries);
			anomalyReports = simpleAnomalyDetector.detect(testSeries);
			sharedState.setAnomalyReports(anomalyReports);
			dio.write("anomaly detection complete.\n");
		}
	}

	public class DisplayResultsCommand extends Command {
		private List<AnomalyReport> anomalyReports = new ArrayList<AnomalyReport>();

		public DisplayResultsCommand() {
			super("Display results command");
			anomalyReports = sharedState.getAnomalyReports();
		}

		@Override
		public void execute() {
			for (AnomalyReport anomalyReport : anomalyReports) {
				dio.write(anomalyReport.timeStep + "\t" + anomalyReport.description + "\n");
			}
			dio.write("Done.\n");
		}
	}

	public class UploadAnomaliesCommand extends Command {
		private List<AnomalyReport> anomalyReports = new ArrayList<AnomalyReport>();
		private List<AnomalyReport> updateAnomalyReports = new ArrayList<AnomalyReport>();
		Double falsePositive = 0.0, truePostive = 0.0;
		Double truePositiveRate = 0.0, falsePositiveRate = 0.0;
		PrintWriter printWriter;
		double P;
		double N;
		String str;

		public UploadAnomaliesCommand() {
			super("display results command");
			falsePositive = sharedState.falsePositive;
			truePostive = sharedState.truePostive;
			anomalyReports = sharedState.getAnomalyReports();
			UpdateAnomalyReports();
			N = sharedState.testTimeSeries.getNumberOfItemsInColumn();
		}

		@Override
		public void execute() {
			List<Integer> timesList = new ArrayList<Integer>();
			dio.write("Please upload your local anomalies file.\n");
			dio.readText();
			while (true) {
				str = dio.readText();
				if (str.equals("done")) {
					break;
				}
				String[] strings = str.split(",");
				Integer startTimestep = Integer.parseInt(strings[0]);
				Integer endTimestep = Integer.parseInt(strings[1]);
				timesList.add(startTimestep);
				timesList.add(endTimestep);
				P += 1;
				N -= (endTimestep - startTimestep + 1);
			}
			for (int i = 0; i < timesList.size() - 1; i++) {
				for (int j = 0; j < updateAnomalyReports.size() - 1; j++) {
					if (isTruePositive(updateAnomalyReports.get(j).timeStep, updateAnomalyReports.get(j + 1).timeStep,
							timesList.get(i), timesList.get(i + 1))) {
						truePostive++;
					}
				}
			}
			double numberOfReports = updateAnomalyReports.size() / 2;
			if (truePostive < numberOfReports) {
				falsePositive = numberOfReports - truePostive;
			} else if (truePostive > numberOfReports) {
				truePostive = numberOfReports;
			}
			dio.write("Upload complete.\n");
			truePositiveRate = roundAvoid(truePostive / P, 3);
			falsePositiveRate = roundAvoid(falsePositive / N, 3);
			dio.write("True Positive Rate: " + truePositiveRate + "\n");
			dio.write("False Positive Rate: " + falsePositiveRate + "\n");
		}

		public double roundAvoid(double value, double afterPoint) {
			afterPoint = Math.pow(10, afterPoint);
			return Math.round(Math.floor(value * afterPoint)) / afterPoint;
		}

		public boolean isTruePositive(Long currentTimestep, Long nextTimestep, Integer start, Integer end) {
			return ((currentTimestep >= start && currentTimestep <= end)
					|| (nextTimestep >= start && nextTimestep <= end));
		}

		private void UpdateAnomalyReports() {
			Boolean same = false;
			AnomalyReport firstAnomalyReport = anomalyReports.get(0);
			updateAnomalyReports.add(firstAnomalyReport);
			for (int i = 0; i < anomalyReports.size(); i++) {
				if (firstAnomalyReport.description.equals(anomalyReports.get(i).description)) {
					firstAnomalyReport = anomalyReports.get(i);
					same = true;
				} else {
					updateAnomalyReports.add(firstAnomalyReport);
					firstAnomalyReport = anomalyReports.get(i);
					updateAnomalyReports.add(firstAnomalyReport);
					same = false;
				}
			}
			if (same) {
				updateAnomalyReports.add(firstAnomalyReport);
			}
		}
	}

}
