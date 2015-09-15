package ViewGroup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoyy on 2015/9/15 0015.
 */
public class FlowLayout extends ViewGroup {
    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int ModeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int ModeHeigh = MeasureSpec.getMode(heightMeasureSpec);
        int SizeWitdth = MeasureSpec.getSize(widthMeasureSpec);
        int SizeHeigh = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;


        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View clid = getChildAt(i);
            measureChild(clid, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) clid.getLayoutParams();
            int clidWidth = lp.leftMargin + lp.rightMargin + clid.getMeasuredWidth();
            int clidHeight = lp.topMargin + lp.bottomMargin + clid.getMeasuredHeight();
            if (lineWidth + clidWidth > SizeWitdth) {
                width = Math.max(lineWidth, clidWidth);
                lineWidth = clidWidth;
                height += lineHeight;
                lineHeight = clidHeight;
            } else {
                lineWidth += clidWidth;
                lineHeight = Math.max(lineHeight, clidHeight);
            }

            if (i == cCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        setMeasuredDimension((ModeWidth == MeasureSpec.EXACTLY) ? SizeWitdth : width,
                (ModeHeigh == MeasureSpec.EXACTLY) ? SizeHeigh : height);

    }

    /**
     * 存储所有的View，按行记录
     */
    private List<List<View>> mAllViews = new ArrayList<List<View>>();
    /**
     * 记录每一行的最大高度
     */
    private List<Integer> mLineHeight = new ArrayList<Integer>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        List<View> lineViews = new ArrayList<>();
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View clid = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) clid.getLayoutParams();
            int clidWidth = clid.getMeasuredWidth();
            int clidHeight = clid.getMeasuredHeight();


            if (clidWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);
                lineWidth = 0;
                lineHeight = 0;
                lineViews = new ArrayList<>();
            }
            lineWidth += clidWidth + lp.rightMargin + lp.leftMargin;
            lineHeight = Math.max(lineHeight, clidHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(clid);
        }

        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        int top = 0;
        int left = 0;
        for (int i = 0; i < mAllViews.size(); i++) {

            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            for (int j = 0; j < lineViews.size(); j++) {
                View clid = lineViews.get(j);
                if (clid.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) clid.getLayoutParams();

                int lc = left + lp.leftMargin;
                int rc = lc + clid.getMeasuredWidth();
                int tc = top + lp.topMargin;
                int bc = tc + clid.getMeasuredHeight();
                Log.e("TAG", "child" + ": l = " + lc + " , t = " + t + " , r ="
                        + rc + " , b = " + bc);
                clid.layout(lc, tc, rc, bc);
                left += clid.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
            }
            top += lineHeight;
            left = 0;
        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
