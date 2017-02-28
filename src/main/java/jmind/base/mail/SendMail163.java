package jmind.base.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail163 {

    public static void main(String[] args) {
        if (args.length < 1) {
            String ss = "请在类名后，输入用户名和密码";
            System.out.println(ss);
            System.exit(-1);
        }

        String userName = args[0];
        String password = args[1];

        String smtpHost = "smtp.163.com";
        String from = "weiboxie@163.com";
        String to = "weiboxie@sina.com";//"Richard.SunRui@gmail.com";
        SmtpAuth auth = null;

        //获得系统属性
        Properties pro = System.getProperties();
        auth = new SmtpAuth(userName, password);
        //auth=new PasswordAuthentication("haierreiah","302302");
        //设置邮件服务器
        pro.put("mail.smtp.host", smtpHost);
        pro.put("mail.smtp.auth", "true");
        pro.put("mail.smtp.port", "25");

        //获取绘画对象
        Session ses = Session.getDefaultInstance(pro, auth);
        ses.setPasswordAuthentication(new URLName(smtpHost), auth.getPasswordAuthentication());
        //定义message
        MimeMessage message = new MimeMessage(ses);
        try {
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Jmail tset");
            message.setText("成功哦，恭喜！！！我来了 哈哈");
            //发送message
            Transport.send(message);
            System.out.print("send successfully");

        } catch (AddressException e) {

            e.printStackTrace();
        } catch (MessagingException ex) {

            ex.printStackTrace();

        }
    }

}