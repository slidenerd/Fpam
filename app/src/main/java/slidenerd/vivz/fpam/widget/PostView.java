package slidenerd.vivz.fpam.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import slidenerd.vivz.fpam.R;
import slidenerd.vivz.fpam.log.L;
import slidenerd.vivz.fpam.util.DisplayUtils;
import slidenerd.vivz.fpam.util.ViewUtils;

/**
 * TODO: document your custom view class, move all dimension related information as setters to XML
 */
public class PostView extends View {
    //Number of characters to show in collapsed mode
    public static final int CHARACTER_LIMIT = 200;

    //Text size used to display 'name'
    private static final float TEXT_SIZE_NAME = 16.0F;

    //Text size used to display 'updated_time'
    private static final float TEXT_SIZE_TIME = 12.0F;

    //Text size used to display 'message' or post content
    private static final float TEXT_SIZE_MESSAGE = 14.0F;

    //Text size used to display 'Read More' or 'Read Less' handle that expands and collapses 'message'
    private static final float TEXT_SIZE_HANDLE = 12.0F;

    //Default height of a post prior to onMeasure
    private static final int HEIGHT = 96;
    private static final int PROFILE_PICTURE_RADIUS = 40;
    //Width of our post view
    private int mWidth;
    //Height of our post view
    private int mHeight;
    //Common paint object for drawing all the text in our post view
    private TextPaint mPaint;
    private String mName = null;
    private String mTime = null;
    private String mMessage = null;
    private String mHandle = "Read More";
    private Context mContext;
    //Padding
    private float mPadLeft;
    private float mPadRight;
    private float mPadTop;
    private float mPadBottom;
    //Rectangle containing the text bounds of 'name','updated_time','message' and 'Read More' or 'Read Less' handle
    private Rect mRectName;
    private Rect mRectTime;
    private Rect mRectMessage;
    private Rect mRectHandle;
    //Container for the multiline text which is a part of the post 'message'.
    private StaticLayout mLayoutMessage;
    private float mSizeName;
    private float mSizeTime;
    private float mSizeMessage;
    //Size of the handle 'Read More'
    private float mSizeHandle;
    //Bounds of the handle 'Read More' or 'Read Less' which is used to fire an event when the user clicks the handle
    private RectF mBoundsHandle;
    //initialize the message in collapsed state
    private boolean mCollapsed = true;
    //dont show 'Read More' handle by default
    private boolean mShowHandle = false;
    private int mSizeUserImage;
    private Bitmap mUserImage;
    private float mSpace1;
    private float mSpace2;
    private float mSpace3;
    private float mSpace4;
    private String mUserImageUri;

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
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        //Initialize text sizes for all the text that needs to be drawn
        mSizeName = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_NAME, displayMetrics);
        mSizeTime = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_TIME, displayMetrics);
        mSizeMessage = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_MESSAGE, displayMetrics);
        mSizeHandle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_HANDLE, displayMetrics);

        //TODO add support for LTR and RTL based on the locale
        mPaint = new TextPaint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);

        //Initialize padding values

        float fourtyDpToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PROFILE_PICTURE_RADIUS, displayMetrics);
        float twentyDpToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20.0F, displayMetrics);
        float sixteenDpToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16.0F, displayMetrics);
        float tenDpToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0F, displayMetrics);
        float fourDpToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0F, displayMetrics);

        mPadLeft = getPaddingLeft() < sixteenDpToPx ? sixteenDpToPx : getPaddingLeft();
        mPadRight = getPaddingRight() < sixteenDpToPx ? sixteenDpToPx : getPaddingRight();
        mPadTop = getPaddingTop() < sixteenDpToPx ? sixteenDpToPx : getPaddingTop();
        mPadBottom = getPaddingBottom() < sixteenDpToPx ? sixteenDpToPx : getPaddingBottom();

        mSpace1 = sixteenDpToPx;
        mSpace2 = fourDpToPx;
        mSpace3 = twentyDpToPx;

        mSpace4 = tenDpToPx;
        mSizeUserImage = Math.round(fourtyDpToPx);
        //Get the width of our device
        mWidth = DisplayUtils.getWidthPixels(mContext);

        //set a default height of 96dp which will be changed again inside onMeasure
        mHeight = (int) DisplayUtils.convertDpToPixel(HEIGHT, context);

        mRectName = new Rect();
        mRectTime = new Rect();
        mRectMessage = new Rect();
        mRectHandle = new Rect();
        mBoundsHandle = new RectF();

    }

    /**
     * @return height of our custom post view depending on the elements present inside
     */
    private int calculateHeight() {
        int height = 0;

        //Add padding
        height += mPadTop;
        height += mPadBottom;

        //If we have a valid 'name' take its height into account
        if (mName != null && !mName.equals("")) {
            mPaint.setTextSize(mSizeName);
            mPaint.getTextBounds(mName, 0, mName.length(), mRectName);
            height += mRectName.height() + mSpace2;
        }

        //If we have a valid 'updated_time' take its height into account
        if (mTime != null && !mTime.equals("")) {
            mPaint.setTextSize(mSizeTime);
            mPaint.getTextBounds(mTime, 0, mTime.length(), mRectTime);
            height += mRectTime.height();
        }

        if (mUserImage != null) {
//            L.m("max of " + mUserImage.getHeight() + " " + height);
            height = Math.max(mUserImage.getHeight(), height);
            height += mPadTop;
        }
        //If we have a valid 'message' take its height into account
        if (mMessage != null && !mMessage.equals("")) {
            mPaint.setTextSize(mSizeMessage);
            mPaint.getTextBounds(mMessage, 0, mMessage.length(), mRectMessage);
            height += mSpace3 + mLayoutMessage.getHeight();
        }

        //If a post exceeds CHARACTER_LIMIT number of characters, show the handle 'Read More'
        if (mShowHandle) {
            mPaint.setTextSize(mSizeHandle);
            mPaint.getTextBounds(mHandle, 0, mHandle.length(), mRectHandle);
            height += mSpace4 + mRectHandle.height() + mSpace4;
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
        setMeasuredDimension(totalWidth, totalHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //TODO add support for drawing RTL and LTR
        float x = mPadLeft;
        float y = mPadTop;

        if (mUserImage != null) {
            canvas.drawBitmap(mUserImage, mPadLeft, mPadTop, mPaint);
            x += mUserImage.getWidth() + mSpace1;
        }
        //If we have a valid 'name' draw it
        if (mName != null && !mName.equals("")) {
            mPaint.setTextSize(mSizeName);
            mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorPrimary));
            y += mRectName.height();
            canvas.drawText(mName, x, y, mPaint);
        }

        //If we have a valid 'updated_time' draw it
        if (mTime != null && !mTime.equals("")) {
            mPaint.setTextSize(mSizeTime);
            mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorTextSecondary));
            y += mRectTime.height() + mSpace2;
            canvas.drawText(mTime, x, y, mPaint);
        }

        if (mUserImage != null) {
            L.m("max of " + mUserImage.getHeight() + " " + y);
            y = Math.max(mUserImage.getHeight(), y);
            y += mPadTop;
        }
        //If we have a valid 'message' draw it
        if (mMessage != null && !mMessage.equals("")) {
            x = mPadLeft;
            y += mSpace3;
            mPaint.setTextSize(mSizeMessage);
            mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorTextPrimary));
            //Save the state of the canvas prior to translation
            canvas.save();

            //Translate the canvas
            canvas.translate(x, y);

            //draws static layout on canvas
            mLayoutMessage.draw(canvas);

            //Restore the canvas to its last saved state
            canvas.restore();
            y += mLayoutMessage.getHeight();
        }

        //If we are showing the handle 'Read More' or 'Read Less' draw it
        if (mShowHandle) {
            mPaint.setTextSize(mSizeHandle);
            mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorPrimary));
            y += mSpace4;
            y += mRectHandle.height();
            canvas.drawText(mHandle, mWidth - mPadRight - mRectHandle.width(), y, mPaint);
            mBoundsHandle.set(mWidth - mPadRight - mRectHandle.width(), y - mRectHandle.height(), mWidth - mPadRight, y);
            y += mSpace4;
        }

