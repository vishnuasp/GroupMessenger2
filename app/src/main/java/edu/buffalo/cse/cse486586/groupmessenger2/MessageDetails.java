package edu.buffalo.cse.cse486586.groupmessenger2;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by aspvi on 3/6/2018.
 */

// MessageDetails class has all the information regarding a particular message.
public class MessageDetails implements Serializable{

    String msg_val;
    String msg_id;
    String msg_type;
    int proposal_no;
    int port_no;
    boolean is_deliverable;


    public MessageDetails(){}

    // get and set methods for class attributes

    public void setMessage(String msg_val){
        this.msg_val = msg_val;
    }
    public void setID(String msg_id){
        this.msg_id = msg_id;
    }
    public void setType(String msg_type){ this.msg_type = msg_type;}
    public void setProposalNo(int proposal_no){
        this.proposal_no = proposal_no;
    }
    public void setPortNo(int port_no){
        this.port_no = port_no;
    }
    public void setDeliverability(boolean is_deliverable){
        this.is_deliverable = is_deliverable;
    }


    public String getMessage(){
        return  this.msg_val;
    }
    public String getID(){
        return this.msg_id;
    }
    public String getType(){
        return this.msg_type;
    }
    public int getProposalNo(){return this.proposal_no;}
    public int getPortNo(){
        return this.port_no;
    }
    public boolean getDeliverability(){
        return this.is_deliverable;
    }


}
