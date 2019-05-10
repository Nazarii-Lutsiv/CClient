package soap;

import command_features.Command;
import lombok.Data;
import lpi.server.soap.*;
import tcp_socket.ProtocolManager;

import javax.xml.ws.WebServiceException;
import java.io.*;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class CommandHendlerSOAP implements Closeable {

    private ChatServer serverWrapper;
    private IChatServer serverProxy;
    private String urlAdress;

    private ProtocolManager protocolManager = new ProtocolManager();
    private String sessionId;
    private String login;
    private String listUsers;
    private volatile String responseInfo;
    private boolean isRegex = false;
    private boolean isConnectinLost = false;

    private TimerTask timerTaskReciveChecker;
    private Timer timerReciveChecker;

    private TimerTask timerTaskPinging;
    private Timer timerPinging;

    public CommandHendlerSOAP(String urlAdress) {
        this.urlAdress = urlAdress;
    }

    public boolean connectToServer() {
        isConnectinLost = false;
        try {
            this.serverWrapper = new ChatServer(new URL(urlAdress));
            this.serverProxy = serverWrapper.getChatServerProxy();
            isConnectWithServerExistWthoutLogin();
        } catch (WebServiceException | MalformedURLException e) {
            isConnectinLost = true;
            close();
            return false;
        }
        return true;
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
                            serverProxy.ping();
                            isRegex = true;
                            break;
                        case CMD_ECHO:
                            responseInfo = new String(serverProxy.echo(new String(protocolManager.parserCommandToGetSinglePostArgument(textFromLable))));
                            isRegex = true;
                            break;
                        case CMD_LOGIN:
                            if (sessionId == null) {
                                String[] itemForLogin = protocolManager.parserCommandToGetPluralPostArgument(textFromLable);
                                sessionId = serverProxy.login(itemForLogin[1], itemForLogin[2]);
                                login = itemForLogin[1];
                                responseInfo = new String("Login OK! " + "Session ID: " + sessionId);
                                timerPinging.cancel();
                                resiveInfoChecker();
                            } else {
                                responseInfo = new String("You have alrady had login!");
                            }
                            isRegex = true;
                            break;
                        case CMD_LIST:
                            if (sessionId != null) {
                                responseInfo = new String("List LogUsers - " + serverProxy.listUsers(sessionId).toString());
                            } else {
                                responseInfo = new String("First you mast login!");
                            }
                            isRegex = true;
                            break;
                        case CMD_MSG:
                            if (sessionId != null) {
                                String[] itemForMsg = protocolManager.parserCommandToGetPluralPostArgument(textFromLable);
                                serverProxy.sendMessage(sessionId, prepareMessageForSend(login, itemForMsg[1], itemForMsg[2]));
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
                                serverProxy.sendFile(sessionId, prepareFileForSend(login, itemForFile[1], fileToSend.getName(), Files.readAllBytes(fileToSend.toPath())));
                                responseInfo = new String("File send!");
                            } else {
                                responseInfo = new String("First you mast login!");
                            }
                            isRegex = true;
                            break;
                        case CMD_EXIT:
                            if (sessionId != null) {
                                close();
                                responseInfo = new String("Disconnected!");
                            } else {
                                responseInfo = new String("Can't exit, no loginUser!");
                            }
                            isRegex = true;
                            break;
                    }
                } catch (ArgumentFault argumentFault) {
                    argumentFault.printStackTrace();
                } catch (ServerFault serverFault) {
                    serverFault.printStackTrace();
                    responseInfo = new String("This loginName don't exist!");
                    isRegex = true;
                } catch (LoginFault loginFault) {
                    loginFault.printStackTrace();
                } catch (NoSuchFileException e) {
                    responseInfo = new String("No such file or directory!");
                    isRegex = true;
                } catch (ConnectException e) {
                    responseInfo = new String("Connection with server lost!");
                    isConnectinLost = true;
                    close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!isRegex) {
            responseInfo = new String("Wrong input command!");
        }
        isRegex = false;
    }

    @Override
    public void close() {
        if (this.serverWrapper != null) {
            try {
                if (sessionId != null) {
                    if (!isConnectinLost) {
                        this.serverProxy.exit(sessionId);
                    }
                    this.sessionId = null;
                    this.serverWrapper = null;
                    listUsers = null;
                    login = null;
                } else {
                    this.sessionId = null;
                    this.serverWrapper = null;
                }
            } catch (ArgumentFault argumentFault) {
                argumentFault.printStackTrace();
            } catch (ServerFault serverFault) {
                serverFault.printStackTrace();
            }
        }
    }

    private Message prepareMessageForSend(String sender, String reciver, String message) {
        Message messageForSend = new Message();
        messageForSend.setSender(sender);
        messageForSend.setReceiver(reciver);
        messageForSend.setMessage(message);
        return messageForSend;
    }

    private FileInfo prepareFileForSend(String sender, String reciver, String fileName, byte[] fileContent) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setSender(sender);
        fileInfo.setReceiver(reciver);
        fileInfo.setFilename(fileName);
        fileInfo.setFileContent(fileContent);
        return fileInfo;
    }

    private synchronized void resiveInfoChecker() {

        timerTaskReciveChecker = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (sessionId != null && serverWrapper != null) {

                        if (listUsers == null) {
                            listUsers = serverProxy.listUsers(sessionId).toString();
                            responseInfo = new String("List LogUsers - " + listUsers);
                        } else {
                            String newListUser = serverProxy.listUsers(sessionId).toString();
                            if (!newListUser.equals(listUsers)) {
                                responseInfo = new String("New list LogUsers - " + newListUser);
                                listUsers = newListUser;
                            }
                        }

                        Message message = serverProxy.receiveMessage(sessionId);
                        if (message != null) {
                            responseInfo = new String("new msg: " + message.getSender() + " - " + message.getMessage());
                        }

                        FileInfo fileInfo = serverProxy.receiveFile(sessionId);
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
                                    responseInfo = new String("File - " + fileInfo.getFilename() + " came from " + fileInfo.getSender() + " is exist and rewrite.");
                                } else {
                                    responseInfo = new String("File - " + fileInfo.getFilename() + " came from " + fileInfo.getSender() + " can't create!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (ArgumentFault argumentFault) {
                    argumentFault.printStackTrace();
                } catch (ServerFault serverFault) {
                    serverFault.printStackTrace();
                } catch (WebServiceException e) {
                    responseInfo = new String("Connection with server lost!");
                    isConnectinLost = true;
                    close();
                    cancel();
                }
            }
        };
        timerReciveChecker = new Timer("TimerReciveChecker");
        timerReciveChecker.scheduleAtFixedRate(timerTaskReciveChecker, 200, 200);
    }

    private synchronized void isConnectWithServerExistWthoutLogin() {
        timerTaskPinging = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (serverWrapper != null) {
                        serverProxy.ping();
                    }
                } catch (WebServiceException e) {
                    responseInfo = new String("Connection with server lost!");
                    isConnectinLost = true;
                    close();
                    cancel();
                }
            }
        };
        timerPinging = new Timer("TimerPinging");
        timerPinging.scheduleAtFixedRate(timerTaskPinging, 500, 500);
    }
}
