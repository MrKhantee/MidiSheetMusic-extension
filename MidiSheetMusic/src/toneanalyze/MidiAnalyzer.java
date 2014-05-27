package toneanalyze;

/**
 * @author baran
 * Class MidiAnalyzer can detect if frequency is equal or into toleration interval with frequency of midi note
 */
public class MidiAnalyzer {
	private double[] table;
	double a;
	double toleration;
	
	/**
	 * construct base midi analyzer with a=440Hz and 2% toleration
	 */
	public MidiAnalyzer(){
		this(440,2);
	}
	
	
	/**
	 * @param baseFrequency
	 * @param toleration
	 */
	public MidiAnalyzer(double baseFrequency, int toleration){
		this.a = baseFrequency;
		this.toleration = (double) toleration/100;
		table = new double[128];
		calcTable();
	}
	
	private void calcTable(){
		double ca;
		for(int i=0;i<table.length;i++){
			ca = (double) (i-69);
			table[i] = a*Math.pow(2, ca/12);
		}
	}
	
	public void print(){
		for(int i=0;i<table.length;i++){
			System.out.print(i);
			System.out.print("\t");
			System.out.println(table[i]);
		}
	}
	
	public double getFrequency(int midiNumber){
		return table[midiNumber];
	}
	
	/**
	 * @return toleration of midi analyzer in %
	 */
	public int getToleration(){
		return (int) toleration*100;
	}
	
	/**
	 * check if requested frequency is into toleration interval of midi tone frequency
	 * @param midiNumber
	 * @param frequency
	 * @return 
	 */
	public boolean checkTone(int midiNumber, double frequency){
		double freq = table[midiNumber]; 
		double min = freq - freq*toleration;
		double max = freq + freq*toleration;
		
		if((frequency >= min) && (frequency <= max))
			return true;
		
		return false;
	}
	
	/**
	 * check if requested frequency is into toleration interval of midi tone frequency
	 * @param midiNumber
	 * @param tone
	 * @return
	 */
	public boolean checkTone(int midiNumber, SoundTone tone){
		return checkTone(midiNumber, tone.getFrequency());
	}
}