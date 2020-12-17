package com.voicemail;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class CheckMail extends AsyncTask<Void,Void,Void> {
    private String host = "";
    private String port = "";
    private String uname = "";
    private String pass = "";
    private Context context;
    private ProgressDialog progressDialog;


    public CheckMail(Context context, String host, String port, String uname, String pass){
        this.context = context;
        this.host = host;
        this.port = port;
        this.uname = uname;
        this.pass = pass;
        Toast.makeText(context, "Came Inside", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context,"Fetching Mails","Please wait...",false,false);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        progressDialog.dismiss();
        //Showing a success message
        Toast.makeText(context,"Mails Fetched",Toast.LENGTH_LONG).show();
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            //Set property values
            Properties propvals = new Properties();
            propvals.put("mail.pop3.host", host);
            propvals.put("mail.pop3.port", "995");
            propvals.put("mail.pop3.starttls.enable", "true");
            Session emailSessionObj = Session.getDefaultInstance(propvals);
            //Create POP3 store object and connect with the server
            Store storeObj = emailSessionObj.getStore("pop3s");
            storeObj.connect(host, uname, pass);
            //Create folder object and open it in read-only mode
            Folder emailFolderObj = storeObj.getFolder("INBOX");
            emailFolderObj.open(Folder.READ_ONLY);
            //Fetch messages from the folder and print in a loop
            javax.mail.Message[] messageobjs = emailFolderObj.getMessages();

            for (int i = 0, n = messageobjs.length; i < n; i++) {
                javax.mail.Message indvidualmsg = messageobjs[i];
                Toast.makeText(this.context, "Printing individual messages", Toast.LENGTH_SHORT).show();
                Toast.makeText(this.context, "No# " + (i + 1), Toast.LENGTH_SHORT).show();
                Toast.makeText(this.context, "Email Subject: " + indvidualmsg.getSubject(), Toast.LENGTH_SHORT).show();
                Toast.makeText(this.context, "Sender: " + indvidualmsg.getFrom()[0], Toast.LENGTH_SHORT).show();
                Toast.makeText(this.context, "Content: " + indvidualmsg.getContent().toString(), Toast.LENGTH_SHORT).show();

            }
            //Now close all the objects
            emailFolderObj.close(false);
            storeObj.close();
        } catch (NoSuchProviderException exp) {
            exp.printStackTrace();
        } catch (MessagingException exp) {
            exp.printStackTrace();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return null;
    }
}
