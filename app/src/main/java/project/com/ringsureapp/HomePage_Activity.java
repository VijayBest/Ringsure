package project.com.ringsureapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import project.com.ringsureapp.Adapter.ContactAdapter;
import project.com.ringsureapp.AppData.ContactData;
import project.com.ringsureapp.AppData.FunctionUtils;
import project.com.ringsureapp.AppData.functions;
import project.com.ringsureapp.DataBase.MyDatabase;
import project.com.ringsureapp.IntegrateApp.CheckSdk;
import project.com.ringsureapp.util.IabHelper;
import project.com.ringsureapp.util.IabResult;
import project.com.ringsureapp.util.Inventory;
import project.com.ringsureapp.util.Purchase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/*** Created by apple on 04/07/16. ***/
public class HomePage_Activity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    ListView listContactGeneral;
    RelativeLayout txtvwContact, txtvwCallLog, txtvwSMS,rlTimer;
    ArrayList<ContactData> dataList;
    ArrayList<ContactData> tempList;
    ImageView imgMessage, imgRefresh, imgOverFlow,imgDelete,tvTimer;

    EditText etStarttime,etEndTime;
    // Delete data from user machine
    private static Context context;

    /*      Time picker      */
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    private int pHour;
    private int pMinute;
    /** This integer will uniquely define the dialog to be used for displaying time picker.*/
    static final int TIME_DIALOG_ID = 0;

    MyDatabase delObj= new MyDatabase(context);

    ArrayList<ContactData> refreshList;
    ArrayList<ContactData> tempRefreshList;

    EditText editSearch;

    ContactAdapter adapter;
    MyDatabase dbHelper;
    boolean showContact = false, showRecent = false, showMessage = false , showTimer = false;
    int callstart = 0;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public static String TAG = "pref";

    public static final int PERMISSION_REQUEST_CODE_SMS = 2;
    public static final int PERMISSION_REQUEST_CODE_CONTACT = 4;
    public static final int PERMISSION_REQUEST_CODE_PHONE = 3;

    ArrayList<ContactData> tempUserList;

    public static String ContactSize = "contact";
    public static String RecentSize = "recent";

    float a;

    ContactsGetter contactsGetter;
    private static final String TAGs = "project.com.ringsureapp";
    IabHelper mHelper;
    static final String ITEM_SKU = "project.com.ringsureapp";
//    static final String ITEM_SKU = "android.test.purchased";
//    boolean purchases = false;

    MediaPlayer mp;
    RelativeLayout rlMenu, rlShare, rlRate, rlBuy, rlTab,rlReset;

    TextView txtGeneral, txtSilent, txtTitle;
    View vGeneral, vSilent;
    boolean boolGeneral = true, boolSilent = false;

    boolean tab = false, sync = false;
    private Dialog dialog;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_CONTACT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!CheckSdk.checkPermission(Manifest.permission.READ_SMS, getApplicationContext())) {
                        CheckSdk.requestPermission(Manifest.permission.READ_SMS, PERMISSION_REQUEST_CODE_SMS, getApplicationContext(), HomePage_Activity.this);
                    }
                    getSupportLoaderManager().initLoader(1, null, this);
                    tab = false;
                    sync = false;
