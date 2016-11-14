package com.topnews.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.topnews.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ��д��ListView,��ÿ�����ŵ�ʱ����ʾ
 */
public class HeadListView extends ListView implements AbsListView.OnScrollListener {


    private ImageView top_refresh;

    public interface HeaderAdapter {
        public static final int HEADER_GONE = 0;
        public static final int HEADER_VISIBLE = 1;
        public static final int HEADER_PUSHED_UP = 2;

        int getHeaderState(int position);

        void configureHeader(View header, int position, int alpha);
    }

    private static final int MAX_ALPHA = 255;


    private static final int STATE_PULL_TO_REFRESH = 1;
    private static final int STATE_RELEASE_TO_REFRESH = 2;
    private static final int STATE_REFRESHING = 3;
    private int startY = -1;
    public int mCurrentState = STATE_PULL_TO_REFRESH;
    /* �ǲ��ǳ���Ƶ����  true����   false :����*/
    public boolean isCityChannel = false;

    /* �ǲ��ǵ�һ��ITEM��  true����   false :����*/
    public boolean isfirst = true;

    private HeaderAdapter mAdapter;
    private View mHeaderView;
    private View mHeaderView1;
    private boolean mHeaderViewVisible;
    private int mHeaderViewWidth;
    private int mHeaderViewHeight;
    private TextView tvTitle;
    private TextView tvDate;
    private ImageView ivArrow;
    private TextView footTitle;
    private RotateAnimation animUp;
    private RotateAnimation animDown;
    private ProgressBar pbProgress;
    private View mFooterView;
    private int measuredHeight;
    private int mHeaderViewMeasuredHeight;


    public HeadListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();
    }

    public HeadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
    }

    public HeadListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHeaderView();
        initFooterView();
    }


    public void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh_header, null);
        this.addHeaderView(mHeaderView);
//        View view = inflate(getContext(), R.layout.main_head, null);
//        top_refresh = (ImageView) view.findViewById(R.id.top_refresh);
        tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);
        tvDate = (TextView) mHeaderView.findViewById(R.id.tv_date);
        ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
        pbProgress = (ProgressBar) mHeaderView.findViewById(R.id.progressBar);
        mHeaderView.measure(0, 0);
        mHeaderViewMeasuredHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mHeaderViewMeasuredHeight, 0, 0);
        initAnim();
        setCurrentTime();
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    private void initFooterView() {
        isLoadMore = false;
        mFooterView = View.inflate(getContext(), R.layout.pull_to_refresh_footer, null);
        this.addFooterView(mFooterView);
        footTitle = (TextView) mFooterView.findViewById(R.id.tv_title);
        mFooterView.measure(0, 0);
        measuredHeight = mFooterView.getMeasuredHeight();
        mFooterView.setPadding(0, -measuredHeight, 0, 0);
        this.setOnScrollListener(this);
    }

    public void setCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
        String time = simpleDateFormat.format(new Date());
        tvDate.setText(time);
    }

    private void initAnim() {
        animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);
        animDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        getParent().requestDisallowInterceptTouchEvent(true);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
//                Log.d("Y1", String.valueOf(startY));
                break;
            case MotionEvent.ACTION_MOVE:
//				if (startY == -1) {
//					startY = (int) ev.getY();
////                    Log.d("Y", String.valueOf(startY));
//				}

                if (mCurrentState == STATE_REFRESHING) {
                    break;
                }
                int endY = (int) ev.getY();
                int dy = endY - startY;

                int firstVisiblePosition = getFirstVisiblePosition();

                if (dy > 0 && firstVisiblePosition == 0) {
                    int padding = dy - mHeaderViewMeasuredHeight;
                    mHeaderView.setPadding(0, padding, 0, 0);

                    if (padding > 0 && mCurrentState != STATE_RELEASE_TO_REFRESH) {
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                        refreshState();
                    } else if (padding < 0 && mCurrentState != STATE_PULL_TO_REFRESH) {
                        mCurrentState = STATE_PULL_TO_REFRESH;
                        refreshState();
                    }
//                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                startY = -1;

                if (mCurrentState == STATE_RELEASE_TO_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    refreshState();

                    mHeaderView.setPadding(0, 0, 0, 0);
                    if (mListener != null) {
                    //                �ûص�����˵��õ�����
                    mListener.onRefresh();
                    }
//					�ɿ�ˢ��ʱ����ֹ�����¼�����
//                    return true;
                } else if (mCurrentState == STATE_PULL_TO_REFRESH) {
                    mHeaderView.setPadding(0, -mHeaderViewMeasuredHeight, 0, 0);
//                    return true;
                }
                break;
            default:
                break;
        }

        return super.onTouchEvent(ev);
    }

    private void refreshState() {
//        Log.d("times", "11");
        switch (mCurrentState) {
            case STATE_PULL_TO_REFRESH:
                tvTitle.setText("����ˢ��");
                ivArrow.startAnimation(animDown);
                pbProgress.setVisibility(INVISIBLE);
                ivArrow.setVisibility(VISIBLE);
                break;
            case STATE_RELEASE_TO_REFRESH:
                tvTitle.setText("�ɿ�ˢ��");
                ivArrow.startAnimation(animUp);
                pbProgress.setVisibility(INVISIBLE);
                ivArrow.setVisibility(VISIBLE);
                break;
            case STATE_REFRESHING:
                tvTitle.setText("����ˢ��...");
                ivArrow.clearAnimation();
                pbProgress.setVisibility(VISIBLE);
                ivArrow.setVisibility(INVISIBLE);

//                �ûص�����˵��õ����⡪�����˹����Ƶ��� case MotionEvent.ACTION_UP:��
//                mListener.onRefresh();
                break;
            default:
                break;
        }
    }

    //�������û�е���notifyDataSetChanged()��ȴ������ui��ˢ�������ݣ�����why����
