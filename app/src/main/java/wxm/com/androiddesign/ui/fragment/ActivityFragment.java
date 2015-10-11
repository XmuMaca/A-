package wxm.com.androiddesign.ui.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import wxm.com.androiddesign.module.AtyItem;
import wxm.com.androiddesign.adapter.MyRecycerAdapter;
import wxm.com.androiddesign.R;

import wxm.com.androiddesign.network.JsonConnection;
import wxm.com.androiddesign.services.LocationServices;
import wxm.com.androiddesign.utils.ScrollManager;


/**
 * Created by zero on 2015/6/25.
 */
public class ActivityFragment extends Fragment {
    private static final String TAG="ActivityFragment";

    public static final int HOT = 0x1;
    public static final int NEARBY = 0x2;
    public static final int HIGHT = 0x3;
    private int type;
    private String userId;

    @Bind(R.id.recyclerview_list)
    RecyclerView recyclerView;


    static MyRecycerAdapter myRecycerAdapter;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    ScrollManager manager = new ScrollManager();



    public static ActivityFragment newInstance(int type, String muserId) {
        ActivityFragment fragment = new ActivityFragment();
        Bundle args = new Bundle();
        args.putInt("Type", type);
        args.putString("userId", muserId);
        fragment.setArguments(args);
        return fragment;
    }

    static ArrayList<AtyItem> activityItems = new ArrayList<AtyItem>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("Type");
        userId = getArguments().getString("userId");
//        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.refresh_list, viewGroup, false);
        ButterKnife.bind(this, v);
        setupSwipeRefreshLayout(mSwipeRefreshLayout);
        setupRecyclerView(recyclerView);
        return v;
    }

    private void setupSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    private void refreshContent() {
        if (myRecycerAdapter != null) {
            //load content
            mSwipeRefreshLayout.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    myRecycerAdapter.notifyDataSetChanged();

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 5000);

        }
        //load complete
        onContentLoadComplete();
    }

    private void onContentLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setItemAnimator(new MyItemAnimator());
        manager.attach(recyclerView);
        manager.addView(getActivity().findViewById(R.id.fab), ScrollManager.Direction.DOWN);
        new GetAtyTask().execute(type);

    }


    private class GetAtyTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result == true) {
                if (activityItems == null) {
                    return;
                }
                myRecycerAdapter = new MyRecycerAdapter(activityItems, userId, (AppCompatActivity) getActivity(), "ActivityFragment");
                recyclerView.setAdapter(myRecycerAdapter);
            }
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            JSONObject object = new JSONObject();
            try {
                switch (params[0]) {
                    case HOT:
                        object.put("action", "showHotAty");
                        break;
                    case NEARBY:
                        object.put("action", "showNearbyAty");
                        object.put("latitude", LocationServices.Latitude);
                        object.put("longitude", LocationServices.Longitude);
                        break;
                    case HIGHT:
                        object.put("action", "showHightAty");
                        break;
                }
                object.put("userId", userId);
                String jsonarrys = JsonConnection.getJSON(object.toString());
                Log.i("jsonarray", jsonarrys.toString());
                activityItems = new Gson().fromJson(jsonarrys, new TypeToken<List<AtyItem>>() {
                }.getType());
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public static void refresh(AtyItem atyItem, int position) {
        activityItems.remove(position);
        activityItems.add(position, atyItem);
        myRecycerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach"+type);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated" + type);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart" + type);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume" + type);
    }

    @Override
    public void onPause() {
        super.onPause();
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        Log.d(TAG, "onPause" + type);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop" + type);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy" + type);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach" + type);
    }

    public SwipeRefreshLayout getmSwipeRefreshLayout() {
        return mSwipeRefreshLayout;
    }
}
