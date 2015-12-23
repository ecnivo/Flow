Flow-Commons
============
A collection of code that is meant to be used between both the client and server.

###Networking
Networking is all handled through a `PackageSocket`. They are able to send and receive all objects implementing 
`Serializable` through the `.sendPackage()` and `.receivePackage()` functions.

One such `Serializable` is the `Message` class. This is a simplified method of sending information across a network.

Suppose the client wants to tell the server that it is logging in. To do so, we first create a message with the information 
required.

```java
Message message = new Message("auth"); // 'auth' is the type of message
message.put("username", "netdex");
message.put("password", "a8fi3kr"); // not my actual password
```

This constructs a message with the property `username=netdex` and `password=a8fi3kr`. To send this message to the server, we must 
have an instance of `PackageSocket`. A socket may be passed into the constructor to easily create one.

```java
Socket socket = ...;
PackageSocket packageSocket = new PackageSocket(socket);
```

From this point onwards, use `packageSocket` for all your I/O needs to avoid errors. To send the `Message`, do the following:

```java
packageSocket.sendPackage(message);
```

And the client is now done. From the server, receive the message as follows:

```java
Message message = packageSocket.receivePackage(Message.class);
```

To retrieve the properties on the server side, do the following:

```java
String username = message.get("username", String.class);
String password = message.get("password", String.class);
```

And the transportation of information is complete.