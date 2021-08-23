package com.zyblue.fastim.common.codec;


import com.zyblue.fastim.common.constant.CommonConstant;

/**
 * @author will
 * @date 2021/7/6 15:30
 */
public class FastImMsg {
    /**
     * 版本
     */
    private int version;

    /**
     * 业务类型，心跳、推送、单聊、群聊、系统消息、聊天室、客服
     * see cmdType
     */
    private byte cmd;

    /**
     * 消息类型 request response notify
     */
    private byte msgType;

    /**
     * 调试性日志
     */
    private int logId;

    /**
     * 序列化ID，可以用作异步处理
     */
    private int sequenceId;

    /**
     * 消息的内容
     */
    private byte[] data;

    public byte getHeadData() {
        /*
         * 消息的开头的信息标志
         */
        return CommonConstant.Protocol.HEAD_DATA;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte getCmd() {
        return cmd;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
