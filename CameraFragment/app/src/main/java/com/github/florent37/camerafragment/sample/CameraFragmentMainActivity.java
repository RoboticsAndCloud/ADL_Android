package com.github.florent37.camerafragment.sample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


import com.github.florent37.camerafragment.CameraFragment;
import com.github.florent37.camerafragment.CameraFragmentApi;
import com.github.florent37.camerafragment.configuration.Configuration;
import com.github.florent37.camerafragment.listeners.CameraFragmentControlsAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentStateAdapter;
import com.github.florent37.camerafragment.listeners.CameraFragmentVideoRecordTextAdapter;
import com.github.florent37.camerafragment.widgets.CameraSettingsView;
import com.github.florent37.camerafragment.widgets.CameraSwitchView;
import com.github.florent37.camerafragment.widgets.FlashSwitchView;
import com.github.florent37.camerafragment.widgets.MediaActionSwitchView;
import com.github.florent37.camerafragment.widgets.RecordButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.AudioRecorderAgent;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import omrecorder.AudioChunk;
import omrecorder.PullTransport;
import omrecorder.Recorder;


import java.text.DateFormat;
import java.util.concurrent.atomic.AtomicInteger;
//import java.util.logging.Handler;

// how to get dataFormat https://developer.android.com/reference/java/text/DateFormat.html

@SuppressLint("MissingPermission")
public class CameraFragmentMainActivity extends AppCompatActivity  implements SensorEventListener {

    public static final String FRAGMENT_TAG = "camera";
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;
    @Bind(R.id.settings_view)
    CameraSettingsView settingsView;
    @Bind(R.id.flash_switch_view)
    FlashSwitchView flashSwitchView;
    @Bind(R.id.front_back_camera_switcher)
    CameraSwitchView cameraSwitchView;
    @Bind(R.id.record_button)
    RecordButton recordButton;
    @Bind(R.id.photo_video_camera_switcher)
    MediaActionSwitchView mediaActionSwitchView;

    @Bind(R.id.record_duration_text)
    TextView recordDurationText;
    @Bind(R.id.record_size_mb_text)
    TextView recordSizeText;

    @Bind(R.id.cameraLayout)
    View cameraLayout;
    @Bind(R.id.addCameraButton)
    View addCameraButton;

    @Bind(R.id.addAudioButton)
    View addAudioButton;


    private SensorManager sm;
    private ScheduledExecutorService scheduler;
    private int count;



    long motionStartTime = System.currentTimeMillis();
    long motionElapsedTime = 0L;

    final int MOTION_RECORD_TIME = 2 * 1000; // 2 seconds
    final int OverAll_waitime = 10 * 1000;
    // final CameraFragmentApi cameraFragment = getCameraFragment();


    final String imageRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/ADL/Image/";
    public String timeStrForCollection = "";
    public String timeStr_image = "";
    public String timeStr_audio = "";
    public String timeStr_motion = "";
//    public String imageFileName = imageRoot + '/' + timeStrForCollection + ".jpg";
//    public String motionFilePath = MOTION_FILE_PATH + '/' + timeStrForCollection + "_motion.txt";
//    public String audioFilePath = AUDIO_FILE_PATH + '/' + timeStrForCollection + "_rec.wav";

    private static final int REQUEST_RECORD_AUDIO = 0;
    private static final String AUDIO_FILE_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/ADL/Audio/";

    private static final String MOTION_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/ADL/Motion/";
    private static final String BATTERY_INFO_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/ADL/Battery/";


    private final int AUDIO_RECORD_TIME = 2 * 1000; // 2 seconds

    private final int IMAGE_WAIT_TIME = 100 * 10;



    private final int request_image = 1;
    private final int request_audio = 2;
    private final int request_motion = 3;

//    private String ipsend = "192.168.1.134";
    private String ipsend = "192.168.0.101";

    private int port = 59000;

    private boolean cap_flag = false;


    private ServerThread serverThread;

    private static final int SERVER_PORT = 59100;


    public boolean send_flag = false;
    public class ServerThread extends Thread {
        private int serverPort;
        private Handler handler;

