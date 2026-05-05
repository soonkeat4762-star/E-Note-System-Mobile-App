package com.android.noteapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;

public class DrawingView extends View {

    private Paint paint;
    private Path path;
    private final Canvas canvas;
    private final Bitmap bitmap;
    private final int backgroundColor;
    private final Stack<DrawPath> paths = new Stack<>();
    private final Stack<DrawPath> undonePaths = new Stack<>();

    private BrushType currentBrushType = BrushType.PENCIL;

    public enum BrushType {
        PENCIL, PEN, BRUSH, PAINT, ERASER, HIGHLIGHTER
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = createNewPaint();
        path = new Path();

        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        // 获取渐变背景的结束颜色
        Drawable backgroundDrawable = getBackground();
        if (backgroundDrawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) backgroundDrawable;
            ColorStateList colorStateList = gradientDrawable.getColor();
            if (colorStateList != null) {
                backgroundColor = colorStateList.getDefaultColor();
            } else {
                backgroundColor = Color.WHITE; // 如果无法获取颜色，请设置为默认值（白色）
            }
        } else {
            backgroundColor = Color.WHITE; // 如果背景不是渐变，将颜色设置为默认值（白色）
        }
    }

    // Create new paint with default settings
    private Paint createNewPaint() {
        Paint newPaint = new Paint();
        newPaint.setColor(Color.BLACK);
        newPaint.setStrokeWidth(10);
        newPaint.setStyle(Paint.Style.STROKE);
        newPaint.setStrokeJoin(Paint.Join.ROUND);
        newPaint.setStrokeCap(Paint.Cap.ROUND);
        newPaint.setAntiAlias(true);
        return newPaint;
    }

    public Bitmap getBitmap() {
        // 创建一个与视图尺寸相同的空白位图
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        // 使用视图的绘制方法将内容绘制到新创建的位图上
        draw(new Canvas(bitmap));
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
        for (DrawPath drawPath : paths) {
            canvas.drawPath(drawPath.path, drawPath.paint);
        }
        // Draw the current path
        if (path != null) {
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(x, y);
                invalidate(); // Invalidate view to call onDraw method
                break;

            case MotionEvent.ACTION_UP:
                paths.push(new DrawPath(path, paint)); // Save the drawn path
                path = new Path(); // Create a new path for the next stroke
                paint = createNewPaint(); // Create a new paint for the next stroke
                setBrushType(currentBrushType); // Set type of the new paint
                performClick();
                break;

        }

        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public void setBrushSize(float size) {
        paint.setStrokeWidth(size);
    }

    public int getBrushColor() {
        return paint.getColor();
    }

    public void setBrushColor(int color) {
        if (currentBrushType == BrushType.HIGHLIGHTER) {
            // 如果当前画笔类型是highlighter，那么保持半透明
            color = Color.argb(128, Color.red(color), Color.green(color), Color.blue(color));
        }
        paint.setColor(color);
    }

    public void setBrushType(BrushType brushType) {
        currentBrushType = brushType;
        switch (brushType) {
            case PENCIL:
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(10);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setAntiAlias(true);
                paint.setDither(true);
                break;
            case PEN:
                paint.setColor(Color.parseColor("#1434A4"));
                paint.setStrokeWidth(15);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setAntiAlias(true);
                paint.setDither(true);
                break;

            case HIGHLIGHTER:
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(30);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setAntiAlias(true);
                paint.setDither(true);
                paint.setAlpha(128); // 设置半透明效果
                break;

            case BRUSH:
                paint.setColor(Color.GRAY);
                paint.setStrokeWidth(20);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setAntiAlias(true);
                paint.setDither(true);
                break;
            case PAINT:
                paint.setColor(Color.parseColor("#FF89CFF0"));
                paint.setStrokeWidth(30);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setAntiAlias(true);
                paint.setDither(true);
                break;

            case ERASER:
                paint.setColor(backgroundColor);
                paint.setStrokeWidth(40);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setAntiAlias(true);
                paint.setDither(true);
                break;
            default:
                break;
        }

    }

    public void undo() {
        if (!paths.isEmpty()) {
            undonePaths.push(paths.pop());
            redrawCanvas();
        }
    }

    public void redo() {
        if (!undonePaths.isEmpty()) {
            paths.push(undonePaths.pop());
            redrawCanvas();
        }
    }

    public void clear() {
        paths.clear();
        undonePaths.clear();
        redrawCanvas();
    }

    private void redrawCanvas() {
        bitmap.eraseColor(Color.TRANSPARENT);
        for (DrawPath drawPath : paths) {
            canvas.drawPath(drawPath.path, drawPath.paint);
        }
        invalidate();
    }

    private static class DrawPath {
        Path path;
        Paint paint;

        public DrawPath(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
    }
}
