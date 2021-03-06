package com.example.hhj73.fix;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;


public class EmailCertifActivity extends AppCompatActivity {
    Properties props;
    Session mailSession;
    MimeMessage message;
    EditText editText;
    String certificationNum;
    String client_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_certif);
        Intent intent = getIntent();
        client_email = intent.getStringExtra("client_email");

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads().permitDiskWrites().permitNetwork().build());

        sendEmail(client_email);

    }
    public void sendEmail(String client_email){
        String host = "smtp.gmail.com";
        String subject = "F.I.X 가입 인증번호 전달";
        String fromName = "F.I.X 관리자";
        String from = "fam.in.xy@gmail.com";
        String to1 = client_email;
        String content = "인증번호 ["+makeSerialNum()+"]<br>알맞게 기입해주세요.";

        props = new Properties();
        props.put("mail.transport.protocol", "smtp");       //프로토콜 설정
        props.put("mail.host",host);                          //gmail SMTP 서비스 주소(호스트)
        props.put("mail.smtp.port", "465");                  //gmail SMTP 서비스 포트 설정
        props.put("mail.smtp.starttls.enable", "true");
        //gmail 인증용 Secure Socket Layer(SSL) 설정
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.user",from);
        props.put("mail.smtp.auth", "true");              //SMTP 인증 설정

        javax.mail.Authenticator auth = new SMTPAuthenticator();
        mailSession = Session.getDefaultInstance(props, auth);

        message = new MimeMessage(mailSession);

        try{
            message.setFrom(new InternetAddress(from, MimeUtility.encodeText(fromName,"UTF-8","B")));
            InternetAddress[] address1 = {new InternetAddress(to1)};
            message.setRecipients(Message.RecipientType.TO, address1);
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");

            message.setSentDate(new Date());

            Transport.send(message);
        }
        catch (MessagingException e){
            System.out.println("Something Wrong!");
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public String makeSerialNum(){
        Random rnd = new Random();
        certificationNum = rnd.nextInt(8999)+1000+"";
        return certificationNum;
    }

    public void certification(View view) {
        editText = (EditText)findViewById(R.id.certificationNum);
        String userInput = editText.getText().toString();
        if(userInput.equals(certificationNum)){
            Toast.makeText(this,"OK",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),StudentJoinActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this,"Wrong Number. Check Again!",Toast.LENGTH_SHORT).show();
        }
    }

    public void resend(View view) {
        sendEmail(client_email);
    }
}
