package com.github.florent37.camerafragment.sample;

public class TestMain {
    public static void main(String []args){
//        System.out.println("Hello");
//        CommunicationService communicationService = new CommunicationService();
        String ipsend = "10.227.102.0";
        int port = 59000;
        int cnt = 1;
        String currentTime = "20230129000002";
        String image_filename = "/home/ascc/Desktop/test.jpg";
        String audio_filename = "/home/ascc/Desktop/test.wav";
        String motion_filename =  "/home/ascc/Desktop/motion.txt";
//        communicationService.socketImageSendingHandler(ipsend, port, cnt, currentTime, image_filename);

//        communicationService.socketAudioSendingHandler(ipsend, port, currentTime, audio_filename);
//        communicationService.socketMotionSendingHandler(ipsend, port, currentTime, motion_filename);

        new CommunicationImageService().execute(ipsend, String.valueOf(port), "1", currentTime, image_filename);


    }
}
