package com.example.arclient;

import android.util.Log;

import com.dto.DTO;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketBridge implements Castable {

    private static final int PORT = 21248;
    public String SERVER_IP;
    public Socket socket;
    public SocketAddress address;
    public ObjectOutputStream objectOutputStream;

    public SocketBridge (String ip) {

        SERVER_IP = ip;
        address = new InetSocketAddress(SERVER_IP, PORT);

    }

    @Override
    public void cast(Object... obj) {


    }

    @Override
    public void launch(Object... obj) {

        new Thread(()
        -> {

            try {
                socket = new Socket();
                socket.connect(address);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();


    }
    public void release() {

        try {
            objectOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void stream(DTO dto) {

        new Thread(()
        -> {
            try {

                objectOutputStream.writeObject(dto);
                objectOutputStream.flush();

                Log.d("StreamThread", "streamed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Log.d("SocketBridge", "stream : datastream");

    }
}