//        L.m("onDraw called " + (drawCount++) + " times");
    }

    public String getUserName() {
        return mName;
    }

    public void setUserName(String userName) {
        mName = userName;

        //Redraw if the 'name' changes
        invalidate();
    }

    public String getUpdatedTime() {
        return mTime;
    }

    public void setUpdatedTime(String updatedTime) {
        mTime = updatedTime;

        //Redraw if the 'updated_time' changes
        invalidate();
    }

    public void setMessage(String message) {
        mMessage = message;

        //Build our layout containing multiline text once again
        initStaticLayout();

        //Depending on whether we want to show the full text or partial text, the height of the post may change, hence call requestLayout()
        requestLayout();
    }

    public void setProfilePicture(String uri) {
        mUserImageUri = uri;
        Picasso.with(mContext)
                .load(uri)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mUserImage = Bitmap.createBitmap(bitmap);
                        requestLayout();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        L.m("failed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        L.m("onPrepare load");
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mShowHandle && mBoundsHandle.contains(touchX, touchY)) {

                    //Fire the toggle method to run the expand animation if the message is collapsed and run the collapse animation if the message is expanded
                    toggleMessageHeight();
                }
                break;
        }
        return true;
    }

    private void toggleMessageHeight() {
        mCollapsed = !mCollapsed;

        //Get the height of the message prior to expanding or contracting it
        final int previousHeight = calculateHeight();
        initStaticLayout();

        //Get the height of the message after expanding or contracting it
        int currentHeight = calculateHeight();
        //Perform animation
        if (mCollapsed) {
            ViewUtils.collapse(this, previousHeight);
        } else {
            ViewUtils.expand(this, previousHeight);
        }

    }

    private void initStaticLayout() {
        mPaint.setTextSize(mSizeMessage);
        if (mMessage != null && !mMessage.equals("")) {

            //Show the 'Read More' or 'Read Less' handle if there are more than CHARACTER_LIMIT characters
            mShowHandle = mMessage.length() > CHARACTER_LIMIT;

            //Show only a part of the text if it exceeds CHARACTER_LIMIT
            String text = mMessage.length() < CHARACTER_LIMIT ? mMessage : mMessage.substring(0, CHARACTER_LIMIT);
            mLayoutMessage = new StaticLayout(mCollapsed ? text : mMessage, mPaint, Math.round(mWidth - mPadLeft - mPadRight), Layout.Alignment.ALIGN_NORMAL, 1.0F, 1.0F, true);
        }
    }
}
