package jmind.base.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by agile6v on 8/17/16.
 */
public class PSFClient {

    public static final String PSF_DEFAULT_CHARSET              = "UTF-8";

    public static final int PSF_MAX_SERVICE_TYPE_SIZE           = 16;
    public static final int SERVICE_CENTER_HOST_NUMBER_MAX      = 8;
    public static final int PSF_PROTO_MAGIC_NUMBER              = 0x23232323;

    public static final int PSF_PROTO_FID_ALLOC_SERVER          = 69;
    public static final int PSF_PROTO_FID_CLIENT_JOIN           = 71;
    public static final int PSF_PROTO_FID_RPC_REQ_NO_HEADER     = 75;
    public static final int PSF_PROTO_FID_RPC_REQ_WITH_HEADER   = 81;

    public static class PSFRPCRequestData
    {
        public String service_uri;
        public String data="";
        public Hashtable headers;
    }

    public static class PSFProtoHeader
    {
        public int magic_number;
        public byte func_id;
        public byte status;
        public int body_len;
    }

    public static class ConnectionInfo
    {
        Socket sock;
        int port;
        String ip_addr;

        public void connect(int connectTimeout, int networkTimeout) throws IOException
        {
            if (this.sock == null) {
                this.sock = new Socket();
                try {
                    this.sock.setSoTimeout(networkTimeout);
                    this.sock.connect(new InetSocketAddress(this.ip_addr, this.port), networkTimeout);
                } catch (IOException ex) {
                    this.close();
                    throw ex;
                }
            }
        }

        public void close()
        {
            try {
                if (this.sock != null) {
                    this.sock.close();
                    this.sock = null;
                }
            } catch (IOException ex) {
            }
        }

        protected void finalize()
        {
            this.close();
        }
    }

    public static class PSFClientConnectionInfo
    {
        boolean reallocate;
        boolean forbidden_alloc_server;
        ConnectionInfo conn;

        public PSFClientConnectionInfo()
        {
            this.reallocate = true;
            this.forbidden_alloc_server = false;
            this.conn = new ConnectionInfo();
        }

        public void connect(int connectTimeout, int networkTimeout) throws IOException
        {
            this.conn.connect(connectTimeout, networkTimeout);
        }

        public void close()
        {
            this.conn.close();
        }
    }

    public class SFServerInfo
    {
        public String ip_addr;
        public int port;

        public SFServerInfo(String ip_addr, int port) {
            this.ip_addr = ip_addr;
            this.port = port;
        }

    }

    public class PSFClientContext
    {
        public int connect_timeout;
        public int network_timeout;
        public int reconnect_times;
        public int last_connect_host_id;
        public int max_pkg_size;
        public byte[] send_recv_buf;
        public Map<String, PSFClientConnectionInfo> server_manager_hash;
        public SFServerInfo[] service_center_host;

        public PSFClientContext(String[] serviceCenter, int connect_timeout,
                                int network_timeout, int max_pkg_size, int reconnect_times) throws Exception
        {
            this.connect_timeout = connect_timeout;
            this.network_timeout = network_timeout;
            this.reconnect_times = reconnect_times;
            this.max_pkg_size = max_pkg_size;
            this.last_connect_host_id = 0;
            this.server_manager_hash = new ConcurrentHashMap<>();
            this.service_center_host = new SFServerInfo[serviceCenter.length];
            for (int i = 0; i < serviceCenter.length; i++) {
                String[] server = serviceCenter[i].split("\\:", 2);
                if (server.length != 2) {
                    throw new Exception("\"serviceCenter\" is invalid, the correct format is host:port");
                }

                this.service_center_host[i] = new SFServerInfo(server[0].trim(), Integer.parseInt(server[1].trim()));
            }

            this.send_recv_buf = new byte[max_pkg_size];
        }

        public void close()
        {
            if (this.server_manager_hash == null) {
                return;
            }
            Set<Map.Entry<String, PSFClientConnectionInfo>> entrySet;
            Iterator<Map.Entry<String, PSFClientConnectionInfo>> iterator;
            entrySet = this.server_manager_hash.entrySet();
            iterator = entrySet.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, PSFClientConnectionInfo> entry = iterator.next();
                PSFClientConnectionInfo managerConn = entry.getValue();
                managerConn.close();
            }
            this.server_manager_hash = null;
        }