        public ServerThread(int serverPort, Handler handler) {
            this.serverPort = serverPort;
            this.handler = handler;
        }
        // 100 ms not working on the watch, run 30s then quit
        //  105 ms, not working on the watch, run 3 mins then quit
        // 110 ms, quit
        // 120 ms, quit, run 5 mins quit
        // 130 ms, quit, run 2 mins quit
        //
        @Override
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(serverPort);
                while (!Thread.currentThread().isInterrupted()) {
                    if (cap_flag == true) {
                        Message msg1 = handler.obtainMessage();
                        msg1.obj = String.valueOf(10);
                        handler.sendMessage(msg1);
                        sleep(180);
                        continue;

                    }

//                    Socket clientSocket = serverSocket.accept();
//                    sendConnectedMessage();
//
//                    DataInputStream input = new DataInputStream(clientSocket.getInputStream());
//                    int receivedInt = input.readInt();
//                    String message = String.valueOf(receivedInt);
//
//
//                    Message msg = handler.obtainMessage();
//                    msg.obj = message;
//                    handler.sendMessage(msg);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void sendConnectedMessage() {
            Message msg = handler.obtainMessage();
            msg.obj = "connected";
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camerafragment_activity_main);
        ButterKnife.bind(this);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);


        // Threads, create command receiver//


        // Microphone permission
        Util.requestPermission(this, Manifest.permission.RECORD_AUDIO);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String message = (String) msg.obj;
                handleMessageFromClient(message);

            }
        };
        serverThread = new ServerThread(SERVER_PORT, handler);
        serverThread.start();
    }
    private void handleMessageFromClient(String message) {
        if (message.equals("connected")) {
            Toast.makeText(getApplicationContext(), "Client connected", Toast.LENGTH_SHORT).show();
        } else {
            int receivedInt = Integer.parseInt(message);
            Toast.makeText(getApplicationContext(), "Received integer: " + receivedInt, Toast.LENGTH_SHORT).show();
            switch (receivedInt) {
                case 10:
                    capturePhoto();
//                    capturePhotoLocal();
                    break;
                case 11:
                    recordAudio();
//                    recordAudioLocal();
                    break;
                case 12:
                    recordMotionData();
//                    recordMotionDataLocal();
                    break;
                case 13:
                    collectData();
                default:
                    // Handle unrecognized integers if necessary
                    break;
            }
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }


    @OnClick(R.id.flash_switch_view)
    public void onFlashSwitcClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.toggleFlashMode();
        }
    }

    @OnClick(R.id.front_back_camera_switcher)
    public void onSwitchCameraClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchCameraTypeFrontBack();
        }
    }
    @OnClick(R.id.record_button)
    public void onRecordButtonClicked() {
        cap_flag = true;
        this.getBatteryInfo();
//        final Handler handler = new Handler();
//        final int delay = 2000; //milliseconds 2000->both motion and audio,>3000 only audio
//
//        handler.postDelayed(new Runnable(){
//            int count = 0;
//            public void run(){
//                collectData(); // execute the collect_data function
//                count++;
//                if (count < 100) {
//                    handler.postDelayed(this, delay);
//                } else {
//                    handler.removeCallbacks(this); // stop the loop
//                }
//            }
//        }, delay);
        //recordMotionData();


    }


    public void getBatteryInfo() {
        int battery_level = 0;
        int b_microamper_hour = 0;
        int b_current_now = 0;
        int b_current_avg = 0;

        if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            battery_level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            b_microamper_hour = bm.getIntProperty(BatteryManager.	BATTERY_PROPERTY_CHARGE_COUNTER);
            b_current_now = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            b_current_avg = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
        }

        android.text.format.DateFormat df1 = new android.text.format.DateFormat();
        String time_str = df1.format("yyyyMMddhhmmss", new java.util.Date()).toString();


        final String infoFilePath = BATTERY_INFO_FILE_PATH + '/'  + "battery_info.txt";


        String str_line = "time:" + time_str + '\t'
                + "battery:" + String.valueOf(battery_level) + "%" + '\t'
                + "b_microamper_hour:" + String.valueOf(b_microamper_hour) + '\t'
                + "b_current_now:" + String.valueOf(b_current_now) + '\t'
                + "b_current_avg:" + String.valueOf(b_current_avg) + '\t'
                + "\n";

        Toast.makeText(getBaseContext(), str_line, Toast.LENGTH_SHORT).show();

        saveToFile(str_line, infoFilePath);
    }



    public boolean checkFileExist(String file) {
        try {
            File myFile = new File(file);
            if (myFile.exists()) {
                return true;
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Image File not exist",
                    Toast.LENGTH_SHORT).show();
        }

        return false;
    }


    private void capturePhotoLocal() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        timeStr_image = df.format("yyyyMMddhhmmss", new java.util.Date()).toString();

        // if (cameraFragment != null) {
        cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                                                   @Override
                                                   public void onPhotoTaken(byte[] bytes, String filePath) {
                                                       Toast.makeText(getBaseContext(), "onPhotoTaken " + filePath, Toast.LENGTH_SHORT).show();
                                                   }
                                               },
                imageRoot,
                timeStr_image);
        // }

        final String imageFileName = imageRoot + '/' + timeStr_image + ".jpg";
