package media.musicplayer.songs.mp3player.audio.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import media.musicplayer.songs.mp3player.audio.R;
import media.musicplayer.songs.mp3player.audio.model.EqualizerInfo;
import media.musicplayer.songs.mp3player.audio.model.UpdateEQ;
import media.musicplayer.songs.mp3player.audio.ui.MusicPlayerActivity;

/**
 * Created by SF on 15/04/2016.
 */
public class EqualizerView extends View {
    int widthScreen;
    Context mContext;
    PointF sizeView = new PointF();
    EqualizerInfo equalizerInfo;
    Bitmap thumbDrawable;

    public EqualizerView(Context context) {
        super(context);
        init(context);
    }

    public Bitmap getThumbDrawable() {
        return thumbDrawable;
    }

    public void setThumbDrawable(Bitmap thumbDrawable) {
        this.thumbDrawable = thumbDrawable;
        invalidate();
    }

    public float LIMIT_TOP, LIMIT_BOT;
    int radiusBlack, radiusWhite;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        sizeView.x = w;
        sizeView.y = h;
        LIMIT_TOP = h / 6;
        LIMIT_BOT = 5 * h / 6;
        equalizerInfo.setWidthView(w);
        equalizerInfo.setHeightView(h);
        equalizerInfo.calculatorPoint();

    }

    Paint paint = new Paint();

    public void init(Context mContext) {
        this.mContext = mContext;
        equalizerInfo = new EqualizerInfo();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GRAY);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setDither(true);
        radiusWhite = mContext.getResources().getInteger(R.integer.size_black);
        paint.setStrokeWidth(radiusWhite / 2f);

    }

    public EqualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EqualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    CornerPathEffect cornerPathEffect = new CornerPathEffect(100);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (sizeView.x != 0) {
                paint.setStyle(Paint.Style.STROKE);
//            paint.setColor(Color.GRAY);
                paint.setPathEffect(cornerPathEffect);
                canvas.drawPath(equalizerInfo.genPath(), paint);
                paint.setPathEffect(null);
                for (int i = 0; i < equalizerInfo.getPointFArrayList().size(); i++) {
                    if (i % 2 != 0) {
                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor(Color.WHITE);
                        canvas.drawCircle(equalizerInfo.getPointFArrayList().get(i).x, equalizerInfo.getPointFArrayList().get(i).y, radiusWhite, paint);
                        paint.setColor(Color.BLACK);
                        canvas.drawCircle(equalizerInfo.getPointFArrayList().get(i).x, equalizerInfo.getPointFArrayList().get(i).y, 5, paint);
                        paint.setColor(Color.GRAY);
                    }
                }

            }
        } catch (Exception e) {

        }
    }

    float firstX, firstY;
    float lastX, lastY;
    int index = -1;
    Matrix matrix = new Matrix();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    firstX = event.getX();
                    firstY = event.getY();
                    index = -1;
                    for (int i = 0; i < equalizerInfo.getRectFArrayList().size(); i++) {
                        if (equalizerInfo.getRectFArrayList().get(i).contains(firstX, firstY)) {
                            if (i % 2 != 0) index = i;
                            break;
                        }
                    }
                    matrix.reset();
                    paint.setColor(Color.WHITE);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    lastY = event.getY();
                    paint.setColor(Color.WHITE);
                    if (index != -1) {
                        float deltaY = lastY - firstY;
                        float totalValue = equalizerInfo.getPointFArrayList().get(index).y + deltaY;
                        if (totalValue <= LIMIT_TOP) {
                            totalValue = LIMIT_TOP;
                            deltaY = 0;
                        }
                        if (totalValue >= LIMIT_BOT) {
                            totalValue = LIMIT_BOT;
                            deltaY = 0;
                        }
                        matrix.setTranslate(0, deltaY);
                        equalizerInfo.getPointFArrayList().get(index).y += deltaY;
                        updateEQ(index, equalizerInfo.convertDrawToBand(equalizerInfo.getPointFArrayList().get(index).y));
                        equalizerInfo.updateBandValue(index);
                        matrix.mapRect(equalizerInfo.getRectFArrayList().get(index), equalizerInfo.getRectFArrayList().get(index));
                    }
                    firstY = lastY;
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    paint.setColor(Color.GRAY);
                    updateEQPref();
                    invalidate();
                    break;
            }
        } catch (Exception e) {

        }
        return true;
    }

    public void updateView(EqualizerInfo equalizerInfo) {
        this.equalizerInfo.importAnotherEqualizer(equalizerInfo);
        applyCurrentEqualizer(equalizerInfo);
        updateEQPref();
        invalidate();
    }

    public void updateEQPref() {
        EventBus.getDefault().post(new UpdateEQ());
    }

    public EqualizerInfo getEqualizerInfo() {
        return equalizerInfo;
    }

    public void setEqualizerInfo(EqualizerInfo equalizerInfo) {
        equalizerInfo.setWidthView(this.equalizerInfo.getWidthView());
        equalizerInfo.setHeightView(this.equalizerInfo.getHeightView());
        this.equalizerInfo = equalizerInfo;
        updateView(equalizerInfo);
    }

    public void applyCurrentEqualizer(EqualizerInfo equalizerInfo) {
        try {

            if (MusicPlayerActivity.musicSrv.getEqualizerHelper() == null) return;

            updateEQ(1, Math.round(equalizerInfo.getBand1Value()));
            updateEQ(3, Math.round(equalizerInfo.getBand2Value()));
            updateEQ(5, Math.round(equalizerInfo.getBand3Value()));
            updateEQ(7, Math.round(equalizerInfo.getBand4Value()));
            updateEQ(9, Math.round(equalizerInfo.getBand5Value()));
        } catch (Exception e) {

        }
    }

    public void updateEQ(int index, int seekBarLevel) {
        try {

            if (MusicPlayerActivity.musicSrv.getEqualizerHelper() == null) return;
            if (!MusicPlayerActivity.musicSrv.getEqualizerHelper().isEnable()) return;
            short sixtyHertzBand = MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentEqualizer().getBand(50000);

            switch (index) {
                case 1:
                    sixtyHertzBand = MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentEqualizer().getBand(50000);
                    break;
                case 3:
                    sixtyHertzBand = MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentEqualizer().getBand(130000);
                    break;
                case 5:
                    sixtyHertzBand = MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentEqualizer().getBand(320000);
                    break;
                case 7:
                    sixtyHertzBand = MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentEqualizer().getBand(800000);
                    break;
                case 9:
                    sixtyHertzBand = MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentEqualizer().getBand(2000000);
                    break;
            }
            //Set the gain level text based on the slider position.
            if (seekBarLevel == 16) {
                MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentEqualizer().setBandLevel(sixtyHertzBand, (short) 0);
            } else if (seekBarLevel < 16) {

                if (seekBarLevel == 0) {
                    MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentEqualizer().setBandLevel(sixtyHertzBand, (short) (-1500));
                } else {
                    MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentEqualizer().setBandLevel(sixtyHertzBand, (short) -((16 - seekBarLevel) * 100));
                }

            } else if (seekBarLevel > 16) {
                MusicPlayerActivity.musicSrv.getEqualizerHelper().getCurrentEqualizer().setBandLevel(sixtyHertzBand, (short) ((seekBarLevel - 16) * 100));
            }
        } catch (Exception e) {
            Log.e("Exception", " " + e.getMessage());
        }
    }
}
