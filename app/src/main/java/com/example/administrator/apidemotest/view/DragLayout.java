package com.example.administrator.apidemotest.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class DragLayout extends LinearLayout {
    private ViewDragHelper mDragger;
    public boolean isExpand = false;
    private View contentView,actionView;

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            //true表示子view都可以允许拖拽，也可以通过child判断指定那些可以拖拽，那些不可以。
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == contentView;
            }

            /**
             * @param child
             * @param left  是移动View child的现任左坐标
             * @param dx
             * @return 返回值是移动View child重新绘制的左坐标
             */
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                getParent().requestDisallowInterceptTouchEvent(true);
                //控制View的左坐标在-
                final int minLeft = -getPaddingLeft() - actionView.getWidth();
                final int newLeft = Math.min(Math.max(minLeft, left), 0);
                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return child.getTop();
            }

            /**
             * 当捕捉到的View发生位置变化的时候的处理
             * left就是相对原始位置的偏移量
             */
            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                if (changedView == contentView) {
                    actionView.offsetLeftAndRight(dx);
                }
                invalidate();//不写这句的话，就会出现第二次拉动的时候右边的变空白
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }


            //就是Motion.ACTION_UP的操作
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int detX = releasedChild.getLeft() <= -actionView.getWidth() / 2 ? -actionView.getWidth() : 0;
                mDragger.settleCapturedViewAt(detX, releasedChild.getTop());//把捕捉到的View位置设置到x,y位置上,
                if (detX < 0) {
                    isExpand = true;
                }else {
                    isExpand = false;
                }
                invalidate();//这方法启动才能调用computeScroll()方法
            }
        });
    }

    //最重要的是，在onTouchEvent和onInterceptTouchEvent处理touch事件的时候，让VDH也接收到事件。
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.w("onTouchEvent", String.valueOf(event.getAction()));
        mDragger.processTouchEvent(event);
        return false; //没有拦截onTouch，让下面的click事件能触发；
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.w("onInterceptTouchEvent", String.valueOf(ev.getAction()));
        onTouchEvent(ev);
        return false;
    }


    /**
     * 该方法是检测当前x,y是否于settleCapturedViewAt(x,y)的坐标相同，不同的话就computeScroll当前x++，y++的方式趋向于目标坐标.
     * 且mDragger.continueSettling(true)为真
     */
    @Override
    public void computeScroll() {
        if (mDragger.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        actionView.getLayoutParams().width=getMeasuredHeight()*2;
        actionView.setLayoutParams(actionView.getLayoutParams());
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onFinishInflate() {
        actionView=getChildAt(1);
        contentView=getChildAt(0);
        super.onFinishInflate();
    }


    //展开就关闭右边视图
    public void CloseExpand() {
        isExpand = false;
        if (contentView.getLeft() < -10) {
            mDragger.smoothSlideViewTo(contentView, 0, 0);
            invalidate();
        }

    }

}
