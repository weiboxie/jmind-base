package jmind.base.mail;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import jmind.base.util.DataUtil;
import jmind.base.util.GlobalConstants;

import com.sun.mail.smtp.SMTPTransport;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SendMail {

    /**
     * 
     * @param smtpServer 邮件服务器  mail.letv.com
     * @param from 发送邮箱
     * @param password  密码
     * @param to 接收邮箱，多个用逗号隔开，不能用分号
     * @param name  
     * @param subject  主题
     * @param body 内容，支持html
     * @return
     */
    public static boolean sendMail(String smtpServer, String from, String password, String to, String name,
            String subject, String body) {
        //  smtpServer = "mail.opi-corp.com";
        //  userName="weibo.xie@opi-corp.com";
        //  password="bobo04";
        //  from = "weibo.xie@opi-corp.com";
        try {
            // get Properties
            Properties props = System.getProperties();
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.localhost", smtpServer);
            //    props.put("mail.smtp.port", 25);
            Session session;

            if (DataUtil.isEmpty(from)) {
                props.put("mail.smtp.auth", "false");
                session = Session.getDefaultInstance(props, null);

            } else {
                props.put("mail.smtp.auth", "true");
                SmtpAuth sa = new SmtpAuth(from, password);
                // create Session
                session = Session.getInstance(props, sa);
            }
            // create Mail Message
            MimeMessage msg = new MimeMessage(session);
            InternetAddress ia = new InternetAddress(from, name, GlobalConstants.UTF8);
            msg.setFrom(ia);
            // To address
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            // Subject
            msg.setSubject(subject, "UTF-8");

            // body
            msg.setContent(body, "text/html; charset=UTF-8");

            msg.setSentDate(new Date());
            //_log.info("set message ok!!!!!!!!!!!!");
            // Send off
            Transport.send(msg);

            //_log.info("Message sent OK.");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 
     * @param smtpServer
     * @param from
     * @param password
     * @param to
     * @param name
     * @param subject
     * @param body
     * @param file  附件 File 或者文件地址都可以
     * @return
     */
    public static boolean sendMail(String smtpServer, String from, String password, String to, String name,
            String subject, String body, Vector<?> file) {

        try {
            // get Properties
            Properties props = System.getProperties();
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.localhost", smtpServer);
            Session session;
            //_log.info("send mail start$$$$$$$$$$$$");
            if (!from.equals("")) {
                //_log.info("user"+userName);
                props.put("mail.smtp.auth", "true");
                SmtpAuth sa = new SmtpAuth(from, password);
                // create Session
                session = Session.getInstance(props, sa);
            } else {
                //_log.info("start send mail start$$$$$$$$$$$$ false");
                props.put("mail.smtp.auth", "false");
                session = Session.getDefaultInstance(props, null);
            }
            // create Mail Message
            MimeMessage msg = new MimeMessage(session);
            // From address
            //    InternetAddress ia = new InternetAddress(from, showName, "UTF-8");
            InternetAddress ia = new InternetAddress(from, name, GlobalConstants.UTF8);
            msg.setFrom(ia);
            // To address
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            // Subject
            msg.setSubject(subject, GlobalConstants.UTF8);

            // body
            msg.setContent(body, "text/html; charset=UTF-8");
            // Header
            msg.setHeader("X-Mailer", "LOTONtechEmail");
            // Date
            msg.setSentDate(new Date());
            //_log.info("set message ok!!!!!!!!!!!!");
            // Send off
            Multipart mp = new MimeMultipart();
            // Get accessory when it exist

            if (file != null) {
                Enumeration<?> efile = file.elements();
                while (efile.hasMoreElements()) {
                    MimeBodyPart mbp = new MimeBodyPart();
                    // Get every filename
                    String filename = efile.nextElement().toString();
                    // Get accessory's FileDataSource
                    FileDataSource fds = new FileDataSource(filename);
                    // Get accessory and add it into MimeBodyPart
                    mbp.setDataHandler(new DataHandler(fds));
                    // Add Filename into MimeBodyPart
                    mbp.setFileName(fds.getName());
                    mp.addBodyPart(mbp);
                }
                // Remove all elements from file Vector
                file.removeAllElements();
            }

            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText(body);
            mp.addBodyPart(mbp);
            msg.setContent(mp);
            Transport.send(msg);
            return true;
        } catch (Exception e) {

            e.printStackTrace();

        }
        return false;

    }

    /**
     * 
     * @param smtpServer  邮件服务器
     * @param user 验证用户
     * @param password 密码
     * @param to 接收邮箱 ，多个用逗号隔开，不能用分号
     * @param fromMail  显示发送邮箱 ，可以与user相同， 也可以自定义显示发送邮箱，随便写 例如 abc@def.com
     * @param name  显示的名字
     * @param subject  标题
     * @param body  内容
     * @param file  附件
     * @see http://sendcloud.sohu.com/doc/downloads/code/java/java/#smtp
     * @return
     */
    public static boolean sendBySMTP(final String smtpServer, final String user, final String password, String to,
            String fromMail, String name, String subject, String body, File file) {

        try {
            // 配置javamail
            Properties props = System.getProperties();
            props.setProperty("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.port", 25);
            props.setProperty("mail.smtp.auth", "true");
            props.put("mail.smtp.connectiontimeout", 180);
            props.put("mail.smtp.timeout", 600);
            props.setProperty("mail.mime.encodefilename", "true");

            Session mailSession = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });

            SMTPTransport transport = (SMTPTransport) mailSession.getTransport("smtp");

            MimeMessage message = new MimeMessage(mailSession);
            // 发信人
            message.setFrom(new InternetAddress(fromMail, name, GlobalConstants.UTF8));
            // 收件人地址
            //     message.addRecipient(RecipientType.TO, new InternetAddress(to));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            // 邮件主题
            message.setSubject(subject, GlobalConstants.UTF8);

            Multipart multipart = new MimeMultipart("alternative");

            // 添加html形式的邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setHeader("Content-Type", "text/html;charset=UTF-8");
            contentPart.setHeader("Content-Transfer-Encoding", "base64");
            contentPart.setContent(body, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);

            // 添加附件 ( smtp 方式没法使用文件流 )
            if (file != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                attachmentBodyPart.setFileName(MimeUtility.encodeWord(file.getName()));
                multipart.addBodyPart(attachmentBodyPart);
            }
            message.setContent(multipart);
            // 连接sendcloud服务器，发送邮件
            transport.connect();
            transport.sendMessage(message, message.getRecipients(RecipientType.TO));
            //        transport.getLastServerResponse() ;
            transport.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        boolean b = sendMail("mail.letv.com", "xieweibo@letv.com", "password", "xieweibo@letv.com", "zhangyouxing",
                "zh标题", "内容sfdsf");
        System.out.println(b);
    }

}
