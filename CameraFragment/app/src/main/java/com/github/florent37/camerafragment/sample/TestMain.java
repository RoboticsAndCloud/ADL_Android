package com.github.florent37.camerafragment.sample;

public class TestMain {
    public static void main(String []args){
        System.out.println("Hello");
        CommunicationService communicationService = new CommunicationService();
        String ipsend = "10.227.102.0";
        int port = 59000;
        int cnt = 1;
        String currentTime = "20230129000000";
        String filename = "/home/ascc/Desktop/test.jpg";
        communicationService.socketImageSendingHandler(ipsend, port, cnt, currentTime, filename);

    }
}
