package wxm.com.androiddesign.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
//import com.melnykov.fab.FloatingActionButton;

import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import wxm.com.androiddesign.ui.fragment.DatePickerFragment;
import wxm.com.androiddesign.ui.fragment.FragmentParent;
import wxm.com.androiddesign.ui.fragment.HomeFragment;
import wxm.com.androiddesign.R;
import wxm.com.androiddesign.ui.fragment.LoginFragment;
import wxm.com.androiddesign.ui.fragment.ReleaseFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,LoginFragment.LoginCallBack{
    DrawerLayout drawerLayout;

    public static MainActivity instance = null;

    @Bind(R.id.fab)FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, new HomeFragment()).commit();
        }

        ButterKnife.bind(this);
        setupFab();

        setupNavigationView();
        setupInfo();
    }


    private void setupFab(){

       fab.setOnClickListener(this);
    }

    private void setupInfo(){
        SharedPreferences prefs=getSharedPreferences("wxm.com.androiddesign", Context.MODE_PRIVATE);
        String name=prefs.getString("Username",null);
        String email=prefs.getString("Useremail",null);
        if(name!=null&&email!=null){
            ((TextView) findViewById(R.id.username)).setText(name);
            ( (TextView) findViewById(R.id.user_email)).setText(email);
        }
    }

    @Override
    public void onLongin(String name,String email) {
        SharedPreferences prefs=getSharedPreferences("wxm.com.androiddesign", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putString("Username",name);
        editor.putString("Useremail",email);
        editor.apply();
        ((TextView) findViewById(R.id.username)).setText(name);
        ( (TextView) findViewById(R.id.user_email)).setText(email);
    }

    private void setupDrawerContent(final NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        for(int index=0;index<navigationView.getMenu().size();index++){
                            navigationView.getMenu().getItem(index).setChecked(false);
                        }
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();

                                return true;
                            case R.id.nav_explore:
                                getSupportFragmentManager().beginTransaction().replace(R.id.content, new FragmentParent()).commit();

                                return true;
                            case R.id.nav_attention:
//
                                Snackbar.make( drawerLayout, "关注",
                                        Snackbar.LENGTH_SHORT).show();
                                return true;
                            case R.id.nav_user_setting:

                                return true;
                            case R.id.nav_setting:

                                Snackbar.make(drawerLayout, "设置",
                                        Snackbar.LENGTH_SHORT).show();
                                return true;
                            default:
                                return true;
                        }
                    }
                });
    }


    private void setupNavigationView(){

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigation_view);
        if(navigationView!=null){
            setupDrawerContent(navigationView);
        }
        View header=findViewById(R.id.header);
        header.setClickable(true);
        //header.setElevation(0.1);

        final CircleImageView userPhoto=(CircleImageView)findViewById(R.id.user_photo);


        userPhoto.setClickable(true);
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(((TextView)findViewById(R.id.username)).getText().equals("未登录")){
                    drawerLayout.closeDrawers();
                    showLoginDialog();
                }
                else {
                   Intent intent = new Intent(MainActivity.this, UserAcitivity.class);
                   ActivityOptionsCompat options=ActivityOptionsCompat.makeSceneTransitionAnimation(
                           MainActivity.this, new Pair<View, String>(userPhoto, getResources().getString(R.string.transition_user_photo))
                   );
                   ActivityCompat.startActivity(MainActivity.this, intent, options.toBundle());
                }
            }
        });
        userPhoto.setClickable(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

            //Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
        if(Build.VERSION.SDK_INT>= 21){
            //startActivity(intent);
        }
        Intent intent =new Intent(MainActivity.this,ReleaseActivity.class);
        startActivity(intent);
        }

    private void showLoginDialog(){
        //getSupportFragmentManager().beginTransaction().replace(R.id.content, new ReleaseFragment()).commit();
        FragmentManager fm=getSupportFragmentManager();
        LoginFragment releaseFragment=new LoginFragment();
        releaseFragment.show(fm, "login");
    }


}
