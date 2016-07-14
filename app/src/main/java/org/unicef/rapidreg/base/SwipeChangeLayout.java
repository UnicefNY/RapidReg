package org.unicef.rapidreg.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class SwipeChangeLayout extends ViewGroup {
    public static final String TAG = SwipeChangeLayout.class.getSimpleName();

    public enum DragEdge {
        TOP, BOTTOM
    }

    private DragEdge dragEdge = DragEdge.TOP;
    private static final double AUTO_FINISHED_SPEED_LIMIT = 2000.0;
    private final ViewDragHelper viewDragHelper;
    private View target;
    private View scrollChild;
    private int verticalDragRange = 0;
    private int draggingState = 0;
    private int draggingOffset;
    private ViewGroup shouldGoneContainer;
    private ViewGroup shouldShowContainer;
    private boolean enablePullToBack = true;
    private static final float BACK_FACTOR = 0.2f;
    private float finishAnchor = 0;
    private boolean enableFlingBack = true;
    private SwipeBackListener swipeBackListener;

    public void setFinishAnchor(float offset) {
        finishAnchor = offset;
    }

    public void setDragEdge(DragEdge dragEdge) {
        this.dragEdge = dragEdge;
    }

    public void setEnableFlingBack(boolean b) {
        enableFlingBack = b;
    }

    public void setOnSwipeBackListener(SwipeBackListener listener) {
        swipeBackListener = listener;
    }

    public SwipeChangeLayout(Context context) {
        this(context, null);
    }

    public SwipeChangeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());
    }

    public void setScrollChild(View view) {
        scrollChild = view;
    }

    public void setEnablePullToBack(boolean b) {
        enablePullToBack = b;
    }

    public void setShouldShowContainer(ViewGroup shouldShowContainer) {
        this.shouldShowContainer = shouldShowContainer;
    }

    public void setShouldGoneContainer(ViewGroup shouldGoneContainer) {
        this.shouldGoneContainer = shouldGoneContainer;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (getChildCount() == 0) return;

        View child = getChildAt(0);

        int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childHeight = height - getPaddingTop() - getPaddingBottom();
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childRight = childLeft + childWidth;
        int childBottom = childTop + childHeight;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 1) {
            throw new IllegalStateException("SwipeBackLayout must contains only one direct child.");
        }

        if (getChildCount() > 0) {
            int measureWidth = MeasureSpec.makeMeasureSpec(getMeasuredWidth()
                    - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
            int measureHeight = MeasureSpec.makeMeasureSpec(getMeasuredHeight()
                    - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
            getChildAt(0).measure(measureWidth, measureHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        verticalDragRange = h;

        switch (dragEdge) {
            case TOP:
            case BOTTOM:
                finishAnchor = finishAnchor > 0 ? finishAnchor : verticalDragRange * BACK_FACTOR;
                break;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handled = false;
        ensureTarget();
        if (isEnabled()) {
            handled = viewDragHelper.shouldInterceptTouchEvent(ev);
        } else {
            viewDragHelper.cancel();
        }
        return !handled ? super.onInterceptTouchEvent(ev) : handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(scrollChild, -1);
    }

    public boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(scrollChild, 1);
    }

    private int getDragRange() {
        switch (dragEdge) {
            case TOP:
            case BOTTOM:
                return verticalDragRange;
            default:
                return verticalDragRange;
        }
    }

    private void ensureTarget() {
        if (target == null) {
            if (getChildCount() > 1) {
                throw new IllegalStateException("SwipeBackLayout must contains only one direct child");
            }
            target = getChildAt(0);

            if (scrollChild == null && target != null) {
                if (target instanceof ViewGroup) {
                    findScrollView((ViewGroup) target);
                } else {
                    scrollChild = target;
                }

            }
        }
    }

    private void findScrollView(ViewGroup viewGroup) {
        scrollChild = viewGroup;
        if (viewGroup.getChildCount() > 0) {
            int count = viewGroup.getChildCount();
            View child;
            for (int i = 0; i < count; i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof ViewGroup) {
                    if (((ViewGroup) child).getChildCount() > 0) {
                        int subCount = ((ViewGroup) child).getChildCount();
                        View subChild;
                        for (int j = 0; j < subCount; j++) {
                            subChild = ((ViewGroup) child).getChildAt(j);
                            if (subChild instanceof RecyclerView) {
                                scrollChild = subChild;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void finish() {
        Activity act = (Activity) getContext();
        act.finish();
        act.overridePendingTransition(0, android.R.anim.fade_out);
    }

    private void setShow(ViewGroup viewGroup) {
        viewGroup.setVisibility(VISIBLE);
        scrollChild.setVisibility(VISIBLE);
    }

    private void setGone(ViewGroup viewGroup) {
        viewGroup.setVisibility(GONE);
        scrollChild.setVisibility(GONE);
    }

    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == target && enablePullToBack;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return verticalDragRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {

            int result = 0;

            if (dragEdge == DragEdge.TOP && !canChildScrollUp() && top > 0) {
                final int topBound = getPaddingTop();
                final int bottomBound = verticalDragRange;
                result = Math.min(Math.max(top, topBound), bottomBound);
            } else if (dragEdge == DragEdge.BOTTOM && !canChildScrollDown() && top < 0) {
                final int topBound = -verticalDragRange;
                final int bottomBound = getPaddingTop();
                result = Math.min(Math.max(top, topBound), bottomBound);
            }

            return result;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == draggingState) return;

            if ((draggingState == ViewDragHelper.STATE_DRAGGING || draggingState == ViewDragHelper.STATE_SETTLING) &&
                    state == ViewDragHelper.STATE_IDLE) {
                // the view stopped from moving.
                if (draggingOffset == getDragRange()) {
                    setGone(shouldGoneContainer);
                    setShow(shouldShowContainer);
                }
            }

            draggingState = state;
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            switch (dragEdge) {
                case TOP:
                case BOTTOM:
                    draggingOffset = Math.abs(top);
                    break;
                default:
                    break;
            }

            //The proportion of the sliding.
            float fractionAnchor = (float) draggingOffset / finishAnchor;
            if (fractionAnchor >= 1) fractionAnchor = 1;

            float fractionScreen = (float) draggingOffset / (float) getDragRange();
            if (fractionScreen >= 1) fractionScreen = 1;

            if (swipeBackListener != null) {
                swipeBackListener.onViewPositionChanged(fractionAnchor, fractionScreen);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (draggingOffset == 0) return;

            if (draggingOffset == getDragRange()) return;

            boolean isBack = false;

            if (enableFlingBack && backBySpeed(xvel, yvel)) {
                isBack = !canChildScrollUp();
            } else if (draggingOffset >= finishAnchor) {
                isBack = true;
            } else if (draggingOffset < finishAnchor) {
                isBack = false;
            }

            int finalTop;
            switch (dragEdge) {
                case TOP:
                    finalTop = isBack ? verticalDragRange : 0;
                    smoothScrollToY(finalTop);
                    break;
                case BOTTOM:
                    finalTop = isBack ? -verticalDragRange : 0;
                    smoothScrollToY(finalTop);
                    break;
            }

        }
    }

    private boolean backBySpeed(float xvel, float yvel) {
        switch (dragEdge) {
            case TOP:
            case BOTTOM:
                if (Math.abs(yvel) > Math.abs(xvel) && Math.abs(yvel) > AUTO_FINISHED_SPEED_LIMIT) {
                    return dragEdge == DragEdge.TOP ? !canChildScrollUp() : !canChildScrollDown();
                }
                break;
        }
        return false;
    }

    private void smoothScrollToY(int finalTop) {
        if (viewDragHelper.settleCapturedViewAt(0, finalTop)) {
            ViewCompat.postInvalidateOnAnimation(SwipeChangeLayout.this);
        }
    }

    public interface SwipeBackListener {

        /**
         * Return scrolled fraction of the layout.
         *
         * @param fractionAnchor relative to the anchor.
         * @param fractionScreen relative to the screen.
         */
        void onViewPositionChanged(float fractionAnchor, float fractionScreen);

    }
}
