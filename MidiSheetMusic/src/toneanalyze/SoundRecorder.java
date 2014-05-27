package toneanalyze;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * @author baran
 *
 */
public class SoundRecorder {
	private final int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
	private final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	
	private int sampleRate;	
	private int dataReadSize;
	private AudioRecord audioRecord;
	private FrequencyAnalyzer analyzer;
	
	private boolean recording;
	
	/**
	 * construct SoundRecorder with 8kHz sample rate and 256bytes to read
	 */
	public SoundRecorder(){
		this(8000,256);
	}
	
	/**
	 * construct Sound Recorder with specified sample rate and number of data to read
	 * @param sampleRate
	 * @param dataReadSize
	 */
	public SoundRecorder(int sampleRate, int dataReadSize){
		this.sampleRate = sampleRate;
		this.dataReadSize = dataReadSize;
		
		recording = false;
		
		analyzer = new FrequencyAnalyzer(dataReadSize, sampleRate, WindowType.HANN);
		
		int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
				channelConfiguration, audioEncoding);
		
		audioRecord = new AudioRecord(
				MediaRecorder.AudioSource.DEFAULT, sampleRate,
				channelConfiguration, audioEncoding, bufferSize);
	}
	
	private void startRecording(){
		try {
			audioRecord.startRecording();
			recording = true;
		} catch (IllegalStateException e) {
			Log.e("Failed start recording", e.toString());

		}
	}
	
	private void stopRecording(){
		try {
			audioRecord.stop();
			recording = false;
		} catch (IllegalStateException e) {
			Log.e("Failed stop recording", e.toString());

		}
	}
	
	/**
	 * @return analyzed tone with its frequency and magnitude
	 */
	public SoundTone getTone(){
		short[] buffer = new short[dataReadSize];
		double[] toAnalyze = new double[dataReadSize];
		int bufferReadResult;
		
		startRecording();
		if(recording){
			//read data from audio record
			bufferReadResult = audioRecord.read(buffer, 0, dataReadSize);
		}
		else return new SoundTone(0, 0);
		
		stopRecording();
		
		for (int i = 0; i < dataReadSize && i < bufferReadResult; i++) {
			toAnalyze[i] = (double) buffer[i] / 32768.0; // signed 16bit conversion
		}
		
		return analyzer.getAnalyzedTone(toAnalyze);
	}	
}
