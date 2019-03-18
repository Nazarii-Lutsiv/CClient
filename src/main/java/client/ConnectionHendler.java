package client;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

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
//        if(!selfClose) {
//            try {
//                this.readMessage.join();
//            } catch (InterruptedException e) {
//
//            }
//        }
    }

    public void sendMessage(String textInput){
        byte[] comand = protocolManager.execute(textInput);
        try {
            dataOutputStream.writeInt(comand.length);
            dataOutputStream.write(comand);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Thread sendMessage = new Thread(()->{
//           while (!isClose) {
//               try {
//                   byte[] comand = new byte[]{CMD_PING};
//                   dataOutputStream.writeInt(comand.length);
//                   dataOutputStream.write(comand);
//                   System.out.println("asdasdasd"+comand.length);
//               } catch (IOException e) {
//                   log.info(e.toString());
//               }
//           }
//        });
//        sendMessage.start();
    }

    public void readMessage(){

        readMessage = new Thread(()->{

           while (socket.isConnected()){
               try {
                   int contentSize = dataInputStream.readInt();
                   byte[] message = new byte[contentSize];
                   dataInputStream.readFully(message);
                   protocolManager.responseHendler(message);
                   System.out.println(message.length);
               } catch (IOException e) {
                   readMessage.interrupt();
                   close(true);
                   e.printStackTrace();
               }
           }
        });
        readMessage.start();
    }


    public Logger getLog() {
        return this.log;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public DataInputStream getDataInputStream() {
        return this.dataInputStream;
    }

    public DataOutputStream getDataOutputStream() {
        return this.dataOutputStream;
    }

    public ProtocolManager getProtocolManager() {
        return this.protocolManager;
    }

    public boolean isClose() {
        return this.isClose;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setDataInputStream(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    public void setDataOutputStream(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }

    public void setProtocolManager(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    public void setClose(boolean isClose) {
        this.isClose = isClose;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ConnectionHendler)) return false;
        final ConnectionHendler other = (ConnectionHendler) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$log = this.getLog();
        final Object other$log = other.getLog();
        if (this$log == null ? other$log != null : !this$log.equals(other$log)) return false;
        final Object this$host = this.getHost();
        final Object other$host = other.getHost();
        if (this$host == null ? other$host != null : !this$host.equals(other$host)) return false;
        if (this.getPort() != other.getPort()) return false;
        final Object this$socket = this.getSocket();
        final Object other$socket = other.getSocket();
        if (this$socket == null ? other$socket != null : !this$socket.equals(other$socket)) return false;
        final Object this$dataInputStream = this.getDataInputStream();
        final Object other$dataInputStream = other.getDataInputStream();
        if (this$dataInputStream == null ? other$dataInputStream != null : !this$dataInputStream.equals(other$dataInputStream))
            return false;
        final Object this$dataOutputStream = this.getDataOutputStream();
        final Object other$dataOutputStream = other.getDataOutputStream();
        if (this$dataOutputStream == null ? other$dataOutputStream != null : !this$dataOutputStream.equals(other$dataOutputStream))
            return false;
        final Object this$protocolManager = this.getProtocolManager();
        final Object other$protocolManager = other.getProtocolManager();
        if (this$protocolManager == null ? other$protocolManager != null : !this$protocolManager.equals(other$protocolManager))
            return false;
        if (this.isClose() != other.isClose()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ConnectionHendler;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $log = this.getLog();
        result = result * PRIME + ($log == null ? 43 : $log.hashCode());
        final Object $host = this.getHost();
        result = result * PRIME + ($host == null ? 43 : $host.hashCode());
        result = result * PRIME + this.getPort();
        final Object $socket = this.getSocket();
        result = result * PRIME + ($socket == null ? 43 : $socket.hashCode());
        final Object $dataInputStream = this.getDataInputStream();
        result = result * PRIME + ($dataInputStream == null ? 43 : $dataInputStream.hashCode());
        final Object $dataOutputStream = this.getDataOutputStream();
        result = result * PRIME + ($dataOutputStream == null ? 43 : $dataOutputStream.hashCode());
        final Object $protocolManager = this.getProtocolManager();
        result = result * PRIME + ($protocolManager == null ? 43 : $protocolManager.hashCode());
        result = result * PRIME + (this.isClose() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "ConnectionHendler(log=" + this.getLog() + ", host=" + this.getHost() + ", port=" + this.getPort() + ", socket=" + this.getSocket() + ", dataInputStream=" + this.getDataInputStream() + ", dataOutputStream=" + this.getDataOutputStream() + ", protocolManager=" + this.getProtocolManager() + ", isClose=" + this.isClose() + ")";
    }
}
