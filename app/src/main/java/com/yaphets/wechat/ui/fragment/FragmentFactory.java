package com.yaphets.wechat.ui.fragment;

public class FragmentFactory {
    //private static HashMap<Integer, BaseFragment> mBaseFragments = new HashMap<>();
    public static final int FRAGMENT_COUNT = 3;
    private static BaseFragment[] mBaseFragments = new BaseFragment[FRAGMENT_COUNT];


    public static BaseFragment createFragment(int pos) {
        //BaseFragment baseFragment = mBaseFragments.get(pos);
        BaseFragment baseFragment = mBaseFragments[pos];

        if (baseFragment == null) {
            switch (pos) {
                case 0:
                    baseFragment = new ChatFragment();
                    break;
                case 1:
                    baseFragment = new ContactFragment();
                    break;
                case 2:
                    baseFragment = new DiscoveryFragment();
                    break;
            }
            //mBaseFragments.put(pos, baseFragment);
            mBaseFragments[pos] = baseFragment;
        }
        return baseFragment;
    }
}
