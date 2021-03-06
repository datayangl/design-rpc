package com.luoy.bigdata.rpc.netty.client.stubs;

import com.luoy.bigdata.rpc.netty.client.RequestIdSupport;
import com.luoy.bigdata.rpc.netty.client.ServiceStub;
import com.luoy.bigdata.rpc.netty.client.ServiceTypes;
import com.luoy.bigdata.rpc.netty.serialize.SerializeSupport;
import com.luoy.bigdata.rpc.netty.transport.Transport;
import com.luoy.bigdata.rpc.netty.transport.command.Code;
import com.luoy.bigdata.rpc.netty.transport.command.Command;
import com.luoy.bigdata.rpc.netty.transport.command.Header;
import com.luoy.bigdata.rpc.netty.transport.command.ResponseHeader;

import java.util.concurrent.ExecutionException;

public abstract class AbstractStub implements ServiceStub {
    protected Transport transport;

    protected byte [] invokeRemote(RpcRequest request) {
        Header header = new Header(ServiceTypes.TYPE_RPC_REQUEST, 1, RequestIdSupport.next());
        byte [] payload = SerializeSupport.serialize(request);
        Command requestCommand = new Command(header, payload);
        try {
            Command responseCommand = transport.send(requestCommand).get();
            ResponseHeader responseHeader = (ResponseHeader) responseCommand.getHeader();
            if(responseHeader.getCode() == Code.SUCCESS.getCode()) {
                return responseCommand.getPayload();
            } else {
                throw new Exception(responseHeader.getError());
            }

        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
