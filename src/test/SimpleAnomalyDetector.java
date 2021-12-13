package test;

import java.util.ArrayList;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	
	List<CorrelatedFeatures> correlatedFeatureList = new ArrayList<>();
	float correlationThreshold = 0.9f;
	float maxCorrelathioPerColumn = 0.0f;
	float maxDistance = 0.0f;

	@Override
	public void learnNormal(TimeSeries ts) {
		Point[] points = new Point[ts.getNumberOfItemsInColumn()];
		float[] scaleXValues = null, scaleYValues = null;
		String firstFeatureName = "", secondFeatureName = "";
		for (int i = 0; i < ts.columnNames.size(); i++) {
			maxCorrelathioPerColumn = 0.0f;
			scaleXValues = ts.getColumnByIndex(i);
			firstFeatureName = ts.columnNames.get(i);
			for (int j = i + 1; j < ts.columnNames.size(); j++) {
				float pearson = Math.abs(StatLib.pearson(ts.getColumnByIndex(i), ts.getColumnByIndex(j)));
				if (correlationThreshold < pearson && pearson > maxCorrelathioPerColumn) {
					maxCorrelathioPerColumn = pearson;
					scaleYValues = ts.getColumnByIndex(j);
					secondFeatureName = ts.columnNames.get(j);
				}
			}
			if (maxCorrelathioPerColumn != 0.0f) {
				for (int k = 0; k < points.length; k++) {
					points[k] = new Point(scaleXValues[k], scaleYValues[k]);
				}
				Line linearRegLine = StatLib.linear_reg(points);
				for (Point p : points) {
					if (StatLib.dev(p, linearRegLine) > maxDistance) {
						maxDistance = StatLib.dev(p, linearRegLine);
					}
				}
				addCorrelated(firstFeatureName, secondFeatureName, linearRegLine);
			}

		}
	}

	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		List<AnomalyReport> anomalyReports = new ArrayList<AnomalyReport>();
		for (CorrelatedFeatures cf : correlatedFeatureList) {
			float[] scaleXValues = ts.getColumnByName(cf.feature1);
			float[] scaleYValues = ts.getColumnByName(cf.feature2);
			int timeStep = 0;
			for (int i = 0; i < ts.getNumberOfItemsInColumn(); i++) {
				Point point = new Point(scaleXValues[i], scaleYValues[i]);
				float pointDistance = StatLib.dev(point, cf.lin_reg);
				if (pointDistance > cf.threshold) {
					timeStep = i +1;
					String description = cf.feature1 + "-" + cf.feature2;
					AnomalyReport anomalyReport = new AnomalyReport(description, timeStep);
					anomalyReports.add(anomalyReport);
				}
			}
		}
		return anomalyReports;
	}

	public List<CorrelatedFeatures> getNormalModel() {
		return correlatedFeatureList;
	}
	
	private void addCorrelated(String xName, String yName, Line linearRegLine) {
		CorrelatedFeatures cFeatures = new CorrelatedFeatures(xName, yName, maxCorrelathioPerColumn,
				linearRegLine, maxDistance * 1.1f);
		correlatedFeatureList.add(cFeatures);
	}
}
