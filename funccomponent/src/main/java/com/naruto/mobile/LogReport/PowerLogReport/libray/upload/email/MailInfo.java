/*
 * @(#)Snippet.java		       Project: CrashHandler
 * Date: 2014-5-27
 *
 * Copyright (c) 2014 CFuture09, Institute of Software, 
 * Guangdong Ocean University, Zhanjiang, GuangDong, China.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.naruto.mobile.LogReport.PowerLogReport.libray.upload.email;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 邮件类，用于构造要发送的邮件。
 * 邮箱不可用 550 User has no permission :
 *传入发送邮箱正确的用户名和密码时，总是收到到：550 User has no permission这样的错误，

 其实我们用Java发送邮件时相当于自定义客户端根据用户名和密码进行登录，然后使用SMTP服务发送邮件。但新注册的163邮件默认是不开启客户端授权验证的（对自定的邮箱大师客户端默认开启），

 因此登录总是会被拒绝，验证没有权限。解决办法是进入163邮箱，进入邮箱中心——客户端授权密码，选择开启即可，如
 */
public class MailInfo extends Authenticator {
    private String host;
    private String port;
    private String user;
    private String pass;
    private String from;
    private String to;
    private String subject;
    private String body;

    private Multipart multipart;
    private Properties props;

    public MailInfo() {
    }

    /**
     * @param user    邮箱登录名
     * @param pass    邮箱登录密码
     * @param from    发件人
     * @param to      收件人
     * @param host    主机
     * @param port    SMTP端口号
     * @param subject 邮件主题
     * @param body    邮件正文
     */
    public MailInfo(String user, String pass, String from, String to, String host, String port, String subject, String body) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public MailInfo setHost(String host) {
        this.host = host;
        return this;
    }

    public MailInfo setPort(String port) {
        this.port = port;
        return this;
    }

    public MailInfo setUser(String user) {
        this.user = user;
        return this;
    }

    public MailInfo setPass(String pass) {
        this.pass = pass;
        return this;
    }

    public MailInfo setFrom(String from) {
        this.from = from;
        return this;
    }

    public MailInfo setTo(String to) {
        this.to = to;
        return this;
    }

    public MailInfo setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MailInfo setBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * 初始化。它在设置好用户名、密码、发件人、收件人、主题、正文、主机及端口号之后显示调用。
     */
    public void init() {
        multipart = new MimeMultipart();
        //发送附件时有时候会报java-mail的Error, eg:javax.activation.UnsupportedDataTypeException: no object DCH for MIME type multipart/related;所以务必添加以下几行代码来确定DCH
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
        props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
    }

    /**
     * 发送邮件
     *
     * @return 是否发送成功
     * @throws MessagingException
     */
    public boolean send() throws MessagingException {
        if (!user.equals("") && !pass.equals("") && !to.equals("") && !from.equals("")) {
            Session session = Session.getDefaultInstance(props, this);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            InternetAddress addressTo = new InternetAddress(to);
            msg.setRecipient(MimeMessage.RecipientType.TO, addressTo);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            // setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart, 0);
            // Put parts in message
            msg.setContent(multipart);
            // send email
            Transport.send(msg);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加附件
     *
     * @param filePath 附件路径
     * @param fileName 附件名称
     * @throws Exception
     */
    public void addAttachment(String filePath, String fileName) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        ((MimeBodyPart) messageBodyPart).attachFile(filePath);
        multipart.addBodyPart(messageBodyPart);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, pass);
    }
}