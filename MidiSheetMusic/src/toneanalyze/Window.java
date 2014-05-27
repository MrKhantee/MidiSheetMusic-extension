package toneanalyze;

public class Window {
//	public static final int RECTANGULAR = 0;
//	public static final int BARTLETT = 1;
//	public static final int HANNING = 2;
//	public static final int HAMMING = 3;
//	public static final int BLACKMAN = 4;

	private double[] windows;;
	private int windowSize;
	private WindowType windowType; // default rectangular window

	/**
	 * @author baran
	 * @param nSamples size of array with data to be windowed
	 */
	public Window(int nSamples) {
		windows = generateWindow(nSamples);
		windowSize = nSamples;
		this.windowType = WindowType.RECTANGULAR;
	}

	/**
	 * @param nSamples size of array with data to be windowed  
	 * @param windowType type of window function
	 */
	public Window(int nSamples, WindowType windowType) {
		windows = generateWindow(nSamples);
		windowSize = nSamples;
		this.windowType = windowType;
	}

	private double[] generateWindow(int samples) {
		// generate nSamples window function values
		// for index values 0 .. nSamples - 1
		int m = samples / 2;
		double r;
		double pi = Math.PI;
		double[] w = new double[samples];
		switch (windowType) {
		case BARTLETT: // Bartlett (triangular) window
			for (int n = 0; n < samples; n++)
				w[n] = 1.0f - Math.abs(n - m) / m;
			break;
		case HANN: // Hanning window
			r = pi / (m + 1);
			for (int n = -m; n < m; n++)
				w[m + n] = 0.5f + 0.5f * Math.cos(n * r);
			break;
		case HAMMING: // Hamming window
			r = pi / m;
			for (int n = -m; n < m; n++)
				w[m + n] = 0.54f + 0.46f * Math.cos(n * r);
			break;
		case BLACKMAN: // Blackman window
			r = pi / m;
			for (int n = -m; n < m; n++)
				w[m + n] = 0.42f + 0.5f * Math.cos(n * r) + 0.08f
						* Math.cos(2 * n * r);
			break;
		case RECTANGULAR: // as a default
		default: // Rectangular window function
			for (int n = 0; n < samples; n++)
				w[n] = 1.0f;
		}

		return w;
	}

	/**
	 * apply window function on data in array x
	 * 
	 * @param x
	 *            array with samples to be windowed
	 * @return windowed array
	 */
	public double[] doWindow(double[] x) {
		if (x.length == windowSize) {
			for (int i = 0; i < x.length; i++) {
				x[i] *= windows[i];
			}
		} else {
			throw new IllegalArgumentException(
					"Window is not applicable to this number of samples");
		}

		return x;
	}
}
