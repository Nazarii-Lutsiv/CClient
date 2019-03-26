package client;

import lombok.Data;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

@Data
public class ConnectionHendler implements Closeable{
    private Logger log = Logger.getLogger(this.getClass().getName());
    private String host;
    private int port;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private ProtocolManager protocolManager;
    private volatile boolean isClose = false;
    private Thread readMessage;
    private Thread showResponse;
    private volatile String responseText;

    private static final byte CMD_PING = 1;


    public ConnectionHendler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void listenSocket() {
        //Create socket connection
        if(socket != null && socket.isConnected()){
           close(true);
        }
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(this.host, this.port), 2000);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            protocolManager = new ProtocolManager();
            if (socket.isConnected()) {
                System.out.println("Socket is created and connected!");
                this.isClose = false;
            }

        } catch (SocketTimeoutException te){

            System.out.println("Socket timeout connection");
        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void close() throws IOException {
        close(false);
    }

    public void close(boolean selfClose) {
        if (socket == null) return;

        try {
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
            this.isClose = true;
            System.out.println("Socket close");
        } catch (IOException e) {
            System.out.println("Can`t close connection: " + e);
        }
        socket = null;
    }

    public void sendMessage(String textInput){
        byte[] comand = protocolManager.execute(textInput);
        try {
            dataOutputStream.writeInt(comand.length);
            dataOutputStream.write(comand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readMessage(){
        readMessage = new Thread(()->{
           while (socket.isConnected()){
               try {
                   int contentSize = dataInputStream.readInt();
                   byte[] message = new byte[contentSize];
                   dataInputStream.readFully(message);
                   responseText = new String(protocolManager.responseHendler(message));
//                   System.out.println(message.length);
               } catch (IOException e) {
                   readMessage.interrupt();
                   close(true);
                   e.printStackTrace();
               }
           }
        });
        readMessage.start();
        return responseText;
    }



}
