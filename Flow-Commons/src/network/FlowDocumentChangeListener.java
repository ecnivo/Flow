package network;

import message.Data;

/**
 * Created by Netdex on 12/29/2015.
 */
public abstract class FlowDocumentChangeListener {

    public abstract void onFlowDocumentUpdateMessageReceived(Data data);
}
