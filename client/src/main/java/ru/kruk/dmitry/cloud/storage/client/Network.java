package ru.kruk.dmitry.cloud.storage.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Network {

    private static final Network instance = new Network();
    private static Network getInstance(){
        return instance;
    }

    public Network(){ }

    private Channel currentChannel;

    public Channel getCurrentChannel(){
        return currentChannel;
    }

    public void startNetwork(CountDownLatch countDownLatch, List<Callback> callbackList){
        EventLoopGroup group = new NioEventLoopGroup();
        try {
        Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress("localhost", 8189))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new ClientHandler(callbackList));
                        currentChannel = socketChannel;
                    }
                });

            ChannelFuture channelFuture = clientBootstrap.connect().sync();
            countDownLatch.countDown();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            GUIHelper.showError(e);
        }finally {
            try {
                group.shutdownGracefully().sync();
            }catch (InterruptedException e){
                GUIHelper.showError(e);
            }
        }
    }

    public void stop(){
        currentChannel.close();
    }

}
