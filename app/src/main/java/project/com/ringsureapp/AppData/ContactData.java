package project.com.ringsureapp.AppData;

/**
 * Created by tarun on 24/6/16.
 */

public class ContactData {
    
    String id,Name,phoneNumber,CALLMODEGeneral, MESSAGEMODEGeneral,CallModeSilent , MessageModeSilent,CATEGORY;
    int RecentOrder , SMSOrder;

    public ContactData(String id, String name, String phoneNumber, String CALLMODEGeneral, String MESSAGEMODEGeneral, String callModeSilent, String messageModeSilent,String CATEGORY,int RecentOrder,int SMSOrder) {
        this.id = id;
        Name = name;
        this.phoneNumber = phoneNumber;
        this.CALLMODEGeneral = CALLMODEGeneral;
        this.MESSAGEMODEGeneral = MESSAGEMODEGeneral;
        CallModeSilent = callModeSilent;
        MessageModeSilent = messageModeSilent;
        this.CATEGORY = CATEGORY;
        this.RecentOrder = RecentOrder;
        this.SMSOrder = SMSOrder;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCALLMODEGeneral() {
        return CALLMODEGeneral;
    }

    public String getMESSAGEMODEGeneral() {
        return MESSAGEMODEGeneral;
    }

    public String getCallModeSilent() {
        return CallModeSilent;
    }

    public String getMessageModeSilent() {
        return MessageModeSilent;
    }

    public String getCategory() {
        return CATEGORY;
    }

    public int getRecentOrder(){
        return RecentOrder;
    }

    public int getSMSOrder(){
        return SMSOrder;
    }
}

