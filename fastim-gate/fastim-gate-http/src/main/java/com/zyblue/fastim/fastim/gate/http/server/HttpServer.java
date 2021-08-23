package com.zyblue.fastim.fastim.gate.http.server;

import com.zyblue.fastim.fastim.gate.http.initializer.HttpInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HttpServer {

    private final static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    @Value("${server.port}")
    private int nettyPort;

    private static NioEventLoopGroup bossGroup;

    private static NioEventLoopGroup workerGroup;

    private static Channel serverChannel;

    @PostConstruct
    public void start() {
        /*
         * 开启服务
         */
        startHttpServer();
    }

    private void startHttpServer(){
        // bossGroup设置1个，参考NG。 在不 bind 多端口的情况下 BossEventLoopGroup 中只需要包含一个 EventLoop，也只能用上一个，多了没用。
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
        int cpuCount = Runtime.getRuntime().availableProcessors();
        workerGroup = new NioEventLoopGroup(cpuCount << 1 + 1, new DefaultThreadFactory("worker"));

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // 给服务器端连接请求队列中队列的大小,达到上限则不进行新的tcp连接
                .option(ChannelOption.SO_BACKLOG, 1024)
                // 客户端连接是否长连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 客户端连接开启Nagle算法，true表示关闭，false表示开启   高实时性就关闭，否则开启
                .childOption(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioServerSocketChannel>() {
                    @Override
                    protected void initChannel(NioServerSocketChannel ch) {
                        logger.info("服务端启动中");
                    }
                })
                .childHandler(new HttpInitializer());
        bind(serverBootstrap, nettyPort);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));
    }

    /**
     * 绑定端口
     */
    private void bind(ServerBootstrap serverBootstrap, Integer nettyPort){
        ChannelFuture future2 = serverBootstrap.bind(nettyPort).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("端口绑定成功 ImServer start success!");
            } else {
                logger.info("端口绑定失败! port:{}", nettyPort);
                logger.info("重新绑定ing");
                bind(serverBootstrap, nettyPort + 1);
            }
        });
        serverChannel =  future2.channel();
    }

    private void shutDown(){
        logger.info("http server stop...");
        if (serverChannel != null) {
            serverChannel.close();
        }
        if(bossGroup != null && !bossGroup.isShutdown()){
            bossGroup.shutdownGracefully();
        }

        if(workerGroup != null && !workerGroup.isShutdown()){
            workerGroup.shutdownGracefully();
        }

        logger.info("http server has been successfully stopped.");
    }
}
