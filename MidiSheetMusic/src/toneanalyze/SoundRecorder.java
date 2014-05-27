package toneanalyze;

import java.util.ArrayList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;

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
	
	private ArrayList<ToneTime> toneTimes = null;
	
	private boolean recording;
	private BackgroundRecordingTask backgroundRecordingTask;
	
	private class ToneTime {
		private SoundTone tone;
		
		private double startTime;
		
		private double endTime;

		public SoundTone getTone() {
			return tone;
		}

		public void setTone(SoundTone tone) {
			this.tone = tone;
		}

		public double getStartTime() {
			return startTime;
		}

		public void setStartTime(double startTime) {
			this.startTime = startTime;
		}

		public double getEndTime() {
			return endTime;
		}

		public void setEndTime(double endTime) {
			this.endTime = endTime;
		}
	}
	
	
	private static SoundRecorder instance = null;
	public static SoundRecorder getInstance() {
		if ( instance == null ) {
			instance = new SoundRecorder();
		}
		
		return instance;
	}
	
	/**
	 * construct SoundRecorder with 8kHz sample rate and 256bytes to read
	 */
	private SoundRecorder(){
		this(8000,256);
	}
	
	/**
	 * construct Sound Recorder with specified sample rate and number of data to read
	 * @param sampleRate
	 * @param dataReadSize
	 */
	private SoundRecorder(int sampleRate, int dataReadSize){
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
	
	public void startRecording(){
		recording = true;
		toneTimes = new ArrayList<SoundRecorder.ToneTime>();
		
		backgroundRecordingTask = new BackgroundRecordingTask();
		backgroundRecordingTask.execute( null, null, null );
	}
	
	public void stopRecording(){
		recording = false;
		backgroundRecordingTask.cancel(true);
	}
	
	private class BackgroundRecordingTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			while ( SoundRecorder.this.recording ) {
				SoundRecorder.this.addToneFromMic();
			}
			return null;
		}
	
		
	}
	
	public SoundTone getTone(int timeOfPuleStaart,  int pulseTime) {
		// TODO: take one from the toneTime list (after it is processed)
		return null;
	}
	
	/**
	 * @return analyzed tone with its frequency and magnitude
	 */
	private void addToneFromMic(){
		short[] buffer = new short[dataReadSize];
		double[] toAnalyze = new double[dataReadSize];
		int bufferReadResult;
		
		ToneTime toneTime = new ToneTime();
		toneTime.setStartTime( System.currentTimeMillis() );
		audioRecord.startRecording();
		
		//read data from audio record
		bufferReadResult = audioRecord.read(buffer, 0, dataReadSize);
		
		audioRecord.stop();
		
		for (int i = 0; i < dataReadSize && i < bufferReadResult; i++) {
			toAnalyze[i] = (double) buffer[i] / 32768.0; // signed 16bit conversion
		}
		
		SoundTone tone = analyzer.getAnalyzedTone(toAnalyze);
		
		toneTime.setEndTime( System.currentTimeMillis() );
		toneTime.setTone( tone );
		
		toneTimes.add(toneTime);
	}	
}
