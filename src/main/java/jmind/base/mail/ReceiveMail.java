package jmind.base.mail;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

public class ReceiveMail {

    public static void main(String[] args) throws IOException {
        String password = null;
        String user = null;
        String pop = "pop.163.com";
        boolean b1 = false;
        boolean b2 = true;
        int count = Integer.MAX_VALUE;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-h") || arg.equals("--help")) {
                use();
                System.exit(0);
            }
            if (arg.equals("-u")) {
                user = args[++i];
            } else if (arg.equals("-p")) {
                password = args[++i];
            } else if (arg.equals("-x")) {
                pop = args[++i];
            } else if (arg.equals("-f")) {
                b2 = false;
                try {
                    setOut(new FileWriter(args[++i]));
                } catch (Exception e) {
                    b2 = true;
                    // TODO: handle exception
                }
            } else if (args.equals("-v")) {

                b1 = true;
            } else {
                try {
                    count = Integer.parseInt(arg);
                } catch (Exception e) {
                    System.err.println(arg + "is not a number !");
                }

            }

        }
        if (b2)
            setOut(new PrintWriter(System.out));
        // if(b)setOut(new PrintWriter(new FileWriter("d:\\bobo.txt")));
        if (user == null || password == null) {
            use();
            System.exit(-1);
        }
        //调用receive方法收信
        receive(user, password, pop, count, b1);
        close();
    }

    private static void use() {
        String s1 = "用法： java ReceiveMail  必选 [-u 用户名] [-p 密码] ";
        String s2 = "可选：[-x pop服务器(默认pop.163.com)][-f 输出文件名] [-v 打印邮件内容]  [(int)邮件数量]";
        System.out.println(s1);
        System.out.println(s2);
    }

    public static void close() {
        try {
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setOut(Writer out) {
        ReceiveMail.out = out;
    }

    /**
     * 邮件接收
     */
    protected static void receive(String user, String password, String pop, int count, boolean b1) {

        try {
            Properties prop = new Properties();
            Session ses = Session.getInstance(prop);
            // 使用 pop3协议

            Store store = ses.getStore("pop3");
            //传入收邮件服务器，用户名和密码。
            store.connect(pop, user, password);
            if (store.isConnected()) {
                Folder inbox = store.getFolder("INBOX");
                if (inbox.exists()) {
                    inbox.open(Folder.READ_ONLY);
                    int nCount = inbox.getMessageCount();
                    System.out.println("Inbox contains " + nCount + " messages");
                    // 依次显示收件箱中的每封邮件
                    nCount = Math.min(nCount, count);
                    for (int i = 1; i <= nCount; i++) {
                        MimeMessage msg = (MimeMessage) inbox.getMessage(i);
                        p("Subject : " + msg.getSubject());
                        p("From : " + msg.getFrom()[0].toString());
                        p("Content type : " + msg.getContentType());
                        p("发送时间" + msg.getSentDate());

                        if (b1)
                            p("内容：" + msg.getContent());
                        p("---------" + i + "--------");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *  邮件接收
     * @param user 用户名
     * @param password  密码
     * @param pop 邮件服务
     * @param count 条数
     *
     */
    protected static void receive(String user, String password, String pop, int count) {

        try {
            Properties prop = new Properties();
            Session ses = Session.getInstance(prop);
            // 使用 pop3协议

            Store store = ses.getStore("pop3");
            //传入收邮件服务器，用户名和密码。
            store.connect(pop, user, password);
            if (store.isConnected()) {
                Folder inbox = store.getFolder("INBOX");
                if (inbox.exists()) {
                    inbox.open(Folder.READ_WRITE);

                    int nCount = inbox.getMessageCount();
                    System.out.println("Inbox contains " + nCount + " messages");
                    // 依次显示收件箱中的每封邮件
                    nCount = Math.min(nCount, count);
                    int numberOfNew = inbox.getNewMessageCount();
                    System.out.println(numberOfNew);
                    for (int i = 1; i <= nCount; i++) {
                        MimeMessage msg = (MimeMessage) inbox.getMessage(i);
                        String subject = msg.getSubject();
                        System.out.println("Subject : " + msg.getSubject());
                        if (subject != null && subject.contains("failure")) {
                            p("Subject : " + msg.getSubject());
                            p("From : " + msg.getFrom()[0].toString());
                            p("Content type : " + msg.getContentType());
                            p("发送时间" + msg.getSentDate());

                            msg.setFlag(Flags.Flag.DELETED, false);
                            p("内容：" + msg.getContent());
                            p("---------" + i + "-------------------------");
                        }

                    }

                }
                //  ---------------------------

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static Writer out;

    private static void p(String s) throws IOException {
        out.write(s);
        out.write("\n");
        out.flush();
    }
}
