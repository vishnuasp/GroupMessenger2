package edu.buffalo.cse.cse486586.groupmessenger2;

import android.nfc.Tag;
import android.util.Log;

import java.util.Comparator;

/**
 * Created by aspvi on 3/6/2018.
 */

// Using this comparator to pass to the priority blocking queue constructor.

public class MComparator implements Comparator<MessageDetails> {
    @Override
    public  int compare(MessageDetails m1, MessageDetails m2){
        // Compare messages details

        if((m1.proposal_no - m2.proposal_no)==0){
            return (m1.port_no - m2.port_no);
        }
        else if(m1.proposal_no > m2.proposal_no){
            return 1;
        }
         else{
            return  -1;
        }
//        else{
//            return (m1.proposal_no - m2.proposal_no);
//        }
    }
}
