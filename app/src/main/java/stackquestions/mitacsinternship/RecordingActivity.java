package stackquestions.mitacsinternship;

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

public class RecordingActivity extends AppCompatActivity {

    int samplingRate = 8000, numberChannels = 1, audioEncoding = AudioFormat.ENCODING_PCM_16BIT,
    recordingTime = 5, numberImpulses = 2, threshold = 0;
    TextView remainingImpulses, timeRemaining;
    Button startRecording, startPlayback;
    AudioRecord recorder;
    final CounterClass timer = new CounterClass(5000, 1000);

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
            return null;
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
            timeRemaining.setText(l + "");
        }

        @Override
        public void onFinish() {
            timeRemaining.setText("Sound Capture Complete");
        }
    }
}