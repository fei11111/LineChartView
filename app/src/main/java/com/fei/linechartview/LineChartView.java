package com.fei.linechartview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    private String[] mYMark = new String[]{
            "0.0", "4.0", "8.0", "12.0", "16.0", "20.0",
            "24.0", "28.0", "32.0", "36.0", "40.0", "44.0"
    };

    private List<ChartData> mChartDatas;

    private String[] mXMark;

    private Paint mPaint;
    private Paint mLinePaint;

    //字体大小
    private float mFontSize = 13f;
    //线宽度
    private float mLineWidth = 1f;
    //线颜色
    private int mLineColor = Color.RED;
    //x，y轴颜色
    private int mNormalLineColor = Color.LTGRAY;
    //箭头高度
    private float mArrowHeight = 15f;
    //箭头宽度一半
    private float mArrowHalfWidth;
    //箭头间距
    private float mArrowSpace = 20f;
    //y轴字体水平间距
    private float mYFontHorizontalSpace = 8;
    //x轴字体水平间距
    private float mXFontHorizontalSpace = 20;
    //x轴字体垂直间距
    private float mXFontVerticalSpace = 8;
    //y轴每份的高度大小
    private float mPerY;
    //x轴每份的宽度大小
    private float mPerX;

    private int mFontHeight;//字体高度
    private int mFontWidth;//字体宽度
    private Rect mTextBounds;

    private float mMarkLineWidth = 8f;//刻度线长度

    //圆半径
    private float mCircleRadius = 5;
    //点坐标
    private List<PointF> mPointFs = new ArrayList<>();

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
        mFontSize = typedArray.getDimension(R.styleable.LineChartView_fontSize, sp2px(mFontSize));
        mLineWidth = typedArray.getDimension(R.styleable.LineChartView_lineWidth, dp2px(mLineWidth));
        mLineColor = typedArray.getColor(R.styleable.LineChartView_lineColor, mLineColor);
        typedArray.recycle();

        //画普通x，y轴和标注的画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setTextSize(mFontSize);
        mPaint.setColor(mNormalLineColor);

        //画折线的画笔
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setDither(true);
        mLinePaint.setStrokeWidth(mLineWidth);
        mLinePaint.setTextSize(mFontSize);
        mLinePaint.setColor(mLineColor);

        //箭头高度
        mArrowHeight = dp2px(mArrowHeight);
        //箭头距离刻度间距
        mArrowSpace = dp2px(mArrowSpace);
        //y轴字体水平间距
        mYFontHorizontalSpace = dp2px(mYFontHorizontalSpace);
        //x轴字体水平间距
        mXFontHorizontalSpace = dp2px(mXFontHorizontalSpace);
        //x轴字体垂直间距
        mXFontVerticalSpace = dp2px(mXFontVerticalSpace);
        //刻度宽度
        mMarkLineWidth = dp2px(mMarkLineWidth);

        //文本矩形
        mTextBounds = new Rect();

        //箭头宽度一半
        mArrowHalfWidth = (float) (mArrowHeight / Math.tan(Math.PI / 3));

        //圆点半径
        mCircleRadius = dp2px(mCircleRadius);

        //测试数据
        mChartDatas = new ArrayList<>();
        ChartData chartData = new ChartData("2020-01-10", "1.0");
        ChartData chartData1 = new ChartData("2020-01-11", "11.0");
        ChartData chartData2 = new ChartData("2020-01-12", "9.0");
        ChartData chartData3 = new ChartData("2020-01-13", "30.0");
        ChartData chartData4 = new ChartData("2020-01-14", "42.0");
        ChartData chartData5 = new ChartData("2020-01-15", "25.0");

        mChartDatas.add(chartData);
        mChartDatas.add(chartData1);
        mChartDatas.add(chartData2);
        mChartDatas.add(chartData3);
        mChartDatas.add(chartData4);
        mChartDatas.add(chartData5);
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //mPerY = (总高度-padding-箭头间距-箭头高度)/Y刻度总数
        mPerY = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mArrowSpace - mArrowHeight) / mYMark.length;
        //取最大值的高宽度
        mPaint.getTextBounds(mYMark[mYMark.length - 1], 0, mYMark[mYMark.length - 1].length(), mTextBounds);
        mFontHeight = mTextBounds.height();
        mFontWidth = mTextBounds.width();
        String key = mChartDatas.get(0).getKey();
        mPaint.getTextBounds(key, 0, key.length(), mTextBounds);
        float textWidth = mTextBounds.width();
        //宽度重新设置，使之可以滑动
        int width = (int) (mYFontHorizontalSpace + mFontWidth + mYFontHorizontalSpace + mLineWidth + (mXFontHorizontalSpace * 2 + textWidth)
                * mChartDatas.size() + mArrowSpace + mArrowHeight);
        setMeasuredDimension(width, heightMeasureSpec);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        mPointFs.clear();
        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        //1.画y轴
        //1.1画y轴刻度
        //文本左边
        float yTextLeft = paddingLeft + mYFontHorizontalSpace;
        //y轴左边
        float yLeft = yTextLeft + mFontWidth + mYFontHorizontalSpace;
        //y轴上边
        float yTop = paddingTop + mArrowHeight;
        //文本上边
        float yTextTop = yTop + mArrowSpace;
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        //baseline差距
        float dy = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        for (int i = 0; i < mYMark.length; i++) {
            String text = mYMark[mYMark.length - i - 1];
            //刻度
            canvas.drawLine(yLeft, yTextTop + i * mPerY, yLeft + mMarkLineWidth, yTop + mArrowSpace + i * mPerY, mPaint);
            mPaint.getTextBounds(text, 0, text.length(), mTextBounds);
            float textWidth = mTextBounds.width();
            //与最大字的宽度差距
            float distance = (mFontWidth - textWidth) / 2;
            //画文本
            canvas.drawText(text, yTextLeft + distance, yTextTop + i * mPerY + dy, mPaint);
        }
        //1.2y轴线
        canvas.drawLine(yLeft, yTop, yLeft, yTop + mArrowSpace + (mYMark.length - 1) * mPerY, mPaint);
        //1.3y轴箭头
        float arrowLeft = yLeft;
        float arrowTop = yTop;
        drawTriangle(arrowLeft, arrowTop, arrowLeft, arrowTop - mArrowHeight, mArrowHeight,
                mArrowHalfWidth, canvas, mPaint);

        //2.画x轴
        //2.1获取字体高宽度
        float xTop = yTop + mArrowSpace + (mYMark.length - 1) * mPerY;
        String key = mChartDatas.get(0).getKey();
        mPaint.getTextBounds(key, 0, key.length(), mTextBounds);
        float textWidth = mTextBounds.width();
        float textHeight = mTextBounds.height();
        //x轴宽度=文本宽度+文本水平宽度*2+箭头间距
        canvas.drawLine(yLeft, xTop,
                yLeft + mChartDatas.size() * (textWidth + mXFontHorizontalSpace * 2) + mArrowSpace,
                xTop, mPaint);
        //2.2画x轴刻度
        //记录之前点坐标
        float previousDataX = 0;
        float previousDataY = 0;
        for (int i = 0; i < mChartDatas.size(); i++) {
            //记录每个点的坐标
            //刻度
            float dataX = yLeft + (mXFontHorizontalSpace * 2 + textWidth) * i + textWidth / 2 + mXFontHorizontalSpace;
            canvas.drawLine(dataX,
                    xTop - mMarkLineWidth,
                    yLeft + (mXFontHorizontalSpace * 2 + textWidth) * i + textWidth / 2 + mXFontHorizontalSpace,
                    xTop,
                    mPaint);
            String xValue = mChartDatas.get(i).getKey();
            canvas.drawText(xValue, yLeft + mXFontHorizontalSpace * (i + 1) + (textWidth + mXFontHorizontalSpace) * i,
                    xTop + mXFontVerticalSpace + textHeight / 2 + dy, mPaint);
            //将值转成int类型，除以4，并且取余
            float value = Float.parseFloat(mChartDatas.get(i).getValue());
            int yIndex = (int) (value / 4);
            float offset = value % 4 * mPerY / 4;
            float dataY = yTextTop + (mYMark.length - 1 - yIndex) * mPerY - offset;
            //3.画点
            canvas.drawCircle(dataX, dataY, mCircleRadius, mLinePaint);
            //4.点连线
            if (previousDataX != 0) {
                canvas.drawLine(previousDataX, previousDataY, dataX, dataY, mLinePaint);
            }
            previousDataX = dataX;
            previousDataY = dataY;
            //5.画点上的值
            mPaint.getTextBounds(mChartDatas.get(i).getValue(), 0, mChartDatas.get(i).getValue().length(), mTextBounds);
            textWidth = mTextBounds.width();
            textHeight = mTextBounds.height();
            // 圆点x位置-文本一半
            float textLeft = dataX - textWidth / 2;
            float textTop = dataY - mCircleRadius - textHeight / 2;
            canvas.drawText(mChartDatas.get(i).getValue(), textLeft, textTop + dy, mPaint);
        }
        //2.3x轴箭头
        //x轴箭头left = x轴right+箭头间距
        float xArrowLeft = yLeft + (mXFontHorizontalSpace * 2 + textWidth) * mChartDatas.size() + mArrowSpace;
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

    public void setXMark(String[] xMark) {
        this.mXMark = xMark;
    }
}