//                    new AsyncgetContact(false, false).execute();
                } else {
                    final Dialog dialog = new Dialog(HomePage_Activity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(R.layout.dialog_alert);

                    Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckSdk.requestPermission(Manifest.permission.READ_CONTACTS, PERMISSION_REQUEST_CODE_CONTACT, getApplicationContext(), HomePage_Activity.this);
                            dialog.dismiss();
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
                break;
            case PERMISSION_REQUEST_CODE_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    new AsyncgetRecentContact(false, false).execute();

                } else {
                    final Dialog dialog = new Dialog(HomePage_Activity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(R.layout.dialog_alert);

                    Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckSdk.requestPermission(Manifest.permission.READ_CALL_LOG, PERMISSION_REQUEST_CODE_PHONE, getApplicationContext(), HomePage_Activity.this);
                            dialog.dismiss();
                        }
                    });


                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
                break;
            case PERMISSION_REQUEST_CODE_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (showMessage) {
                        new AsyncgetSMS(false, false).execute();
                    }

                } else {
                    final Dialog dialog = new Dialog(HomePage_Activity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setContentView(R.layout.dialog_alert);

                    Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckSdk.requestPermission(Manifest.permission.READ_SMS, PERMISSION_REQUEST_CODE_SMS, getApplicationContext(), HomePage_Activity.this);
                            dialog.dismiss();
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.homepage);

        dataList = new ArrayList<>();
        tempList = new ArrayList<>();
        tempUserList = new ArrayList<>();
        pref = getSharedPreferences(TAG, MODE_PRIVATE);
        editor = pref.edit();
        dbHelper = new MyDatabase(HomePage_Activity.this);
        listContactGeneral = (ListView) findViewById(R.id.listContactGeneral);
        listContactGeneral.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                rlMenu.setVisibility(View.GONE);
                return false;
            }
        });

        txtGeneral = (TextView) findViewById(R.id.txtGeneral);
        txtGeneral.setOnClickListener(this);
        txtSilent = (TextView) findViewById(R.id.txtSilent);
        txtSilent.setOnClickListener(this);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        //txtTitle.setText("Configure ringer mode");

        vGeneral = (View) findViewById(R.id.vGeneral);
        vSilent = (View) findViewById(R.id.vSilent);

        txtvwContact = (RelativeLayout) findViewById(R.id.txtvwContact);
        txtvwContact.setOnClickListener(this);
        txtvwCallLog = (RelativeLayout) findViewById(R.id.txtvwCallLog);
        txtvwCallLog.setOnClickListener(this);
        txtvwSMS = (RelativeLayout) findViewById(R.id.txtvwSMS);
        txtvwSMS.setOnClickListener(this);
        rlTimer =(RelativeLayout) findViewById(R.id.rlTimer);
        rlTimer.setOnClickListener(this);

        tvTimer=(ImageView)findViewById(R.id.ivTimer);
        tvTimer.setOnClickListener(this);

        imgOverFlow = (ImageView) findViewById(R.id.imgOverFlow);
        imgOverFlow.setOnClickListener(this);

        // Delete option applied
        imgDelete = (ImageView)findViewById(R.id.ivReset);
        imgDelete.setOnClickListener(this);

        imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
        imgRefresh.setOnClickListener(this);

        rlMenu = (RelativeLayout) findViewById(R.id.rlMenu);
        rlReset = (RelativeLayout)findViewById(R.id.rlReset);
        rlReset.setOnClickListener(this);
        rlShare = (RelativeLayout) findViewById(R.id.rlShare);
        rlShare.setOnClickListener(this);
        rlRate = (RelativeLayout) findViewById(R.id.rlRate);
        rlRate.setOnClickListener(this);
        rlBuy = (RelativeLayout) findViewById(R.id.rlBuy);
        rlBuy.setOnClickListener(this);
        rlTab = (RelativeLayout) findViewById(R.id.rlTab);
        rlTab.setOnClickListener(this);

        imgMessage = (ImageView) findViewById(R.id.imgMessage);

        editSearch = (EditText) findViewById(R.id.editSearch);
        editSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rlMenu.setVisibility(View.GONE);
                return false;
            }
        });
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                rlMenu.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (showContact) {
                    if (editSearch.getText().toString().trim().length() != 0) {
                        if (dataList.size() > 0) {
                            tempUserList.clear();
                            for (int i = 0; i < dataList.size(); i++) {
                                if (dataList.get(i).getName().toLowerCase().contains(editSearch.getText().toString().toLowerCase()) || dataList.get(i).getPhoneNumber().contains(editSearch.getText().toString())) {
                                    ContactData data = dbHelper.getDataFromNumberSilent(dataList.get(i).getPhoneNumber());
// ContactData data = dataList.get(i);
                                    dataList.set(i, data);
                                    tempUserList.add(data);
                                }
                            }

                            if (boolGeneral) {
                                adapter = new ContactAdapter(HomePage_Activity.this, tempUserList, true, listContactGeneral, imgMessage);
                            } else {
                                adapter = new ContactAdapter(HomePage_Activity.this, tempUserList, false, listContactGeneral, imgMessage);
                            }

                            listContactGeneral.setAdapter(adapter);
                        }
                    } else {
                        if (boolGeneral) {
                            adapter = new ContactAdapter(HomePage_Activity.this, dataList, true, listContactGeneral, imgMessage);
                        } else {
                            adapter = new ContactAdapter(HomePage_Activity.this, dataList, false, listContactGeneral, imgMessage);
                        }

                        listContactGeneral.setAdapter(adapter);
                    }

                } else if (showRecent) {
                    if (editSearch.getText().toString().trim().length() != 0) {
                        if (dataList.size() > 0) {
                            tempUserList.clear();
                            for (int i = 0; i < dataList.size(); i++) {
                                if (dataList.get(i).getName().toLowerCase().contains(editSearch.getText().toString().toLowerCase()) || dataList.get(i).getPhoneNumber().contains(editSearch.getText().toString().toLowerCase())) {
                                    ContactData data = dbHelper.getDataFromNumberSilent(dataList.get(i).getPhoneNumber());
//                                    ContactData data = dataList.get(i);
                                    dataList.set(i, data);
                                    tempUserList.add(data);
                                }
                            }
                            if (boolGeneral) {
                                adapter = new ContactAdapter(HomePage_Activity.this, tempUserList, true, listContactGeneral, imgMessage);

                            } else {
                                adapter = new ContactAdapter(HomePage_Activity.this, tempUserList, false, listContactGeneral, imgMessage);

                            }
                            listContactGeneral.setAdapter(adapter);
                        }
                    } else {
                        if (boolGeneral) {
                            adapter = new ContactAdapter(HomePage_Activity.this, dataList, true, listContactGeneral, imgMessage);
                        } else {
                            adapter = new ContactAdapter(HomePage_Activity.this, dataList, false, listContactGeneral, imgMessage);
                        }

                        listContactGeneral.setAdapter(adapter);
                    }
                } else if (showMessage) {
                    if (editSearch.getText().toString().trim().length() != 0) {
                        if (dataList.size() > 0) {
                            tempUserList.clear();
                            for (int i = 0; i < dataList.size(); i++) {
                                if (dataList.get(i).getName().toLowerCase().contains(editSearch.getText().toString().toLowerCase()) || dataList.get(i).getPhoneNumber().contains(editSearch.getText().toString().toLowerCase())) {
                                    ContactData data = dbHelper.getDataFromNumberSilent(dataList.get(i).getPhoneNumber());
// ContactData data = dataList.get(i);
                                    dataList.set(i, data);
                                    tempUserList.add(data);
                                }
                            }
                            if (boolGeneral) {
                                adapter = new ContactAdapter(HomePage_Activity.this, tempUserList, true, listContactGeneral, imgMessage);

                            } else {
                                adapter = new ContactAdapter(HomePage_Activity.this, tempUserList, false, listContactGeneral, imgMessage);

                            }
                            listContactGeneral.setAdapter(adapter);
                        }
                    } else {
                        if (boolGeneral) {
                            adapter = new ContactAdapter(HomePage_Activity.this, dataList, true, listContactGeneral, imgMessage);
                        } else {
                            adapter = new ContactAdapter(HomePage_Activity.this, dataList, false, listContactGeneral, imgMessage);
                        }
                        listContactGeneral.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        a = CheckSdk.getAPIVerison();

        // In app purchase
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjakUd7cziHoW77HecGon11w9D8lAUE25y5eDhluHi9Yw+q5dbS58tBafm7q5ChyDhZWzCF96HDZqI7oPehy9PPOHtYg5f6xr0IZi8ZEQfrgukfKN4BUL6Z64qrjKDzuDdcm4orcDvOuVcECUzD440qViJdSDaumXeqEC5nYkZx9HVJH7BaWwoteD2Rc1QFp1qwCW0q8pg1MetBsJD6/d/mxxuvjMf98l3U4Vkb1XBgfHuQmwS/wNtgKHSFmHkEHLMKELT9Y0VE4wpREwoBDosL0XTAkGj6txAjYl0nTOHQxlR+9nS6P8ijfCMLo1UwcmvAFNDU0n91twrMEqkgj0jwIDAQAB";

        mHelper = new IabHelper(this, base64EncodedPublicKey);
        List<String> moreSKU = new ArrayList<>();
        moreSKU.add(ITEM_SKU);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(TAG, "In-app Billing setup failed: " + result);
                } else {
                    Log.d(TAG, "In-app Billing is set up OK");
                    mHelper.enableDebugLogging(true, TAG);
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                }
            }
        });

        if (a == 6.0) {
            if (CheckSdk.checkPermission(Manifest.permission.READ_CONTACTS, getApplicationContext())) {
                getSupportLoaderManager().initLoader(1, null, this);
                tab = false;
                sync = false;
//                new AsyncgetContact(false, false).execute();
            } else {
                CheckSdk.requestPermission(Manifest.permission.READ_CONTACTS, PERMISSION_REQUEST_CODE_CONTACT, getApplicationContext(), HomePage_Activity.this);
//
            }
        } else {
            getSupportLoaderManager().initLoader(1, null, this);
            tab = false;
            sync = false;
            //     new AsyncgetContact(false, false).execute();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtvwContact:
                SetUI(true, false, false,false);
                showContact = true;
                showRecent = false;
                showMessage = false;
                showTimer = false;
                getSupportLoaderManager().initLoader(1, null, this);
                tab = true;
                sync = false;
                //new AsyncgetContact(true, false).execute();
                break;
            case R.id.txtvwCallLog:
                SetUI(false, true, false,false);
                showContact = false;
                showRecent = true;
                showMessage = false;
                showTimer = false;
                if (a == 6.0) {
                    if (CheckSdk.checkPermission(Manifest.permission.READ_CALL_LOG, getApplicationContext())) {
                        new AsyncgetRecentContact(true, false).execute();
                    } else {
                        CheckSdk.requestPermission(Manifest.permission.READ_CALL_LOG, PERMISSION_REQUEST_CODE_PHONE, getApplicationContext(), HomePage_Activity.this);
                    }
                } else {
                    new AsyncgetRecentContact(true, false).execute();
                }
                break;
            case R.id.txtvwSMS:
//              if(pref.getBoolean("purchase",false)){
                SetUI(false, false,true ,false );
                showContact = false;
                showRecent = false;
                showMessage = true;
                showTimer = false;

                if (a == 6.0) {
                    if (CheckSdk.checkPermission(Manifest.permission.READ_SMS, getApplicationContext())) {
                        new AsyncgetSMS(true, false).execute();
                    } else {
                        CheckSdk.requestPermission(Manifest.permission.READ_SMS, PERMISSION_REQUEST_CODE_SMS, getApplicationContext(), HomePage_Activity.this);
                    }
                } else {
                    new AsyncgetSMS(true, false).execute();
                }
//               }else{
//                    UpgradeApp();
//               }
                break;

            /* *****  Timer Work code   ************ */
            case R.id.rlTimer:
                // Create custom dialog object
                SetUI(false, false,false ,true );
                showContact = false;
                showRecent = false;
                showMessage = false;
                showTimer = true;

                final Dialog dialog = new Dialog(HomePage_Activity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setContentView(R.layout.dialog_timer);

                dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                etStarttime = (EditText) dialog.findViewById(R.id.etStarttime);
                etStarttime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(TIME_DIALOG_ID);
                        updateDisplay();
                        displayToast();

                    }
                });

                /** Get the current time */
                final Calendar cal = Calendar.getInstance();
                pHour = cal.get(Calendar.HOUR_OF_DAY);
                pMinute = cal.get(Calendar.MINUTE);

                /** Display the current time in the TextView */
                updateDisplay();
                etEndTime = (EditText) dialog.findViewById(R.id.etEndTime);
                etEndTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(TIME_DIALOG_ID);
                        updateDisplayendtime();
                        displayToastEnd();
                    }
                });

                // Submitt button Time
               Button submittTimer = (Button)dialog.findViewById(R.id.ivSubmittTimer);
                submittTimer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       dialog.dismiss();
                    }
                });

                dialog.show();
                break;

            /**********   Timer functionality End here ****************/

            // Delete Database code
            case R.id.imgRefresh:
                imgRefresh.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
                if (showContact) {
                    getSupportLoaderManager().initLoader(1, null, this);
                    tab = false;
                    sync = true;
//                    new AsyncgetContact(false, true).execute();
                } else if (showRecent) {
                    new AsyncgetRecentContact(false, true).execute();
                } else if (showMessage) {
                    new AsyncgetSMS(false, true).execute();
                }
                break;
            case R.id.rlShare:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I am using this free app to mute/unmute unknown callers while I am at work. I am recommend this amazing app, DOWNLOAD IT  \n https://play.google.com/store/apps/details?id=project.com.ringsureapp");
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                rlMenu.setVisibility(View.GONE);
                break;
            case R.id.rlBuy:
                rlMenu.setVisibility(View.GONE);
                UpgradeApp();
                break;
            case R.id.rlReset:
                Log.e(""," Working Properly Code Reset");
                MyDatabase db = new MyDatabase(HomePage_Activity.this);
                db.deleteAllData();

                imgRefresh.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
                if (showContact) {
                    getSupportLoaderManager().initLoader(1, null, this);
                    tab = false;
                    sync = true;
//                    new AsyncgetContact(false, true).execute();
                } else if (showRecent) {
                    new AsyncgetRecentContact(false, true).execute();
                } else if (showMessage) {
                    new AsyncgetSMS(false, true).execute();
                }
                break;
            case R.id.rlRate:
                rlMenu.setVisibility(View.GONE);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                //Copy App URL from Google Play Store.
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=project.com.ringsureapp"));

                startActivity(intent);
                break;
            case R.id.imgOverFlow:
                rlMenu.setVisibility(View.VISIBLE);
                break;
            case R.id.rlTab:
                rlMenu.setVisibility(View.GONE);
                break;
            case R.id.txtGeneral:
                HideKeyboard();
                txtTitle.setText("Configure ringer mode");
                boolGeneral = true;
                boolSilent = false;
                vSilent.setVisibility(View.INVISIBLE);
                vGeneral.setVisibility(View.VISIBLE);
                if (editSearch.getText().toString().trim().length() > 0) {

                    for (int i = 0; i < tempUserList.size(); i++) {
                        ContactData cdata = dbHelper.getDataFromNumberSilent(tempUserList.get(i).getPhoneNumber());
                        tempUserList.set(i, cdata);
                    }


                    if (boolGeneral) {
                        adapter = new ContactAdapter(HomePage_Activity.this, tempUserList, true, listContactGeneral, imgMessage);
                    } else {
                        adapter = new ContactAdapter(HomePage_Activity.this, tempUserList, false, listContactGeneral, imgMessage);
                    }

                    listContactGeneral.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    if (showContact) {
                        getSupportLoaderManager().initLoader(1, null, this);
                        tab = true;
                        sync = false;
                        //new AsyncgetContact(true, false).execute();
                    } else if (showRecent) {

                        new AsyncgetRecentContact(true, false).execute();

                    } else if (showMessage) {
                        new AsyncgetSMS(true, false).execute();
                    }
                }
               break;
            case R.id.txtSilent:
                HideKeyboard();
                txtTitle.setText("Configure silent mode");
                boolGeneral = false;
                boolSilent = true;
                vSilent.setVisibility(View.VISIBLE);
                vGeneral.setVisibility(View.INVISIBLE);
                if (editSearch.getText().toString().trim().length() > 0) {
                    if (boolGeneral) {
                        adapter = new ContactAdapter(HomePage_Activity.this, tempUserList, true, listContactGeneral, imgMessage);
                    } else {
                        adapter = new ContactAdapter(HomePage_Activity.this, tempUserList, false, listContactGeneral, imgMessage);
                    }

                    listContactGeneral.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    if (showContact) {
                        getSupportLoaderManager().initLoader(1, null, this);
                        tab = true;
                        sync = false;
                        //   new AsyncgetContact(true, false).execute();

                    } else if (showRecent) {

                        new AsyncgetRecentContact(true, false).execute();

                    } else if (showMessage) {
                        new AsyncgetSMS(true, false).execute();
                    }
                }

                break;
            default:
                break;
        }
    }

    private void UpgradeApp() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.upgrade_user);

        RelativeLayout rlUpgrade = (RelativeLayout) dialog.findViewById(R.id.rlUpgrade);
        RelativeLayout rlNotNow = (RelativeLayout) dialog.findViewById(R.id.rlNotNow);

        rlUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mHelper.launchPurchaseFlow(HomePage_Activity.this, ITEM_SKU, 10001,
                        mPurchaseFinishedListener, "mypurchasetoken");
                dialog.dismiss();

