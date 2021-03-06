package stackquestions.mitacsinternship;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RecordingActivity extends AppCompatActivity {

    int samplingRate = 8000, numberChannels = 1, audioEncoding = AudioFormat.ENCODING_PCM_16BIT,
    recordingTime = 5, numberImpulses = 2, threshold = 0, samplesPerPoint = 32;
    TextView remainingImpulses, timeRemaining;
    Button startRecording, startPlayback;
    AudioRecord recorder;
    final CounterClass timer = new CounterClass(5000, 1000);
    double REFSPL = 0.00002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        remainingImpulses = (TextView) findViewById(R.id.recording_tv_impulses);
        timeRemaining = (TextView) findViewById(R.id.recording_tv_time);
        startRecording = (Button) findViewById(R.id.recording_button_start);
        startPlayback = (Button) findViewById(R.id.recording_button_playback);

        startRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CaptureAudio().execute();
            }
        });
    }

    private class CaptureAudio extends AsyncTask<Void, Integer, Integer>{
        @Override
        protected void onPreExecute() {
            int bufferSize = AudioRecord.getMinBufferSize(samplingRate, numberChannels, audioEncoding);
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, samplingRate, numberChannels,
                    audioEncoding, bufferSize);
            if (recorder.getState() != AudioRecord.STATE_INITIALIZED){
                Toast.makeText(RecordingActivity.this, "Cannot initialize recording",
                        Toast.LENGTH_SHORT).show();
                recorder.release();
                recorder = null;
            }
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int remainingImpulses = numberImpulses;
            int detectBufferLength = 256;
            int sampleBufferLength = nearestPow2Length(recordingTime * samplingRate);
            short[] detectBuffer = new short[detectBufferLength];
            short[][] sampleBuffer = new short[remainingImpulses][sampleBufferLength];
            recorder.startRecording();
            while (remainingImpulses > 0){
                publishProgress(-1, -1);
                int samplesRead = 0;
                while (samplesRead < detectBufferLength) samplesRead +=
                        recorder.read(detectBuffer, samplesRead, detectBufferLength - samplesRead);
                if (detectImpulse(detectBuffer)){
                    remainingImpulses --;
                    publishProgress(0, remainingImpulses);
                    System.arraycopy(detectBuffer, 0, sampleBuffer[remainingImpulses], 0, detectBufferLength);
                    samplesRead = detectBufferLength;
                    while (samplesRead < sampleBufferLength) samplesRead +=
                            recorder.read(sampleBuffer[remainingImpulses], samplesRead, sampleBufferLength - samplesRead);
                }
                if (isCancelled()) {
                    detectBuffer = null;
                    sampleBuffer = null;
                    return -1;
                }
            }
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
            // save recorded audio to an external file
             saveRecord(sampleBuffer, sampleBufferLength);

            // normalizing the data
            double[][] samples = normalizeData(sampleBuffer, sampleBufferLength);

            // apply Basic smoothing window
            applyBasicWindow(samples, sampleBufferLength);

            // perform fft on the signal
            int error = doubleFFT(samples, sampleBufferLength);
            if (error == -1) {sampleBuffer = null; return -1;}

            // average the dataset here
            double[] toStorage = averagedDataset(samples, sampleBufferLength);

            // Averaging the values over 32 points
            // tempBuffer stores the averaged time Domain values for all impulses
            int width = toStorage.length / samplesPerPoint / 2;
            double[] tempBuffer = new double[width];
            double maxYval = graphAveragedTimeDomain(tempBuffer, toStorage);
            System.out.println("The maximum Y value obtained from the graph is : " + maxYval);

            // tempImpBuffer stores the averaged frequency Domain values for each impulse
            double[] tempImpBuffer = new double[width];
            // maxTemp has to be returned as an array of values (number of Impulses)
            double[] maxTemp = graphAveragedFrequencyDomain(tempImpBuffer, samples);

            // Perform Digital Signal Processing here
            dsp();

            return null;
        }

        // Perform Digital Signal Processing here
        // Perform 1. Ratio Background Noise, 2. Percentage Worse Case
        public void dsp(){

        }

        // Reduces the number of data points in the frequency domain
        public double[] graphAveragedFrequencyDomain(double[] tempImpBuffer, double[][] samples){
            double[] maxTemp = new double[numberImpulses];
            for (int i=0; i<numberImpulses; i++){
                maxTemp[i] = 0;
                for (int k=0; k<tempImpBuffer.length; k++) {
                    for (int n = 0; n < samplesPerPoint; n++)
                        tempImpBuffer[k] += (samples[i][k * samplesPerPoint + n]) / REFSPL;
                    if (maxTemp[i] < tempImpBuffer[k]) maxTemp[i] = tempImpBuffer[k];
                }
                ByteBuffer byteImpBuffer = ByteBuffer.allocate(tempImpBuffer.length * 8);
                for (int k=0; k<tempImpBuffer.length; k++) byteImpBuffer.putDouble(tempImpBuffer[k]);
            }
            return maxTemp;
        }

        // Reduces the number of data points, the graph loads in an optimal time
        public double graphAveragedTimeDomain(double[] tempBuffer, double[] toStorage){
            double maxYval = 0;
            for (int k=0; k<tempBuffer.length; k++){
                for (int n=0; n<samplesPerPoint; n++)
                    tempBuffer[k] += toStorage[k*samplesPerPoint + n];
                tempBuffer[k] /= samplesPerPoint;
                if (tempBuffer[k] > maxYval) maxYval = tempBuffer[k];
            }
            return maxYval;
        }

        public double[] averagedDataset(double[][] samples, int sampleBufferLength){
            double[] toStorage = new double[sampleBufferLength];
            for (int k=0; k<numberImpulses; k++){
                for (int n=0; n<sampleBufferLength; n++)
                    toStorage[n] += samples[k][n] / REFSPL;
                for (int n=0; n<sampleBufferLength; n++)
                    toStorage[n] /= numberImpulses;
            }
            return toStorage;
        }

        public int doubleFFT(double[][] samples, int sampleBufferLength){
            double[] real = new double[sampleBufferLength], imag = new double[sampleBufferLength];
            for (int k=0; k<sampleBufferLength; k++){
                System.arraycopy(samples[k], 0, real, 0, sampleBufferLength);
                for (int n=0; n<sampleBufferLength; n++) imag[n] = 0;
                int error = FFTbase.fft(real, imag, true);
                for (int n=0; n<sampleBufferLength; n++) samples[k][n] =
                        Math.sqrt(real[n] * real[n] + imag[n] * imag[n]);
                if (isCancelled()) return -1;
            }
            return 0;
        }

        public void applyBasicWindow(double[][] samples, int sampleBufferLength){
            for (int i=0; i<numberImpulses; i++){
                samples[i][0] *= 0.0625;
                samples[i][1] *= 0.125;
                samples[i][2] *= 0.25;
                samples[i][3] *= 0.5;
                samples[i][4] *= 0.75;
                samples[i][5] *= 0.875;
                samples[i][6] *= 0.9375;

                samples[i][sampleBufferLength - 7] *= 0.9375;
                samples[i][sampleBufferLength - 6] *= 0.875;
                samples[i][sampleBufferLength - 5] *= 0.75;
                samples[i][sampleBufferLength - 4] *= 0.5;
                samples[i][sampleBufferLength - 3] *= 0.25;
                samples[i][sampleBufferLength - 2] *= 0.125;
                samples[i][sampleBufferLength - 1] *= 0.0625;
            }
        }

        public double[][] normalizeData(short[][] sampleBuffer, int sampleBufferLength){
            double[][] samples = new double[numberImpulses][sampleBufferLength];
            // normalizing time domain data
            for (int k=0; k<numberImpulses; k++){
                double max = 0;
                for (int n=0; n<sampleBufferLength; n++){
                    samples[k][n] = (double) sampleBuffer[k][n];
                    if (samples[k][n] > max) max = samples[k][n];
                }
                for (int h=0; h<sampleBufferLength; h++)
                    samples[k][h] /= max;
            }

            return samples;
        }

        public void saveRecord(short[][] sampleBuffer, int sampleBufferLength){
            File file = new File(Environment.getExternalStorageDirectory(), "recordedMedia.wav");
            if (file.exists()) file.delete();
            try {
                file.createNewFile();
            } catch (IOException e){
                System.out.println("Exception while creating new file of type : " + e.toString());
            }
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                for (int k=numberImpulses - 1; k>=0; k--)
                    for (int n=0; n<sampleBufferLength; n++)
                        dataOutputStream.writeShort(sampleBuffer[k][n]);
            } catch (IOException e){
                System.out.println("Exception while saving file of type : " + e.toString());
            }
        }

        public boolean detectImpulse(short[] detectBuffer){
            for (int k=0; k<detectBuffer.length; k++)
                if (detectBuffer[k] > threshold) return true;
            return false;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            if (recorder != null) {recorder.release(); recorder = null;}
            else {
                startPlayback.setVisibility(Button.VISIBLE);
                startPlayback.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        File file = new File(Environment.getExternalStorageDirectory(), "recordedMedia.wav");
                        short[] audio = new short[(int) (file.length() / 2)];
                        try {
                            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(
                                    new FileInputStream(file)));
                            int dataRead = 0;
                            while (dataInputStream.available() > 0)
                                audio[dataRead++] = dataInputStream.readShort();
                            dataInputStream.close();
                        } catch (IOException e) {
                            System.out.println("Exception while doing playback of type : " + e.toString());
                        }

                        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                samplingRate, numberChannels, audioEncoding, audio.length, AudioTrack.MODE_STREAM);
                        audioTrack.play();
                        audioTrack.write(audio, 0, audio.length);
                    }
                });
            }
            super.onPostExecute(integer);
        }

        public int nearestPow2Length(int length){
            return (int) Math.pow(2, (int) (Math.log(length) / Math.log(2) + 0.5));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] == 0) timer.start();
            if (values[1] != -1) remainingImpulses.setText("Number of impulses remaining : " +
                    values[1].toString());
        }
    }

    public class CounterClass extends CountDownTimer{
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            timeRemaining.setText((int)Math.ceil(l/1000) + "");
        }

        @Override
        public void onFinish() {
            timeRemaining.setText("Sound Capture Complete");
        }
    }
}