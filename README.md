# CClient REST

CClient is a chat client with simple UI, that can send and receive messages of a certain type (texts or files) from the server. 
In this case for sending and receiving message used REST.

## Running the program

* Download repository CClient from GitHub.

* Open this application in any IDE with Java language support.

* Run the main method that is in the CClientREST.java class.

## Usage

* To connect and disconnect from the server set up the appropriate text boxes and buttons in UI.

* To send messages, need to use the declared commands in the message sending field. The appropriate commands are listed below in the table.

* To get files to the desired location on your disk, you need to change the ` public static String pathToDirectForDownloadFile = "F:\\TestCClient\\";` field, which is in the ProtocolManager.java class.

Command Name |Structure of the command|Example|
-------------|------------------------|-------|
Ping|#ping: <br> #p|#p|
Echo|#ec: ; <br> #echo: ; <br> #ec: (some text); <br> #echo: (some text);|#ec: hello;|
Login|#login: login-nazar password-rie123; <br> #login: l-nazar p-rie123;| #login: l-nazar p-rie123;|
Login List|#list|#list|
Send Msg|#msg: login-(login user) text-(some text); <br> #msg: l-(login user) t-(some text);|#msg: l-nazar t-hello;|
Send File|#file: login-(login user) file-(URL dir with name file); <br> #f: l-(login user) f-(URL dir with name file);| #f: l-nazar f-F:\TestCClient\test1.txt;|

