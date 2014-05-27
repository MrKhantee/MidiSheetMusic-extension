package toneanalyze;

/**
 * @author baran
 * SoundTone class represents tone with it frequency and magnitude
 */
public class SoundTone {
	private double frequency;
	private double magnitude;
	
	/**
	 * Constructor for SoundTone object
	 * @param frequency frequency of tone
	 * @param magnitude magnitude of tone
	 */
	public SoundTone(double frequency, double magnitude){
		this.frequency = frequency;
		this.magnitude = magnitude;
	}

	/**
	 * @return frequency of tone
	 */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * @return magnitude of tone
	 */
	public double getMagnitude() {
		return magnitude;
	}
}
