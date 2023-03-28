package com.github.florent37.camerafragment.sample;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class CommunicationImageService extends AsyncTask<String, Void, Void> {
    private SocketChannel socketChannelBasic = null;
    private final int STATE_ADL_ACTIVITY_WMU_AUDIO = 15;
    private final int STATE_ADL_ACTIVITY_WMU_IMAGE = 16;
    private final int STATE_ADL_ACTIVITY_WMU_MOTION = 17;




    public static final int MAXLINE = 50000;
//    public static final int MAXPHOTO = 50000;
//    public static final int MAXTEXT = 10000;

    @Override
    protected Void doInBackground(String ...params) {

        //this method will be running on background thread so don't update UI frome here
        //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
        String ipsend = params[0];
        int port =  Integer.parseInt(params[1]);
        int cnt = Integer.parseInt(params[2]);
        String timeStrForCollection = params[3];
        String imageFileName = params[4];

        socketImageSendingHandler(ipsend, port, cnt, timeStrForCollection, imageFileName);


        return null;
    }

    public void socketMotionSendingHandler(String ipsend, int port, String currentTime, String filename) {
        Socket echoSocket;
        OutputStream sout;




        try {
            echoSocket = new Socket(ipsend, port);        // 1st statement
            sout = echoSocket.getOutputStream();
//            InputStream inputStream = echoSocket.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String command = reader.readLine();
//            System.out.println("Command is :" + command);
            sout.write(STATE_ADL_ACTIVITY_WMU_MOTION);
            System.out.println("Send data image type:" + STATE_ADL_ACTIVITY_WMU_MOTION);


            ByteBuffer bytebuf = ByteBuffer.allocate(14);
            System.out.println("cur len:" + currentTime.getBytes().length);

            bytebuf.put(currentTime.getBytes(), 0, currentTime.length());
            bytebuf.flip();

            sout.write(bytebuf.array());
            System.out.println("Cur time:" + currentTime.length());


            //  Send Audio
            File myFile = new File(filename);
            System.out.println("Image length:" + myFile.length());
            byte[] mybytearray = new byte[(int) myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);

            sout.write(mybytearray);


            sout.close();
            echoSocket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


        return;

    }

    public void socketAudioSendingHandler(String ipsend, int port, String currentTime, String filename) {
        Socket echoSocket;
        OutputStream sout;


        try {
            echoSocket = new Socket(ipsend, port);        // 1st statement
            sout = echoSocket.getOutputStream();
            sout.write(STATE_ADL_ACTIVITY_WMU_AUDIO);
            System.out.println("Send data image type:" + STATE_ADL_ACTIVITY_WMU_AUDIO);


            ByteBuffer bytebuf = ByteBuffer.allocate(14);
            System.out.println("cur len:" + currentTime.getBytes().length);

            bytebuf.put(currentTime.getBytes(), 0, currentTime.length());
            bytebuf.flip();

            sout.write(bytebuf.array());
            System.out.println("Cur time:" + currentTime.length());


            //  Send Audio
            File myFile = new File(filename);
            System.out.println("Image length:" + myFile.length());
            byte[] mybytearray = new byte[(int) myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);

            sout.write(mybytearray);


            sout.close();
            echoSocket.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


        return;

    }






    public void socketImageSendingHandler(String ipsend, int port, int cnt, String currentTime, String filename){
        Socket echoSocket;
        OutputStream sout;


        try {
            echoSocket = new Socket(ipsend, port);        // 1st statement
            echoSocket.setReuseAddress(true);
//            InputStream inputStream = echoSocket.getInputStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String command = reader.readLine();
//            System.out.println("Command is :" + command);
            sout = echoSocket.getOutputStream();
            sout.write(STATE_ADL_ACTIVITY_WMU_IMAGE);
            //System.out.println("Send data image type:" + STATE_ADL_ACTIVITY_WMU_IMAGE);


            ByteBuffer bytebuf = ByteBuffer.allocate(18);
            bytebuf.putInt(cnt);
            //System.out.println("bytebuf:" + bytebuf.array()[3]);
            //System.out.println("cur len:" + currentTime.getBytes().length);

            bytebuf.put(currentTime.getBytes(), 0, currentTime.length());
            bytebuf.flip();

            sout.write(bytebuf.array());
            //System.out.println("Cur time:" + currentTime.length());


            //  Send images
            File myFile = new File(filename);
            //System.out.println("Image length:" + myFile.length());
            byte[] mybytearray = new byte[(int) myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(mybytearray, 0, mybytearray.length);

//            bytebuf = ByteBuffer.allocate((int)(myFile.length()));
//
//            bytebuf.put(mybytearray, 0, (mybytearray.length));
//            bytebuf.flip();

            sout.write(mybytearray);

//            InputStream imageStream = getContentResolver().openInputStream(filename);
//            final Bitmap imageBitMap = BitmapFactory.decodeStream(imageStream);
//
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            imageBitMap.compress(Bitmap.CompressFormat.JPEG,0, bos);
//            byte[] array = bos.toByteArray();
//            sout.write(array);


//            Bitmap bmp=((BitmapDrawable)imageView.getDrawable()).getBitmap(); //String str = et.getText().toString();
//
//
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            bmp.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
//            byte[] array = bos.toByteArray();
//
//            BufferedImage image = ImageIO.read(new File(filename));
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            ImageIO.write(image, "jpg", byteArrayOutputStream);
//
//
//            sout.write(byteArrayOutputStream.toByteArray());
//            sout.flush();
//            System.out.println("Flushed: " + System.currentTimeMillis());


            sout.close();
            echoSocket.close();




        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // if scou != Null, scout.close()
        }



        return;

//
//
//        if (socketChannelBasic == null) {
//            System.out.println("Request Connection");
//            try {
//                socketChannelBasic = SocketChannel.open();
//                socketChannelBasic.configureBlocking(true);
//                SocketAddress socketAddress = new InetSocketAddress(ipsend, port);
//                if(socketChannelBasic.connect(socketAddress)==true) {
//
//                    System.out.println("Basic Socket Listener started");
//                }else {
//                    System.out.println("Connection failed");
//                    try {
//                        socketChannelBasic.close();
//                        System.out.println("Basic Socket Closed");
//                    } catch (IOException ee){
//                        ee.printStackTrace();
//                    }
//                }
//
//
//
//
//            } catch (IOException e) {
//                System.out.println("Connection Exception failed");
//                e.printStackTrace();
//                try {
//                    socketChannelBasic.close();
//                    System.out.println("Basic Socket Closed");
//                } catch (IOException ee){
//                    ee.printStackTrace();
//                }
//            }
//        }
//
//
//        try {
//            ByteArrayOutputStream data = new ByteArrayOutputStream();
//            DataOutputStream dos = new DataOutputStream(data);
//            dos.writeInt(STATE_ADL_ACTIVITY_WMU_IMAGE);
//            dos.flush();
//            byte [] packedData = data.toByteArray();
//
//
//            System.out.println("Send data:" + packedData[3]);
//            System.out.println("Send data len:" + packedData.length);
//
//
//            ByteBuffer bytebuf = ByteBuffer.allocate(MAXLINE);
//            bytebuf.put(packedData, 0, (packedData.length < MAXLINE ? packedData.length : MAXLINE));
//            bytebuf.flip();
//            socketChannelBasic.write(bytebuf);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            ByteArrayOutputStream data = new ByteArrayOutputStream();
//            DataOutputStream dos = new DataOutputStream(data);
//
//            dos.writeUTF(currentTime); // this includes a 16-bit unsigned length
//            dos.flush();
//
//            byte [] packedData = data.toByteArray();
//            System.out.println("Cur time:" + currentTime.length());
//            System.out.println("Send data:" + packedData);
//            System.out.println("Send data len:" + packedData.length);
//
//
//            ByteBuffer bytebuf = ByteBuffer.allocate(MAXLINE);
//            bytebuf.put(packedData, 0, (packedData.length < MAXLINE ? packedData.length : MAXLINE));
//            bytebuf.flip();
//            socketChannelBasic.write(bytebuf);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            // Send images
//            File myFile = new File(filename);
////            System.out.println("Image length:" + myFile.length());
////            byte[] mybytearray = new byte[(int) myFile.length()];
////            FileInputStream fis = new FileInputStream(myFile);
////            BufferedInputStream bis = new BufferedInputStream(fis);
////            bis.read(mybytearray, 0, mybytearray.length);
////            ByteBuffer bytebuf = ByteBuffer.allocate(MAXLINE);
////            bytebuf.put(mybytearray, 0, (mybytearray.length));
////            bytebuf.flip();
////            socketChannelBasic.write(bytebuf);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
////         Send data
//
//
//
//        // Timer
//        try {
//            socketChannelBasic.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        socketChannelBasic = null;
//
    }

//    public static final int MAXLINE = 8;
//
//    public static final int MAXLINE = 1500;
//    public static final int MAXPHOTO = 50000;
//    public static final int MAXTEXT = 10000;
//    public static final String IP_addr = "10.42.0.1";
////    public static final String IP_addr = "192.168.1.126";
//    // todo : asccbot_v3.py TEXT_SERVER_IP update, ros_socket update
////    public static final String IP_addr = "10.227.223.235";
//
//
//    private final IBinder binder = new LocalBinder();
//
//    public class LocalBinder extends Binder {
//        CommunicationService getService() {
//            return CommunicationService.this;
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO automatically generated method stub
//        return binder;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return super.onUnbind(intent);
//    }
//    // Basic Socket
//    private boolean basic_receiver_switch = false;
//    private boolean basic_connect_switch = true;
//    private boolean basic_connected = false;
//
//    private SocketChannel socketChannelCommand = null;
//    private boolean command_receiver_switch = false;
//    private boolean command_connect_switch = true;
//    private boolean command_connected = false;
//
//    private SocketChannel socketChannelVideo = null;
//    private boolean video_receiver_switch = false;
//    private boolean video_connect_switch = true;
//    private boolean video_connected = false;
//
//    private SocketChannel socketChannelText = null;
//    private boolean text_receiver_switch = false;
//    private boolean text_connect_switch = true;
//    private boolean text_connected = false;
//
//    public void onCreate() {
//        super.onCreate();
//        //ConnectBasicServer();
//    }
//
//    public void onDestroy() {
//        super.onDestroy();
//        DisConnectBasicServer();
//        DisConnectVideoServer();
//        DisConnectCommandServer();
//    }
//    //******* Basic Socket ****************************************************************
//    // Connect to the Basic Socket Server
//    public void ConnectBasicServer() {
//        socketChannelBasic = null;
//        basic_receiver_switch = false;
//        basic_connected = false;
//        basic_connect_switch=true;
//        BasicServerConnect basicServerConnect = new BasicServerConnect();
//        basicServerConnect.start();
//    }
//    // Disconnect the server basic service
//    public void DisConnectBasicServer() {
//        try {
//            basic_receiver_switch = false;
//            basic_connect_switch = false;
//            socketChannelBasic.close();
//            basic_connected = false;
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//    // ConnectThread to send the connection request to Basic Socket Server
//    private class BasicServerConnect extends Thread {
//        public void run() {
//            System.out.println("Connection Thread Created");
//            while (basic_connect_switch==true) {
//                if (basic_connected==false) {
//                    System.out.println("Request Connection");
//                    try {
//                        socketChannelBasic = SocketChannel.open();
//                        socketChannelBasic.configureBlocking(true);
//                        SocketAddress socketAddress = new InetSocketAddress(IP_addr, 27133);
//                        if(socketChannelBasic.connect(socketAddress)==true) {
//                            basic_connected = true;
//                            basic_connect_switch = false;
//                            StartBasicServerListener();
//                            System.out.println("Basic Socket Listener started");
//                        }else {
//                            System.out.println("Connection failed");
//                            try {
//                                socketChannelBasic.close();
//                                System.out.println("Basic Socket Closed");
//                            } catch (IOException ee){
//                                ee.printStackTrace();
//                            }
//                        }
//                    } catch (IOException e) {
//                        System.out.println("Connection Exception failed");
//                        e.printStackTrace();
//                        try {
//                            socketChannelBasic.close();
//                            System.out.println("Basic Socket Closed");
//                        } catch (IOException ee){
//                            ee.printStackTrace();
//                        }
//                    }
//                }
//                // Timer
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }
//    //start basic listener
//    private void StartBasicServerListener() {
//        basic_receiver_switch = true;
//        BasicServerListener BasicServerListener = new BasicServerListener();
//        BasicServerListener.start();
//    }
//    // Basic receiver thread
//    private class BasicServerListener extends Thread {
//        public void run() {
//            System.out.println("Basic Socket Listener created");
//            while (basic_receiver_switch==true) {
//                if (basic_connected == true) {
//                    ByteBuffer buf = ByteBuffer.allocate(MAXLINE);
//                    try {
//                        if (socketChannelBasic.read(buf) < 0) {
//                            System.out.println("Receiver failed:");
//                            //basic_receiver_switch = false;
//                            //basic_connected = false;
//                            try {
//                                socketChannelBasic.close();
//                            } catch (IOException ee) {
//                                ee.printStackTrace();
//                            }
//                            if (basic_connected == true){
//                                System.out.println("Request Basic Connection");
//                                ConnectBasicServer();
//                            }
//                        } else {
//                            buf.flip();
//                            String acmds = new String(buf.array(), "UTF-8");
//                            System.out.println("Received from Linux:");
//                            System.out.println(acmds);
//                            if (acmds.equals("Serv-Cli")){
//                                AppManager.socket.SendTextToBasicServer("Basics**");
//                            }
//                            else {
//                                Message msg_task_manager = new Message();
//                                msg_task_manager.obj = acmds;
//                                AppManager.StartActivityHandler.sendMessage(msg_task_manager);
//                            }
//                        }
//
//                    } catch (IOException e) {
//                        System.out.println("Receiver failed:");
//                        basic_receiver_switch = false;
//                        e.printStackTrace();
//                        try {
//                            socketChannelBasic.close();
//                        } catch (IOException ee) {
//                            ee.printStackTrace();
//                        }
//                        if(basic_connected==true){
//                            ConnectBasicServer();
//                        }
//                    }
//
//                }
//
//            }
//
//        }
//    }
//    // Send the message to the server basic service
//    public void SendByteToBasicServer(byte[] data) {
//        if (basic_connected==true) {
//            try {
//                ByteBuffer bytebuf = ByteBuffer.allocate(MAXLINE);
//                bytebuf.put(data, 0, (data.length < MAXLINE ? data.length : MAXLINE));
//                bytebuf.flip();
//                socketChannelBasic.write(bytebuf);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                //basic_receiver_switch = false;
//                //basic_connected = false;
//                try {
//                    socketChannelBasic.close();
//                } catch (IOException ee) {
//                    ee.printStackTrace();
//                }
//                if (basic_connected==false){
//                    ConnectBasicServer();
//                }
//            }
//        }
//    }
//    public void SendTextToBasicServer(String string) {
//        if (basic_connected==true) {
//            try {
//                ByteBuffer bytebuf = ByteBuffer.wrap(string.getBytes("UTF-8"));
//                System.out.println("SendTextToBasicServer buf:" + bytebuf );
//                socketChannelBasic.write(bytebuf);
//                bytebuf.flip();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                //basic_receiver_switch = false;
//                //basic_connected = false;
//                try {
//                    socketChannelBasic.close();
//                } catch (IOException ee) {
//                    ee.printStackTrace();
//                }
//                if (basic_connected ==false){
//                    ConnectBasicServer();
//                }
//            }
//        }
//    }
//    //******* Commander Socket ****************************************************
//    //Connect to commander
//    public void ConnectCommandServer() {
//        System.out.println("ConnectCommandServer");
//        socketChannelCommand = null;
//        command_receiver_switch = false;
//        command_connect_switch = true;
//        command_connected = false;
//        CommandServerConnect commandServerConnect = new CommandServerConnect();
//        commandServerConnect.start();
//    }
//
//    public void DisConnectCommandServer() {
//        try {
//            System.out.println("DisConnectCommandServer");
//            command_receiver_switch = false;
//            command_connect_switch = false;
//            socketChannelCommand.close();
//            command_connected = false;
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//    // ConnectThread to send the connection request to Co102dpmmand Socket Server
//    private class CommandServerConnect extends Thread {
//        public void run() {
//            System.out.println("Connection Command Thread Created");
//            while (command_connect_switch==true) {
//                if (command_connected==false) {
//                    System.out.println("Request Connection:command");
//                    try {
//                        socketChannelCommand = SocketChannel.open();
//                        socketChannelCommand.configureBlocking(true);
//                        SocketAddress socketAddress = new InetSocketAddress(IP_addr, 27135);
//                        if(socketChannelCommand.connect(socketAddress)==true) {
//                            command_connected = true;
//                            command_connect_switch = false;
//                            StartCommandServerListener();
//                            System.out.println("Command Socket Listener started");
//                        }else {
//                            System.out.println("Command Connection failed");
//                            try {
//                                socketChannelCommand.close();
//                                System.out.println("Command Socket Closed");
//                            } catch (IOException ee){
//                                ee.printStackTrace();
//                            }
//                        }
//                    } catch (IOException e) {
//                        System.out.println("Command Connection Exception failed");
//                        e.printStackTrace();
//                        try {
//                            socketChannelCommand.close();
//                            System.out.println("Command Socket Closed");
//                        } catch (IOException ee){
//                            ee.printStackTrace();
//                        }
//                    }
//                }
//                // Timer
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }
//    //start command listener
//    private void StartCommandServerListener() {
//        command_receiver_switch=true;
//        CommandServerListener CommandServerListener = new CommandServerListener();
//        CommandServerListener.start();
//    }
//
//    // Command receiver thread
//    private class CommandServerListener extends Thread {
//        public void run() {
//            System.out.println("Command Socket Listener created");
//            while (command_receiver_switch==true) {
//                if (command_connected == true) {
//                    ByteBuffer buf = ByteBuffer.allocate(MAXLINE);
//                    try {
//                        if (socketChannelCommand.read(buf) < 0) {
//                            System.out.println("Command Receiver failed:");
//                            //command_receiver_switch = false;
//                            //command_connected = false;
//                            try {
//                                socketChannelCommand.close();
//                            } catch (IOException ee) {
//                                ee.printStackTrace();
//                            }
//                            if (command_connected == true){
//                                ConnectCommandServer();
//                            }
//                        } else {
//                            buf.flip();
//                            // RosScissorPaper Activity
//                            String cmd = new String(buf.array(), "UTF-8");
//                            System.out.println("Received:");
//                            System.out.println(cmd);
//                            if (cmd.equals("Serv-Cli")){
//                                AppManager.socket.SendTextToCommandServer("Commands");
//                            }
//                            else {
//                                if (AppManager.activity == 8) {
//                                    if (cmd.equals("rsp_play")) { //Start playing
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = 1;
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else if (cmd.equals("rsp_stop")) { // Stop playing
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = 0;
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else if (cmd.equals("rsp_over")) {  // Game over
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = 2;
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else if (cmd.contains("x")){
//                                        String sc = cmd.replaceAll("x","");
//                                        int i = sc.indexOf("-");
//                                        String sc_msg = "     YOU: "+sc.substring(0,i) + "   -   ROBOT: "+sc.substring(i+1);
//                                        Message msg_score = new Message();
//                                        msg_score.obj = sc_msg;
//                                        AppManager.ScoreRSPHandler.sendMessage(msg_score);
//                                    }
//                                    else {
//                                        System.out.println("Received the wrong package for the RSP game");
//                                    }
//                                }
//                                else if (AppManager.activity == 10) { //Doctor
//                                    if (cmd.substring(0,5).equals("timer")) { //Start timer
//                                        AppManager.delay = Long.parseLong(cmd.substring(5,8));
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = 10000011;
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else if (cmd.substring(0,5).equals("time*")) { //set timer
//                                        AppManager.delay = Long.parseLong(cmd.substring(5,8));
//                                    } else if (cmd.equals("stoptime")) { //Stop timer
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = 10000010;
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else if (cmd.equals("smiling*")) { //Smiling
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = 10000012;
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else if (cmd.equals("asking**")) { //Start playing
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = 10000001;
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else if (cmd.equals("waiting*")) { // Stop playing
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = 10000000;
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else if (cmd.equals("doctor00")) { // Stop playing
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = 10001111;
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    }
//                                    // Cognitive Assessment
//                                    else if (cmd.substring(0,4).equals("cog*")){
//                                        Message msg_rsp_result = new Message();
//                                        int num = Integer.parseInt("1100"+ cmd.substring(4,8));
//                                        System.out.println(num);
//                                        msg_rsp_result.what = num;
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else if (cmd.substring(0,4).equals("pain")){
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = Integer.parseInt("1200"+ cmd.substring(4,8));
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else if (cmd.substring(0,4).equals("fcmf")){
//                                        Message msg_rsp_result = new Message();
//                                        msg_rsp_result.what = Integer.parseInt("1400"+ cmd.substring(4,8));
//                                        AppManager.CommandActivityHandler.sendMessage(msg_rsp_result);
//                                    } else {
//                                        System.out.println("Received the wrong package for the RSP game");
//                                    }
//                                }
//                            }
//                        }
//
//                    } catch (IOException e) {
//                        System.out.println("Command Receiver failed:");
//                        e.printStackTrace();
//                        try {
//                            socketChannelCommand.close();
//                        } catch (IOException ee) {
//                            ee.printStackTrace();
//                        }
//                        if (command_connected ==true){
//                            ConnectCommandServer();
//                        }
//                    }
//
//                }
//
//            }
//
//        }
//    }
//
//    //send byte[] to command server
//    public void SendByteToCommandServer(byte[] data) {
//        if (command_connected==true) {
//            try {
//                ByteBuffer bytebuf = ByteBuffer.allocate(MAXLINE);
//                bytebuf.put(data, 0, (data.length < MAXLINE ? data.length : MAXLINE));
//                bytebuf.flip();
//                socketChannelCommand.write(bytebuf);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                //command_receiver_switch = false;
//                //command_connected = false;
//                try {
//                    socketChannelCommand.close();
//                } catch (IOException ee) {
//                    ee.printStackTrace();
//                }
//                if (command_connected==true){
//                    ConnectCommandServer();
//                }
//            }
//        }
//    }
//    //send text to Command server
//    public void SendTextToCommandServer(String string) {
//        if (command_connected==true) {
//            try {
//                ByteBuffer bytebuf = ByteBuffer.wrap(string.getBytes("UTF-8"));
//                System.out.println("SendTextToCommandServer buf:" + bytebuf.toString());
//                socketChannelCommand.write(bytebuf);
//                bytebuf.flip();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                //command_receiver_switch = false;
//                //command_connected = false;
//                try {
//                    socketChannelCommand.close();
//                } catch (IOException ee) {
//                    ee.printStackTrace();
//                }
//                if (command_connected==true){
//                    ConnectCommandServer();
//                }
//            }
//        }
//    }
//
//    //******* Video Socket ****************************************************
//    // Connect to Video Server
//    public void ConnectVideoServer() {
//        System.out.println("ConnectVideoServer");
//        socketChannelVideo = null;
//        video_receiver_switch = false;
//        video_connect_switch = true;
//        video_connected = false;
//        VideoServerConnect videoServerConnect = new VideoServerConnect();
//        videoServerConnect.start();
//    }
//    public void DisConnectVideoServer() {
//        try {
//            video_receiver_switch = false;
//            video_connect_switch = false;
//            video_connected = false;
//            //if (video_connected == true){
//            socketChannelVideo.close();
//            //}
//
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//    // Video Server Connection Thread
//    private class VideoServerConnect extends Thread {
//        public void run() {
//            //video_connect_switch = true;
//            System.out.println("Video Connection Thread Created");
//            while (video_connect_switch==true) {
//                if (video_connected==false) {
//                    System.out.println("Request Video Connection");
//                    try {
//                        socketChannelVideo = SocketChannel.open();
//                        socketChannelVideo.configureBlocking(true);
//                        SocketAddress socketAddress = new InetSocketAddress(IP_addr, 27136);
//                        if(socketChannelVideo.connect(socketAddress)==true) {
//                            video_connected = true;
//                            video_connect_switch = false;
//                            StartVideoServerListener();
//                            System.out.println("Video Socket Listener started");
//                        }else {
//                            System.out.println("Video Connection failed");
//                            try {
//                                socketChannelVideo.close();
//                                System.out.println("Video Socket Closed");
//                            } catch (IOException ee){
//                                ee.printStackTrace();
//                            }
//                        }
//                    } catch (IOException e) {
//                        System.out.println("Video Connection Exception failed");
//                        e.printStackTrace();
//                        try {
//                            socketChannelVideo.close();
//                            System.out.println("Video Socket Closed");
//                        } catch (IOException ee){
//                            ee.printStackTrace();
//                        }
//                    }
//                }
//                // Timer
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }
//    //
//    private void StartVideoServerListener() {
//        video_receiver_switch = true;
//        VideoServerListener videoServerListener = new VideoServerListener();
//        videoServerListener.start();
//    }
//    // Video receiver
//    private class VideoServerListener extends Thread {
//        public void run() {
//            System.out.println("Video Socket Receiver created");
//            int total = 0;
//            int length = 0;
//            int sent_bytes = 0;
//            ByteBuffer total_photo = ByteBuffer.allocate(MAXPHOTO);
//            //video_receiver_switch = true;
//            while (video_receiver_switch==true) {
//                ByteBuffer buf = ByteBuffer.allocate(MAXPHOTO);
//                if (video_connected == true) {
//                    try {
//                        int count = socketChannelVideo.read(buf);
//                        if (count < 0) {
//                            //System.out.println("Video Receiver failed:");
//                            //video_receiver_switch = false;
//                            //video_connected = false;
//                            try {
//                                socketChannelVideo.close();
//                            } catch (IOException ee) {
//                                ee.printStackTrace();
//                            }
//                            if (video_connected==true)
//                            {
//                                ConnectVideoServer();
//                            }
//                        } else {
//                            //System.out.println("***************** Video Received");
//                            //System.out.println(count);
//                            buf.flip();
//                            //if ((photo[0] == 0x04) && (photo[1] == 0x01)) {
//                            if ((buf.get(0) == 0x04) && (buf.get(1) == 0x01)) {
//                                //length = photo[2] & 0x000000ff | photo[3] << 8 & 0x0000ff00 | photo[4] << 16 & 0x00ff0000 | photo[5] << 24;
//                                length = buf.get(2) & 0x000000ff | buf.get(3) << 8 & 0x0000ff00 | buf.get(4) << 16 & 0x00ff0000 | buf.get(5) << 24;
//                                //System.out.print("+Length:");
//                                //System.out.println(length);
//                                total = count;
//                                sent_bytes = length+6;
//                                total_photo.clear();
//                                total_photo.put(buf);
//                                //System.out.print("+ Total:");
//                                //System.out.println(total);
//                                //System.out.print("+Buf limit:");
//                                //System.out.println(buf.limit());
//                                //System.out.print("+Total photo limit:");
//                                //System.out.println(total_photo.limit());
//                            }
//                            else{
//                                total = total + count;
//                                //System.out.print("+ Total:");
//                                //System.out.println(total);
//                                //System.out.print("+ Buf limit:");
//                                //System.out.println(buf.limit());
//                                if (total<= MAXPHOTO) {
//                                    total_photo.put(buf);
//                                } else{
//                                    total = 0;
//                                    total_photo.clear();
//                                }
//                                //System.out.print("+ Total photo limit:");
//                                //System.out.println(total_photo.limit());
//                            }
//                            if (total==sent_bytes){
//                                byte[] photo = new byte[MAXPHOTO];
//                                //System.out.print("+ Total photo limit:");
//                                //System.out.println(total_photo.limit());
//                                total_photo.flip();
//                                total_photo.get(photo,0,(total_photo.limit() < MAXPHOTO ? total_photo.limit() : MAXPHOTO));
//                                ///*
//                                Bitmap image_bitmap = BitmapFactory.decodeByteArray(photo, 6, length);
//                                //System.out.println("Decoded");
//                                Message msg_camera_result = new Message();
//                                msg_camera_result.obj = image_bitmap;
//                                AppManager.CameraActivityHandler.sendMessage(msg_camera_result);
//                                //System.out.println("Camera Updated");
//                                total = 0;
//                                //*/
//                                /*AppManager.image = BitmapFactory.decodeByteArray(photo, 6, length);
//                                Message msg_camera_result = new Message();
//                                msg_camera_result.what = 1;
//                                AppManager.CameraActivityHandler.sendMessage(msg_camera_result);
//                                //System.out.println("Camera Updated");
//                                total = 0;
//                                */
//                            }
//                            else if(total>sent_bytes){
//                                total = 0;
//                                total_photo.clear();
//                            }
//                        }
//
//                    } catch (IOException e) {
//                        System.out.println("Video Receiver failed:");
//                        e.printStackTrace();
//                        try {
//                            socketChannelVideo.close();
//                        } catch (IOException ee) {
//                            ee.printStackTrace();
//                        }
//                        if (video_connected == true)
//                        {
//                            ConnectVideoServer();
//                        }
//                        break;
//                    }
//
//                }
//
//            }
//
//        }
//    }
//    public void SendByteToVideoServer(byte[] data) {
//        if (video_connected==true) {
//            try {
//                ByteBuffer bytebuf = ByteBuffer.allocate(MAXLINE);
//                bytebuf.put(data, 0, (data.length < MAXLINE ? data.length : MAXLINE));
//                bytebuf.flip();
//                socketChannelVideo.write(bytebuf);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                //video_receiver_switch = false;
//                //video_connected = false;
//                try {
//                    socketChannelVideo.close();
//                } catch (IOException ee) {
//                    ee.printStackTrace();
//                }
//                if (video_connected==true)
//                {
//                    ConnectVideoServer();
//                }
//            }
//        }
//    }
//    public void SendBitmapToVideoServer(Bitmap bitmap) {
//        if (video_connected==true) {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte [] data = stream.toByteArray();
//            byte[] result = new byte[4];
//            int bytes = stream.size();
//            result[0] = (byte) (bytes >> 24);
//            result[1] = (byte) (bytes >> 16);
//            result[2] = (byte) (bytes >> 8);
//            result[3] = (byte) (bytes >> 0);
//            try {
//                ByteBuffer bytebuf = ByteBuffer.allocate(50000);
//                byte[] zeros = new byte[50000-4-bytes];
//                bytebuf.put(result).put(data).put(zeros);
//                bytebuf.flip();
//                socketChannelVideo.write(bytebuf);
//                System.out.println("Sent to text server socket:********");
//                System.out.println(bytes);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                //video_receiver_switch = false;
//                //video_connected = false;
//                try {
//                    socketChannelVideo.close();
//                } catch (IOException ee) {
//                    ee.printStackTrace();
//                }
//                if (video_connected==true)
//                {
//                    ConnectVideoServer();
//                }
//            }
//        }
//    }
//    public void SendTextToVideoServer(String string) {
//        if (video_connected==true) {
//            try {
//                ByteBuffer bytebuf = ByteBuffer.wrap(string.getBytes("UTF-8"));
//                socketChannelVideo.write(bytebuf);
//                bytebuf.flip();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                System.out.println("Failed to send twxt to Video Server");
//                //video_receiver_switch = false;
//                //video_connected = false;
//                try {
//                    socketChannelVideo.close();
//                } catch (IOException ee) {
//                    ee.printStackTrace();
//                }
//                if (video_connected==true){
//                    ConnectVideoServer();
//                }
//            }
//        }
//    }
//
//    //******* Text Socket ****************************************************
//// Connect to Text Server
//    public void ConnectTextServer() {
//        System.out.println("ConnectTextServer");
//        socketChannelText = null;
//        text_receiver_switch = false;
//        text_connect_switch = true;
//        text_connected = false;
//        TextServerConnect textServerConnect = new TextServerConnect();
//        textServerConnect.start();
//    }
//    public void DisConnectTextServer() {
//        try {
//            text_receiver_switch = false;
//            text_connect_switch = false;
//            //if (text_connected == true){
//            socketChannelText.close();
//            //}
//            text_connected = false;
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//    // Text Server Connection Thread
//    private class TextServerConnect extends Thread {
//        public void run() {
//            System.out.println("Text Connection Thread Created");
//            while (text_connect_switch==true) {
//                if (text_connected==false) {
//                    System.out.println("Request Text Connection");
//                    try {
//                        socketChannelText = SocketChannel.open();
//                        socketChannelText.configureBlocking(true);
//                        SocketAddress socketAddress = new InetSocketAddress(IP_addr, 27139);
//                        if(socketChannelText.connect(socketAddress)==true) {
//                            text_connected = true;
//                            text_connect_switch = false;
//                            StartTextServerListener();
//                            System.out.println("Text Socket Listener started");
//                        }else {
//                            System.out.println("Text Connection failed");
//                            try {
//                                socketChannelText.close();
//                                System.out.println("Text Socket Closed");
//                            } catch (IOException ee){
//                                ee.printStackTrace();
//                            }
//                        }
//                    } catch (IOException e) {
//                        System.out.println("Text Connection Exception failed");
//                        e.printStackTrace();
//                        try {
//                            socketChannelText.close();
//                            System.out.println("Text Socket Closed");
//                        } catch (IOException ee){
//                            ee.printStackTrace();
//                        }
//                    }
//                }
//                // Timer
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }
//    //
//    private void StartTextServerListener() {
//        text_receiver_switch = true;
//        CommunicationService.TextServerListener textServerListener = new CommunicationService.TextServerListener();
//        textServerListener.start();
//    }
//    // Text receiver
//    private class TextServerListener extends Thread {
//        public void run() {
//            System.out.println("Text Socket Receiver created");
//            int total = 0;
//            int length = 0;
//            int sent_bytes = 0;
//            ByteBuffer total_text = ByteBuffer.allocate(MAXTEXT);
//            while (text_receiver_switch==true) {
//                ByteBuffer buf = ByteBuffer.allocate(MAXTEXT);
//                if (text_connected == true) {
//                    try {
//                        int count = socketChannelText.read(buf);
//                        if (count < 0) {
//                            System.out.println("Text Receiver failed:");
//                            try {
//                                socketChannelText.close();
//                            } catch (IOException ee) {
//                                ee.printStackTrace();
//                            }
//                            if (text_connected==true)
//                            {
//                                ConnectTextServer();
//                            }
//                        } else {
//                            System.out.println("***************** Text Received");
//                            System.out.println(count);
//                            buf.flip();
//                            if ((buf.get(0) == 0x05) && (buf.get(1) == 0x01)) {
//                                length = buf.get(2) & 0x000000ff | buf.get(3) << 8 & 0x0000ff00 | buf.get(4) << 16 & 0x00ff0000 | buf.get(5) << 24;
//                                total = count;
//                                sent_bytes = length+6;
//                                total_text.clear();
//                                total_text.put(buf);
//                            }
//                            else{
//                                total = total + count;
//                                if (total<= MAXTEXT) {
//                                    total_text.put(buf);
//                                } else{
//                                    total = 0;
//                                    total_text.clear();
//                                }
//                            }
//                            if (total==sent_bytes){
//                                byte[] text = new byte[length];
//                                total_text.flip();
//                                //total_text.get(text,0,(total_text.limit() < MAXTEXT ? total_text.limit() : MAXTEXT));
//                                total_text.position(6);
//                                total_text.get(text,0,length);
//                                ///*
//                                String text_msg = new String(text, "UTF-8");
//                                System.out.println("Json Received:************************");
//                                System.out.println(text_msg);
//
//                                if (AppManager.DoctorActivityHandler != null) {
//                                    Message msg_text_result = new Message();
//                                    msg_text_result.obj = text_msg;
//                                    AppManager.DoctorActivityHandler.sendMessage(msg_text_result);
//                                }
//                                if (AppManager.ReminderActivityHandler != null) {
//                                    Message msg_text_result = new Message();
//                                    msg_text_result.obj = text_msg;
//                                    AppManager.ReminderActivityHandler.sendMessage(msg_text_result);
//                                }
//                                if (AppManager.ReminderDashboardActivityHandler != null) {
//                                    Message msg_text_result = new Message();
//                                    msg_text_result.obj = text_msg;
//                                    AppManager.ReminderDashboardActivityHandler.sendMessage(msg_text_result);
//                                }
//                                //System.out.println("Camera Updated");
//                                total = 0;
//                            }
//                            else if(total>sent_bytes){
//                                total = 0;
//                                total_text.clear();
//                            }
//                        }
//
//                    } catch (IOException e) {
//                        System.out.println("Text Receiver failed:");
//                        e.printStackTrace();
//                        try {
//                            socketChannelText.close();
//                        } catch (IOException ee) {
//                            ee.printStackTrace();
//                        }
//                        if (text_connected==true)
//                        {
//                            ConnectTextServer();
//                        }
//                        break;
//                    }
//
//                }
//
//            }
//
//        }
//    }
//    public void SendByteToTextServer(byte[] data) {
//        if (text_connected==true) {
//            try {
//                ByteBuffer bytebuf = ByteBuffer.allocate(MAXLINE);
//                bytebuf.put(data, 0, (data.length < MAXLINE ? data.length : MAXLINE));
//                bytebuf.flip();
//                socketChannelText.write(bytebuf);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                //text_receiver_switch = false;
//                //text_connected = false;
//                try {
//                    socketChannelText.close();
//                } catch (IOException ee) {
//                    ee.printStackTrace();
//                }
//                if (text_connected==true)
//                {
//                    ConnectTextServer();
//                }
//            }
//        }
//    }
//    public void SendBitmapToTextServer(Bitmap bitmap) {
//        if (text_connected==true) {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte [] data = stream.toByteArray();
//            byte[] result = new byte[4];
//            int bytes = stream.size();
//            result[0] = (byte) (bytes >> 24);
//            result[1] = (byte) (bytes >> 16);
//            result[2] = (byte) (bytes >> 8);
//            result[3] = (byte) (bytes >> 0);
//            try {
//                ByteBuffer bytebuf = ByteBuffer.allocate(200000);
//                byte[] zeros = new byte[200000-4-bytes];
//                bytebuf.put(result).put(data).put(zeros);
//                bytebuf.flip();
//                socketChannelText.write(bytebuf);
//                System.out.println("Sent to text server socket:********");
//                System.out.println(bytes);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                //video_receiver_switch = false;
//                //video_connected = false;
//                try {
//                    socketChannelText.close();
//                } catch (IOException ee) {
//                    ee.printStackTrace();
//                }
//                if (text_connected==true)
//                {
//                    ConnectTextServer();
//                }
//            }
//        }
//    }
//    public void SendTextToTextServer(String string) {
//        if (text_connected==true) {
//            try {
//                ByteBuffer bytebuf = ByteBuffer.wrap(string.getBytes("UTF-8"));
//                socketChannelText.write(bytebuf);
//                bytebuf.flip();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                System.out.println("Failed to send twxt to Text Server");
//                //text_receiver_switch = false;
//                //text_connected = false;
//                try {
//                    socketChannelText.close();
//                } catch (IOException ee) {
//                    ee.printStackTrace();
//                }
//                if (text_connected==true){
//                    ConnectTextServer();
//                }
//            }
//        }
//    }

}
