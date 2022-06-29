package com.luoy.bigdata.rpc.netty.client;

import com.luoy.bigdata.rpc.netty.transport.Transport;

public interface ServiceStub {
    void setTransport(Transport transport);
}
