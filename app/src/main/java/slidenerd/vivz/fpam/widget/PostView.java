package slidenerd.vivz.fpam.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import slidenerd.vivz.fpam.ui.CircleTransform;
import slidenerd.vivz.fpam.util.DisplayUtils;
import slidenerd.vivz.fpam.util.ValidationUtils;
import slidenerd.vivz.fpam.util.ViewUtils;

/**
 * TODO: document your custom view class, move all dimension related information as setters to XML, manage custom view state inside RecyclerView
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
    private static final int HEIGHT = 72;
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
    private int mWidthPostImage;
    private int mHeightPostImage;
    private Bitmap mUserImage;
    private Bitmap mPostImage;
    private String mUserImageUri;
    private String mPostImageUri;

    private float mSpace1;
    private float mSpace2;
    private float mSpace3;

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

    private void initSizes(Context context) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int TO_SP = TypedValue.COMPLEX_UNIT_SP;
        int TO_DP = TypedValue.COMPLEX_UNIT_DIP;
        //Initialize padding values
        mPadLeft = getPaddingLeft();
        mPadRight = getPaddingRight();
        mPadTop = getPaddingTop();
        mPadBottom = getPaddingBottom();

        //Initialize text sizes for all the text that needs to be drawn
        mSizeName = TypedValue.applyDimension(TO_SP, TEXT_SIZE_NAME, metrics);
        mSizeTime = TypedValue.applyDimension(TO_SP, TEXT_SIZE_TIME, metrics);
        mSizeMessage = TypedValue.applyDimension(TO_SP, TEXT_SIZE_MESSAGE, metrics);
        mSizeHandle = TypedValue.applyDimension(TO_SP, TEXT_SIZE_HANDLE, metrics);

        //Init font sizes for
        mSizeUserImage = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PROFILE_PICTURE_RADIUS, metrics));
        mSpace1 = TypedValue.applyDimension(TO_DP, 16.0F, metrics);
        mSpace2 = TypedValue.applyDimension(TO_DP, 4.0F, metrics);
        mSpace3 = TypedValue.applyDimension(TO_DP, 10.0F, metrics);

        //Get the width of our device
        mWidth = DisplayUtils.getWidthPixels(mContext);

        //set a default height of 96dp which will be changed again inside onMeasure
        mHeight = (int) DisplayUtils.dpToPx(HEIGHT, context);

        mWidthPostImage = mWidth;
        mHeightPostImage = Math.round(mWidthPostImage * 9 / 16.0F);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mContext = context;
        initSizes(context);

        //TODO add support for LTR and RTL based on the locale
        mPaint = new TextPaint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.LEFT);

        mRectName = new Rect();
        mRectTime = new Rect();
        mRectMessage = new Rect();
        mRectHandle = new Rect();
        mBoundsHandle = new RectF();

        mUserImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile);
    }

    /**
     * @return height of our custom post view depending on the elements present inside
     */
    private int calculateHeight() {
        float height = 0.0F;

        //Add padding
        height += mPadTop;
        height += mPadBottom;

        //If we have a valid 'name' take its height into account and the space between the 'name' and the 'updated_time' into account
        if (ValidationUtils.notNullOrEmpty(mName)) {
            mPaint.setTextSize(mSizeName);
            mPaint.getTextBounds(mName, 0, mName.length(), mRectName);
            height += mRectName.height() + mSpace2;
        }

        //If we have a valid 'updated_time' take its height into account, since our name may be null, the extra space between the 'name' and 'updated_time' is not added here
        if (ValidationUtils.notNullOrEmpty(mTime)) {
            mPaint.setTextSize(mSizeTime);
            mPaint.getTextBounds(mTime, 0, mTime.length(), mRectTime);
            height += mRectTime.height();
        }

        //We always have a default user image loaded, hence take the maximum of the image height and the height obtained by adding the 'name', 'updated_time' and space between them into account
        height = Math.max(mUserImage.getHeight(), height);

        //Add this padding space to account for separation between user image and post message or image whichever comes next.
        height += mPadTop;

        //To the maximum of user image height and the section containing 'name' and 'updated_time', add spacing to account for separation between user image and the next element regardless of whether its a post image or message.
        height += mSpace3;

        //If we have a valid 'message' take its height into account
        if (ValidationUtils.notNullOrEmpty(mMessage)) {
            mPaint.setTextSize(mSizeMessage);
            mPaint.getTextBounds(mMessage, 0, mMessage.length(), mRectMessage);
            height += mLayoutMessage.getHeight();
            if (mShowHandle) {
                mPaint.setTextSize(mSizeHandle);
                mPaint.getTextBounds(mHandle, 0, mHandle.length(), mRectHandle);
                height += mSpace3 + mRectHandle.height() + mSpace3;
            }
        }

        //Add the height of the post image if any to the total height
        if (mPostImage != null) {
            height += mHeightPostImage;
        }
        return Math.round(height);
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

        //If the image is null, we already have spacing added to x and y in the form of mPadLeft and mPadTop, if the image is not null, then we add the spacing between the image and the username represented mSpace1
        if (mUserImage != null) {
            canvas.drawBitmap(mUserImage, mPadLeft, mPadTop, mPaint);
            x += mUserImage.getWidth() + mSpace1;
        }
        //If we have a valid 'name' draw it, and add the space between 'name' and 'updated_time' represented by mSpace2.
        if (ValidationUtils.notNullOrEmpty(mName)) {
            mPaint.setTextSize(mSizeName);
            mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorTextPrimary));
            y += mRectName.height();
            canvas.drawText(mName, x, y, mPaint);
            y += mSpace2;
        }

        //If we have a valid 'updated_time' draw it
        if (ValidationUtils.notNullOrEmpty(mTime)) {
            mPaint.setTextSize(mSizeTime);
            mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorTextSecondary));
            y += mRectTime.height();
            canvas.drawText(mTime, x, y, mPaint);
        }

        //We always have a profile picture placeholder displayed for the user regardless of whether a real one is loaded from the internet or not, the height of our first section is the maximum of the height of our profile picture and the sum of heights of the sections 'name' plus 'updated_time'
        y = Math.max(mUserImage.getHeight(), y);
        y += mPadTop;
        y += mSpace3;

        //If we have a valid 'message' draw it
        if (ValidationUtils.notNullOrEmpty(mMessage)) {
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

            //Add the top separation between the message and the handle regardless of whether the handle is shown or not
            y += mSpace3;

            //if we have a handle draw it
            if (mShowHandle) {
                mPaint.setTextSize(mSizeHandle);
                mPaint.setColor(DisplayUtils.getResolvedColor(mContext, R.color.colorPrimary));
                y += mRectHandle.height();
                canvas.drawText(mHandle, mWidth - mPadRight - mRectHandle.width(), y, mPaint);
                mBoundsHandle.set(mWidth - mPadRight - mRectHandle.width(), y - mRectHandle.height(), mWidth - mPadRight, y);
            }

            //Add the bottom separation between the message and the handle regardless of whether the handle is shown or not
            y += mSpace3;
        }

        if (mPostImage != null) {
            canvas.drawBitmap(mPostImage, 0, y, mPaint);
            y += mHeightPostImage;
        }
    }

    public String getUserName() {
        return mName;
    }

    public void setUserName(String userName) {
        mName = userName;

        if (ValidationUtils.notNullOrEmpty(userName)) {
            //Redraw if the 'name' changes

            invalidate();
        }
    }

    public String getUpdatedTime() {
        return mTime;
    }

    public void setUpdatedTime(String updatedTime) {
        mTime = updatedTime;

        if (ValidationUtils.notNullOrEmpty(updatedTime)) {
            //Redraw if the 'updated_time' changes
            invalidate();
        }
    }

    public void setMessage(String message) {
        mMessage = message;

        if (ValidationUtils.notNullOrEmpty(message)) {
            //Build our layout containing multiline text once again
            initStaticLayout();

            //Depending on whether we want to show the full text or partial text, the height of the post may change, hence call requestLayout()
            requestLayout();
        }
    }

    public void setProfilePicture(String uri) {
        mUserImageUri = uri;
        Picasso.with(mContext)
                .load(uri)
                .resize(mSizeUserImage, mSizeUserImage)
                .transform(new CircleTransform())
                .centerCrop()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mUserImage = bitmap;
                        requestLayout();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        L.m("failed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    public void setPostPicture(String uri) {
        mPostImageUri = uri;
        Picasso.with(mContext)
                .load(uri)
                .resize(mWidthPostImage, mHeightPostImage)
                .centerCrop()
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mPostImage = bitmap;
                        requestLayout();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        L.m("failed");
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
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
        //Show the 'Read More' or 'Read Less' handle if there are more than CHARACTER_LIMIT characters
        mShowHandle = mMessage.length() > CHARACTER_LIMIT;
        //Show only a part of the text if it exceeds CHARACTER_LIMIT
        String text = mMessage.length() < CHARACTER_LIMIT ? mMessage : mMessage.substring(0, CHARACTER_LIMIT);
        mLayoutMessage = new StaticLayout(mCollapsed ? text : mMessage, mPaint, Math.round(mWidth - mPadLeft - mPadRight), Layout.Alignment.ALIGN_NORMAL, 1.2F, 0.0F, true);
    }
}
