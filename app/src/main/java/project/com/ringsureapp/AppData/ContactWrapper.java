package project.com.ringsureapp.AppData;

/**
 * Created by Piyush on 3/10/2016.
 */

public class ContactWrapper {

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    private String phoneno,name, gCall, gMsg,sCall,sMsg;

    public ContactWrapper() {
    }

    public ContactWrapper(String name, String phoneno,  String gCall, String gMsg,String sCall,String sMsg) {
        this.name=name;
        this.phoneno = phoneno;
        this.gCall=gCall;
        this.gMsg=gMsg;
        this.sCall = sCall;
        this.sMsg = sMsg;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getgCall() {
        return gCall;
    }

    public void setgCall(String gCall) {
        this.gCall = gCall;
    }

    public String getgMsg() {
        return gMsg;
    }

    public void setgMsg(String gMsg) {
        this.gMsg = gMsg;
    }

    public String getsCall() {
        return sCall;
    }

    public void setsCall(String sCall) {
        this.sCall = sCall;
    }

    public String getsMsg() {
        return sMsg;
    }

    public void setsMsg(String sMsg) {
        this.sMsg = sMsg;
    }
}