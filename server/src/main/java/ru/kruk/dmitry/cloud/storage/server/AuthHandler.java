package ru.kruk.dmitry.cloud.storage.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.kruk.dmitry.cloud.storage.common.Command;
import ru.kruk.dmitry.cloud.storage.common.Sender;

import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;


public class AuthHandler extends ChannelInboundHandlerAdapter {
    private DataBaseAuthService authService;

    public AuthHandler(DataBaseAuthService authService){

        this.authService = authService;
    }

    public enum State {
        LOGIN, LOGIN_LENGTH, PASSWORD, PASSWORD_LENGTH;
    }

    private State currentState = State.LOGIN_LENGTH;
    private int loginLength;
    private int passwordLength;
    private String login;
    private String password;

private Logger logger = LogManager.getLogger();


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)  {
        ByteBuf buf = (ByteBuf) msg;
        try {
            getLoginAndPassword(buf);
        }catch (UnsupportedEncodingException e){
            logger.error(e);
        }
        String userDirectory = authService.getDirectoryByLoginPassword(login, password);

        if(userDirectory == null){
            Sender.sendCommand(ctx.channel(), Command.AUTH_ERR);
            logger.debug("Wrong login " + login + " or password: " + password);
            return;
        }else{
            Sender.sendCommand(ctx.channel(), Command.AUTH_OK);
            ctx.pipeline().addLast(new MainHandler(Paths.get("server" , userDirectory)));
            ctx.pipeline().remove(this);
            logger.debug("User: " + login + " is successfully authorized");

        }
    }

    private void getLoginAndPassword(ByteBuf buf) throws UnsupportedEncodingException {
        while(buf.readableBytes() > 0){
            if(currentState == State.LOGIN_LENGTH){
                if(buf.readableBytes() >= 4){
                    loginLength = buf.readInt();
                    logger.debug("STATE: Get login length - " + loginLength);
                    currentState = State.LOGIN;
                }
            }

            if(currentState == State.LOGIN){
                if(buf.readableBytes()>=loginLength){
                    byte[] loginBytes = new byte[loginLength];
                    buf.readBytes(loginBytes);
                    login = new String(loginBytes, "UTF-8");
                    logger.debug("STATE: login received - " + login);
                    currentState = State.PASSWORD_LENGTH;
                }
            }
            if(currentState == State.PASSWORD_LENGTH){
                if(buf.readableBytes()>=4){
                    passwordLength = buf.readInt();
                    logger.debug("STATE: Get password length - " + passwordLength);
                    currentState = State.PASSWORD;
                }
            }
            if(currentState == State.PASSWORD){
            if(buf.readableBytes()>=passwordLength) {
                byte[] passwordBytes = new byte[passwordLength];
                buf.readBytes(passwordBytes);
                password = new String(passwordBytes, "UTF-8");
                logger.debug("STATE: password received - " + password);
            }
            currentState = State.LOGIN_LENGTH;
            break;
            }
        }
        if(buf.readableBytes() == 0){
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error(cause);
        ctx.close();
    }
}