//    ɵ��
//    �������е���ʱ����������ʱչʾЧ���ã�ʵ����Ŀ��Ҫɾ����
    public void onRefreshComplete() {
        if (!isLoadMore) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    mHeaderView.setPadding(0, -mHeaderViewMeasuredHeight, 0, 0);
                    mCurrentState = STATE_PULL_TO_REFRESH;
                    tvTitle.setText("����ˢ��");
                    pbProgress.setVisibility(INVISIBLE);
                    ivArrow.setVisibility(VISIBLE);
                    Toast.makeText(getContext(), "ˢ�³ɹ�", Toast.LENGTH_SHORT).show();
                }
            }, 2000);
        } else {
            isLoadMore = false;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    mFooterView.setPadding(0, -measuredHeight, 0, 0);
                }
            }, 2000);
//            footTitle.setText("û�и�������");
        }
    }

    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHeaderView1 != null) {
            mHeaderView1.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView1 != null) {
            measureChild(mHeaderView1, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView1.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView1.getMeasuredHeight();
        }
    }

    public void setPinnedHeaderView(View view) {
        mHeaderView1 = view;
        if (mHeaderView1 != null) {
            //listview���ϱߺ��±��к�ɫ����Ӱ��xml�У� android:fadingEdge="none"
            setFadingEdgeLength(0);
        }
        requestLayout();
    }

    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (HeaderAdapter) adapter;
    }

    public void configureHeaderView(int position) {
        if (mHeaderView1 == null) {
            return;
        }
//		Log.d("position", String.valueOf(position));
//  ���������������������������������������������������������������������þ���Ȼ�����ˢ��bug
        int state = mAdapter.getHeaderState(position);
        switch (state) {
            case HeaderAdapter.HEADER_GONE: {
                mHeaderViewVisible = false;
                break;
            }

            case HeaderAdapter.HEADER_VISIBLE: {
                mAdapter.configureHeader(mHeaderView1, position, MAX_ALPHA);
                if (mHeaderView1.getTop() != 0) {
                    mHeaderView1.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                mHeaderViewVisible = true;
                break;
            }

            case HeaderAdapter.HEADER_PUSHED_UP: {
                View firstView = getChildAt(0);
                int bottom = firstView.getBottom();
                int headerHeight = mHeaderView1.getHeight();
                int y;
                int alpha;
                if (bottom < headerHeight) {
                    y = (bottom - headerHeight);
                    alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
                } else {
                    y = 0;
                    alpha = MAX_ALPHA;
                }
                mAdapter.configureHeader(mHeaderView1, position, alpha);
                if (mHeaderView1.getTop() != y) {
                    mHeaderView1.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                }
                mHeaderViewVisible = true;
                break;
            }
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible) {
            drawChild(canvas, mHeaderView1, getDrawingTime());
        }
    }

    public interface OnRefreshListener {
        public void onRefresh();

        public void onLoadMore();

//        void rotateTopRefresh();
    }

    private OnRefreshListener mListener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    private boolean isLoadMore;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        Log.d("main", "����������������");
        if (scrollState == SCROLL_STATE_IDLE) {
            int lastVisiblePosition = getLastVisiblePosition();
            if (lastVisiblePosition == this.getCount() - 1 && !isLoadMore) {
//                Log.d("main", "����������������");
                isLoadMore = true;
                mFooterView.setPadding(0, 0, 0, 0);
                setSelection(this.getCount() - 1);

//                for (int i =0;i<1000000000;i++) {
//                }
                if (mListener != null) {
                    mListener.onLoadMore();
                }
            }
        }
    }

    //��������
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        if (view instanceof HeadListView) {
//            Log.d("first", "first:" + view.getFirstVisiblePosition());
            if (isCityChannel) {
                if (view.getFirstVisiblePosition() == 0) {
                    isfirst = true;
                } else {
                    isfirst = false;
                }
                ((HeadListView) view).configureHeaderView(firstVisibleItem - 1);
            } else {
                ((HeadListView) view).configureHeaderView(firstVisibleItem);
            }
        }
    }

}
