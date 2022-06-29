package com.luoy.bigdata.rpc.netty.client;

import com.luoy.bigdata.rpc.netty.transport.Transport;

public interface StubFactory {
    <T> T createStub(Transport transport, Class<T> serviceClass);
}