        protected void finalize()
        {
            this.close();
        }
    }

    private PSFClientContext m_context;

    public String call(String serviceType, PSFRPCRequestData request) throws Exception
    {
        return this.call(serviceType, request, 0);
    }

    public String call(String serviceType, PSFRPCRequestData request,
                       int timeout) throws Exception
    {
        boolean restore = false;
        int default_network_timeout;
        PSFClientConnectionInfo clientInfo;
        String response = null;

        if (m_context == null) {
            throw new Exception("context may not have been initialized.");
        }

        if (serviceType.length() > PSF_MAX_SERVICE_TYPE_SIZE) {
            throw new Exception("serivce_type size is greater than 16.");
        }

        if ((10 + 2 + serviceType.length() + request.data.length()) > m_context.max_pkg_size) {
            throw new Exception("the packet size exceeds max_pkg_size.");
        }

        default_network_timeout = m_context.network_timeout;
        if (default_network_timeout != timeout && timeout > 0) {
            m_context.network_timeout = timeout;
            restore = true;
        }

        try {
            clientInfo = getServerManagerConn(m_context, serviceType);
            response = executeRpcCall(clientInfo, request, serviceType);
        } catch (Exception e) {
            throw e;
        }

        if (restore) {
            m_context.network_timeout = default_network_timeout;
        }

        return response;
    }

    private void close()
    {
        m_context.close();
    }

    public PSFClient(String[] serviceCenter) throws Exception
    {
        this(serviceCenter,2000,30000,64*1024,3);
    }

    public PSFClient(String[] serviceCenter, int connect_timeout, int network_timeout,
                     int max_pkg_size, int reconnect_times) throws Exception
    {
        this.init(serviceCenter, connect_timeout, network_timeout,
                max_pkg_size, reconnect_times);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                close();
            }
        });
    }

    private void init(String[] serviceCenter, int connect_timeout, int network_timeout,
                      int max_pkg_size, int reconnect_times) throws Exception
    {
        if (serviceCenter.length < 1 || serviceCenter.length > SERVICE_CENTER_HOST_NUMBER_MAX) {
            throw new Exception("the \"server_center\" is invalid, "
                    + "must be greater than 1 and less than " + SERVICE_CENTER_HOST_NUMBER_MAX);
        }

        m_context = new PSFClientContext(serviceCenter, connect_timeout,
                network_timeout, max_pkg_size, reconnect_times);
    }

    public void reconnectServerManager(PSFClientConnectionInfo info,
                                       String service_type) throws IOException
    {
        boolean ret = false;
        PSFProtoHeader header;

        for (int i = 0; i < m_context.reconnect_times; i++) {
            try {
                info.connect(m_context.connect_timeout, m_context.network_timeout);
                ret = true;
                break;
            } catch (IOException ex) {
                ex.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException err) {}
            }
        }

        if (ret == false) {
            info.reallocate = !info.forbidden_alloc_server;
            throw new IOException("failed to connect server manager");
        }

        try {
            int len = buildClientJoinPackage(m_context, service_type);

            OutputStream out = info.conn.sock.getOutputStream();
            out.write(m_context.send_recv_buf, 0, len);

            InputStream in = info.conn.sock.getInputStream();
            header = recvHeader(in);
            if (header.status != 0) {
                throw new IOException("recv header status != 0 from server manager.");
            }
        } catch (IOException e) {
            info.reallocate = !info.forbidden_alloc_server;
            info.close();
            throw e;
        }
    }

    public boolean connectServiceCenter(PSFClientContext context,
                                        ConnectionInfo conn)
    {
        int host_id;
        boolean ret = false;

        host_id = context.last_connect_host_id + 1;
        for (int i = 0; i < context.service_center_host.length; i++) {
            host_id = (host_id + i) % context.service_center_host.length;
            conn.port = context.service_center_host[host_id].port;
            conn.ip_addr = context.service_center_host[host_id].ip_addr;

            try {
                conn.connect(m_context.connect_timeout, m_context.network_timeout);
                context.last_connect_host_id = host_id;
                ret = true;
                break;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return ret;
    }

    public byte[] readFully(InputStream in, int expectLength) throws IOException
    {
        int remain;
        int bytes;
        byte[] data  = new byte[expectLength];
        remain = expectLength;
        while (remain > 0 && (bytes=in.read(data, expectLength - remain, remain)) > 0) {
            remain -= bytes;
        }

        if (remain != 0) {
            throw new IOException("connection closed");
        }

        return data;
    }

    public PSFProtoHeader recvHeader(InputStream in) throws IOException
    {
        int bytes;
        byte[] recv_buff = this.readFully(in, 10);

        PSFProtoHeader header = new PSFProtoHeader();
        header.magic_number = buff2int(recv_buff, 0);
        header.func_id = recv_buff[4];
        header.status = recv_buff[5];
        header.body_len = buff2int(recv_buff, 6);

        if (header.magic_number != PSF_PROTO_MAGIC_NUMBER) {
            throw new IOException("recv package magic_number " + header.magic_number + " != " + PSF_PROTO_MAGIC_NUMBER);
        }

        if (header.body_len < 0) {
            throw new IOException("recv package body_len " + header.body_len + " < 0");
        }

        return header;
    }


    public void allocateFromServiceCenter(PSFClientConnectionInfo clientInfo,
                                          String serivce_type) throws IOException
    {
        boolean ret;
        byte[] recvBuff = null;
        ConnectionInfo serviceCenterConn = new ConnectionInfo();

        ret = connectServiceCenter(m_context, serviceCenterConn);
        if (!ret) {
            throw new IOException("failed to connect to service center");
        }

        try {
            int len = buildAllocatePackage(m_context, serivce_type);

            OutputStream out = serviceCenterConn.sock.getOutputStream();
            out.write(m_context.send_recv_buf, 0, len);

            InputStream in = serviceCenterConn.sock.getInputStream();

            PSFProtoHeader header = recvHeader(in);
            if (header.body_len > 0) {
                recvBuff = this.readFully(in, header.body_len);
            }

            if (header.status != 0) {
                String error;
                error = "recv package status " + header.status + " != 0";
                if (recvBuff != null) {
                    error += ", error info: " + new String(recvBuff, PSF_DEFAULT_CHARSET);
                }
                throw new IOException(error);
            }

            if (header.body_len <= 3) {
                throw new IOException("recv package body_len " + header.body_len + " <= 3");
            }

            /* server manager ip & port */
            clientInfo.conn.port = buff2short(recvBuff, 0);
            clientInfo.conn.ip_addr = new String(recvBuff, 3, recvBuff[2]);
        } finally {
            serviceCenterConn.close();
        }
    }

    public void reconnectServerManagerEx(PSFClientConnectionInfo info,
                                         String service_type) throws IOException
    {
        try {
            reconnectServerManager(info, service_type);
        } catch (IOException e) {
            checkAllocAndConnectServerManager(info, service_type);
        }
    }

    public int checkAllocAndConnectServerManager(PSFClientConnectionInfo info,
                                                 String service_type) throws IOException
    {
        if (info.reallocate) {
            info.close();
            allocateFromServiceCenter(info, service_type);
            info.reallocate = false;
            info.forbidden_alloc_server = false;
        }

        reconnectServerManager(info, service_type);
        return 0;
    }

    public PSFClientConnectionInfo getServerManagerConn(PSFClientContext context,
                                                        String service_type) throws IOException
    {
        PSFClientConnectionInfo connInfo;

        connInfo = context.server_manager_hash.get(service_type);
        if (connInfo == null) {
            connInfo = new PSFClientConnectionInfo();
            checkAllocAndConnectServerManager(connInfo, service_type);
            context.server_manager_hash.put(service_type, connInfo);
        } else if (connInfo.conn.sock == null) {
            checkAllocAndConnectServerManager(connInfo, service_type);
        }

        return connInfo;
    }

    public String executeRpcCall(PSFClientConnectionInfo clientInfo,
                                 PSFRPCRequestData request, String service_type) throws IOException
    {
        PSFProtoHeader header;
        byte[] data;
        String response = null;
        boolean retry = true;
        InputStream in;

        do {
            try {
                int len = buildRpcRequestPackage(m_context, request);

                try {
                    OutputStream out = clientInfo.conn.sock.getOutputStream();
                    out.write(m_context.send_recv_buf, 0, len);
                } catch (IOException e) {
                    if (retry) {
                        retry = false;
                        reconnectServerManagerEx(clientInfo, service_type);
                        continue;
                    }
                    throw e;
                }

                try {
                    in = clientInfo.conn.sock.getInputStream();
                    header = recvHeader(in);
                } catch (IOException e) {
                    if (retry) {
                        retry = false;
                        clientInfo.reallocate = !clientInfo.forbidden_alloc_server;
                        checkAllocAndConnectServerManager(clientInfo, service_type);
                        continue;
                    }
                    throw e;
                }

                if (header.status != 0) {
                    throw new IOException("failed to connect to service manager");
                }

                if (header.body_len > 0) {
                    data = this.readFully(in, header.body_len);
                    response = new String(data, PSF_DEFAULT_CHARSET);
                }
            } catch (IOException e) {
                clientInfo.close();
                throw e;
            }

            break;
        } while (true);

        return response;
    }

    public int buildRpcRequestPackage(PSFClientContext context,
                                      PSFRPCRequestData request) throws UnsupportedEncodingException
    {
        int header_count;
        int header_len;
        int func_id;
        int body_len;
        byte[] buff;
        byte[] header;
        byte[] service_uri;
        byte[] data;
        StringBuilder str = new StringBuilder();

        if (request.headers != null && (header_count = request.headers.size()) > 0) {
            Enumeration e = request.headers.keys();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                str.append(key + "=" + request.headers.get(key));
                if (--header_count > 0) {
                    str.append("&");
                }
            }
            header = str.toString().getBytes(PSF_DEFAULT_CHARSET);
            header_len = header.length;
        } else {
            header = null;
            header_len = 0;
        }

        data = request.data.getBytes(PSF_DEFAULT_CHARSET);
        service_uri = request.service_uri.getBytes(PSF_DEFAULT_CHARSET);

        body_len = 2 + service_uri.length + data.length;
        if (header_len == 0) {
            func_id = PSF_PROTO_FID_RPC_REQ_NO_HEADER;
        } else {
            func_id = PSF_PROTO_FID_RPC_REQ_WITH_HEADER;
            body_len += 2 + header_len;
        }

        int offset = buildPsfProtoHeader(context.send_recv_buf,
                func_id, 0, body_len);

        if (header_len > 0) {
            buff = short2buff((short) header_len);
            System.arraycopy(buff, 0, context.send_recv_buf, offset, buff.length);
            offset += buff.length;
        }

        buff = short2buff((short)service_uri.length);
        System.arraycopy(buff, 0, context.send_recv_buf, offset, buff.length);
        offset += buff.length;

        System.arraycopy(service_uri, 0, context.send_recv_buf, offset, service_uri.length);
        offset += service_uri.length;

        System.arraycopy(data, 0, context.send_recv_buf, offset, data.length);
        offset += data.length;

        if (header_len > 0) {
            System.arraycopy(header, 0, context.send_recv_buf, offset, header.length);
            offset += header.length;
        }

        return offset;
    }

    public int buildPsfProtoHeader(byte[] header, int func_id, int status, int body_len)
    {
        byte[] hex_len;
        int offset;

        offset = 0;
        hex_len = int2buff(PSF_PROTO_MAGIC_NUMBER);
        System.arraycopy(hex_len, 0, header, offset, hex_len.length);
        offset += hex_len.length;
        header[offset++] = (byte)func_id;
        header[offset++] = (byte)status;
        hex_len = int2buff(body_len);
        System.arraycopy(hex_len, 0, header, offset, hex_len.length);
        offset += hex_len.length;

        return offset;
    }

    public int buildAllocatePackage(PSFClientContext context, String service_type)
            throws UnsupportedEncodingException
    {
        byte[] data;
        int body_len = 1/* service_type_len(1byte) */ + service_type.length();

        int offset = buildPsfProtoHeader(context.send_recv_buf,
                PSF_PROTO_FID_ALLOC_SERVER, 0, body_len);

        context.send_recv_buf[offset++] = (byte) service_type.length();
        data = service_type.getBytes(PSF_DEFAULT_CHARSET);
        System.arraycopy(data, 0, context.send_recv_buf, offset, data.length);
        offset += data.length;

        return offset;
    }

    public int buildClientJoinPackage(PSFClientContext context, String service_type)
            throws UnsupportedEncodingException
    {
        byte[] data;

        int body_len = 1 /* service_type_len(1byte) */ + service_type.length();

        int offset = buildPsfProtoHeader(context.send_recv_buf,
                PSF_PROTO_FID_CLIENT_JOIN, 0, body_len);

        context.send_recv_buf[offset++] = (byte) service_type.length();

        data = service_type.getBytes(PSF_DEFAULT_CHARSET);
        System.arraycopy(data, 0, context.send_recv_buf, offset, data.length);
        offset += data.length;

        return offset;
    }

    /**
     * buff convert to int
     * @param bs the buffer (big-endian)
     * @param offset the start position based 0
     * @return int number
     */
    public static int buff2int(byte[] bs, int offset)
    {
        return  (((int)(bs[offset] >= 0 ? bs[offset] : 256+bs[offset])) << 24) |
                (((int)(bs[offset+1] >= 0 ? bs[offset+1] : 256+bs[offset+1])) << 16) |
                (((int)(bs[offset+2] >= 0 ? bs[offset+2] : 256+bs[offset+2])) <<  8) |
                ((int)(bs[offset+3] >= 0 ? bs[offset+3] : 256+bs[offset+3]));
    }

    /**
     * int convert to buff (big-endian)
     * @param n number
     * @return 4 bytes buff
     */
    public static byte[] int2buff(int n)
    {
        byte[] bs;

        bs = new byte[4];
        bs[0] = (byte)((n >> 24) & 0xFF);
        bs[1] = (byte)((n >> 16) & 0xFF);
        bs[2] = (byte)((n >> 8) & 0xFF);
        bs[3] = (byte)(n & 0xFF);

        return bs;
    }

    /**
     * buff convert to int
     * @param bs the buffer (big-endian)
     * @param offset the start position based 0
     * @return int number
     */
    public static int buff2short(byte[] bs, int offset)
    {
        return (((int)(bs[offset] >= 0 ? bs[offset] : 256+bs[offset])) << 8) |
                ((int)(bs[offset+1] >= 0 ? bs[offset+1] : 256+bs[offset+1]));
    }

    public static byte[] short2buff(short n)
    {
        byte[] b = new byte[2];
        b[1] = (byte) (n & 0xff);
        b[0] = (byte) ((n >> 8) & 0xff);
        return b;
    }


//    public static void main(String[] args) throws Exception {
//
//        int count = 0;
//
//        String[] serviceCenter = {"10.0.11.243:5201", "10.0.11.244:5201"};
//
//        PSFClient psf = new PSFClient(serviceCenter, 2000, 30000, 64 * 1024, 3);
//
//        String response = new String();
//        PSFClient.PSFRPCRequestData request = new PSFClient.PSFRPCRequestData();
//        Hashtable hashtable = new Hashtable();
//        hashtable.put("uid", 5555);
//        hashtable.put("a",10);
//        request.headers = hashtable;
//        request.data = "{\"user_id\":\"5555\",\"full\":true,\"user_type\":\"PA\", \"哦哦哦哦哦\": \"啦啦啦啦啦\"}";
//        request.service_uri = "/device/getDeviceIdByUserId";
//
//        while (true) {
//            if (count++ == 1000000) {
//                break;
//            }
//
//            try {
//                response = psf.call("device", request);
//                if (count % 10000 == 0) {
//                    System.out.println(response);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        psf.close();
//        System.out.println("Done.");
//    }
}

