package wxm.com.androiddesign.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import wxm.com.androiddesign.R;
import wxm.com.androiddesign.module.MyUser;
import wxm.com.androiddesign.module.User;
import wxm.com.androiddesign.network.JsonConnection;

/**
 * Created by zero on 2015/6/30.
 */
public class MultpleiProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public User user;
    Boolean isEdit = false;
    Context context;
    static boolean flag=false;
    String checkEmail = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    String checkPhone = "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";
    String checkGender = "['男''女']";
    String checkQq = "[1-9][0-9]{4,}";

    public MultpleiProfileAdapter(User user,Context context) {
        Log.i("pro", "pro");
        this.user = user;
        this.context = context;
        if(MyUser.userId.equals(user.getUserId())){
            flag=true;
        }else {
            flag=false;
        }
    }

    public MultpleiProfileAdapter(Context context) {
        this.context = context;
    }

    public void changeEditState(List<EditText> list, Boolean isEdit) {
        for (EditText editText : list) {
            editText.setEnabled(isEdit);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == 0) {
            return new BaseInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.user_base_info, parent, false
            ), new BaseInfoViewHolder.MyViewHolderClicks() {
                @Override
                public void mEditProfile(ImageView edit, List<EditText> list) {

                    if (!isEdit) {
                        edit.setImageResource(R.drawable.ic_action_done_black);
                        isEdit = true;
                        changeEditState(list, isEdit);

                    } else if (isEdit) {
                        if(!list.get(0).getText().toString().matches(checkGender)){
                            Toast.makeText(context,"unavailable gender!",Toast.LENGTH_SHORT).show();
                        }else if(!list.get(2).getText().toString().matches(checkQq)){
                            Toast.makeText(context,"unavailable qq!",Toast.LENGTH_SHORT).show();
                        }else {
                            edit.setImageResource(R.drawable.ic_action_mode_edit_black);
                            isEdit = false;
                            changeEditState(list, isEdit);
                            user.setUserGender(list.get(0).getText().toString());
                            user.setUserAddress(list.get(1).getText().toString());
                            user.setQq(list.get(2).getText().toString());
                            new UpDateProfile().execute();
                        }
                    }
                }
            });
        } else if(viewType==1)
            return new UserInfoViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.user_base_info_1, parent, false
        ), new UserInfoViewHolder.MyViewHolderClicks() {
            @Override
            public void mEditProfile(ImageView edit, List<EditText> list) {
                if (!isEdit) {
                    edit.setImageResource(R.drawable.ic_action_done_black);
                    isEdit = true;
                    changeEditState(list, isEdit);

                } else if (isEdit) {
                    if(!list.get(1).getText().toString().matches(checkEmail)){
                        Toast.makeText(context,"unavailable email!",Toast.LENGTH_SHORT).show();
                    }else if(!list.get(2).getText().toString().matches(checkPhone)){
                        Toast.makeText(context,"unavailable phone!",Toast.LENGTH_SHORT).show();
                    }else {
                        edit.setImageResource(R.drawable.ic_action_mode_edit_black);
                        isEdit = false;
                        changeEditState(list, isEdit);
                        user.setUserName(list.get(0).getText().toString());
                        user.setUserEmail(list.get(1).getText().toString());
                        user.setUserPhone(list.get(2).getText().toString());

                        new UpDateProfile().execute();
                    }
                }
            }
        });

        else return new FooViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.foo_item, parent, false
            ));
    }

    private class UpDateProfile extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            user.setAction("editProfile");
            JsonConnection.getJSON(new Gson().toJson(user));
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserInfoViewHolder) {
            ((UserInfoViewHolder)holder).user_email.setText(user.getUserEmail());
            ((UserInfoViewHolder) holder).user_name.setText(user.getUserName());
            ((UserInfoViewHolder) holder).user_phone.setText(user.getUserPhone());


        } else if (holder instanceof BaseInfoViewHolder) {
            ((BaseInfoViewHolder)holder).user_gender.setText(user.getUserGender());
            ((BaseInfoViewHolder)holder).user_place.setText(user.getUserLocation());
            ((BaseInfoViewHolder)holder).user_qq.setText(user.getQq());

        }
    }

    public void setText(EditText v,String text){
        v.setText(text);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else if(position==1)return 1;
        else return 2;
    }

    public static class BaseInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        List<EditText> editTextList = new ArrayList<>();
        @Bind(R.id.user_gender)
        EditText user_gender;
        @Bind(R.id.user_place)
        EditText user_place;
        @Bind(R.id.user_qq)
        EditText user_qq;
        @Bind(R.id.edit_info)
        ImageView edit;

        MyViewHolderClicks mListener;

        public BaseInfoViewHolder(View itemView, MyViewHolderClicks mListener) {
            super(itemView);
            this.mListener = mListener;
            ButterKnife.bind(this, itemView);
            if(flag){
                edit.setVisibility(View.VISIBLE);
            }else edit.setVisibility(View.GONE);
            editTextList.add(user_gender);
            editTextList.add(user_place);
            editTextList.add(user_qq);
            edit.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            mListener.mEditProfile((ImageView) v, editTextList);
        }

        public interface MyViewHolderClicks {
            public void mEditProfile(ImageView edit, List<EditText> list);
        }

    }


    public static class UserInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        List<EditText> editTextList = new ArrayList<>();
        @Bind(R.id.user_name)
        EditText user_name;
        @Bind(R.id.user_email)
        EditText user_email;
        @Bind(R.id.user_phone)
        EditText user_phone;


        @Bind(R.id.edit_info)
        ImageView edit;

        MyViewHolderClicks mListener;

        public UserInfoViewHolder(View itemView, MyViewHolderClicks mListener) {
            super(itemView);
            this.mListener = mListener;
            ButterKnife.bind(this, itemView);
            if(flag){
                edit.setVisibility(View.VISIBLE);
            }else edit.setVisibility(View.GONE);
            editTextList.add(user_name);
            editTextList.add(user_email);
            editTextList.add(user_phone);
            edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.mEditProfile((ImageView) v, editTextList);
        }

        public interface MyViewHolderClicks {
            public void mEditProfile(ImageView edit, List<EditText> list);
        }


    }

    public static class FooViewHolder extends RecyclerView.ViewHolder{

        public FooViewHolder(View itemView) {
            super(itemView);
        }
    }
}