//        while(checkFileExist(imageFileName) == false) {
//        }
//        cafe.adriel.androidaudiorecorder.Util.wait(IMAGE_WAIT_TIME, () -> {
////            new CommunicationImageService().execute(ipsend, String.valueOf(port), "1", timeStr_image, imageFileName);
//            System.out.println(imageFileName);
//            Toast.makeText(getBaseContext(), "Image Sent ", Toast.LENGTH_SHORT).show();
//        });
    }

    private void capturePhoto() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        timeStr_image = df.format("yyyyMMddhhmmss", new java.util.Date()).toString();

        String imageFileNameLocal = imageRoot + '/' + timeStr_image + ".jpg";

        int cnt = 0;
        while (checkFileExist(imageFileNameLocal) == true) {
            cnt += 1;
            timeStr_image = timeStr_image + "_" + String.valueOf(cnt);
            imageFileNameLocal = imageRoot + '/' + timeStr_image + ".jpg";
        }

        // if (cameraFragment != null) {
        cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultAdapter() {
                                                   @Override
                                                   public void onPhotoTaken(byte[] bytes, String filePath) {
                                                       Toast.makeText(getBaseContext(), "onPhotoTaken " + filePath, Toast.LENGTH_SHORT).show();
                                                   }
                                               },
                imageRoot,
                timeStr_image);
        // }

        timeStr_image = df.format("yyyyMMddhhmmss", new java.util.Date()).toString();
        final String imageFileName = imageRoot + '/' + timeStr_image + ".jpg";
        cafe.adriel.androidaudiorecorder.Util.wait(IMAGE_WAIT_TIME, () -> {
            new CommunicationImageService().execute(ipsend, String.valueOf(port), "1", timeStr_image, imageFileName);
            System.out.println(imageFileName);
            Toast.makeText(getBaseContext(), "Image Sent ", Toast.LENGTH_SHORT).show();
        });
    }



    private void recordMotionDataLocal() {
        android.text.format.DateFormat df1 = new android.text.format.DateFormat();
        timeStr_motion = df1.format("yyyyMMddhhmmss", new java.util.Date()).toString();
        final String motionFilePath = MOTION_FILE_PATH + '/' + timeStr_motion + "_motion.txt";
        //long motionStartTime = System.currentTimeMillis();
        //cafe.adriel.androidaudiorecorder.Util.wait(1, () -> startSensor());
        startSensor();
        cafe.adriel.androidaudiorecorder.Util.wait(MOTION_RECORD_TIME, () -> {
            stopSensor();
//            new CommunicationMotionService().execute(ipsend, String.valueOf(port), timeStr_motion, motionFilePath);
//
//            Toast.makeText(getBaseContext(), "Motion " + motionFilePath, Toast.LENGTH_SHORT).show();

        });
        long motionStartTime = System.currentTimeMillis();
        long motionElapsedTime = 0L;
        motionElapsedTime = (new Date()).getTime() - motionStartTime;
    }


    private void recordMotionData() {
        android.text.format.DateFormat df1 = new android.text.format.DateFormat();
        timeStr_motion = df1.format("yyyyMMddhhmmss", new java.util.Date()).toString();
        final String motionFilePath = MOTION_FILE_PATH + '/' + timeStr_motion + "_motion.txt";
        //long motionStartTime = System.currentTimeMillis();
        //cafe.adriel.androidaudiorecorder.Util.wait(1, () -> startSensor());
        startSensor();
        cafe.adriel.androidaudiorecorder.Util.wait(MOTION_RECORD_TIME, () -> {
            stopSensor();
            new CommunicationMotionService().execute(ipsend, String.valueOf(port), timeStr_motion, motionFilePath);

            Toast.makeText(getBaseContext(), "Motion " + motionFilePath, Toast.LENGTH_SHORT).show();

        });
        long motionStartTime = System.currentTimeMillis();
        long motionElapsedTime = 0L;
        motionElapsedTime = (new Date()).getTime() - motionStartTime;
    }

    private void recordAudioLocal() {
        android.text.format.DateFormat df2 = new android.text.format.DateFormat();
        timeStr_audio = df2.format("yyyyMMddhhmmss", new java.util.Date()).toString();
        final String audioFilePath = AUDIO_FILE_PATH + '/' + timeStr_audio + "_rec.wav";
        try {
            File dir = new File(AUDIO_FILE_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            Log.w("creating file error", e.toString());
        }

        final AudioRecorderAgent audioAgent = new AudioRecorderAgent().setFilePath(audioFilePath)
                .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                .setSource(AudioSource.MIC)
                //.setFilePath(audioFilePath)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_44100)
                .setAutoStart(false)
                .setKeepDisplayOn(true);

        audioAgent.resumeRecordingWithDuration();

        cafe.adriel.androidaudiorecorder.Util.wait(AUDIO_RECORD_TIME, () -> {
            audioAgent.pauseRecording();
            audioAgent.stopRecording();
//            new CommunicationAudioService().execute(ipsend, String.valueOf(port), timeStr_audio, audioFilePath);
        });
    }

    private void recordAudio() {
        android.text.format.DateFormat df2 = new android.text.format.DateFormat();
        timeStr_audio = df2.format("yyyyMMddhhmmss", new java.util.Date()).toString();
        final String audioFilePath = AUDIO_FILE_PATH + '/' + timeStr_audio + "_rec.wav";
        try {
            File dir = new File(AUDIO_FILE_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            Log.w("creating file error", e.toString());
        }

        final AudioRecorderAgent audioAgent = new AudioRecorderAgent().setFilePath(audioFilePath)
                .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                .setSource(AudioSource.MIC)
                //.setFilePath(audioFilePath)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_44100)
                .setAutoStart(false)
                .setKeepDisplayOn(true);

        audioAgent.resumeRecordingWithDuration();

        cafe.adriel.androidaudiorecorder.Util.wait(AUDIO_RECORD_TIME, () -> {
            audioAgent.pauseRecording();
            audioAgent.stopRecording();
            new CommunicationAudioService().execute(ipsend, String.valueOf(port), timeStr_audio, audioFilePath);
        });
    }
    public void collectData()  {

        capturePhoto();
//        recordMotionData();
//        recordAudio();
    }

















    @OnClick(R.id.settings_view)
    public void onSettingsClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.openSettingDialog();
        }
    }

    @OnClick(R.id.photo_video_camera_switcher)
    public void onMediaActionSwitchClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchActionPhotoVideo();
        }
    }

    @OnClick(R.id.addCameraButton)
    public void onAddCameraClicked() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            final String[] permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
            } else addCamera();
        } else {
            addCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            addCamera();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() {
        addCameraButton.setVisibility(View.GONE);
        addAudioButton.setVisibility(View.GONE);

        cameraLayout.setVisibility(View.VISIBLE);

        final CameraFragment cameraFragment = CameraFragment.newInstance(new Configuration.Builder()
                .setCamera(Configuration.CAMERA_FACE_REAR).setMediaQuality(Configuration.MEDIA_QUALITY_LOWEST).build()); // set quality
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();

        if (cameraFragment != null) {

            cameraFragment.setStateListener(new CameraFragmentStateAdapter() {

                @Override
                public void onCurrentCameraBack() {
                    cameraSwitchView.displayBackCamera();
                }

                @Override
                public void onCurrentCameraFront() {
                    cameraSwitchView.displayFrontCamera();
                }

                @Override
                public void onFlashAuto() {
                    flashSwitchView.displayFlashAuto();
                }

                @Override
                public void onFlashOn() {
                    flashSwitchView.displayFlashOn();
                }

                @Override
                public void onFlashOff() {
                    flashSwitchView.displayFlashOff();
                }

                @Override
                public void onCameraSetupForPhoto() {
                    mediaActionSwitchView.displayActionWillSwitchVideo();

                    recordButton.displayPhotoState();
                    flashSwitchView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCameraSetupForVideo() {
                    mediaActionSwitchView.displayActionWillSwitchPhoto();

                    recordButton.displayVideoRecordStateReady();
                    flashSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void shouldRotateControls(int degrees) {
                    ViewCompat.setRotation(cameraSwitchView, degrees);
                    ViewCompat.setRotation(mediaActionSwitchView, degrees);
                    ViewCompat.setRotation(flashSwitchView, degrees);
                    ViewCompat.setRotation(recordDurationText, degrees);
                    ViewCompat.setRotation(recordSizeText, degrees);
                }

                @Override
                public void onRecordStateVideoReadyForRecord() {
                    recordButton.displayVideoRecordStateReady();
                }

                @Override
                public void onRecordStateVideoInProgress() {
                    recordButton.displayVideoRecordStateInProgress();
                }

                @Override
                public void onRecordStatePhoto() {
                    recordButton.displayPhotoState();
                }

                @Override
                public void onStopVideoRecord() {
                    recordSizeText.setVisibility(View.GONE);
                    //cameraSwitchView.setVisibility(View.VISIBLE);
                    settingsView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onStartVideoRecord(File outputFile) {
                }
            });

            cameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                @Override
                public void lockControls() {
                    cameraSwitchView.setEnabled(false);
                    recordButton.setEnabled(false);
                    settingsView.setEnabled(false);
                    flashSwitchView.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    cameraSwitchView.setEnabled(true);
                    recordButton.setEnabled(true);
                    settingsView.setEnabled(true);
                    flashSwitchView.setEnabled(true);
                }

                @Override
                public void allowCameraSwitching(boolean allow) {
                    cameraSwitchView.setVisibility(allow ? View.VISIBLE : View.GONE);
                }

                @Override
                public void allowRecord(boolean allow) {
                    recordButton.setEnabled(allow);
                }

                @Override
                public void setMediaActionSwitchVisible(boolean visible) {
                    mediaActionSwitchView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                }
            });

            cameraFragment.setTextListener(new CameraFragmentVideoRecordTextAdapter() {
                @Override
                public void setRecordSizeText(long size, String text) {
                    recordSizeText.setText(text);
                }

                @Override
                public void setRecordSizeTextVisible(boolean visible) {
                    recordSizeText.setVisibility(visible ? View.VISIBLE : View.GONE);
                }

                @Override
                public void setRecordDurationText(String text) {
                    recordDurationText.setText(text);
                }

                @Override
                public void setRecordDurationTextVisible(boolean visible) {
                    recordDurationText.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopSensor();
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Acceleration capturedAcceleration = getAccelerationFromSensor(event);
        String motion_file_path = MOTION_FILE_PATH + '/' + timeStr_motion + "_motion.txt";

        motionElapsedTime = (new Date()).getTime() - motionStartTime;
        if (motionElapsedTime > MOTION_RECORD_TIME) {  // 2 seconds
            TextView acceleration = (TextView) findViewById(R.id.acceleration);
            acceleration.setText("X:" + capturedAcceleration.getX() +
                    "\nY:" + capturedAcceleration.getY() +
                    "\nZ:" + capturedAcceleration.getZ() +
                    "\nTimestamp:" + "stop");



//            stopSensor();

            return;
        }

        updateTextView(capturedAcceleration);
        String str_line = "" + capturedAcceleration.getX() + "\t" + capturedAcceleration.getY() + "\t" + capturedAcceleration.getZ() + "\n";

        saveToFile(str_line, motion_file_path);
//        sendDataToCassandra(capturedAcceleration);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do nothing
    }

    private void startSensor() {
        motionStartTime = System.currentTimeMillis();
        Sensor accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopSensor() {
        sm.unregisterListener(this);
    }

    /**
     * Update acceleration text view with new values.
     *
     * @param capturedAcceleration
     */
    private void updateTextView(Acceleration capturedAcceleration) {
        TextView acceleration = (TextView) findViewById(R.id.acceleration);
        acceleration.setText("X:" + capturedAcceleration.getX() +
                "\nY:" + capturedAcceleration.getY() +
                "\nZ:" + capturedAcceleration.getZ() +
                "\nTimestamp:" + capturedAcceleration.getTimestamp());

        String str = "X:" + capturedAcceleration.getX() +
                "\nY:" + capturedAcceleration.getY() +
                "\nZ:" + capturedAcceleration.getZ() +
                "\nTimestamp:" + capturedAcceleration.getTimestamp();

//        Toast.makeText(getBaseContext(), "acc  " + str, Toast.LENGTH_SHORT).show();
    }


    /**
     * Get accelerometer sensor values and map it into an acceleration model.
     *
     * @param event
     * @return an acceleration model.
     */
    private Acceleration getAccelerationFromSensor(SensorEvent event) {
        long timestamp = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
        return new Acceleration(event.values[0], event.values[1], event.values[2], timestamp);
    }


    public void recordAudio(View v) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String timeStr = df.format("yyyy-MM-dd-hh:mm:ss", new java.util.Date()).toString();

        String file_path = AUDIO_FILE_PATH + '/' + timeStr + "_rec.wav";
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(file_path)
                .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
                .setRequestCode(REQUEST_RECORD_AUDIO)

                // Optional
                .setSource(AudioSource.MIC)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_44100)
                .setAutoStart(false)
                .setKeepDisplayOn(true)

                // Start recording
                .record();
    }


    public void recordAudioAuto() {

        long motionStartTime = System.currentTimeMillis();
        long motionElapsedTime = 0L;
        motionElapsedTime = (new Date()).getTime() - motionStartTime;

        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String timeStr = df.format("yyyy-MM-dd-hh:mm:ss", new java.util.Date()).toString();

        String file_path = AUDIO_FILE_PATH + '/' + timeStr + "_rec.wav";

        AudioRecorderAgent audioAgent = new AudioRecorderAgent().setFilePath(file_path)
                .setColor(ContextCompat.getColor(this, R.color.recorder_bg))
//                .setRequestCode(REQUEST_RECORD_AUDIO)

                // Optional
                .setSource(AudioSource.MIC)
                .setFilePath(file_path)
                .setChannel(AudioChannel.STEREO)
                .setSampleRate(AudioSampleRate.HZ_44100)
                .setAutoStart(false)
                .setKeepDisplayOn(true);


        audioAgent.resumeRecordingWithDuration();





    }



    public void saveToFile(String text, String file) {


        try {
            File dir = new File(MOTION_FILE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        catch(Exception e){
            Log.w("creating file error", e.toString());
        }


        try {
            File myFile = new File(file);
            if (!myFile.exists()) {
                myFile.createNewFile();
            }

            FileOutputStream fOut = new FileOutputStream(myFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(text);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(getBaseContext(),
                    "Done writing" + file,
                    Toast.LENGTH_SHORT).show();

            System.out.println("Writing:" + text + " to file:" + file);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }


    }
}