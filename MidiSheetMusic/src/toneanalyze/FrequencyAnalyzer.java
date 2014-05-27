package toneanalyze;

import ca.uol.aig.fftpack.RealDoubleFFT;

/**
 * @author baran
 * SoundAnalayzer class is used to analyze sound from input data
 */
public class FrequencyAnalyzer {
	
	private RealDoubleFFT fft;
	private Window window;
	private double[] toTransform;
	private double[] magnitudes;
	//private double frequency;
	private int fftSize;
	private int sampleRate;
	
	private boolean transformed;
	
	/**
	 * @param N number of bytes to transform and analyze
	 * @param sampleRate rate of sampling frequency
	 */
	public FrequencyAnalyzer(int N, int sampleRate){
		this(N,sampleRate,WindowType.RECTANGULAR);
	}
	

	/**
	 * @param N number of bytes to transform and analyze
	 * @param sampleRate rate of sampling frequency
	 * @param windowType type of window for window function
	 */
	public FrequencyAnalyzer(int N, int sampleRate, WindowType windowType){
		fftSize = N;
		fft = new RealDoubleFFT(N);
		window = new Window(N, windowType);
		toTransform = new double[N];
		magnitudes = new double[N/2];
//		frequency = -1;
		transformed = false;
	}
	
	/**
	 * transform data using fft
	 */
	private void transform(){
		transformed = true;
		fft.ft(toTransform);
	}
	
	/**
	 * count magnitudes for every sample
	 */
	private void countMagnitudes(){
		if(!transformed){
			transform();
		}
		
		for (int i = 0; i< (toTransform.length / 2); i++){
			double re = toTransform[2*i];
			double im = toTransform[2*i+1];
			magnitudes[i] = Math.sqrt(re*re + im*im);
		}
	}
	
	/**
	 * @param soundDdata
	 * @return tone with its frequency and magnitude
	 */
	public SoundTone getAnalyzedTone(double[] soundDdata){
		if(soundDdata.length != fftSize){
			throw new IllegalArgumentException(
					"Size of sound data buffer is not equal to FFT size");
		}
		
		toTransform = window.doWindow(soundDdata);
		transform();
		countMagnitudes();
		
		double max_magnitude = -1;
		int max_index = -1;
		
		//find maximal magnitude
		for (int i = 0; i < magnitudes.length; i++){
			if(magnitudes[i] > max_magnitude){
				max_index = i;
				max_magnitude = magnitudes[i];
			}
		}		
			
		return new SoundTone((max_index * sampleRate) / (fftSize), magnitudes[max_index]); 
		//Tone with calculated frequency and magnitude
	}
}
