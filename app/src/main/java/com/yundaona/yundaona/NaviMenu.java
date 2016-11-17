package com.yundaona.yundaona;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;


/**
 * create by Aaron on 2016.10.26
 * 暂时只能兼容两个弹出来的View   第一个View作为用户点击按钮
 */
public class NaviMenu extends ViewGroup implements OnClickListener {

    private static final String TAG = "ArcMenu";

    /**
     * 用户点击的按钮
     */
    private View mButton;
    /**
     * 当前ArcMenu的状态
     */
    private Status mCurrentStatus = Status.CLOSE;

    /**
     * 底部栏高度
     */
    private int BottomBarHeight = 56;

    /**
     * 回调接口
     */
    private OnMenuItemClickListener onMenuItemClickListener;

    /**
     * 状态的枚举类
     */
    public enum Status {
        OPEN, CLOSE
    }


    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    public NaviMenu(Context context) {
        this(context, null);
    }

    public NaviMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NaviMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //DP转PX
    private void init() {
        BottomBarHeight = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BottomBarHeight, getResources().getDisplayMetrics()));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.UNSPECIFIED,
                    MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            /**
             * 初始化用户点击按钮位置
             */
            layoutButton();

            int count = getChildCount();
            /**
             * 根据需求 设置两个View 的位置
             */
            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1);
                child.setVisibility(View.GONE);

                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();

                int C1w = getMeasuredWidth() / 2 - (cWidth / 2) - (cWidth / 2 + cWidth / 4);
                int C1h = getMeasuredHeight() - cHeight - cHeight;

                int C2w = getMeasuredWidth() / 2 - (cWidth / 2) + (cWidth / 2 + cWidth / 4);
                int C2h = getMeasuredHeight() - cHeight - cHeight;

                if (i == 0) {
                    child.layout(C1w, C1h, C1w + cWidth, C1h + cHeight);
                } else if (i == 1) {
                    child.layout(C2w, C2h, C2w + cWidth, C2h + cHeight);
                }
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) { //如果是打开的  就获取焦点
        if (mCurrentStatus == Status.CLOSE) {
            return false;
        } else {
            mCurrentStatus = Status.OPEN;
            ChangeLayout();
            return true;
        }
    }

    /**
     * 第一个子View为按钮，为按钮布局且初始化点击事件
     */
    private void layoutButton() {
        View cButton = getChildAt(0);
        cButton.setOnClickListener(this);

        int width = cButton.getMeasuredWidth();
        int height = cButton.getMeasuredHeight();
        int marinBottom = (BottomBarHeight - height) / 2; //居中处理

        int l = getMeasuredWidth() / 2 - (width / 2);
        int t = getMeasuredHeight() - height - marinBottom;
        cButton.layout(l, t, l + width, t + height);
    }

    /**
     * 为按钮添加点击事件
     */
    @Override
    public void onClick(View v) {
        ChangeLayout();
    }

    private void ChangeLayout() {
        mButton = findViewById(R.id.id_button);
        if (mButton == null) {
            mButton = getChildAt(0);
        }
        rotateView(mButton, 0f, 360f, 300);
        toggleMenu(400);
    }


    /**
     * 按钮的旋转动画
     *
     * @param view
     * @param fromDegrees
     * @param toDegrees
     * @param durationMillis
     */
    public void rotateView(View view, float fromDegrees,
                           float toDegrees, int durationMillis) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(durationMillis);
        view.startAnimation(rotate);
        if (mCurrentStatus == Status.CLOSE) {
            view.setBackgroundResource(R.drawable.main_menu_close);
            setBackgroundColor(Color.parseColor("#80000000")); //打开的时候 空间阴影
        } else {
            view.setBackgroundResource(R.drawable.main_menu_open);
            setBackgroundColor(Color.parseColor("#00000000"));
        }
        mButton.requestLayout();
    }

    /**
     * 设置动画效果
     *
     * @param durationMillis
     */
    public void toggleMenu(int durationMillis) {
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);
            int cWidth = childView.getMeasuredWidth();
            int cHeight = childView.getMeasuredHeight();


            AnimationSet animset = new AnimationSet(true);
            Animation animation = null;
            if (mCurrentStatus == Status.CLOSE) {//open
                if (i == 1) {//如果是第二个  则往反方向移动
                    animation = new TranslateAnimation(-(cWidth / 2 + cWidth / 4), 0f, cHeight, 0f); //设置他移动差值
                } else {
                    animation = new TranslateAnimation((cWidth / 2 + cWidth / 4), 0f, cHeight, 0f);
                }
                childView.setClickable(true);
                childView.setFocusable(true);
            } else {//close
                if (i == 1) {
                    animation = new TranslateAnimation(0, -(cWidth / 2 + cWidth / 4), 0, cHeight);
                } else {
                    animation = new TranslateAnimation(0, (cWidth / 2 + cWidth / 4), 0, cHeight);
                }
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            animset.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE)
                        childView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            animation.setFillAfter(true);
            animation.setStartOffset((i * 100) / (count - 1));
            RotateAnimation rotate = new RotateAnimation(0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setFillAfter(true);

            animset.setInterpolator(new OvershootInterpolator(2F));
            animset.addAnimation(rotate);
            animset.addAnimation(animation);
            animset.setDuration(durationMillis);

            Animation biggerAnimation = new AlphaAnimation(0.0f, 1.1f);
            Animation smallAnimation = new AlphaAnimation(1.1f, 0.0f);

            if (mCurrentStatus == Status.CLOSE) {
                animset.addAnimation(biggerAnimation);
            } else {
                animset.addAnimation(smallAnimation);
            }

            childView.startAnimation(animset);
            final int index = i + 1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMenuItemClickListener != null)
                        onMenuItemClickListener.onClick(childView, index - 1);
                    mCurrentStatus = Status.OPEN;
                    ChangeLayout();
                }
            });

        }
        changeStatus();
        Log.e(TAG, mCurrentStatus.name() + "");
    }

    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN
                : Status.CLOSE);
    }

    public OnMenuItemClickListener getOnMenuItemClickListener() {
        return onMenuItemClickListener;
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

}