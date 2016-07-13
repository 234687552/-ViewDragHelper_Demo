package com.example.administrator.apidemotest;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DragLayout extends LinearLayout {
    OnExpandListener listener;
    private ViewDragHelper mDragger;
    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            //true表示子view都可以允许拖拽，也可以通过child判断指定那些可以拖拽，那些不可以。
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == getChildAt(0);
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
                final int minLeft = -getPaddingLeft() - getChildAt(1).getWidth();
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
                if (changedView == getChildAt(0)) {
                    getChildAt(1).offsetLeftAndRight(dx);
                }
                invalidate();//不写这句的话，就会出现第二次拉动的时候右边的变空白
                super.onViewPositionChanged(changedView, left, top, dx, dy);
            }


            //就是Motion.ACTION_UP的操作
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                int detX=releasedChild.getLeft()<=-getChildAt(1).getWidth()/2?-getChildAt(1).getWidth():0;
                mDragger.settleCapturedViewAt(detX, releasedChild.getTop());//把捕捉到的View位置设置到x,y位置上,

                listener.ExpandListener(detX+"");
                invalidate();//这方法启动才能调用computeScroll()方法
            }
        });
    }
    //最重要的是，在onTouchEvent和onInterceptTouchEvent处理touch事件的时候，让VDH也接收到事件。
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.w("onTouchEvent", String.valueOf(event.getAction()));
        mDragger.processTouchEvent(event);//在touch事件发生时候 VDH也处理事件，然后return true让事件继续传递下去
        return true;
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

    //保证第二个view是第一个view的1/3
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getChildAt(1).getLayoutParams().width=getChildAt(0).getWidth()/3;
        getChildAt(1).setLayoutParams(getChildAt(1).getLayoutParams());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //展开就关闭右边视图
    public void CloseExpand(){
        if (getChildAt(0).getLeft()<-10){
            mDragger.smoothSlideViewTo(getChildAt(0),0, 0);
            invalidate();
        }
    }
    public interface OnExpandListener{
        public void ExpandListener(String isExpand);
    }
}
