package com.tiktok.service;

import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.invoker.auth.ApiKeyAuth;
import com.tiktok.model.entities.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cloudmersive.client.AudioApi;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

@Service
public class SoundService extends GlobalService {
    @Autowired
    private ApiClient defaultClient;

    protected void newSound(Video video, String path) {
        Socket socket = new Socket();

        SocketAddress socketAddress = new InetSocketAddress("localhost", 6969);
        try {
            socket.connect(socketAddress, 35000); //12000 are milli seconds
            ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
            Apikey.setApiKey("c7e1df56-448d-4480-9471-4f4b3dd404fb"); // Uncomment the following line to set a prefix for the API key, e.g. "Token" (defaults to null) //Apikey.setApiKeyPrefix("Token");

            AudioApi apiInstance = new AudioApi();
            File inputFile = new File(path);// File | Input file to perform the operation on.
            String fileUrl = "sounds" + File.separator + System.nanoTime() + ".mp3"; // String | Optional; URL of an audio file being used for conversion. Use this option for files larger than 2GB.
            Integer bitRate = 56; // Integer | Optional; Specify the desired bitrate of the converted audio file in kilobytes per second (kB/s). Value may be between 48 and 1,411. By default, the optimal bitrate will be chosen automatically.

            try {
                byte[] result = apiInstance.audioConvertToMp3(inputFile, fileUrl, bitRate);
                System.out.println("the sound is done");
            } catch (ApiException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }


}