//               new  AsyncActionUpgrade(userId,userName).execute();
            }
        });

        rlNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }


    public void SetUI(boolean contact, boolean recent, boolean message ,boolean timer ) {
//        txtvwContact.setBackgroundColor(Color.parseColor("#1256DB"));
        txtvwContact.setBackgroundColor(Color.parseColor("#00000000"));
        txtvwCallLog.setBackgroundColor(Color.parseColor("#00000000"));
        txtvwSMS.setBackgroundColor(Color.parseColor("#00000000"));
        rlTimer.setBackgroundColor(Color.parseColor("#00000000"));

        if (contact) {
            txtvwContact.setBackgroundColor(Color.parseColor("#1256DB"));
        } else if (recent) {
            txtvwCallLog.setBackgroundColor(Color.parseColor("#1256DB"));
        } else if (message) {
            txtvwSMS.setBackgroundColor(Color.parseColor("#1256DB"));
        }else if (timer) {
            rlTimer.setBackgroundColor(Color.parseColor("#1256DB"));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh) {

            if (showContact) {
                getSupportLoaderManager().initLoader(1, null, this);
                tab = false;
                sync = true;

                //    new AsyncgetContact(false, true).execute();

            } else if (showRecent) {

                new AsyncgetRecentContact(false, true).execute();

            } else if (showMessage) {
                new AsyncgetSMS(false, true).execute();
            }
            return true;

        } else if (id == R.id.buynow) {
            mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,
                    mPurchaseFinishedListener, "mypurchasetoken");
        } else if (id == R.id.share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "This is the text that will be shared");
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        }

        return super.onOptionsItemSelected(item);
    }


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase) {
            if (result.isFailure()) {
                // Handle error
//                editor.putBoolean("purchase",true).commit();
//                imgMessage.setImageResource(R.drawable.message_buy);
//                rlBuy.setVisibility(View.GONE);
//                adapter.notifyDataSetChanged();
//                Toast.makeText(HomePage_Activity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
//                editor.putBoolean("purchase",true).commit();
//                imgMessage.setImageResource(R.drawable.message_buy);
//                rlBuy.setVisibility(View.GONE);
//                adapter.notifyDataSetChanged();
//                Toast.makeText(HomePage_Activity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                        rlBuy.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

//                        Toast.makeText(HomePage_Activity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        // handle error
//                        editor.putBoolean("purchase",true).commit();
//                        imgMessage.setImageResource(R.drawable.message_buy);
//                        rlBuy.setVisibility(View.GONE);
//                        adapter.notifyDataSetChanged();
                    }
                }
            };


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        dialog = new Dialog(HomePage_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_progress);

        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        return new CursorLoader(this, CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        HashMap<String, ContactData> contactList = new HashMap<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            phone = FunctionUtils.getInstance().getFormattedNumber(phone);
            if (phone.length() > 8) {
                ContactData data = new ContactData(id, name, phone, "General", "General", "Silent", "Silent", "", 0, 0);
                if (!contactList.containsKey(phone)) {
                    contactList.put(phone, data);
                }
            }

            cursor.moveToNext();
        }

        new AsyncgetContact(tab, sync, new ArrayList<ContactData>(contactList.values())).execute();
    }

    private void getContacts(ContactsGetter contactsGetter) {
        this.contactsGetter = contactsGetter;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    public class AsyncgetContact extends AsyncTask<String, String, String> {
        private ArrayList<ContactData> contactData;
        //  Dialog dialog;
        boolean tab, sync;
        String dataJson;
        JSONObject dataJsonObject = new JSONObject();
        JSONArray dataArray = new JSONArray();


        HttpClient client;

        HttpResponse response;

        public AsyncgetContact(boolean tab, boolean sync) {
            this.tab = tab;
            this.sync = sync;
//            data = "{\"data\":[{\"ContactMobileNumber\":\"9855435541\",\"ContactName\":\"Neeraj\",\"ContactEmail\":\"neeraj@gmail.com\"}]}";


            if (!pref.getBoolean("getcontact", false)) {

                dataJson = "{\"data\":[{";
            }

            if (sync) {
                refreshList = new ArrayList<>();
                tempRefreshList = new ArrayList<>();
            } else {
                dataList.clear();
                tempList.clear();

            }

        }

        public AsyncgetContact(boolean tab, boolean sync, ArrayList<ContactData> contactData) {
            this.tab = tab;
            this.sync = sync;
//            data = "{\"data\":[{\"ContactMobileNumber\":\"9855435541\",\"ContactName\":\"Neeraj\",\"ContactEmail\":\"neeraj@gmail.com\"}]}";


            if (!pref.getBoolean("getcontact", false)) {

                dataJson = "{\"data\":[{";
            }

            if (sync) {
                refreshList = new ArrayList<>();
                tempRefreshList = new ArrayList<>();
            } else {
                dataList.clear();
                tempList.clear();

            }
            this.contactData = contactData;
        }

        @Override
        protected void onPreExecute() {

          /*  dialog = new Dialog(HomePage_Activity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_progress);

            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            if (!pref.getBoolean("getcontact", false)) {
                client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
            }

            if (sync) {

                //     tempRefreshList = FunctionUtils.getInstance().getContacts();

                tempRefreshList = contactData;

                refreshList = FunctionUtils.getInstance().getSortedList(tempRefreshList);
              /*  tempRefreshList = functions.ReadContact(HomePage_Activity.this);
                refreshList = functions.CustomRemoveDuplicat(tempRefreshList, true);*/
                for (int i = 0; i < refreshList.size(); i++) {
                    if (dbHelper.getData2(refreshList.get(i).getPhoneNumber(), "1", "12", "123", "13") == 0) {
                        ContactData data = refreshList.get(i);
                        ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "1", data.getRecentOrder(), data.getSMSOrder());
                        dbHelper.InsertContact(data1);
                    }
                }

                refreshList.clear();
                tempRefreshList.clear();
                tempRefreshList = dbHelper.getAllData("1", "12", "123", "13");
                refreshList = FunctionUtils.getInstance().getSortedList(tempRefreshList);

                //  refreshList = functions.CustomRemoveDuplicat(tempRefreshList, true);
            } else {
                if (dbHelper.getData("1") == 0) {
                    //       tempList = FunctionUtils.getInstance().getContacts();
                    tempList = contactData;
                    dataList = FunctionUtils.getInstance().getSortedList(tempList);
                    /*tempList = functions.ReadContact(HomePage_Activity.this);
                    dataList = functions.CustomRemoveDuplicat(tempList, true);*/
                    for (int i = 0; i < dataList.size(); i++) {
                        ContactData data = dataList.get(i);
                        ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "1", data.getRecentOrder(), data.getSMSOrder());
                        dbHelper.InsertContact(data1);

                        if (!pref.getBoolean("getcontact", false)) {
                            dataArray.put(FunctionUtils.getInstance().getJSONObject(data1));
                            if (i == 0) {
                                dataJson = dataJson + "\"ContactMobileNumber\":\"" + data1.getPhoneNumber() + "\",\"ContactName\":\"" + data1.getName() + "\",\"ContactEmail\":\"\"}";
                            } else {
                                dataJson = dataJson + ",{\"ContactMobileNumber\":\"" + data1.getPhoneNumber() + "\",\"ContactName\":\"" + data1.getName() + "\",\"ContactEmail\":\"\"}";
                            }
                        }


                    }
                    if (!pref.getBoolean("getcontact", false)) {
                        try {
                            dataJsonObject.put("data", dataArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dataJson = dataJson + "]}";
                    }

                } else {
                    if (!pref.getBoolean("getcontact", false)) {
                        //    tempList = FunctionUtils.getInstance().getContacts();

                        tempList = contactData;
                        dataList = FunctionUtils.getInstance().getSortedList(tempList);
                     /*   ArrayList<ContactData> tempLists = functions.ReadContact(HomePage_Activity.this);
                        ArrayList<ContactData> dataLists = functions.CustomRemoveDuplicat(tempLists, true);
                     */
                        for (int i = 0; i < dataList.size(); i++) {
                            ContactData data = dataList.get(i);

                            if (!pref.getBoolean("getcontact", false)) {
                                dataArray.put(FunctionUtils.getInstance().getJSONObject(data));

                                if (i == 0) {
                                    dataJson = dataJson + "\"ContactMobileNumber\":\"" + data.getPhoneNumber() + "\",\"ContactName\":\"" + data.getName() + "\",\"ContactEmail\":\"\"}";
                                } else {
                                    dataJson = dataJson + ",{\"ContactMobileNumber\":\"" + data.getPhoneNumber() + "\",\"ContactName\":\"" + data.getName() + "\",\"ContactEmail\":\"\"}";
                                }
                            }
                        }
                        if (!pref.getBoolean("getcontact", false)) {
                            try {
                                dataJsonObject.put("data", dataArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dataJson = dataJson + "]}";
                        }
                    }
                }
                tempList.clear();
                dataList.clear();
                tempList = dbHelper.getAllData("1", "12", "123", "13");
                dataList = FunctionUtils.getInstance().getSortedList(tempList);

                //  dataList = functions.CustomRemoveDuplicat(tempList, true);
                editor.putInt(ContactSize, dataList.size()).commit();
                showContact = true;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing())
                dialog.dismiss();

            if (!pref.getBoolean("getcontact", false)) {
                editor.putBoolean("getcontact", true).commit();
                try {

                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .readTimeout(2, TimeUnit.MINUTES)
                            .connectTimeout(2, TimeUnit.MINUTES)
                            .build();

                    API api = new Retrofit.Builder().baseUrl("http://traala.com.md-in-45.webhostbox.net/dnd/admin/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient)
                            .build().create(API.class);

                    Call<ResponseBody> call = api.saveContact("api", "uploadcontact_for_ringsure", dataJsonObject.toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String result1 = new String(response.body().bytes());
                                Log.d("res", result1);
                                setContactData(result1, sync, dialog);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d("ressss", "SocketTiimeout");

                            t.printStackTrace();
                            //    dialog.dismiss();
                            //    setContactData1(sync, dialog);
                        }
                    });
                        /*HttpPost post = new HttpPost("http://traala.com.md-in-45.webhostbox.net/dnd/admin/index.php");
//
                        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                        nameValuePairs.add(new BasicNameValuePair("control", "api"));
                        nameValuePairs.add(new BasicNameValuePair("task", "uploadcontact_for_ringsure"));
                        nameValuePairs.add(new BasicNameValuePair("contact_list", dataJson));
                        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        response = client.execute(post);
                        if (response != null) {
                            InputStream in = response.getEntity().getContent();
                            result = ModelFunction.convertStreamToString(in);
                        }*/
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.toString());
                    //   setContactData1(sync, dialog);
                }
            }

            setContactData1(sync, dialog);

            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private void setContactData1(boolean sync, Dialog dialog) {
        try {
            if (sync) {
                editSearch.setHint("" + refreshList.size() + " Contacts");
                dataList.clear();
                dataList = refreshList;
                if (boolGeneral) {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, true, listContactGeneral, imgMessage);
                } else {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, false, listContactGeneral, imgMessage);
                }

                listContactGeneral.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                editSearch.setHint("" + dataList.size() + " Contacts");
                if (boolGeneral) {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, true, listContactGeneral, imgMessage);
                } else {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, false, listContactGeneral, imgMessage);
                }
                listContactGeneral.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //   dialog.dismiss();
    }

    private void setContactData(String result, boolean sync, Dialog dialog) {
        if (!pref.getBoolean("getcontact", false)) {
            try {
                JSONTokener tokener = new JSONTokener(result);
                JSONObject finalResult = new JSONObject(tokener);
                String returnCode = finalResult.getString("Contactlist");
                JSONTokener tokener1 = new JSONTokener(returnCode);
                JSONObject finalResult1 = new JSONObject(tokener1);

                String resultText = finalResult1.getString("status");
                if (resultText.equals("1")) {
                    editor.putBoolean("getcontact", true).commit();

                } else if (resultText.equals("0")) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //{"Contactlist":{"response":"Successfully Update Contact List ","status":"1"}}
        //  setContactData1(sync, dialog);
    }

    public class AsyncgetRecentContact extends AsyncTask<String, String, String> {
        Dialog dialog;
        boolean tab, sync;
        ArrayList<ContactData> tData;

        public AsyncgetRecentContact(boolean tab, boolean sync) {
            this.tab = tab;
            this.sync = sync;

            if (sync) {
                refreshList = new ArrayList<>();
                tempRefreshList = new ArrayList<>();
                tData = new ArrayList<>();
            } else {
                dataList.clear();
                tempList.clear();
            }
        }
        @Override
        protected void onPreExecute() {
            dialog = new Dialog(HomePage_Activity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_progress);

            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            if (sync) {
                tempList = FunctionUtils.getInstance().getRecentContacts();
              /*  tData = functions.getRecentContacts(HomePage_Activity.this);
                tempList = functions.CustomRemoveDuplicat(tData, false);*/
                for (int i = 0; i < tempList.size(); i++) {
                    if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") == 0 && dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "3") == 0 && dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "13") == 0) {

                        if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "12") == 0 && dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "123") == 0) {
                            if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "2") == 0) {
                                ContactData data = new ContactData(tempList.get(i).getId(), tempList.get(i).getName(), tempList.get(i).getPhoneNumber(), tempList.get(i).getCALLMODEGeneral(), tempList.get(i).getMESSAGEMODEGeneral(), tempList.get(i).getCallModeSilent(), tempList.get(i).getMessageModeSilent(), "2", i, tempList.get(i).getSMSOrder());
                                dbHelper.InsertContact(data);
                            } else {
                                ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                                ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "2", i, data.getSMSOrder());
                                dbHelper.UpdateContact(data1);
                            }
                        } else {
                            if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "12") != 0) {
                                ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                                ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "12", i, data.getSMSOrder());
                                dbHelper.UpdateContact(data1);
                            } else {
                                ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                                ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "123", i, data.getSMSOrder());
                                dbHelper.UpdateContact(data1);
                            }
                        }

                    } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "13") != 0) {
                        ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                        ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "123", i, data.getSMSOrder());
                        dbHelper.UpdateContact(data1);

                    } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") != 0 || dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "3") != 0) {

                        if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") != 0) {
                            ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                            ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "12", i, data.getSMSOrder());
                            dbHelper.UpdateContact(data1);
                        } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "3") != 0) {
                            ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                            ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "23", i, data.getSMSOrder());
                            dbHelper.UpdateContact(data1);
                        }
                    }
                }
                refreshList.clear();
                tempRefreshList.clear();
                dataList.clear();
                tempList = dbHelper.getAllDataRecent("2", "12", "123", "23");
                dataList = functions.CustomRemoveDuplicat(tempList, false);

            } else {
                if (dbHelper.getData("2") == 0) {
                    // tempList = functions.getRecentContacts(HomePage_Activity.this);
                    tempList = FunctionUtils.getInstance().getRecentContacts();

                    for (int i = 0; i < tempList.size(); i++) {
                        if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") == 0 && dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "3") == 0 && dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "13") == 0) {
                            ContactData data = new ContactData(tempList.get(i).getId(), tempList.get(i).getName(), tempList.get(i).getPhoneNumber(), tempList.get(i).getCALLMODEGeneral(), tempList.get(i).getMESSAGEMODEGeneral(), tempList.get(i).getCallModeSilent(), tempList.get(i).getMessageModeSilent(), "2", i, tempList.get(i).getSMSOrder());

                            if (dbHelper.getDataFromNumber(data.getPhoneNumber(), "12") == 0 && dbHelper.getDataFromNumber(data.getPhoneNumber(), "123") == 0) {
                                if (dbHelper.getDataFromNumber(data.getPhoneNumber(), "2") == 0) {
                                    dbHelper.InsertContact(data);
                                }
                            }

                        } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "13") != 0) {
                            ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                            ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "123", i, data.getSMSOrder());
                            dbHelper.UpdateContact(data1);

                        } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") != 0 || dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "3") != 0) {

                            if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") != 0) {
                                ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                                ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "12", i, data.getSMSOrder());
                                dbHelper.UpdateContact(data1);
                            } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "3") != 0) {
                                ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                                ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "23", i, data.getSMSOrder());
                                dbHelper.UpdateContact(data1);
                            }
                        }
                    }
                }
                tempList.clear();
                dataList.clear();
                tempList = dbHelper.getAllDataRecent("2", "12", "123", "23");
                dataList = functions.CustomRemoveDuplicat(tempList, false);
                editor.putInt(RecentSize, dataList.size()).commit();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (sync) {
                editSearch.setHint("" + dataList.size() + " Contacts");
                if (boolGeneral) {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, true, listContactGeneral, imgMessage);
                } else {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, false, listContactGeneral, imgMessage);
                }
                listContactGeneral.setAdapter(adapter);
            } else {
                editSearch.setHint("" + dataList.size() + " Contacts");
                if (boolGeneral) {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, true, listContactGeneral, imgMessage);
                } else {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, false, listContactGeneral, imgMessage);
                }
                listContactGeneral.setAdapter(adapter);
                if (tab) {
                    showContact = false;
                    showRecent = true;
                    showMessage = false;
                }
            }
            dialog.dismiss();
        }
    }
    public class AsyncgetSMS extends AsyncTask<String, String, String> {
        Dialog dialog;
        boolean tab, sync;
        ArrayList<ContactData> tData;

        public AsyncgetSMS(boolean tab, boolean sync) {
            this.tab = tab;
            this.sync = sync;
            if (sync) {
                refreshList = new ArrayList<>();
                tempRefreshList = new ArrayList<>();
            } else {
                dataList.clear();
                tempList.clear();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = new Dialog(HomePage_Activity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.dialog_progress);

            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            if (sync) {
                tData = functions.fetchInbox(HomePage_Activity.this);
                tempList = functions.CustomRemoveDuplicat(tData, false);
                for (int i = 0; i < tempList.size(); i++) {
                    if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") == 0 && dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "2") == 0 && dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "12") == 0) {

                        if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "13") == 0 && dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "123") == 0) {
                            if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "3") == 0) {
                                ContactData data = new ContactData(tempList.get(i).getId(), tempList.get(i).getName(), tempList.get(i).getPhoneNumber(), tempList.get(i).getCALLMODEGeneral(), tempList.get(i).getMESSAGEMODEGeneral(), tempList.get(i).getCallModeSilent(), tempList.get(i).getMessageModeSilent(), "3", tempList.get(i).getRecentOrder(), i);
                                dbHelper.InsertContact(data);
                            } else {
                                ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                                ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "3", data.getRecentOrder(), i);
                                dbHelper.UpdateContact(data1);
                            }
                        } else {
                            if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "13") != 0) {
                                ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                                ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "13", data.getRecentOrder(), i);
                                dbHelper.UpdateContact(data1);
                            } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "123") != 0) {
                                ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                                ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "123", data.getRecentOrder(), i);
                                dbHelper.UpdateContact(data1);
                            }
                        }

                    } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "12") != 0) {
                        ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                        ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "123", data.getRecentOrder(), i);
                        dbHelper.UpdateContact(data1);

                    } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") != 0 || dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "2") != 0) {

                        if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") != 0) {
                            ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                            ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "13", data.getRecentOrder(), i);
                            dbHelper.UpdateContact(data1);
                        } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "2") != 0) {
                            ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                            ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "23", data.getRecentOrder(), i);
                            dbHelper.UpdateContact(data1);
                        }
                    }
                }
                refreshList.clear();
                dataList.clear();
                tempList = dbHelper.getAllDataMessage("3", "13", "123", "23");
                dataList = functions.CustomRemoveDuplicat(tempList, false);
            } else {

                if (dbHelper.getData("3") == 0) {
                    tempList = functions.fetchInbox(HomePage_Activity.this);
                    for (int i = 0; i < tempList.size(); i++) {
                        if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") == 0 && dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "2") == 0 && dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "12") == 0) {
                            ContactData data = new ContactData(tempList.get(i).getId(), tempList.get(i).getName(), tempList.get(i).getPhoneNumber(), tempList.get(i).getCALLMODEGeneral(), tempList.get(i).getMESSAGEMODEGeneral(), tempList.get(i).getCallModeSilent(), tempList.get(i).getMessageModeSilent(), "3", tempList.get(i).getRecentOrder(), i);

                            if (dbHelper.getDataFromNumber(data.getPhoneNumber(), "13") == 0 && dbHelper.getDataFromNumber(data.getPhoneNumber(), "123") == 0) {
                                if (dbHelper.getDataFromNumber(data.getPhoneNumber(), "3") == 0) {
                                    dbHelper.InsertContact(data);
                                }
                            }
                        } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "12") != 0) {
                            ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                            ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "123", data.getRecentOrder(), i);
                            dbHelper.UpdateContact(data1);

                        } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") != 0 || dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "2") != 0) {

                            if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "1") != 0) {
                                ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                                ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "13", data.getRecentOrder(), i);
                                dbHelper.UpdateContact(data1);
                            } else if (dbHelper.getDataFromNumber(tempList.get(i).getPhoneNumber(), "2") != 0) {
                                ContactData data = dbHelper.getDataFromNumberSilent(tempList.get(i).getPhoneNumber());
                                ContactData data1 = new ContactData(data.getId(), data.getName(), data.getPhoneNumber(), data.getCALLMODEGeneral(), data.getMESSAGEMODEGeneral(), data.getCallModeSilent(), data.getMessageModeSilent(), "23", data.getRecentOrder(), i);
                                dbHelper.UpdateContact(data1);
                            }
                        }
                    }
                }
                tempList.clear();
                dataList.clear();
                tempList = dbHelper.getAllDataMessage("3", "13", "123", "23");
                dataList = functions.CustomRemoveDuplicat(tempList, false);
