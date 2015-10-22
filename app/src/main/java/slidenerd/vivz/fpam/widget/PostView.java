package slidenerd.vivz.fpam.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.util.DisplayUtils;
import slidenerd.vivz.fpam.util.ViewUtils;

/**
 * TODO: document your custom view class.
 */
public class PostView extends View {
    public static final int CHARACTER_LIMIT = 200;
    private static final float USERNAME_TEXTSIZE_SP = 16.0F;
    private static final float UPDATED_TIME_TEXTSIZE_SP = 12.0F;
    private static final float MESSAGE_TEXTSIZE_SP = 14.0F;
    private static final float HANDLE_TEXTSIZE_SP = 12.0F;
    private static final int DEFAULT_HEIGHT_DP = 48;
    public static int timesCalled = 0;
    private Drawable mExampleDrawable;

    private int mWidth;
    private int mHeight;
    private TextPaint mPaint;

    private float mTextWidth;
    private float mTextHeight;

    private String mUserName = null;
    private String mUpdatedTime = null;
    private String mMessage = null;
    private String mHandle = "Read More";
    private Context mContext;

    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;

    private Rect mRectUserName;
    private Rect mRectUpdatedTime;
    private Rect mRectMessage;
    private Rect mRectReadMore;

    //Container for the multiline text which is a part of the post message.
    private StaticLayout mLayoutMessage;

    private float mTextSizeUserName;
    private float mTextSizeUpdatedTime;
    private float mTextSizeMessage;

    //Size of the handle 'Read More'
    private float mTextSizeHandle;

    private RectF mBoundsHandle;

    //initialize the message in collapsed state
    private boolean mCollapsed = true;

    //dont show 'Read More' handle by default
    private boolean mShowHandle = false;

    private int previousNumberOfLines = 0;


    public PostView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PostView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mContext = context;

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PostView, defStyle, 0);

        if (a.hasValue(R.styleable.PostView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.PostView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }
        a.recycle();

        mTextSizeUserName = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, USERNAME_TEXTSIZE_SP, getResources().getDisplayMetrics());
        mTextSizeUpdatedTime = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, UPDATED_TIME_TEXTSIZE_SP, getResources().getDisplayMetrics());
        mTextSizeMessage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, MESSAGE_TEXTSIZE_SP, getResources().getDisplayMetrics());
        mTextSizeHandle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, HANDLE_TEXTSIZE_SP, getResources().getDisplayMetrics());

        mPaint = new TextPaint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);

        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        mWidth = DisplayUtils.getWidthPixels(mContext);
        //set a default height of 100dp which will be changed again inside onMeasure
        mHeight = (int) DisplayUtils.convertDpToPixel(DEFAULT_HEIGHT_DP, context);

        mRectUserName = new Rect();
        mRectUpdatedTime = new Rect();
        mRectMessage = new Rect();
        mRectReadMore = new Rect();
        mBoundsHandle = new RectF();

    }

    private int calculateHeight() {
        int height = 0;
        height += mPaddingTop;
        height += mPaddingBottom;

        if (mUserName != null && !mUserName.equals("")) {
            mPaint.setTextSize(mTextSizeUserName);
            mPaint.getTextBounds(mUserName, 0, mUserName.length(), mRectUserName);
            height += mRectUserName.height();
        }

        if (mUpdatedTime != null && !mUpdatedTime.equals("")) {
            mPaint.setTextSize(mTextSizeUpdatedTime);
            mPaint.getTextBounds(mUpdatedTime, 0, mUpdatedTime.length(), mRectUpdatedTime);
            height += mRectUpdatedTime.height();
        }

        if (mMessage != null && !mMessage.equals("")) {
            mPaint.setTextSize(mTextSizeMessage);
            mPaint.getTextBounds(mMessage, 0, mMessage.length(), mRectMessage);
            height += mLayoutMessage.getHeight();
        }

        if (mShowHandle) {
            mPaint.setTextSize(mTextSizeHandle);
            mPaint.getTextBounds(mHandle, 0, mHandle.length(), mRectReadMore);
            height += mRectReadMore.height();
        }
        return height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int totalWidth;
        int totalHeight;
        totalWidth = resolveSize(mWidth, widthMeasureSpec);
        //calculate post height based on the size of the post
        mHeight = calculateHeight();
        totalHeight = resolveSize(mHeight, heightMeasureSpec);
        //MUST CALL THIS
        L.m("onMeasure called " + (timesCalled++) + " times");
        setMeasuredDimension(totalWidth, totalHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = mPaddingLeft;
        float y = mPaddingTop;

        if (mUserName != null && !mUserName.equals("")) {
            mPaint.setTextSize(mTextSizeUserName);
            mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorPrimary));
            y += mRectUserName.height();
            canvas.drawText(mUserName, x, y, mPaint);
        }

        if (mUpdatedTime != null && !mUpdatedTime.equals("")) {
            mPaint.setTextSize(mTextSizeUpdatedTime);
            mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorTextSecondary));
            y += mRectUpdatedTime.height();
            canvas.drawText(mUpdatedTime, x, y, mPaint);
        }

        if (mMessage != null && !mMessage.equals("")) {
            mPaint.setTextSize(mTextSizeMessage);
            mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorTextPrimary));
            canvas.save();
            canvas.translate(x, y);
            //draws static layout on canvas
            mLayoutMessage.draw(canvas);
            canvas.restore();
            y += mLayoutMessage.getHeight();
        }

        if (mShowHandle) {
            mPaint.setTextSize(mTextSizeHandle);
            mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorPrimary));
            y += mRectReadMore.height();
            canvas.drawText(mHandle, mWidth - mPaddingRight - mRectReadMore.width(), y, mPaint);
            mBoundsHandle.set(mWidth - mPaddingRight - mRectReadMore.width(), y - mRectReadMore.height(), mWidth - mPaddingRight, y);
        }

    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
        invalidate();
    }

    public String getUpdatedTime() {
        return mUpdatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        mUpdatedTime = updatedTime;
        invalidate();
    }

    public void setMessage(String message) {
        mMessage = message;
        initStaticLayout();
        requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mShowHandle && mBoundsHandle.contains(touchX, touchY)) {
                    expandCollapseMessage();
                }

                break;
        }
        return true;
    }

    private void expandCollapseMessage() {
        mCollapsed = !mCollapsed;
        int previousHeight = calculateHeight();
        initStaticLayout();
        if (mCollapsed) {
            ViewUtils.collapse(this, previousHeight);
        } else {
            ViewUtils.expand(this, previousHeight);
        }
    }

    private void initStaticLayout() {
        mPaint.setTextSize(mTextSizeMessage);
        if (mMessage != null && !mMessage.equals("")) {
            mShowHandle = mMessage.length() > CHARACTER_LIMIT;
            String text = mMessage.length() < CHARACTER_LIMIT ? mMessage : mMessage.substring(0, CHARACTER_LIMIT);
            mLayoutMessage = new StaticLayout(mCollapsed ? text : mMessage, mPaint, mWidth - mPaddingLeft - mPaddingRight, Layout.Alignment.ALIGN_NORMAL, 1.0F, 1.0F, true);
        }
    }

    public class ExpandAnimation extends Animation {

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}
