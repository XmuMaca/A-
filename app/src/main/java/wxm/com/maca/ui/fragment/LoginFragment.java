package wxm.com.maca.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import wxm.com.maca.R;
import wxm.com.maca.module.MyUser;
import wxm.com.maca.module.User;
import wxm.com.maca.network.JsonConnection;
import wxm.com.maca.ui.MainActivity;
import wxm.com.maca.utils.MyUtils;
import wxm.com.maca.utils.PrefUtils;

/**
 * Created by zero on 2015/6/29.
 */
public class LoginFragment extends DialogFragment implements PlatformActionListener,Handler.Callback {

    private static final String TAG="LoginFragment";
    public static final int MAIN=1;
    public static final int SIGN=2;
    private static final int MSG_AUTH_CANCEL=0x2;
    private static final int MSG_AUTH_ERROR=0x3;
    private static final int MSG_AUTH_COMPLETE=0x4;

    LoginCallBack loginCallBack;
    @Bind(R.id.email_edit_text)
    EditText user_email;
    @Bind(R.id.password_edit_text)
    EditText password;

    AppCompatActivity activity;
    String mPassword;
    String mId;
    Handler handler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    String mResult = "";
    int mType;
    String mLoginType;
    User user = new User();

    public static LoginFragment newInstance(int type){
        LoginFragment loginFragment=new LoginFragment();
        Bundle args=new Bundle();
        args.putInt("Type", type);
        loginFragment.setArguments(args);
        return loginFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType=getArguments().getInt("Type");
        handler=new Handler(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        if(outState==null)
//            super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_login, container);
        ButterKnife.bind(this, view);
        Point size= MyUtils.getScreenSize(getActivity());
        getDialog().getWindow().setLayout(size.x - 30, WindowManager.LayoutParams.WRAP_CONTENT);
        return view;
    }

    @OnClick(R.id.login_btn)
    public void Login() {
        myClickHandler();
    }

    @OnClick(R.id.sina_login_btn)
    public void onSinaLogin(){
        Log.v(TAG, "Sina");
        final Platform sina= ShareSDK.getPlatform(SinaWeibo.NAME);
        mLoginType=MyUser.SINA;
        if(sina.isValid()){
            Log.v(TAG, "已授权");
            getInfo(sina);
        }else
        {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    authorize(sina);
                }
            });
        }

    }

    @OnClick(R.id.qq_login_btn)
    public void onQQLogin(){
        Log.v(TAG, "QQ");
        final Platform qq= ShareSDK.getPlatform(QQ.NAME);
        mLoginType=MyUser.QQ;
        if(qq.isValid()){
            Log.v(TAG, "已授权");
            getInfo(qq);
        }else{
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    authorize(qq);
                }
            });
        }

    }

    private void authorize(Platform platform){
        if (platform==null){
            Log.w(TAG,"platform is null");
            return;
        }
        Log.i(TAG,"authorize");
        platform.setPlatformActionListener(this);
        platform.authorize();
        platform.SSOSetting(true);
        platform.showUser(null);
    }

    public void getInfo(Platform platform){
        PlatformDb db=platform.getDb();
        mId=db.getUserId();
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("action", "login" +mLoginType);
            jsonObject.put("userId", mId);
            Log.d(TAG, jsonObject.toString());
            new LoginTask(getContext()).execute(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void myClickHandler() {
        ConnectivityManager check = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = check.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            user.setAction("loginemail");
            user.setUserEmail(user_email.getText().toString());
            mPassword=password.getText().toString();
            user.setUserPassword(mPassword);
            mLoginType=MyUser.EMAIL;
            new LoginTask(getActivity()).execute(new Gson().toJson(user));

        } else {
        }
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
        if (action==Platform.ACTION_USER_INFOR){
            Log.i(TAG,"onComplete");
            Message msg=new Message();
            msg.what=MSG_AUTH_COMPLETE;
            msg.obj=platform;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        if(action==Platform.ACTION_USER_INFOR){
            Log.i(TAG, "onError:" + throwable.getMessage() + "|" + throwable.toString());
            handler.sendEmptyMessage(MSG_AUTH_ERROR);
        }
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int action) {
        if (action==Platform.ACTION_USER_INFOR){
            Log.i(TAG, "onCancel");
            handler.sendEmptyMessage(MSG_AUTH_CANCEL);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what) {
            case MSG_AUTH_CANCEL: {
                //取消授权
                //materialDialog.dismiss();
                Log.i(TAG, "MSG_AUTH_CANCEL");
            } break;
            case MSG_AUTH_ERROR: {
                //授权失败
//                materialDialog.dismiss();
                Log.i(TAG, "MSG_AUTH_ERROR");
            } break;
            case MSG_AUTH_COMPLETE: {
                //授权成功
                Log.i(TAG, "MSG_AUTH_COMPLETE");
                Platform platform=(Platform)msg.obj;
                getInfo(platform);
            } break;
        }
        return false;
    }



    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        Context context;
        User mUser;
        public LoginTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dismiss();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            mResult = JsonConnection.getJSON(params[0]);
            Log.d(TAG,mResult);
            if (mResult != "") {
                if (mResult.contains("false")) {
                    return false;
                } else if (mResult.contains("true")){
                    mUser= new Gson().fromJson(mResult, User.class);
                    if(mLoginType==MyUser.EMAIL){
                        MyUser.setUserEmail(mUser.getUserEmail());
                        MyUser.setUserPassword(mUser.getUserPassword());
                        SharedPreferences prefs = activity.getSharedPreferences("wxm.com.androiddesign", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("UserEmail",mUser.getUserEmail());
                        editor.putString("UserPassword",mPassword);
                        PrefUtils.markUse(context);
                        editor.putString("LoginType", mLoginType);
                        editor.putString("easemobId",mUser.getEasemobId());
                        editor.apply();
                    }else {
                        SharedPreferences prefs = activity.getSharedPreferences("wxm.com.androiddesign", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        MyUser.setUserId(mId);
                        MyUser.setLoginType(mLoginType);
                        editor.putString("UserId", mId);
                        editor.putString("LoginType",mLoginType);
                        PrefUtils.markUse(context);
                        editor.putString("easemobId", mUser.getEasemobId());
                        editor.apply();
                    }
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result == true) {
                if (mType==SIGN){
                    Intent intent = new Intent(activity,MainActivity.class);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    activity.finish();
                }
                else if(mType==MAIN){
                    loginCallBack.onLongin(mUser);
                }
            } else {
//                new MaterialDialog.Builder(MyApplication.applicationContext)
//                        .title("登陆失败")
//                        .content("请重新登陆")
//                        .positiveText("确定")
//                        .show();
            }
        }
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity=(AppCompatActivity)activity;
        if (activity instanceof MainActivity) {
            loginCallBack = (LoginCallBack) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface LoginCallBack {
        public void onLongin(User user);
    }
}
