package project.com.ringsureapp.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;

import project.com.ringsureapp.AppData.ContactData;
import project.com.ringsureapp.AppData.FunctionUtils;
import project.com.ringsureapp.DataBase.MyDatabase;
import project.com.ringsureapp.HomePage_Activity;
import project.com.ringsureapp.IntegrateApp.CheckSdk;
import project.com.ringsureapp.R;
import project.com.ringsureapp.util.IabHelper;
import project.com.ringsureapp.util.IabResult;
import project.com.ringsureapp.util.Inventory;
import project.com.ringsureapp.util.Purchase;

/**
 * Created by tarun on 24/6/16.
 */
public class ContactAdapter extends BaseAdapter {

    HomePage_Activity context;
    ArrayList<ContactData> data;
    MyDatabase dbHelper;
    private LayoutInflater mLayoutInflater;
    HashMap<Integer, Boolean> data1;
    HashMap<Integer, Boolean> dataMessage;
    HashMap<Integer, Boolean> dataCallSilent;
    HashMap<Integer, Boolean> dataMessageSilent;
    HashMap<Integer, Boolean> DataAds;
    boolean general;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static String TAG = "pref";

    public static final int PERMISSION_REQUEST_CODE_SMS = 2;
    int position1;

    ListView listview;
    static final String ITEM_SKU = "project.com.ringsureapp";
    //    static final String ITEM_SKU = "android.test.purchased";
    IabHelper mHelper;
    ImageView imgMessage;
    AdView mAdView;

    public ContactAdapter(HomePage_Activity context, ArrayList<ContactData> data, boolean general, ListView listview, ImageView imgMessage) {
        this.context = context;
        this.data = data;
        this.general = general;
        this.listview = listview;
        this.imgMessage = imgMessage;
        dbHelper = new MyDatabase(context);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        data1 = new HashMap<>();
        dataMessage = new HashMap<>();
        dataCallSilent = new HashMap<>();
        dataMessageSilent = new HashMap<>();
        DataAds = new HashMap<>();
        if (general) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getCALLMODEGeneral().equals("Silent")) {
                    data1.put(i, true);
                } else if (data.get(i).getCALLMODEGeneral().equals("General")) {
                    data1.put(i, false);
                }
                if (data.get(i).getMESSAGEMODEGeneral().equals("Silent")) {
                    dataMessage.put(i, true);
                } else if (data.get(i).getMESSAGEMODEGeneral().equals("General")) {
                    dataMessage.put(i, false);
                }
                if (i % 20 == 0) {
                    DataAds.put(i, true);
                } else {
                    DataAds.put(i, false);
                }
            }
        } else {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getCallModeSilent().equals("Silent")) {
                    dataCallSilent.put(i, true);
                } else if (data.get(i).getCallModeSilent().equals("General")) {
                    dataCallSilent.put(i, false);
                }
                if (data.get(i).getMessageModeSilent().equals("Silent")) {
                    dataMessageSilent.put(i, true);
                } else if (data.get(i).getMessageModeSilent().equals("General")) {
                    dataMessageSilent.put(i, false);
                }

                if (i % 20 == 0) {
                    DataAds.put(i, true);
                } else {
                    DataAds.put(i, false);
                }
            }
        }
        pref = context.getSharedPreferences(TAG, context.MODE_PRIVATE);
        editor = pref.edit();
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        position1 = position;
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(
                    R.layout.contactadapter, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtContactName = (TextView) convertView
                    .findViewById(R.id.txtContactName);
            viewHolder.imgGeneral = (ImageView) convertView
                    .findViewById(R.id.imgGeneral);
            viewHolder.imgMessage = (ImageView) convertView
                    .findViewById(R.id.imgMessage);
            viewHolder.rlClick = (RelativeLayout) convertView.findViewById(R.id.rlClick);
            viewHolder.txtNumber = (TextView) convertView.findViewById(R.id.txtNumber);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        mAdView = (AdView) convertView.findViewById(R.id.ad_view);

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Start loading the ad in the background.

        if (DataAds.size() > 0) {
            if (DataAds.get(position)) {
                if (!pref.getBoolean("purchase", false)) {
                    if (FunctionUtils.getInstance().isNetworkConnected())
                        mAdView.setVisibility(View.VISIBLE);
                    else
                        mAdView.setVisibility(View.GONE);
                    mAdView.loadAd(adRequest);
                } else {
                    mAdView.setVisibility(View.GONE);
                }

            } else {
                mAdView.setVisibility(View.GONE);

            }
        }

        if (!pref.getBoolean("purchase", false)) {
            if (position % 20 == 0) {
                if (FunctionUtils.getInstance().isNetworkConnected())
                    mAdView.setVisibility(View.VISIBLE);
                else
                    mAdView.setVisibility(View.GONE);
                mAdView.loadAd(adRequest);
                DataAds.put(position, true);
            } else {
                mAdView.setVisibility(View.GONE);
                DataAds.put(position, false);
            }
        } else {
            mAdView.setVisibility(View.GONE);
        }

        if (general) {
            viewHolder.imgGeneral.setVisibility(View.VISIBLE);
            viewHolder.imgMessage.setVisibility(View.VISIBLE);

            if (data1.size() > 0) {
                if (data1.get(position)) {
                    viewHolder.imgGeneral.setImageResource(R.drawable.call_mute);
                } else {
                    viewHolder.imgGeneral.setImageResource(R.drawable.call_unmute);
                }
            }

            if (dataMessage.size() > 0) {
                if (dataMessage.get(position)) {
                    viewHolder.imgMessage.setImageResource(R.drawable.message_mute);
                } else {
                    viewHolder.imgMessage.setImageResource(R.drawable.messagemute);
                }
            }
        } else {
            viewHolder.imgGeneral.setVisibility(View.VISIBLE);
            viewHolder.imgMessage.setVisibility(View.VISIBLE);

            if (dataCallSilent.size() > 0) {
                if (dataCallSilent.get(position)) {
                    viewHolder.imgGeneral.setImageResource(R.drawable.call_mute);
                } else {
                    viewHolder.imgGeneral.setImageResource(R.drawable.call_unmute);
                }
            }
            if (dataMessageSilent.size() > 0) {
                if (dataMessageSilent.get(position)) {
                    viewHolder.imgMessage.setImageResource(R.drawable.message_mute);
                } else {
                    viewHolder.imgMessage.setImageResource(R.drawable.messagemute);
                }
            }
        }
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjakUd7cziHoW77HecGon11w9D8lAUE25y5eDhluHi9Yw+q5dbS58tBafm7q5ChyDhZWzCF96HDZqI7oPehy9PPOHtYg5f6xr0IZi8ZEQfrgukfKN4BUL6Z64qrjKDzuDdcm4orcDvOuVcECUzD440qViJdSDaumXeqEC5nYkZx9HVJH7BaWwoteD2Rc1QFp1qwCW0q8pg1MetBsJD6/d/mxxuvjMf98l3U4Vkb1XBgfHuQmwS/wNtgKHSFmHkEHLMKELT9Y0VE4wpREwoBDosL0XTAkGj6txAjYl0nTOHQxlR+9nS6P8ijfCMLo1UwcmvAFNDU0n91twrMEqkgj0jwIDAQAB";

        mHelper = new IabHelper(context, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                }
            }
        });
        viewHolder.rlClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.txtContactName.setText(data.get(position).getName());
        viewHolder.txtNumber.setText(data.get(position).getPhoneNumber());
