Flow-Commons
============
A collection of code that is meant to be used between both the client and server.

###Networking
Networking is all handled through a *PackageSocket*. They are able to send and receive *Parcelables*, which are a data type
which can be converted to a byte array and back. After conversion to a byte array, they can be sent across the network, or 
written to a file. The following is the current structure of Parcelables:

* Parcelable
  * Message
  * DocumentMessage
    * DocumentInsertMessage
    * DocumentDeleteMessage
  * Document

Each additional level of Parcelable adds additional information to the packet, in a hierarchical manner. For example,
Parcelables themselves do not contain any information themselves. A Document contains information regarding its contents, 
line count etc. A message contains its type and a signature, while a DocumentMessage has that in addition to whatever information 
it may need. This structure makes it so that all Parcelables under a subtype will have the headers of that subtype.

###Implementation
You might be wondering, how the heck do I use this in my code? Well, here's a brief abstract implementation:

After initiating a connection with the server/client, you receive a `Socket`. To create a `PackageSocket` from the socket, 
pass in the socket into the constructor as follows:

```java
Socket socket = ...;
PackageSocket packageSocket = new PackageSocket(socket);
```

This creates a wrapper around the socket that has additional, nice, useful methods for you to use. Now, lets say I am the client 
and I have a document open, and the user added a character somewhere into the document. How do we let the server know that the 
document changed?

First we create a new `DocumentInsertMessage` as described below:

```java
DocumentInsertMessage documentInsertMessage = new DocumentInsertMessage('C', 1, 60);
```

This means the user will be inserting a capital letter 'C' on line 1 index 60. Now we send the message as follows:

```java
packageSocket.sendParcelable(documentInsertMessage);
```

And that's it. On the server side, we will receive the message as such:

```java
DocumentInsertMessage message = new DocumentInsertMessage(); // note the empty constructor
packageSocket.receiveParcelable(message);
```

The information received is copied into the reference passed in, and also returned as a result.
