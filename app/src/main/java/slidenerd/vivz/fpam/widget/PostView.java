package slidenerd.vivz.fpam.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.util.DisplayUtils;

/**
 * TODO: document your custom view class.
 */
public class PostView extends View {
    private static final float DEFAULT_USERNAME_TEXT_SIZE_DP = 16.0F;
    private static final float DEFAULT_UPDATED_TIME_TEXT_SIZE_DP = 12.0F;
    private static final float DEFAULT_MESSAGE_TEXT_SIZE_DP = 14.0F;
    private static final float DEFAULT_READ_MORE_TEXT_SIZE_DP = 12.0F;

    private static final int DEFAULT_HEIGHT_DP = 48;

    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private int mPostWidth;
    private int mPostHeight;
    private TextPaint mPaintContent;

    private float mTextWidth;
    private float mTextHeight;

    private String mStringUserName = "Vivek Ramesh";
    private String mStringUpdatedTime = "2 mins ago";
    private String mStringMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum";
    private String mStringReadMore = "Read More";
    private Context mContext;

    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;

    private int mContentWidth;
    private int mContentHeight;

    private Rect mRectUserName;
    private Rect mRectUpdatedTime;
    private Rect mRectMessage;
    private Rect mRectReadMore;

    private DynamicLayout mLayout;

    private float mTextSizeUserName;
    private float mTextSizeUdpatedTime;
    private float mTextSizeMessage;
    private float mTextSizeReadMore;

    private RectF mBoundsReadMore;

    private boolean mCollapsed = false;


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

        mExampleString = a.getString(
                R.styleable.PostView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.PostView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.PostView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.PostView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.PostView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }
        a.recycle();

        mTextSizeUserName = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_USERNAME_TEXT_SIZE_DP, getResources().getDisplayMetrics());
        mTextSizeUdpatedTime = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_UPDATED_TIME_TEXT_SIZE_DP, getResources().getDisplayMetrics());
        mTextSizeMessage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_MESSAGE_TEXT_SIZE_DP, getResources().getDisplayMetrics());
        mTextSizeReadMore = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_READ_MORE_TEXT_SIZE_DP, getResources().getDisplayMetrics());

        mPaintContent = new TextPaint();
        mPaintContent.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaintContent.setTextAlign(Paint.Align.LEFT);
        mPaintContent.setTextSize(mTextSizeMessage);

        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();

        mPostWidth = DisplayUtils.getWidthPixels(mContext);
        mPostHeight = (int) DisplayUtils.convertDpToPixel(DEFAULT_HEIGHT_DP, context);

        mRectUserName = new Rect();
        mRectUpdatedTime = new Rect();
        mRectMessage = new Rect();
        mRectReadMore = new Rect();
        mBoundsReadMore = new RectF();

        mLayout = new DynamicLayout(mCollapsed ? mStringMessage.substring(0, 100) : mStringMessage, mPaintContent, mPostWidth - mPaddingLeft - mPaddingRight, Layout.Alignment.ALIGN_NORMAL, 1.0F, 1.0F, true);
    }

    private int calculateHeight() {
        int height = 0;
        height += mPaddingTop;
        height += mPaddingBottom;

        mPaintContent.setTextSize(mTextSizeUserName);
        mPaintContent.getTextBounds(mStringUserName, 0, mStringUserName.length(), mRectUserName);
        height += mRectUserName.height();

        mPaintContent.setTextSize(mTextSizeUdpatedTime);
        mPaintContent.getTextBounds(mStringUpdatedTime, 0, mStringUpdatedTime.length(), mRectUpdatedTime);
        height += mRectUpdatedTime.height();

        mPaintContent.setTextSize(mTextSizeMessage);
        mPaintContent.getTextBounds(mStringMessage, 0, mStringMessage.length(), mRectMessage);
        height += mLayout.getHeight();

        mPaintContent.setTextSize(mTextSizeReadMore);
        mPaintContent.getTextBounds(mStringReadMore, 0, mStringReadMore.length(), mRectReadMore);
        height += mRectReadMore.height();

        return height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int totalWidth;
        int totalHeight;
        totalWidth = resolveSize(mPostWidth, widthMeasureSpec);
        mContentWidth = totalWidth - mPaddingLeft - mPaddingRight;
        //calculate post height based on the size of the post
        mPostHeight = calculateHeight();
        totalHeight = resolveSize(mPostHeight, heightMeasureSpec);
        mContentHeight = totalHeight - mPaddingTop - mPaddingBottom;
        //MUST CALL THIS
        setMeasuredDimension(totalWidth, totalHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = mPaddingLeft;
        float y = mPaddingTop;

        mPaintContent.setTextSize(mTextSizeUserName);
        mPaintContent.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorPrimary));
        mPaintContent.getTextBounds(mStringUserName, 0, mStringUserName.length(), mRectUserName);
        y += mRectUserName.height();
        canvas.drawText(mStringUserName, x, y, mPaintContent);

        mPaintContent.setTextSize(mTextSizeUdpatedTime);
        mPaintContent.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorTextSecondary));
        mPaintContent.getTextBounds(mStringUpdatedTime, 0, mStringUpdatedTime.length(), mRectUpdatedTime);
        y += mRectUpdatedTime.height();
        canvas.drawText(mStringUpdatedTime, x, y, mPaintContent);

        mPaintContent.setTextSize(mTextSizeMessage);
        mPaintContent.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorTextPrimary));
        mPaintContent.getTextBounds(mStringMessage, 0, mStringMessage.length(), mRectMessage);
        canvas.save();
        canvas.translate(x, y);

        //draws static layout on canvas
        mLayout.draw(canvas);
        canvas.restore();

        y += mLayout.getHeight();
        mPaintContent.setTextSize(mTextSizeReadMore);
        mPaintContent.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorPrimary));
        mPaintContent.getTextBounds(mStringReadMore, 0, mStringReadMore.length(), mRectReadMore);
        y += mRectReadMore.height();
        canvas.drawText(mStringReadMore, mPostWidth - mPaddingRight - mRectReadMore.width(), y, mPaintContent);
        mBoundsReadMore.set(mPostWidth - mPaddingRight - mRectReadMore.width(), y - mRectReadMore.height(), mPostWidth - mPaddingRight, y);
    }

    public String getUserName() {
        return mStringUserName;
    }

    public void setUserName(String userName) {
        mStringUserName = userName;
    }

    public String getUpdatedTime() {
        return mStringUpdatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        mStringUpdatedTime = updatedTime;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mBoundsReadMore.contains(touchX, touchY)) {
                    L.m("yes " + touchX + " " + touchY + " " + mBoundsReadMore);
                    expandCollapseMessage();
                }

                break;
        }
        return true;
    }

    private void expandCollapseMessage() {
        mCollapsed = !mCollapsed;
        mPaintContent.setTextSize(mTextSizeMessage);
        mLayout = new DynamicLayout(mCollapsed ? mStringMessage.substring(0, 100) : mStringMessage, mPaintContent, mPostWidth - mPaddingLeft - mPaddingRight, Layout.Alignment.ALIGN_NORMAL, 1.0F, 1.0F, true);
        requestLayout();
    }
}