//        if(data.get(position).getName().equals("Demo8")){
////            Toast.makeText(context, "okay demo8", Toast.LENGTH_SHORT).show();
//        }
        viewHolder.imgGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (general) {
                    if (dbHelper.getPhoneMode(data.get(position).getPhoneNumber()).equals("General")) {
                        viewHolder.imgGeneral.setImageResource(R.drawable.call_mute);
                        ContactData cdata = dbHelper.getDataFromNumberSilent(data.get(position).getPhoneNumber());
                        ContactData cdata1 = new ContactData(cdata.getId(), cdata.getName(), cdata.getPhoneNumber(), "Silent", cdata.getMESSAGEMODEGeneral(), cdata.getCallModeSilent(), cdata.getMessageModeSilent(), cdata.getCategory(), cdata.getRecentOrder(), cdata.getSMSOrder());
                        dbHelper.UpdateContact(cdata1);
                        data1.put(position, true);
                    } else {
                        viewHolder.imgGeneral.setImageResource(R.drawable.call_unmute);
                        ContactData cdata = dbHelper.getDataFromNumberSilent(data.get(position).getPhoneNumber());
                        ContactData cdata1 = new ContactData(cdata.getId(), cdata.getName(), cdata.getPhoneNumber(), "General", cdata.getMESSAGEMODEGeneral(), cdata.getCallModeSilent(), cdata.getMessageModeSilent(), cdata.getCategory(), cdata.getRecentOrder(), cdata.getSMSOrder());
                        dbHelper.UpdateContact(cdata1);
                        data1.put(position, false);
                    }
                } else {
                    if (dbHelper.getPhoneModeSilent(data.get(position).getPhoneNumber()).equals("General")) {
                        viewHolder.imgGeneral.setImageResource(R.drawable.call_mute);
                        ContactData cdata = dbHelper.getDataFromNumberSilent(data.get(position).getPhoneNumber());
                        ContactData cdata1 = new ContactData(cdata.getId(), cdata.getName(), cdata.getPhoneNumber(), cdata.getCALLMODEGeneral(), cdata.getMESSAGEMODEGeneral(), "Silent", cdata.getMessageModeSilent(), cdata.getCategory(), cdata.getRecentOrder(), cdata.getSMSOrder());
                        dbHelper.UpdateContact(cdata1);
                        dataCallSilent.put(position, true);
                    } else {
                        viewHolder.imgGeneral.setImageResource(R.drawable.call_unmute);
                        ContactData cdata = dbHelper.getDataFromNumberSilent(data.get(position).getPhoneNumber());
                        ContactData cdata1 = new ContactData(cdata.getId(), cdata.getName(), cdata.getPhoneNumber(), cdata.getCALLMODEGeneral(), cdata.getMESSAGEMODEGeneral(), "General", cdata.getMessageModeSilent(), cdata.getCategory(), cdata.getRecentOrder(), cdata.getSMSOrder());
                        dbHelper.UpdateContact(cdata1);
                        dataCallSilent.put(position, false);
                    }
                }
            }
        });
        viewHolder.imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float a = CheckSdk.getAPIVerison();
                if (general) {
//                    if (pref.getBoolean("purchase", false)) {
                    if (dbHelper.getMessageMode(data.get(position).getPhoneNumber()).equals("General")) {
                        viewHolder.imgMessage.setImageResource(R.drawable.message_mute);
                        ContactData cdata = dbHelper.getDataFromNumberSilent(data.get(position).getPhoneNumber());
                        ContactData cdata1 = new ContactData(cdata.getId(), cdata.getName(), cdata.getPhoneNumber(), cdata.getCALLMODEGeneral(), "Silent", cdata.getCallModeSilent(), cdata.getMessageModeSilent(), cdata.getCategory(), cdata.getRecentOrder(), cdata.getSMSOrder());
                        dbHelper.UpdateContact(cdata1);
                        dataMessage.put(position, true);
                    } else {
                        viewHolder.imgMessage.setImageResource(R.drawable.messagemute);
                        ContactData cdata = dbHelper.getDataFromNumberSilent(data.get(position).getPhoneNumber());
                        ContactData cdata1 = new ContactData(cdata.getId(), cdata.getName(), cdata.getPhoneNumber(), cdata.getCALLMODEGeneral(), "General", cdata.getCallModeSilent(), cdata.getMessageModeSilent(), cdata.getCategory(), cdata.getRecentOrder(), cdata.getSMSOrder());
                        dbHelper.UpdateContact(cdata1);
                        dataMessage.put(position, false);
                    }
//                    } else {
//                        UpgradeApp();
//                    }
                } else {

//                    if(pref.getBoolean("purchase",false)){
                    if (dbHelper.getMessageModeSilent(data.get(position).getPhoneNumber()).equals("General")) {
                        viewHolder.imgMessage.setImageResource(R.drawable.message_mute);
                        ContactData cdata = dbHelper.getDataFromNumberSilent(data.get(position).getPhoneNumber());
                        ContactData cdata1 = new ContactData(cdata.getId(), cdata.getName(), cdata.getPhoneNumber(), cdata.getCALLMODEGeneral(), cdata.getMESSAGEMODEGeneral(), cdata.getCallModeSilent(), "Silent", cdata.getCategory(), cdata.getRecentOrder(), cdata.getSMSOrder());
                        dbHelper.UpdateContact(cdata1);
                        dataMessageSilent.put(position, true);
                    } else {
                        viewHolder.imgMessage.setImageResource(R.drawable.messagemute);
                        ContactData cdata = dbHelper.getDataFromNumberSilent(data.get(position).getPhoneNumber());
                        ContactData cdata1 = new ContactData(cdata.getId(), cdata.getName(), cdata.getPhoneNumber(), cdata.getCALLMODEGeneral(), cdata.getMESSAGEMODEGeneral(), cdata.getCallModeSilent(), "General", cdata.getCategory(), cdata.getRecentOrder(), cdata.getSMSOrder());
                        dbHelper.UpdateContact(cdata1);
                        dataMessageSilent.put(position, false);
                    }
//                    }else{
//                        UpgradeApp();
//                    }
                }
            }
        });
        viewHolder.rlClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(context, listview);
            }
        });
        return convertView;
    }
    public static class ViewHolder {
        TextView txtContactName, txtNumber;
        ImageView imgGeneral, imgMessage;
        RelativeLayout rlClick;
    }
    private void UpgradeApp() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.upgrade_user);

        RelativeLayout rlUpgrade = (RelativeLayout) dialog.findViewById(R.id.rlUpgrade);
        RelativeLayout rlNotNow = (RelativeLayout) dialog.findViewById(R.id.rlNotNow);

//        rlUpgrade.setVisibility(View.GONE);

        rlNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        rlUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity act = (Activity) context;
                mHelper.launchPurchaseFlow(act, ITEM_SKU, 10001,
                        mPurchaseFinishedListener, "mypurchasetoken");
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public static void hideSoftKeyboard(Context activity, View view) {
        Activity act = (Activity) activity;
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), 0);
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
                editor.putBoolean("purchase", true).commit();
//                imgMessage.setImageResource(R.drawable.message_buy);
                notifyDataSetChanged();
                return;
            } else if (purchase.getSku().equals(ITEM_SKU)) {
                consumeItem();

                editor.putBoolean("purchase", false).commit();
            }
        }
    };
    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }
    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
//                        txtvwSMS.setEnabled(true);
//                        purchases = true;
                        editor.putBoolean("purchase", true).commit();
//                        imgMessage.setImageResource(R.drawable.message_buy);
                        notifyDataSetChanged();
                    } else {
                        // handle error
                    }
                }
            };
}
