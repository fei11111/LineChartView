package com.fei.linechartview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

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

    private String[] mYMark = new String[]{
            "0.0", "4.0", "8.0", "12.0", "16.0", "20.0",
            "24.0", "28.0", "32.0", "36.0", "40.0", "44.0"
    };

    private String[] mXMark;

    private Paint mPaint;
    private Paint mLinePaint;

    //字体大小
    private float mFontSize = 23f;
    //线宽度
    private float mLineWidth = 5f;
    //线颜色
    private int mLineColor = Color.RED;

    private float mArrowHeight = 15f;//箭头高度
    private float mArrowSpace = 20f;//箭头间距
    private float mFontSpace = 20;//字体间距，x轴是字体上下间距，y轴是字体左右间距
    private float mPerY;//y轴每份的高度大小
    private float mPerX;//x轴每份的宽度大小

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
        mPaint.setColor(Color.BLACK);

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
        //字体间距
        mFontSpace = dp2px(mFontSpace);
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //mPerY = (总高度-padding-箭头间距-箭头高度)/Y刻度总数
        mPerY = (getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - mArrowSpace - mArrowHeight) / mYMark.length;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //1.画x轴

        //2.画y轴
        //3.画点
        //4.点连线
        //5.画点上的值
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
//    private void drawTriangle(float fromX, float fromY, float toX, float toY,
//                              float height, float bottom, Canvas canvas, Paint paint) {
//        // height和bottom分别为三角形的高与底的一半,调节三角形大小
//        float distance = (float) getPositionDistance(fromX, fromY, toX, toY);// 获取线段距离
//        float dx = toX - fromX;// 有正负，不要取绝对值
//        float dy = toY - fromY;// 有正负，不要取绝对值
//        float vX = toX - (height / distance * dx);
//        float vY = toY - (height / distance * dy);
//        //终点的箭头
//        Path path = new Path();
//        path.moveTo(toX, toY);// 此点为三边形的起点
//        path.lineTo(vX + (bottom / distance * dy), vY
//                - (bottom / distance * dx));
//        path.lineTo(vX - (bottom / distance * dy), vY
//                + (bottom / distance * dx));
//        path.close(); // 使这些点构成封闭的三边形
//        canvas.drawPath(path, paint);
//    }
//
//    /**
//     * 获取两点之间的距离
//     */
//    private double getPositionDistance(float startX, float startY, float endX, float endY) {
//        float dx = startX - endX;
//        float dy = startY - endY;
//        return Math.sqrt(dx * dx + dy * dy);
//    }


    public void setXMark(String[] xMark) {
        this.mXMark = xMark;
    }
}
