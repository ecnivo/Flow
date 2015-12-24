Flow-Commons
============
A collection of code that is meant to be used between both the client and server.

###Networking
Networking is all handled through a `PackageSocket`. They are able to send and receive all objects implementing 
`Serializable` through the `.sendPackage()` and `.receivePackage()` functions.

One such `Serializable` is the `Data` class. This is a simplified method of sending information across a network.

Suppose the client wants to tell the server that it is logging in. To do so, we first create a data with the information 
required.

```java
Data data = new Data("auth"); // 'auth' is the type of data
data.put("username", "netdex");
data.put("password", "a8fi3kr"); // not my actual password
```

This constructs a data with the property `username=netdex` and `password=a8fi3kr`. To send this data to the server, we must 
have an instance of `PackageSocket`. A socket may be passed into the constructor to easily create one.

```java
Socket socket = ...;
PackageSocket packageSocket = new PackageSocket(socket);
```

From this point onwards, use `packageSocket` for all your I/O needs to avoid errors. To send the `Data`, do the following:

```java
packageSocket.sendPackage(data);
```

And the client is now done. From the server, receive the data as follows:

```java
Data data = packageSocket.receivePackage(Data.class);
```

To retrieve the properties on the server side, do the following:

```java
String username = data.get("username", String.class);
String password = data.get("password", String.class);
```

And the transportation of information is complete.