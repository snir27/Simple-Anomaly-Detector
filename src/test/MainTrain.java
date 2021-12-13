package test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

public class MainTrain {
	
	static Random r=new Random();

	// this is a simple test to put you on the right track
	static void generateTrainCSV(float a1,float b1, float a2, float b2){
		try {
			PrintWriter out=new PrintWriter(new FileWriter("trainFile1.csv"));
			out.println("A,B,C,D");
			Line ac=new Line(a1,b1);
			Line bd=new Line(a2,b2);
			for(int i=1;i<=100;i++){
				float a=i;
				float b=r.nextInt(40);
				out.println(a+","+b+","+(ac.f(a)-0.02+(r.nextInt(40))/100.0f)+","+(bd.f(b)-0.02+(r.nextInt(40))/100.0f));
			}
			out.close();
		}catch(IOException e) {}
	}

	static void generateTestCSV(float a1,float b1, float a2, float b2, int anomaly){
		try {
			PrintWriter out=new PrintWriter(new FileWriter("testFile1.csv"));
			out.println("A,B,C,D");
			Line ac=new Line(a1,b1);
			Line bd=new Line(a2,b2);
			for(int i=1;i<=100;i++){
				float a=i;
				float b=r.nextInt(40);
				if(i!=anomaly)
					out.println(a+","+b+","+(ac.f(a)-0.02+(r.nextInt(40))/100.0f)+","+(bd.f(b)-0.02+(r.nextInt(40))/100.0f));
				else
					out.println(a+","+b+","+(ac.f(a)+1)+","+(bd.f(b)-0.02+(r.nextInt(40))/100.0f));
			}
			out.close();
		}catch(IOException e) {}
	}

	static void checkCorrelationTrain(CorrelatedFeatures c,String f1, String f2, float a, float b){
		if(c.feature1.equals(f1)){
			if(!c.feature2.equals(f2))
				System.out.println("wrong correlated feature of "+f1+" (-20)");
			else{
				if(c.corrlation<0.99)
					System.out.println(f1+"-"+f2+" wrong correlation detected (-5)");
				if(c.lin_reg.a<a-0.5f || c.lin_reg.a>a+0.5f)
					System.out.println(f1+"-"+f2+" wrong value of line_reg.a (-5)");
				if(c.lin_reg.b<b-0.5f || c.lin_reg.b>b+0.5f)
					System.out.println(f1+"-"+f2+" wrong value of line_reg.b (-5)");
				if(c.threshold>0.3)
					System.out.println(f1+"-"+f2+" wrong threshold detected (-5)");
			}
		}

	}
	
	
	public static void main(String[] args) {
		float a1=1+r.nextInt(10), b1=-50+r.nextInt(100);
		float a2=1+r.nextInt(20) , b2=-50+r.nextInt(100);


		// test the learned model: (40 points)
		// expected correlations:
		//	A-C: y=a1*x+b1
		//	B-D: y=a2*x+b2

		generateTrainCSV(a1,b1,a2,b2);
		TimeSeries ts=new TimeSeries("trainFile1.csv");
		SimpleAnomalyDetector ad=new SimpleAnomalyDetector();
		ad.learnNormal(ts);
		List<CorrelatedFeatures> cf=ad.getNormalModel();

		if(cf.size()!=2)
			System.out.println("wrong size of correlated features (-40)");
		else
			for(CorrelatedFeatures c : cf) {
				checkCorrelationTrain(c,"A","C",a1,b1); // 20 points
				checkCorrelationTrain(c,"B","D",a2,b2); // 20 points
			}

		// test the anomaly detector: (60 points)
		// one simply anomaly is injected to the data
		int anomaly=5+r.nextInt(90); // one anomaly injected in a random time step
		generateTestCSV(a1,b1,a2,b2,anomaly);
		TimeSeries ts2=new TimeSeries("testFile1.csv");
		List<AnomalyReport> reports = ad.detect(ts2);

		boolean anomlyDetected=false;
		int falseAlarms=0;
		for(AnomalyReport ar : reports) {
			if(ar.description.equals("A-C") && ar.timeStep == anomaly)
				anomlyDetected=true;
			else
				falseAlarms++;
		};

		if(!anomlyDetected)
			System.out.println("the anomaly was not detected (-30)");

		if(falseAlarms>0)
			System.out.println("you have "+falseAlarms+" false alarms (-"+Math.min(30,falseAlarms*3)+")");

		

		System.out.println("done");
	}

}
