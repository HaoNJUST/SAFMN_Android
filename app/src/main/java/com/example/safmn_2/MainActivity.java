package com.example.safmn_2;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.example.safmn_2.adapter.ViewPagerAdapter;
import com.example.safmn_2.fragment.homeFragment;
import com.example.safmn_2.fragment.moreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private homeFragment home_Fragment;

    private BottomNavigationView navigationView;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        navigationView = findViewById(R.id.nav_bottom);
        viewPager = findViewById(R.id.viewpager);

        //主页的homeFragment
        home_Fragment = new homeFragment();

        //         将 MyFragment 添加到 Activity 中
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.viewpager, home_Fragment)
                .commit();



        //将viewpage和底部导航栏同步
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new homeFragment()); //新建一个homeFragment对象将这个对象加入到数组fragments中
        fragments.add(new moreFragment());

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments);
        //创建对象并通过构造函数初始化，该适配器可以知道要显示哪些片段。
        viewPager.setAdapter(viewPagerAdapter);

        //将前面创建的 viewPagerAdapter 适配器设置给 viewPager 视图组件，以便在 ViewPager 中显示相应的页面。
        //底部导航栏监听事件

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.item_home) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (item.getItemId() == R.id.item_more) {
                    viewPager.setCurrentItem(1);
                    return true;
                }
                return false;
            }
        });

        // 添加页面切换的监听器，根据页面切换实现菜单切换
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                switch (position){  // 根据页面位置更新导航栏的选中状态
                    case 0:
                        navigationView.setSelectedItemId(R.id.item_home);
                        //将导航栏中的选中项设置为 R.id.item_home
                        break;
                    case 1:
                        navigationView.setSelectedItemId(R.id.item_more);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });//通过使用页面切换监听器，
        // 我们可以根据页面切换的情况来更改导航栏的选中状态，
        // 从而实现页面切换时导航栏菜单的同步切换效果。
    }
}