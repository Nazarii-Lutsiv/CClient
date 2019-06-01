package rest;

import command_features.Command;
import lombok.Data;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import tcp_socket.ProtocolManager;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class ReqRespHendlerREST implements Closeable {

    private javax.ws.rs.client.Client client;

    private static String URL_PING = new String("http://localhost:8080/chat/server/ping");
    private static String URL_ECHO = new String("http://localhost:8080/chat/server/echo");
    private static String URL_LOGIN = new String("http://localhost:8080/chat/server/user");
    private static String URL_LIST = new String("http://localhost:8080/chat/server/users");
    private static String URL_MSG;
    private static String URL_RECEIVE_MSG;
    private static String URL_FILE;
    private static String URL_RECEIVE_FILE;

    private ProtocolManager protocolManager = new ProtocolManager();

    private volatile String responseInfo;
    private String listUsers;
    private boolean isRegex = false;
    private boolean isConnectinLost = false;

    private TimerTask timerTaskReciveUserListChecker;
    private Timer timerReciveUserListChecker;

    private TimerTask timerTaskReciveFileChecker;
    private Timer timerReciveFileChecker;

    private String currentURLReq;
    private UserInfo userInfo;
    private Response response;
    private WrappedList wrappedList;

    private java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
    private java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
    private String fileContent;

    public ReqRespHendlerREST() {
        this.client = javax.ws.rs.client.ClientBuilder.newClient();
    }

    public void reConnectToServer() {
        this.client = javax.ws.rs.client.ClientBuilder.newClient();
    }

    public void commandExecute(String textFromLabel) {
        commandCheker(protocolManager.textParser(textFromLabel));
    }

    private void commandCheker(String textFromLable) {
        Matcher matcher;
        try {
            for (final Command command : Command.values()) {
                matcher = Pattern.compile(command.getRegex()).matcher(textFromLable);
                if (matcher.find()) {
                    try {
                        switch (command) {
                            case CMD_PING:
                                responseInfo = client.target(URL_PING).request(MediaType.TEXT_PLAIN_TYPE).get(String.class);
                                currentURLReq = URL_PING;
                                isRegex = true;
                                break;
                            case CMD_ECHO:
                                String echoText = new String(protocolManager.parserCommandToGetSinglePostArgument(textFromLable));
                                responseInfo = client.target(URL_ECHO).request(MediaType.TEXT_PLAIN_TYPE).post(Entity.text(echoText), String.class);
                                currentURLReq = URL_ECHO;
                                isRegex = true;
                                break;
                            case CMD_LOGIN:
                                String[] itemForLogin = protocolManager.parserCommandToGetPluralPostArgument(textFromLable);
                                loginUser(itemForLogin);
                                currentURLReq = URL_LOGIN;
                                resiveUserListChecker();
                                resiveFileChecker();
                                isRegex = true;
                                break;
                            case CMD_LIST:
                                wrappedList = client.target(URL_LIST).request(MediaType.APPLICATION_JSON_TYPE).get(WrappedList.class);
                                responseInfo = new String("ListUsers - " + wrappedList.items.toString());
                                listUsers = wrappedList.items.toString();
                                currentURLReq = URL_LIST;
                                isRegex = true;
                                break;
                            case CMD_MSG:
                                String[] itemForMsg = protocolManager.parserCommandToGetPluralPostArgument(textFromLable);
                                sendMsg(itemForMsg);
                                currentURLReq = URL_MSG;
                                isRegex = true;
                                break;
                            case CMD_FILE:
                                String[] itemForFile = protocolManager.parserCommandToGetPluralPostArgument(textFromLable);
                                sendFile(itemForFile);
                                isRegex = true;
                                break;
                        }
                        System.out.println(response.getStatus());
                    } catch (NotAuthorizedException e) {
                        responseInfo = new String("First you need autorize!");
                    }
                }
            }
            if (!isRegex) {
                responseInfo = new String("Wrong input command!");
            }
            isRegex = false;
        } catch (ProcessingException e) {
            responseInfo = new String("No connection with server!");
            isConnectinLost = true;
            close();
        }
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
            reConnectToServer();
        }
    }

    private void loginUser(String[] itemForLogin) {
        userInfo = new UserInfo();
        userInfo.setLogin(itemForLogin[1]);
        userInfo.setPassword(itemForLogin[2]);
        Entity<UserInfo> userInfoEntity = Entity.entity(userInfo, MediaType.APPLICATION_JSON_TYPE);
        response = client.target(URL_LOGIN).request(MediaType.TEXT_PLAIN_TYPE).put(userInfoEntity);
        this.client.register(HttpAuthenticationFeature.basic(userInfo.getLogin(), userInfo.getPassword()));
        if (response.getStatus() == Response.Status.CREATED.getStatusCode())
            responseInfo = new String("New user registered");
        else if (response.getStatus() == Response.Status.ACCEPTED.getStatusCode()) {
            responseInfo = new String("Login Ok!");
        }
        URL_RECEIVE_MSG = new String("http://localhost:8080/chat/server/" + userInfo.getLogin() + "/messages");
        URL_RECEIVE_FILE = new String("http://localhost:8080/chat/server/" + userInfo.getLogin() + "/files");
    }

    private void sendMsg(String[] itemForMsg) {
        URL_MSG = new String("http://localhost:8080/chat/server/" + itemForMsg[1] + "/messages");
        response = client.target(URL_MSG).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.text(itemForMsg[2]));
        if (response.getStatus() == Response.Status.CREATED.getStatusCode())
            responseInfo = new String("Message send.");
        else if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode())
            responseInfo = new String("The target username or message was not specified properly or are incorrect!");
        else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode())
            responseInfo = new String("First need to login!");
        else if (response.getStatus() == Response.Status.NOT_ACCEPTABLE.getStatusCode())
            responseInfo = new String("Target user has too much pending message!");
        else if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
            responseInfo = new String("Internal server error!");
    }

    private void sendFile(String[] itemForFile) {
        URL_FILE = new String("http://localhost:8080/chat/server/" + itemForFile[1] + "/files");
        Path pathToFile = Paths.get(itemForFile[2]);
        File fileToSend = new File(pathToFile.toString());
        try {
            fileContent = encoder.encodeToString(Files.readAllBytes(fileToSend.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileInfo fileInfotoSend = new FileInfo(userInfo.getLogin(), fileToSend.getName(), fileContent);
        response = client.target(URL_FILE).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(fileInfotoSend));
        if (response.getStatus() == Response.Status.CREATED.getStatusCode())
            responseInfo = new String("File send.");
        else if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode())
            responseInfo = new String("The target username or file was't specified properly or are incorrect!");
        else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode())
            responseInfo = new String("First need to login!");
        else if (response.getStatus() == Response.Status.NOT_ACCEPTABLE.getStatusCode())
            responseInfo = new String("Target user has too much pending file!");
        else if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
            responseInfo = new String("Internal server error!");
    }

    private synchronized void resiveUserListChecker() {

        timerTaskReciveUserListChecker = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (userInfo != null) {
                        if (wrappedList == null) {
                            wrappedList = client.target(URL_LIST).request(MediaType.APPLICATION_JSON_TYPE).get(WrappedList.class);
                            responseInfo = new String("ListUsers - " + wrappedList.items.toString());
                        } else {
                            WrappedList newWrappedList = client.target(URL_LIST).request(MediaType.APPLICATION_JSON_TYPE).get(WrappedList.class);
                            if (!newWrappedList.items.equals(wrappedList.items)) {
                                responseInfo = new String("New list LogUsers - " + newWrappedList.items.toString());
                                wrappedList.items = newWrappedList.items;
                            }
                        }

                        WrappedList wrappedListMsg = client.target(URL_RECEIVE_MSG).request(MediaType.APPLICATION_JSON_TYPE).get(WrappedList.class);
                        if (wrappedListMsg.items != null) {
                            Message message = client.target(URL_RECEIVE_MSG + "/" + wrappedListMsg.items.get(0)).request(MediaType.APPLICATION_JSON_TYPE).get(Message.class);
                            responseInfo = new String("new msg: " + message.getSender() + "-" + message.getMessage());
                            Response deleteMsg = client.target(URL_RECEIVE_MSG + "/" + wrappedListMsg.items.get(0)).request(MediaType.APPLICATION_JSON_TYPE).delete();
                            System.out.println(deleteMsg.getStatus() + " - from delete msg.");
                        }

                    }
                } catch (ProcessingException e) {
                    responseInfo = new String("Connection with server lost!");
                    isConnectinLost = true;
                    close();
                    cancel();
                } catch (WebApplicationException e) {
//                    Make 400 Error (every time), all works without handle this error. I don't know what do with it. So it's a stub.
//                    e.printStackTrace();
                }
            }
        };
        timerReciveUserListChecker = new Timer("TimerReciveUserListChecker");
        timerReciveUserListChecker.scheduleAtFixedRate(timerTaskReciveUserListChecker, 500, 500);
    }

    private synchronized void resiveFileChecker() {
        timerTaskReciveFileChecker = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (userInfo != null) {
                        WrappedList wrappedListFile = client.target(URL_RECEIVE_FILE).request(MediaType.APPLICATION_JSON_TYPE).get(WrappedList.class);
                        if (wrappedListFile.items != null) {
                            FileInfo fileInfo = client.target(URL_RECEIVE_FILE + "/" + wrappedListFile.items.get(0)).request(MediaType.APPLICATION_JSON_TYPE).get(FileInfo.class);
                            byte[] decodedContent = decoder.decode(fileInfo.getContent());
                            Path path = Paths.get(ProtocolManager.pathToDirectForDownloadFile + fileInfo.getFilename());
                            File file = new File(path.toString());

                            try {
                                if (file.createNewFile()) {
                                    Files.write(path, decodedContent, StandardOpenOption.APPEND);
                                    responseInfo = new String("File - " + fileInfo.getFilename() + " came from " + fileInfo.getSender() + " and save.");
                                } else if (file.exists()) {
                                    file.delete();
                                    file.createNewFile();
                                    Files.write(path, decodedContent, StandardOpenOption.APPEND);
                                    responseInfo = new String("File - " + fileInfo.getFilename() + " came from " + fileInfo.getSender() + " is exist and rewrite.");
                                } else {
                                    responseInfo = new String("File - " + fileInfo.getFilename() + " came from " + fileInfo.getSender() + " can't create!");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Response deleteFile = client.target(URL_RECEIVE_FILE + "/" + wrappedListFile.items.get(0)).request(MediaType.APPLICATION_JSON_TYPE).delete();
                            System.out.println(deleteFile.getStatus() + " - from delete msg.");
                        }
                    }
                } catch (ProcessingException e) {
                    cancel();
                } catch (MultiException e) {
                    cancel();
                } catch (WebApplicationException e) {
//                    Make 400 Error (every time), all works without handle this error. I don't know what do with it. So it's a stub.
//                    e.printStackTrace();
                }
            }
        };
        timerReciveFileChecker = new Timer("TimerReciveFileChecker");
        timerReciveFileChecker.scheduleAtFixedRate(timerTaskReciveFileChecker, 2000, 2000);
    }

}
