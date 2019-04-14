package rmi;

import command_features.Command;
import lombok.Data;
import lpi.server.rmi.IServer;
import tcp_socket.ProtocolManager;

import java.io.Closeable;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class RemoteHendlerRMI implements Closeable {

    private Registry registry;
    private IServer proxy;
    private int port;
    private String hostname;
    private ProtocolManager protocolManager = new ProtocolManager();
    private String sessionId;
    private String responseInfo;

    public RemoteHendlerRMI(String hostname, int port){
        this.port = port;
        this.hostname = hostname;
    }

    public void registClient(){
        try {
            this.registry = LocateRegistry.getRegistry(hostname, port);
            this.proxy = (IServer) registry.lookup(IServer.RMI_SERVER_NAME);
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.registry != null) {
            try {
                this.registry.unbind(IServer.RMI_SERVER_NAME);
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
            this.registry = null;
        }

//        if (this.proxy != null) {
//            UnicastRemoteObject.unexportObject(IServer.class, true);
//            this.proxy = null;
//        }
    }

    public void commandExecute (String textFromLabel) {
        commandCheker(protocolManager.textParser(textFromLabel));
    }

    private void commandCheker (String textFromLable) {
        Matcher matcher;
        for (final Command command : Command.values()) {
            matcher = Pattern.compile(command.getRegex()).matcher(textFromLable);
            if (matcher.find()) {
                try {
                    switch (command) {
                        case CMD_PING:
                            proxy.ping();
                            break;
                        case CMD_ECHO:
                            registClient();
                            responseInfo = new String(proxy.echo(new String(protocolManager.parserCommandToGetSinglePostArgument(textFromLable))));
                            break;
                        case CMD_LOGIN:
                            String[] itemForLogin = protocolManager.parserCommandToGetPluralPostArgument(textFromLable);
                            sessionId = proxy.login(itemForLogin[1], itemForLogin[2]);
                            if (sessionId != null){
                                responseInfo = new String("Login OK!" + "Session ID: " + sessionId);
                            } else {
                                responseInfo = new String("Some trouble with login!");
                            }
                            break;
                        case CMD_MSG:
//                            String[] itemForMsg = protocolManager.parserCommandToGetPluralPostArgument(textFromLable);
//                            proxy.sendMessage(sessionId , Message(itemForMsg[1], itemForMsg[2]));
//                            responseInfo = new String("Message send!");
                            break;
//                        case CMD_FILE:

                    }
                } catch (RemoteException e) {
//                    e.printStackTrace();
                }
            }
        }
    }

}
