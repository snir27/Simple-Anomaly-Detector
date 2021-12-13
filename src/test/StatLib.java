package test;

public class StatLib {

	// simple average
	public static float avg(float[] x) {
		float sum = 0;
		for (float i : x) {
			sum += i;
		}
		float average = sum / x.length;
		return average;
	}

	// returns the variance of X and Y
	public static float var(float[] x) {
		int n = x.length;
		float variance = 0;
		for (int i = 0; i < x.length; i++) {
			variance += Math.pow(x[i] - avg(x), 2);
		}
		return (float) (variance / n);
	}

	// Returns the covariance of X and Y
	public static float cov(float[] x, float[] y) {
		float sum = 0;
		int n = x.length;
		for (int i = 0; i < n; i++) {
			sum += (x[i] - avg(x)) * (y[i] - avg(y));
		}
		float covariance = sum / n;

		return (float) covariance;
	}

	// Returns the Pearson correlation coefficient of X and Y
	public static float pearson(float[] x, float[] y) {
		float sdX = (float) Math.sqrt(var(x));
		float sdY = (float) Math.sqrt(var(y));
		return cov(x, y) / (float) (sdX * sdY);
	}

	// Performs a linear regression and returns the line equation
	public static Line linear_reg(Point[] points) {
		float a = 0, b = 0;
		int size = points.length;
		float[] xArr = new float[size];
		float[] yArr = new float[size];
		for (int i = 0; i < size; i++) {
			xArr[i] = points[i].x;
			yArr[i] = points[i].y;
		}
		a = cov(xArr, yArr) / var(xArr);
		b = avg(yArr) - (float) (a * avg(xArr));
		return new Line(a, b);
	}

	// returns the deviation between point p and the line equation of the points
	public static float dev(Point p, Point[] points) {
		Line line = linear_reg(points);
		float y = line.a * p.x - line.b;
		return y;
	}

	// returns the deviation between point p and the line
	public static float dev(Point p, Line l) {
		float y = l.f(p.x);
		float dev = Math.abs(p.y - y);
		return dev;
	}

}