//                        dataList = functions.RemoveDuplicat(tempList, true);
                editor.putInt(RecentSize, dataList.size()).commit();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (sync) {
                editSearch.setHint("" + dataList.size() + " Contacts");
                if (boolGeneral) {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, true, listContactGeneral, imgMessage);
                } else {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, false, listContactGeneral, imgMessage);
                }
                listContactGeneral.setAdapter(adapter);
            } else {
                editSearch.setHint("" + dataList.size() + " Contacts");
                if (boolGeneral) {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, true, listContactGeneral, imgMessage);
                } else {
                    adapter = new ContactAdapter(HomePage_Activity.this, dataList, false, listContactGeneral, imgMessage);
                }
                listContactGeneral.setAdapter(adapter);
                if (tab) {
                    showContact = false;
                    showRecent = false;
                    showMessage = true;
                }
            }
            dialog.dismiss();
        }
    }

    public IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                // handle error here
//                Toast.makeText(HomePage_Activity.this,"error",Toast.LENGTH_LONG).show();
            } else {
                // does the user have the premium upgrade?
                boolean mIsRemoveAdds = inventory.hasPurchase(ITEM_SKU);
                if (!mIsRemoveAdds) {
                    rlBuy.setVisibility(View.VISIBLE);
//                    imgMessage.setImageResource(R.drawable.message);
                    editor.putBoolean("purchase", false).commit();
                } else {
                    rlBuy.setVisibility(View.GONE);
//                    imgMessage.setImageResource(R.drawable.message_buy);
                    editor.putBoolean("purchase", true).commit();
//                    Toast.makeText(HomePage_Activity.this,"premium",Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    public void HideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private interface API {
        @FormUrlEncoded
        @POST("index.php")
        Call<ResponseBody> saveContact(@Field("control") String control, @Field("task") String task, @Field("contact_list") String contact_list);
    }

    private interface ContactsGetter {
        public void getContacts(ArrayList<ContactData> contactData);
    }

    /** Callback received when the user "picks" a time in the dialog */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    pHour = hourOfDay;
                    pMinute = minute;

                }
            };

    /** Updates the time in the EditText StartTime */
    private void updateDisplay() {
        etStarttime.setText(
                new StringBuilder()
                        .append(pad(pHour)).append(":")
                        .append(pad(pMinute)));
    }
    /** Updates the time in the EditText EndTime */
    private void updateDisplayendtime() {
        etEndTime.setText(
                new StringBuilder()
                        .append(pad(pHour)).append(":")
                        .append(pad(pMinute)));
    }

    /** Displays a notification when the time is updated */
    private void displayToast() {
        Toast.makeText(this, new StringBuilder().append("Time choosen is ").append(etStarttime.getText()),Toast.LENGTH_SHORT).show();

    }

        /** Displays a notification when the time is updated */
    private void displayToastEnd() {
        Toast.makeText(this, new StringBuilder().append("Time choosen is ").append(etEndTime.getText()),Toast.LENGTH_SHORT).show();

    }
    /** Add padding to numbers less than ten */
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    /** Create a new dialog for time picker */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, pHour, pMinute, false);
        }
        return null;
    }
}
