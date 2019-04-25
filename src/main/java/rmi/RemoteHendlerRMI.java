package rmi;

import command_features.Command;
import lombok.Data;
import lpi.server.rmi.IServer;
import tcp_socket.ProtocolManager;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;
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
    private String[] listUsers;
    private volatile String responseInfo;
    private boolean isRegex;

    private TimerTask timerTaskReciveChecker;
    private Timer timerReciveChecker;

    public RemoteHendlerRMI(String hostname, int port) {
        this.port = port;
        this.hostname = hostname;
    }

    public void registClient() {
        try {
            this.registry = LocateRegistry.getRegistry(hostname, port);
            this.proxy = (IServer) registry.lookup(IServer.RMI_SERVER_NAME);
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (this.registry != null) {
            try {
                timerTaskReciveChecker.cancel();
                timerReciveChecker.cancel();
                if (sessionId != null) {
                    proxy.exit(sessionId);
                    sessionId = null;
                    listUsers = null;
                }
            } catch (AccessException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void commandExecute(String textFromLabel) {
        commandCheker(protocolManager.textParser(textFromLabel));
    }

    private void commandCheker(String textFromLable) {
        Matcher matcher;
        for (final Command command : Command.values()) {
            matcher = Pattern.compile(command.getRegex()).matcher(textFromLable);
            if (matcher.find()) {
                try {
                    switch (command) {
                        case CMD_PING:
                            proxy.ping();
                            isRegex = true;
                            break;
                        case CMD_ECHO:
                            responseInfo = new String(proxy.echo(new String(protocolManager.parserCommandToGetSinglePostArgument(textFromLable))));
                            isRegex = true;
                            break;
                        case CMD_LOGIN:
                            if (sessionId == null) {
                                String[] itemForLogin = protocolManager.parserCommandToGetPluralPostArgument(textFromLable);
                                sessionId = proxy.login(itemForLogin[1], itemForLogin[2]);
                                responseInfo = new String("Login OK! " + "Session ID: " + sessionId);
                                resiveInfoChecker();
                            } else {
                                responseInfo = new String("You have alrady had login!");
                            }
                            isRegex = true;
                            break;
                        case CMD_LIST:
                            if (sessionId != null) {
                                responseInfo = new String("List LogUsers - " + parsToString(proxy.listUsers(sessionId)));
                            } else {
                                responseInfo = new String("First you mast login!");
                            }
                            isRegex = true;
                            break;
                        case CMD_MSG:
                            if (sessionId != null) {
                                String[] itemForMsg = protocolManager.parserCommandToGetPluralPostArgument(textFromLable);
                                proxy.sendMessage(sessionId, new IServer.Message(itemForMsg[1], itemForMsg[2]));
                                responseInfo = new String("Message send!");
                            } else {
                                responseInfo = new String("First you mast login!");
                            }
                            isRegex = true;
                            break;
                        case CMD_FILE:
                            if (sessionId != null) {
                                String[] itemForFile = protocolManager.parserCommandToGetPluralPostArgument(textFromLable);
                                Path pathToFile = Paths.get(itemForFile[2]);
                                File fileToSend = new File(pathToFile.toString());
                                proxy.sendFile(sessionId, new IServer.FileInfo(itemForFile[1].toString(), fileToSend));
                                responseInfo = new String("File send!");
                            } else {
                                responseInfo = new String("First you mast login!");
                            }
                            isRegex = true;
                            break;
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!isRegex) {
            responseInfo = new String("Wrong input command!");
        }
    }

    private synchronized void resiveInfoChecker() {

        timerTaskReciveChecker = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (sessionId != null && registry != null) {

                        if (listUsers == null) {
                            listUsers = proxy.listUsers(sessionId);
                            responseInfo = new String("List LogUsers - " + parsToString(listUsers));
                        } else {
                            String[] newListUser = proxy.listUsers(sessionId);
                            if (!isSameMas(listUsers, newListUser)) {
                                responseInfo = new String("New list LogUsers - " + parsToString(newListUser));
                                listUsers = newListUser;
                            }
                        }

                        IServer.Message message = proxy.receiveMessage(sessionId);
                        if (message != null) {
                            responseInfo = new String("new msg: " + message.getReceiver() + "- " + message.getMessage());
                        }

                        IServer.FileInfo fileInfo = proxy.receiveFile(sessionId);
                        if (fileInfo != null) {
                            responseInfo = new String("New file " + fileInfo.getFilename() + " came from " + fileInfo.getSender());

                            Path path = Paths.get(ProtocolManager.pathToDirectForDownloadFile + fileInfo.getFilename());
                            File file = new File(path.toString());
                            try {
                                if (file.createNewFile()) {
                                    Files.write(path, fileInfo.getFileContent(), StandardOpenOption.APPEND);
                                } else if (file.exists()) {
                                    file.delete();
                                    file.createNewFile();
                                    Files.write(path, fileInfo.getFileContent(), StandardOpenOption.APPEND);
                                    responseInfo = new String("File - " + fileInfo.getFilename() + " is exist and rewrite.");
                                } else {
                                    responseInfo = new String("File - " + fileInfo.getFilename() + " can't create!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                    cancel();
                }
            }
        };
        timerReciveChecker = new Timer("TimerReciveChecker");
        timerReciveChecker.scheduleAtFixedRate(timerTaskReciveChecker, 500, 500);
    }

    private String parsToString(String[] strMas) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String string : strMas) {
            stringBuffer.append(string + "");
        }
        return stringBuffer.toString();
    }

    private boolean isSameMas(String[] prevMas, String[] newMas) {
        if (prevMas.length != newMas.length) {
            return false;
        } else {
            for (int i = 0; i < prevMas.length; i++) {
                if (!prevMas[i].equals(newMas[i])) {
                    return false;
                }
            }
            return true;
        }
    }
}
