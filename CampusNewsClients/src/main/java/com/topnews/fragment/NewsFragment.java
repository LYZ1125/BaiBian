package com.topnews.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.topnews.CityListActivity;
import com.topnews.DetailsActivity;
import com.topnews.MainActivity;
import com.topnews.R;
import com.topnews.adapter.NewsAdapter;
import com.topnews.bean.NewsEntity;
import com.topnews.tool.Constants;
import com.topnews.view.HeadListView;

import java.util.ArrayList;

public class NewsFragment extends Fragment {
    private final static String TAG = "NewsFragment";
    Activity activity;
    ArrayList<NewsEntity> newsList = new ArrayList<NewsEntity>();
    HeadListView mHeadListView;
    NewsAdapter mAdapter;
    String text;
    int channel_id;
    ImageView detail_loading;
    public final static int SET_NEWSLIST = 0;
    private static int NEWSITEM_ID = 0;
    //Toast��ʾ��
    private RelativeLayout notify_view;
    private TextView notify_view_text;
    private TextView footTitle;
    private int currentPosition;
    private TextView item_title;
    //    //��ʼ��itemʱ����id��ʼ��
//    private ArrayList<Integer> isReaded =new ArrayList<Integer>();
//    private ImageView popicon;
//

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle args = getArguments();
        text = args != null ? args.getString("text") : "";
        channel_id = args != null ? args.getInt("id", 0) : 0;
        initData();
//        getFragmentManager().beginTransaction()
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        this.activity = activity;
        super.onAttach(activity);
    }

    /**
     * �˷�����˼Ϊfragment�Ƿ�ɼ� ,�ɼ�ʱ���������
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            //fragment�ɼ�ʱ��������
            if (newsList != null && newsList.size() != 0) {
                handler.obtainMessage(SET_NEWSLIST).sendToTarget();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        handler.obtainMessage(SET_NEWSLIST).sendToTarget();
                    }
                }).start();
            }
        } else {
            //fragment���ɼ�ʱ��ִ�в���
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
//
//    public HeadListView getListView(){
////        if (mHeadListView == null) {
////            Log.d("main!!!", String.valueOf(mHeadListView));
////        }
//        return mHeadListView;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment, null);
//        Log.d("position2", String.valueOf(view));
        mHeadListView = (HeadListView) view.findViewById(R.id.mListView);
//        Log.d("position3", String.valueOf(mHeadListView));
        TextView item_textView = (TextView) view.findViewById(R.id.item_textview);
        detail_loading = (ImageView) view.findViewById(R.id.detail_loading);
        //Toast��ʾ��
        notify_view = (RelativeLayout) view.findViewById(R.id.notify_view);
        notify_view_text = (TextView) view.findViewById(R.id.notify_view_text);
        item_title = (TextView) view.findViewById(R.id.item_title);
        //pop
//        popicon = (ImageView) view.findViewById(R.id.popicon);
        item_textView.setText(text);
        return view;
    }

    public HeadListView getmHeadListView() {
        return mHeadListView;
    }

    private void initData() {
        newsList = Constants.getNewsList(activity, NEWSITEM_ID);
    }

    private int loadTime = 0;
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case SET_NEWSLIST:
                    detail_loading.setVisibility(View.GONE);
                    if (mAdapter == null) {
                        mAdapter = new NewsAdapter(activity, newsList);
                        //�ж��ǲ��ǳ��е�Ƶ��
                        if (channel_id == Constants.CHANNEL_CITY) {
                            //�ǳ���Ƶ��
                            mAdapter.setCityChannel(true);
                            initCityChannel();
                        }
                    }
                    mHeadListView.setAdapter(mAdapter);
//				mHeadListView.setOnScrollListener(mAdapter);
//                  ���û���channel��Ƶ������
                    mHeadListView.setPinnedHeaderView(LayoutInflater.from(activity).inflate(R.layout.list_item_section, mHeadListView, false));


                    mHeadListView.setOnRefreshListener(new HeadListView.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
//						getDataFromServer();
                            ((MainActivity) getActivity()).rotateTopRefresh();
                            refreshData();
                        }

                        @Override
                        public void onLoadMore() {
                            if (loadTime++ <= 1) {
//                                new Thread(new Runnable() {
//                                    public void run() {
//                                        try {
//                                            Thread.sleep(5000);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                        handler.sendMessage(handler.obtainMessage(SET_NEWSLIST));
//                                    }
//                                });
                                NEWSITEM_ID += 10;
                                newsList.addAll(Constants.getNewsList(activity, NEWSITEM_ID));
                            } else {
                                Toast.makeText(activity, "û��������~", Toast.LENGTH_SHORT).show();

                            }
//                            mAdapter.notifyDataSetChanged();
                            mHeadListView.onRefreshComplete();
                        }

                    });

                    mHeadListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
//                            Log.d("position1111", String.valueOf(position));
                            currentPosition = position - 1;
//                            Log.d("position", String.valueOf(position));
//                            Log.d("position", String.valueOf(newsList.size()));
//                            �����һ�������һ��
                            if (currentPosition >= 0 && currentPosition != newsList.size()) {

//                                mHeadListView.findViewWithTag()
//                                mHeadListView.getChildAt(currentPosition).findViewById(R.id.popicon).setOnClickListener(new popAction(position));
//                                popicon.setOnClickListener(new popAction(position));
//                                isReaded.add(mAdapter.getItem(currentPosition).getId());

//                                mAdapter.getItem(currentPosition).setReadStatus(true);
//                                ��Ӧ����getSharedPreferences��¼�������񣬵�����Ϊ����������û��Ψһ��item��ʶ�����޷���֤���׼ȷ����ʱ�����ǡ�
//                                Log.d("itemId", String.valueOf(mAdapter.getItem(currentPosition).getId()));
                                activity.getSharedPreferences("publishTime", Context.MODE_PRIVATE).edit().putBoolean(String.valueOf(mAdapter.getItem(currentPosition).getId()), true).commit();
//                                item_title.setTextColor(Color.GRAY);
                                mAdapter.notifyDataSetChanged();
                                Intent intent = new Intent(activity, DetailsActivity.class);
                                if (channel_id == Constants.CHANNEL_CITY) {
                                    if (currentPosition != 0) {
                                        intent.putExtra("itemId", mAdapter.getItem(currentPosition - 1).getId());
                                        intent.putExtra("news", mAdapter.getItem(currentPosition - 1));
//                                        intent.putExtra("collectStatus", mAdapter.getItem(currentPosition - 1).getCollectStatus());
//                                        startActivity(intent);
                                        startActivityForResult(intent, 0);
                                        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    }
                                } else {
                                    intent.putExtra("itemId", mAdapter.getItem(currentPosition).getId());
                                    intent.putExtra("news", mAdapter.getItem(currentPosition));
//                                    startActivity(intent);
                                    startActivityForResult(intent, 0);
                                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            }
                        }
                    });
//                    ������Ҫ�ģ�Ӧ������ˢ�µ�ʱ��ŵ���initNotify������
//                    if (channel_id == 1) {
//                    initNotify();
//                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
//
//    /**
//     * �����ĸ���ѡ���
//     */
//    private PopupWindow popupWindow;
//    private View popDislike;
//    private View popFavor;
//    private TextView popTextFavor;
//    private List<Integer> isCollect;
//
//    /**
//     * popWindow �رհ�ť
//     */
//    private ImageView btn_pop_close;
//
//    /**
//     * ��ʼ��������pop
//     * @param position
//     */
//    private void initPopWindow(int position) {
//        Log.d("log", "here");
//        //����������
//        View popView = LayoutInflater.from(activity).inflate(R.layout.list_item_pop, null);
//        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
//        //����popwindow���ֺ���ʧ����
//        popupWindow.setAnimationStyle(R.style.PopMenuAnimation);
//        btn_pop_close = (ImageView) popView.findViewById(R.id.btn_pop_close);
//        popDislike = popView.findViewById(R.id.ll_pop_dislike);
//        popFavor = popView.findViewById(R.id.ll_pop_favor);
//        popTextFavor = (TextView) popView.findViewById(R.id.tv_pop_favor);
//        if (mAdapter.getItem(position).getCollectStatus()) {
//            Drawable drawable = popFavor.getResources().getDrawable(R.drawable.listpage_more_like_seleted_normal);
//            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//            popTextFavor.setCompoundDrawables(drawable, null, null, null);
//            popTextFavor.setText("���ղ�");
//        }
//    }
//
//    /**
//     * ��ʾpopWindow
//     */
//    public void showPop(View parent, int x, int y, final int position) {
//        initPopWindow(position);
//        //����popwindow��ʾλ��
//        popupWindow.showAtLocation(parent, 0, x, y);
//        //��ȡpopwindow����
//        popupWindow.setFocusable(true);
//        //����popwindow�������������򣬱�رա�
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.update();
//        if (popupWindow.isShowing()) {
//
//        }
//        btn_pop_close.setOnClickListener(new OnClickListener() {
//            public void onClick(View paramView) {
//                popupWindow.dismiss();
//            }
//        });
//
//        popFavor.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Log.d("newsId3", String.valueOf(getItem(position)));
//                mAdapter.getItem(position).setCollectStatus(true);
//                isCollect.add(mAdapter.getItem(position).getId());
//                Drawable drawable = popFavor.getResources().getDrawable(R.drawable.listpage_more_like_seleted_normal);
//                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                popTextFavor.setCompoundDrawables(drawable, null, null, null);
//                popTextFavor.setText("���ղ�");
//            }
//        });
//
//        popDislike.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//    }
//
//    /**
//     * ÿ��ITEM��more��ť��Ӧ�ĵ������
//     */
//    public class popAction implements OnClickListener {
//        int position;
//
//        public popAction(int position) {
//            this.position = position;
//        }
//
//        @Override
//        public void onClick(View v) {
//            int[] arrayOfInt = new int[2];
//            //��ȡ�����ť������
//            v.getLocationOnScreen(arrayOfInt);
//            int x = arrayOfInt[0];
//            int y = arrayOfInt[1];
//            showPop(v, x, y, position);
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("collect", String.valueOf(resultCode));
//        if (resultCode == 0) {
//            mAdapter.getItem(currentPosition).setCollectStatus(false);
//        } else {
//            mAdapter.getItem(currentPosition).setCollectStatus(true);
//        }
//    }

    public void refreshData() {
        NEWSITEM_ID += 10;
        ArrayList<NewsEntity> moreNewsList = Constants.getNewsList(activity, NEWSITEM_ID);
        this.newsList.addAll(0, moreNewsList);
        initNotify(moreNewsList.size());
        mHeadListView.onRefreshComplete();
    }

    /* ��ʼ��ѡ����е�header*/
    public void initCityChannel() {
        View headView = LayoutInflater.from(activity).inflate(R.layout.city_category_list_tip, null);
        TextView chose_city_tip = (TextView) headView.findViewById(R.id.chose_city_tip);
        chose_city_tip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(activity, CityListActivity.class);
                startActivity(intent);
            }
        });
        mHeadListView.addHeaderView(headView);
    }

    /* ��ʼ��֪ͨ��Ŀ������ˢ��֪ͨ*/
    private void initNotify(final int count) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                notify_view_text.setText(String.format(getString(R.string.ss_pattern_update), count));
                notify_view.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        notify_view.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        }, 1000);
    }
//
//    private void initNotifyNoMoreNews() {
//        Log.d("initNotifyNoMoreNews", "here");
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                footTitle.setText("û�и�������");
//            }
//        }, 1000);
//    }


    /* �ݻ���ͼ */
    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        Log.d("onDestroyView", "channel_id = " + channel_id);
        mAdapter = null;
    }

    /* �ݻٸ�Fragment��һ����FragmentActivity ���ݻٵ�ʱ������Ŵݻ� */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.d(TAG, "channel_id = " + channel_id);
    }
}
