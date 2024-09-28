package com.example.safmn_2.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;


public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mfragmentList;
    public ViewPagerAdapter(@NonNull FragmentManager fm,List<Fragment> fragmentList) {
        super(fm);//调用父类构造函数，传递FragmentManager 参数
        //用于确保适配器类内部具有有效的 FragmentManager 实例，从而顺利完成片段管理和展示的任务
        this.mfragmentList = fragmentList;

    }//设置 ViewPager 的适配器。

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return  mfragmentList == null ? null: mfragmentList.get(position);
    }
    // mfragmentList == null ? null 判断mfragmentList是否为空，如果不为null，执行：后面的代码。
    //mfragmentList.get(position)获得第position个的fragment对象返回

    @Override
    public int getCount() {
        return  mfragmentList == null ? null: mfragmentList.size();
        // 返回片段对象的数量，即mfragmentList列表中的元素个数
    }
}
