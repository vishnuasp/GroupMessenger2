package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.TextView;
import android.telephony.TelephonyManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import android.widget.Button;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.buffalo.cse.cse486586.groupmessenger2.R;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeoutException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Set;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 *
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final int SERVER_PORT = 10000;
    static final String REMOTE_PORTS[] = {"11108", "11112", "11116", "11120", "11124"};
    static int proposal_num = 0;
    static int agreement_num = 0;
    static int port_number = 0;
    static PriorityBlockingQueue<MessageDetails> Queue_Msgs = new PriorityBlockingQueue<MessageDetails>(100, new MComparator());
    static ConcurrentHashMap<String, MessageDetails> msgMap = new ConcurrentHashMap<String, MessageDetails>();
    static ConcurrentHashMap<String, String> pMap = new ConcurrentHashMap<String, String>();
    String myDelimiter = "###";
    int msg_counter = 0;
    int p_counter =0;
    int a_counter =0;
    int pc =0;

    public int maxi(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    public void printQueue(PriorityBlockingQueue<MessageDetails> Q){
        while (!Q.isEmpty()){
            MessageDetails obx = Q.poll();
            Log.v(TAG,"ID : "+obx.msg_id+"Prop :"+obx.proposal_no+"Port :"+obx.port_no);
        }

    }
    public String getPMaxnCount(String id){
        String mppn, searcher;
        String result = "FAIL";
        int maxppn=0;int k=0;
        int maxport=0;
        Log.e(TAG, "id is : "+id);
        Log.e(TAG, "Checking pMaxCount ");
        for(int j=0;j<5;j++)
        {
            searcher = id+myDelimiter+REMOTE_PORTS[j];
            if(pMap.containsKey(searcher)){
                pc++;
                mppn = pMap.get(searcher);
                k = Integer.parseInt(mppn);
                if(k>maxppn){
                    maxppn = k;
                    maxport = Integer.parseInt(REMOTE_PORTS[j]);
                }

            }
        }
        Log.e(TAG, "PC is = "+Integer.toString(pc));
        if(pc == 5){
            Log.e(TAG, "PC = 5");
            result = "";
            String x = Integer.toString(maxppn);
            String y = Integer.toString(maxport);
            Log.e(TAG, "X = "+x+"Y = "+y);
            result = "T"+myDelimiter+x+myDelimiter+y;
        }
        return result;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());

        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));

        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        port_number = Integer.parseInt(myPort);
        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             *
             * AsyncTask is a simplified thread construct that Android provides. Please make sure
             * you know how it works by reading
             * http://developer.android.com/reference/android/os/AsyncTask.html
             */
            //creating server socket.
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            /*
             * Log is a good way to debug your code. LogCat prints out all the messages that
             * Log class writes.
             *
             * Please read http://developer.android.com/tools/debugging/debugging-projects.html
             * and http://developer.android.com/tools/debugging/debugging-log.html
             * for more information on debugging.
             */
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }

        /*
         * Retrieve a pointer to the input box (EditText) defined in the layout
         * XML file (res/layout/main.xml).
         *
         * This is another example of R class variables. R.id.edit_text refers to the EditText UI
         * element declared in res/layout/main.xml. The id of "edit_text" is given in that file by
         * the use of "android:id="@+id/edit_text""
         */
        final EditText editText = (EditText) findViewById(R.id.editText1);
        //send button
        final Button send_button = (Button) findViewById(R.id.button4);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String message = editText.getText().toString();
                editText.setText("");
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,message,myPort,"FIRST");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }

    // usage of code from pa1 for client and server tasks.
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];

            while(true){
                try{
                    Socket sock = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                    String input = in.readLine();
                    //String agmt="";
                    // PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                    // out.println(input);
                    // String s1 ="";
                    // publishProgress(input);

                    MessageDetails msg_obj = new MessageDetails();
                    //String agmt = "", typeo = "";
                   // String[] agmt_data = new String[6];

                    Log.e(TAG, "Got request from Client " + "");
                    String[] msg_data = input.split(myDelimiter);
                    msg_obj.setType(msg_data[3]);
                    msg_obj.setMessage(msg_data[4]);
                    msg_obj.setPortNo(Integer.parseInt(msg_data[2]));
                    msg_obj.setProposalNo(Integer.parseInt(msg_data[1]));
                    msg_obj.setID(msg_data[0]);
                    msg_obj.setDeliverability(Boolean.parseBoolean(msg_data[5]));
                    Log.e(TAG, "Message txt is:" + msg_data[4]);
//                    ArrayList<MessageDetails> ml = new ArrayList<MessageDetails>();

                    if (msgMap.containsKey(msg_obj.msg_id) == false) {
                        msgMap.put(msg_obj.msg_id, msg_obj);
                    }
                    String t = msg_obj.msg_type;
                    if (t.equals("FIRST")) {

//                        ml.add(msg_obj);
                        Log.e(TAG, "Assigning Proposal Number : " + "");
                        //proposal_num++;
                        proposal_num = maxi(proposal_num, agreement_num) + 1;
                        msg_obj.proposal_no = proposal_num;
                        msg_obj.port_no = port_number;
                        msg_obj.msg_type = "ACK";
                        Queue_Msgs.offer(msg_obj);
                        msgMap.put(msg_obj.msg_id, msg_obj);
                        String pn = Integer.toString(port_number);
                        String pp = Integer.toString(proposal_num);
                        String s = "";
                        s = msg_obj.msg_id + myDelimiter + msg_obj.proposal_no + myDelimiter + msg_obj.port_no + myDelimiter + msg_obj.msg_type +
                                myDelimiter + msg_obj.msg_val
                                + myDelimiter + msg_obj.is_deliverable;

                        PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                        out.println(s);
                        Log.e(TAG, "Sent Proposal Number ");

                        //BufferedReader in1 = new BufferedReader(new InputStreamReader(sock1.getInputStream()));
                        //agmt = in1.readLine();
                        //Log.e(TAG, "Received Agreement Message"+agmt);

                        //agmt_data = agmt.split(myDelimiter);
                        //String typeo = agmt_data[3];
                        //publishProgress("ACK", msg_obj.msg_id, msg_obj.msg_val,pn,s);
                        //Log.e(TAG, "Typeo: " + typeo);

                    }
                    else if (t.equals("AGREEMENT")) {
                        //Log.e(TAG, "DOinB ST AGREEMENT : " + "");
                        int maxp = Integer.parseInt(msg_data[1]);
                        boolean removal;
                        PriorityBlockingQueue<MessageDetails> Q1 = new PriorityBlockingQueue<MessageDetails>(Queue_Msgs);
                        Log.v(TAG,"Q before:");
                        printQueue(Q1);
                        MessageDetails m1 = msgMap.get(msg_obj.msg_id);
                        removal = Queue_Msgs.remove(m1);
                        //m1.proposal_no = msg_obj.proposal_no;
                        m1.proposal_no = maxp;
                        m1.port_no = Integer.parseInt(msg_data[2]);
                        m1.is_deliverable = true;
                        agreement_num = maxi(agreement_num,maxp);
                        if (removal == true) {
                            Queue_Msgs.offer(m1);
                            PriorityBlockingQueue<MessageDetails> Q2 = new PriorityBlockingQueue<MessageDetails>(Queue_Msgs);
                            Log.v(TAG,"Q after:");
                            printQueue(Q2);
                        }
                        while((Queue_Msgs.peek() != null) && Queue_Msgs.peek().is_deliverable == true){
                            //Log.e(TAG, "Queue: " + Queue_Msgs.peek().msg_id);
                            if (Queue_Msgs.peek().is_deliverable == true) {
                               publishProgress("DELIVER", "", "");
                                //publishProgress(Queue_Msgs.peek());
                            }
                        }
//                        if (Queue_Msgs.peek() != null) {
//                            Log.e(TAG, "Queue: " + Queue_Msgs.peek().msg_id);
//                            if (Queue_Msgs.peek().is_deliverable == true) {
//                                publishProgress("DELIVER", "", "");
//                            }
//                        }
                    }
                }
                catch(IOException e){
                    // catch exception here...
                    //Log.e(TAG, "IO Exception");
                }
                catch(NullPointerException e){
                    // catch exception here...
                    Log.e(TAG, "NullPointerException server");
                }
            }

            //return null;
        }

        protected void onProgressUpdate(String... strings) {
            //Log.e(TAG, "OPD ST" + "");
          /*
           * The following code displays what is received in doInBackground().
           */
            //MessageDetails msg_del = message[0];





            String msg_type = strings[0];
//            String msg_id = "";
            String msg_vals ="";
//            //String pn = strings[3];
//          /*
//           * The following code creates a file in the AVD's internal storage and stores a file.
//           *
//           * For more information on file I/O on Android, please take a look at
//           * http://developer.android.com/training/basics/data-storage/files.html
//           */
           MessageDetails msg_obj;
//
           if (msg_type.equals("DELIVER")) {
               // Log.e(TAG, "OPD ST DELIVER: " + "");
//                while (!Queue_Msgs.isEmpty()) {
                  if(!Queue_Msgs.isEmpty()) {
//                    Log.e(TAG, "Insert Key-Val " + "");
                      msg_obj = Queue_Msgs.poll();
//                    msg_id = msg_obj.msg_id;
                      msg_vals = msg_obj.msg_val;
//


                      TextView tv = (TextView) findViewById(R.id.textView1);
                      tv.append((msg_counter) + "   " + msg_vals + "\t\n");


                      Uri.Builder createuri = new Uri.Builder();
                      createuri.authority("edu.buffalo.cse.cse486586.groupmessenger2.provider");
                      createuri.scheme("content");
                      Uri uri = createuri.build();
                      ContentValues keyval = new ContentValues();
                      //putting key value pairs.
                      keyval.put("key", Integer.toString(msg_counter));
                      keyval.put("value", msg_vals);
                      msg_counter++;
                      getContentResolver().insert(uri, keyval);
                      //}
                  }
            }

        }
    }

    /***
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     *
     * @author stevko
     *
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {
            try {
                pc = 0;
                String msgToSend = msgs[0];
                String msg_type = msgs[2];
                String input;
                String TYPEO="";
                String idn="";
                String[] input_data = new String[6];
                // send messages to all five avds including itself.
                Random r = new Random();
                int rng = r.nextInt();
                for(int i=0;i<=4;i++)
                {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(REMOTE_PORTS[i]));

                   // Log.e(TAG, "Created Socket!");
//                    String input;
//                    String TYPEO="";
//                    String[] input_data = new String[6];
                    if (msg_type.equals("FIRST"))
                    {
                        // Initial B-Multicast to all the processes including itself.
                        //msg_type="";
                       // Log.e(TAG, "In Client First: " + "");
                        String m_txt = msgs[0];
                        MessageDetails mobj1 = new MessageDetails();
                        //proposal_num = maxi(proposal_num, agreement_num) + 1;

                        mobj1.setMessage(m_txt);
                        mobj1.setPortNo(port_number);
                        mobj1.setProposalNo(proposal_num);
                        mobj1.setType(msg_type);
                        mobj1.setID(rng + "." + port_number);
                        mobj1.setDeliverability(false);
                        String s = "";
                        s = mobj1.msg_id + myDelimiter + mobj1.proposal_no + myDelimiter + mobj1.port_no + myDelimiter +
                                mobj1.msg_type + myDelimiter + mobj1.msg_val
                                +myDelimiter+mobj1.is_deliverable;
                        Log.e(TAG, "B-Multicasted : " + s);

                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println(s);

                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        input = in.readLine();

                        input_data = input.split(myDelimiter);
                        TYPEO = input_data[3];
                        Log.e(TAG, "TYPE0 : " + TYPEO);

                    }
                    if(TYPEO.equals("ACK"))
                    {
                        // For  ACK.
                       // Log.e(TAG, "In Client ACK : " + "");

                        MessageDetails mobj2 = new MessageDetails();
                        mobj2.setMessage(input_data[4]);
                        mobj2.setPortNo(Integer.parseInt(input_data[2]));
                        mobj2.setProposalNo(Integer.parseInt(input_data[1]));
                        mobj2.setType(input_data[3]);
                        mobj2.setID(input_data[0]);
                        mobj2.setDeliverability(Boolean.parseBoolean(input_data[5]));
                        idn = input_data[0];
                        String propn = input_data[1];
                        String portn = input_data[2];
                      //  Log.e(TAG, "Inserting->PMAP " +"ID: "+idn+" from: "+portn+" Prop: "+propn);
                       // Log.e(TAG, "Proposal No from " + portn+ " is "+propn);
                        pMap.put(idn+myDelimiter+portn ,propn);
                    }
                }
                String res = getPMaxnCount(idn);
                String[] proposal_data = res.split(myDelimiter);
                if(proposal_data[0].equals("T"))
                {
                    Log.e(TAG, "All proposals rcd ");
                    MessageDetails mobj3 = msgMap.get(idn);
                    mobj3.msg_type = "AGREEMENT";
                    mobj3.proposal_no = Integer.parseInt(proposal_data[1]);
                    mobj3.port_no = Integer.parseInt((proposal_data[2]));
                    String snew1 = "";
                    snew1 = mobj3.msg_id + myDelimiter + mobj3.proposal_no + myDelimiter + mobj3.port_no +
                            myDelimiter + mobj3.msg_type + myDelimiter + mobj3.msg_val
                            +myDelimiter+mobj3.is_deliverable;
                    Log.e(TAG,"Sending Agreements to ALL");
                    for(int q=0;q<5;q++)
                    {
                        Log.e(TAG, " Sending AG to!"+Integer.toString(q));
                        Socket socket1 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(REMOTE_PORTS[q]));
                        //                        DataOutputStream dos1 = new DataOutputStream(socket1.getOutputStream());
                        //                        dos1.writeUTF(s1);
//                        BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
//                        out1.write(snew1, 0, snew1.length());
                        PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);
                        out1.println(snew1);
                        Log.e(TAG, " Sending AG value!"+snew1);
                        //out1.flush();
                    }

                }

                //socket.close();

            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
            }

            return null;
        }
    }
}
