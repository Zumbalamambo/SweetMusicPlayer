package com.huwei.sweetmusicplayer.business.fragments;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huwei.sweetmusicplayer.R;
import com.huwei.sweetmusicplayer.business.OnlineSearchActivity;
import com.huwei.sweetmusicplayer.contains.IMusicViewTypeContain;
import com.huwei.sweetmusicplayer.business.fragments.base.BaseFragment;
import com.huwei.sweetmusicplayer.business.ui.adapters.PagerStateAdapter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.IntArrayRes;

/**
 * @author Jayce
 * @date 2015/6/17
 */
@EFragment(R.layout.fragment_main)
public class MainFragment extends BaseFragment implements IMusicViewTypeContain {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById
    ViewPager viewPager;
    @ViewById
    TabLayout tabs;
    @ViewById
    TextView tv_sleepinfo, tv_sleep_cancel;
    @ViewById
    LinearLayout ll_sleepbar;

    @SystemService
    LayoutInflater inflater;
    @IntArrayRes
    int sleep_times[];

    private View mView;

    PagerStateAdapter mPagerAdapter;

    private long sleeptime = 0;
    private final int SLEEP = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //第二次可以直接返回mView
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
            return mView;
        }
        Log.i(TAG, "onCreateView");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSleepCountDown()) {
            setSleepBarVisiable(true);
        } else {
            setSleepBarVisiable(false);
        }

        mPagerAdapter.notifyDataSetChanged();
    }

    @AfterViews
    void init() {

        //防止第二次加载
        if (mView == null) {
            mView = getView();
            initToolBar();
            initPager();
        }
    }

    @Click(R.id.tv_sleep_cancel)
    void tv_sleep_cancelWasClicked() {
        setSleepBarVisiable(false);
        sleeptime = -1;
    }

    void initToolBar() {
        toolbar.setTitle("SweetMusicPlayer");
        toolbar.inflateMenu(R.menu.activity_main_menu);
        initMenu(toolbar.getMenu());
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_search:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }


    private void initMenu(Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        SearchManager searchManager = (SearchManager) mAct.getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(mAct, OnlineSearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));
        searchView.setIconifiedByDefault(true);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initPager() {
        mPagerAdapter = new PagerStateAdapter(
                getActivity().getSupportFragmentManager()) {

            @Override
            public CharSequence getPageTitle(int position) {
                return getResources().getStringArray(R.array.tab_titles)[position];
            }
        };

        // add tabs_recent
//        mPagerAdapter.addFragment(new RecentlyAddedFragment());
        // add tab_songs
        Bundle bundle = new Bundle();
        bundle.putInt(MUSIC_SHOW_TYPE, SHOW_MUSIC);

        String tabs_str[] = getResources().getStringArray(R.array.tab_titles);

        LocalMusicFragment musicFragment = new LocalMusicFragment_();
        musicFragment.setArguments(bundle);
        mPagerAdapter.addFragment(musicFragment);
        // add tab_artists
        mPagerAdapter.addFragment(new LocalArtistFragment_());
        // add tab_albums
        mPagerAdapter.addFragment(new LocalAlbumFragment_());
        //add tab_online
//        mPagerAdapter.addFragment(new OnlineFragment_());
        // add tab_playlists
//        mPagerAdapter.addFragment(new PlaylistsFragment());
        // add tab_genres
//        mPagerAdapter.addFragment(new GenresFragment());

        viewPager.setAdapter(mPagerAdapter);

        tabs.setupWithViewPager(viewPager);

        tabs.setTabsFromPagerAdapter(mPagerAdapter);
    }


    public void setSleepBarVisiable(boolean flag) {
        int visiblity = flag ? View.VISIBLE : View.GONE;
        if (ll_sleepbar.getVisibility() != visiblity) {
            ll_sleepbar.setVisibility(visiblity);
        }
    }

    public boolean isSleepCountDown() {
        return sleeptime > 0;
    }
}
