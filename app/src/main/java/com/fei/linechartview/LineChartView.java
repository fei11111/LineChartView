package com.fei.linechartview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @ClassName: LineChartView
 * @Description: 折线图
 * @Author: Fei
 * @CreateDate: 2021-02-01 21:12
 * @UpdateUser: 更新者
 * @UpdateDate: 2021-02-01 21:12
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LineChartView extends View {

    private final String TAG = "LineChartView";
    //y轴坐标值
    private String[] mYMark = new String[]{
            "0.0", "4.0", "8.0", "12.0", "16.0", "20.0",
            "24.0", "28.0", "32.0", "36.0", "40.0", "44.0"
    };

    //x,y轴,文字画笔
    private Paint mPaint;
    //折线,圆形画笔
    private Paint mLinePaint;

    //字体大小
    private float mFontSize = 13f;
    //线宽度
    private float mLineWidth = 1f;
    //线颜色
    private int mLineColor = Color.RED;
    //x，y轴颜色
    private int mAxleColor = Color.LTGRAY;
    //箭头高度
    private float mArrowHeight = 15f;
    //箭头宽度一半,箭头高度/tan60°
    private float mArrowHalfWidth;
    //箭头间距
    private float mArrowSpace = 20f;
    //y轴字体水平间距
    private float mYFontHorizontalSpace = 8;
    //x轴字体水平间距
    private float mXFontHorizontalSpace = 20;
    //x轴字体垂直间距
    private float mXFontVerticalSpace = 8;
    //圆半径
    private float mCircleRadius = 5;
    //y轴每份的高度大小
    private float mPerY;
    //x轴每份的宽度大小
    private float mPerX;
    //y轴字体最大宽度
    private int mYFontWidth;
    //x轴字体宽度
    private int mXFontWidth;
    //x轴字体高度
    private int mXFontHeight;
    private Rect mTextBounds;
    private float mMarkLineWidth = 8f;//刻度线长度

    //后台返回值
    private List<ChartData> mChartDatas;

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取属性值，后面在增加属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineChartView);
        //字体大小
        mFontSize = typedArray.getDimension(R.styleable.LineChartView_fontSize, sp2px(mFontSize));
        //线宽度
        mLineWidth = typedArray.getDimension(R.styleable.LineChartView_lineWidth, dp2px(mLineWidth));
        //线颜色
        mLineColor = typedArray.getColor(R.styleable.LineChartView_lineColor, mLineColor);
        //x，y轴颜色
        mAxleColor = typedArray.getColor(R.styleable.LineChartView_axleColor, mAxleColor);
        //箭头高度
        mArrowHeight = typedArray.getDimension(R.styleable.LineChartView_arrowHeight, dp2px(mArrowHeight));
        //箭头距离刻度间距
        mArrowSpace = typedArray.getDimension(R.styleable.LineChartView_arrowSpace, dp2px(mArrowSpace));
        //y轴字体水平间距
        mYFontHorizontalSpace = typedArray.getDimension(R.styleable.LineChartView_yFontHorizontalSpace, dp2px(mYFontHorizontalSpace));
        //x轴字体水平间距
        mXFontHorizontalSpace = typedArray.getDimension(R.styleable.LineChartView_xFontHorizontalSpace, dp2px(mXFontHorizontalSpace));
        //x轴字体垂直间距
        mXFontVerticalSpace = typedArray.getDimension(R.styleable.LineChartView_xFontVerticalSpace, dp2px(mXFontVerticalSpace));
        //刻度宽度
        mMarkLineWidth = typedArray.getDimension(R.styleable.LineChartView_markLineWidth, dp2px(mMarkLineWidth));
        //圆点半径
        mCircleRadius = typedArray.getDimension(R.styleable.LineChartView_circleRadius, dp2px(mCircleRadius));
        typedArray.recycle();

        //画普通x，y轴和标注的画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(mAxleColor);

        //画折线的画笔
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setDither(true);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setTextSize(mFontSize);
        mLinePaint.setColor(mLineColor);

        //文本矩形
        mTextBounds = new Rect();
        //箭头宽度一半
        mArrowHalfWidth = (float) (mArrowHeight / Math.tan(Math.PI / 3));

    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mChartDatas == null || mChartDatas.size() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        //测量总宽度
        //取y轴刻度值最大值的高宽度
        mPaint.getTextBounds(mYMark[mYMark.length - 1], 0, mYMark[mYMark.length - 1].length(), mTextBounds);
        mYFontWidth = mTextBounds.width();
        String key = mChartDatas.get(0).getKey();
        mPaint.getTextBounds(key, 0, key.length(), mTextBounds);

        mXFontWidth = mTextBounds.width();
        mXFontHeight = mTextBounds.height();
        //宽度重新设置，使之可以滑动
        //宽度=padding+y轴水平间距+字体宽度+y轴水平间距+y轴线宽度+x轴每段文本和间距宽度*x轴刻度数量+箭头间距+箭头高度
        int width = (int) (getPaddingLeft() + getPaddingRight() + mYFontHorizontalSpace + mYFontWidth + mYFontHorizontalSpace + mLineWidth + (mXFontHorizontalSpace * 2 + mXFontWidth)
                * mChartDatas.size() + mArrowSpace + mArrowHeight);
        setMeasuredDimension(width, heightMeasureSpec);

        //y轴每段大小 = (总高度-padding-箭头间距-箭头高度)/Y刻度总数
        mPerY = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mArrowSpace - mArrowHeight) / mYMark.length;
        //x轴每段大小 = x轴文本间距*2+文本宽度
        mPerX = mXFontWidth + mXFontHorizontalSpace * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mChartDatas == null || mChartDatas.size() == 0) return;
        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        //1.画y轴
        //1.1画y轴刻度
        //文本左边
        float yTextLeft = paddingLeft + mYFontHorizontalSpace;
        //y轴左边
        float yLeft = yTextLeft + mYFontWidth + mYFontHorizontalSpace;
        //y轴上边
        float yTop = paddingTop + mArrowHeight;
        //文本上边
        float yTextTop = yTop + mArrowSpace;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        //baseline差距
        float dy = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        //1.1.1 画刻度和刻度值
        for (int i = 0; i < mYMark.length; i++) {
            String text = mYMark[mYMark.length - i - 1];
            float yMarkTop = yTextTop + i * mPerY;
            canvas.drawLine(yLeft, yMarkTop, yLeft + mMarkLineWidth, yMarkTop, mPaint);
            mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
            float textWidth = mTextBounds.width();
            //与最大字的宽度差距
            float distance = (mYFontWidth - textWidth) / 2;
            //画文本
            canvas.drawText(text, yTextLeft + distance, yMarkTop + dy, mPaint);
        }
        //1.2 y轴线
        //x轴的top刚好等于y轴的bottom
        float yBottom = yTop + mArrowSpace + (mYMark.length - 1) * mPerY;
        canvas.drawLine(yLeft, yTop, yLeft, yBottom, mPaint);
        //1.3 y轴箭头
        float arrowLeft = yLeft;
        float arrowTop = yTop;
        drawTriangle(arrowLeft, arrowTop, arrowLeft, arrowTop - mArrowHeight, mArrowHeight,
                mArrowHalfWidth, canvas, mPaint);

        //2.画x轴
        //x轴的top刚好等于y轴的bottom
        float xTop = yBottom;
        //x轴宽度=文本宽度+文本水平宽度*2+箭头间距
        canvas.drawLine(yLeft, xTop,
                yLeft + mChartDatas.size() * mPerX + mArrowSpace,
                xTop, mPaint);
        //2.2画x轴刻度
        //记录之前点坐标
        float previousCircleX = 0;
        float previousCircleY = 0;
        for (int i = 0; i < mChartDatas.size(); i++) {
            //画刻度
            float circleX = yLeft + mPerX * i + mXFontWidth / 2 + mXFontHorizontalSpace;
            canvas.drawLine(circleX,
                    xTop - mMarkLineWidth,
                    yLeft + mPerX * i + mXFontWidth / 2 + mXFontHorizontalSpace,
                    xTop,
                    mPaint);
            String xValue = mChartDatas.get(i).getKey();
            canvas.drawText(xValue, yLeft + mXFontHorizontalSpace + mPerX * i,
                    xTop + mXFontVerticalSpace + mXFontHeight / 2 + dy, mPaint);
            //将值转成float类型，除以4，并且取余
            float value = Float.parseFloat(mChartDatas.get(i).getValue());
            //获取坐标在y轴哪一段，但是y轴是倒序，还需要mYMark.length - 1 - yIndex才是最终哪一段
            int yIndex = (int) (value / 4);
            //获取y轴哪一段后的一点偏移量
            float offset = value % 4 * mPerY / 4;
            //圆点y轴坐标=y轴位置坐标+(y轴总段数-1-当前段数)*每段大小-偏移量
            float circleY = yTextTop + (mYMark.length - 1 - yIndex) * mPerY - offset;
            //3.画点
            canvas.drawCircle(circleX, circleY, mCircleRadius, mLinePaint);
            //4.点连线
            if (previousCircleX != 0) {
                canvas.drawLine(previousCircleX, previousCircleY, circleX, circleY, mLinePaint);
            }
            //报到为之前坐标，用于画折线
            previousCircleX = circleX;
            previousCircleY = circleY;
            //5.画点上的值
            mPaint.getTextBounds(mChartDatas.get(i).getValue(), 0, mChartDatas.get(i).getValue().length(), mTextBounds);
            float tipTextWidth = mTextBounds.width();
            float tipTextHeight = mTextBounds.height();
            // 圆点x位置-文本一半
            float textLeft = circleX - tipTextWidth / 2;
            //文本y轴坐标 = 圆点y轴坐标-圆半径-x轴垂直间距-文本高度一半，还需要加上dy
            float textTop = circleY - mCircleRadius - mXFontVerticalSpace - tipTextHeight / 2;
            canvas.drawText(mChartDatas.get(i).getValue(), textLeft, textTop + dy, mPaint);
        }
        //2.3x轴箭头
        //x轴箭头left = x轴right+箭头间距
        float xArrowLeft = yLeft + mPerX * mChartDatas.size() + mArrowSpace;
        float xArrowTop = xTop;
        drawTriangle(xArrowLeft,
                xArrowTop, xArrowLeft + mArrowHeight, xArrowTop,
                mArrowHeight, mArrowHalfWidth, canvas, mPaint);
    }


    /***
     * 三角形
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @param height
     * @param bottom
     * @param canvas
     * @param paint
     */
    private void drawTriangle(float fromX, float fromY, float toX, float toY,
                              float height, float bottom, Canvas canvas, Paint paint) {
        // height和bottom分别为三角形的高与底的一半,调节三角形大小
        float distance = (float) getPositionDistance(fromX, fromY, toX, toY);// 获取线段距离
        float dx = toX - fromX;// 有正负，不要取绝对值
        float dy = toY - fromY;// 有正负，不要取绝对值
        float vX = toX - (height / distance * dx);
        float vY = toY - (height / distance * dy);
        //终点的箭头
        Path path = new Path();
        path.moveTo(toX, toY);// 此点为三边形的起点
        path.lineTo(vX + (bottom / distance * dy), vY
                - (bottom / distance * dx));
        path.lineTo(vX - (bottom / distance * dy), vY
                + (bottom / distance * dx));
        path.close(); // 使这些点构成封闭的三边形
        canvas.drawPath(path, paint);
    }

    /**
     * 获取两点之间的距离
     */
    private double getPositionDistance(float startX, float startY, float endX, float endY) {
        float dx = startX - endX;
        float dy = startY - endY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void setData(List chartDatas) {
        this.mChartDatas = chartDatas;
        requestLayout();
    }
}
