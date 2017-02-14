package media.musicplayer.songs.mp3player.audio.model;

import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Duc-Nguyen on 4/17/2016.
 */
public class EqualizerInfo {
    transient PointF startPointF;
    transient PointF endPointF;
    transient PointF band1PointF;
    transient PointF mid12PointF;
    transient PointF band2PointF;
    transient PointF mid23PointF;
    transient PointF band3PointF;
    transient PointF mid34PointF;
    transient PointF band4PointF;
    transient PointF mid45PointF;
    transient PointF band5PointF;
    float band1Value;
    float band2Value;
    float band3Value;
    float band4Value;
    float band5Value;
    String nameEqualizer;
    int bassValue;
    int virtualValue;

    transient RectF rectBand1;
    transient RectF rectBand2;
    transient RectF rectBand3;
    transient RectF rectBand4;
    transient RectF rectBand5;

    int widthView;
    int heightView;
    transient ArrayList<RectF> rectFArrayList = new ArrayList<>();
    transient ArrayList<PointF> pointFArrayList = new ArrayList<>();
    transient Path finalPath = new Path();

    public String getNameEqualizer() {
        return nameEqualizer;
    }

    public void setNameEqualizer(String nameEqualizer) {
        this.nameEqualizer = nameEqualizer;
    }

    public float getBand1Value() {
        return band1Value;
    }

    public void setBand1Value(float band1Value) {
        this.band1Value = band1Value;
    }

    public int getWidthView() {
        return widthView;
    }

    public void setWidthView(int widthView) {
        this.widthView = widthView;
    }

    public int getHeightView() {
        return heightView;
    }

    public void setHeightView(int heightView) {
        this.heightView = heightView;
    }

    public float getBand2Value() {
        return band2Value;
    }

    public void setBand2Value(float band2Value) {
        this.band2Value = band2Value;
    }

    public float getBand3Value() {
        return band3Value;
    }

    public void setBand3Value(float band3Value) {
        this.band3Value = band3Value;
    }

    public float getBand4Value() {
        return band4Value;
    }

    public void setBand4Value(float band4Value) {
        this.band4Value = band4Value;
    }

    public float getBand5Value() {
        return band5Value;
    }

    public void setBand5Value(float band5Value) {
        this.band5Value = band5Value;
    }

    public int getBassValue() {
        return bassValue;
    }

    public void setBassValue(int bassValue) {
        this.bassValue = bassValue;
    }

    public int getVirtualValue() {
        return virtualValue;
    }

    public void setVirtualValue(int virtualValue) {
        this.virtualValue = virtualValue;
    }

    public EqualizerInfo() {
        this.startPointF = new PointF();
        this.endPointF = new PointF();
        this.band1PointF = new PointF();
        this.mid12PointF = new PointF();
        this.band2PointF = new PointF();
        this.mid23PointF = new PointF();
        this.band3PointF = new PointF();
        this.mid34PointF = new PointF();
        this.band4PointF = new PointF();
        this.mid45PointF = new PointF();
        this.band5PointF = new PointF();
        this.rectBand1 = new RectF();
        this.rectBand2 = new RectF();
        this.rectBand3 = new RectF();
        this.rectBand4 = new RectF();
        this.rectBand5 = new RectF();
        this.band1Value = 16;
        this.band2Value = 16;
        this.band3Value = 16;
        this.band4Value = 16;
        this.band5Value = 16;
        this.bassValue = 0;
        this.virtualValue = 0;
        this.nameEqualizer = "";
    }

    public PointF getMid12PointF() {
        return mid12PointF;
    }

    public void setMid12PointF(PointF mid12PointF) {
        this.mid12PointF = mid12PointF;
    }

    public PointF getMid23PointF() {
        return mid23PointF;
    }

    public void setMid23PointF(PointF mid23PointF) {
        this.mid23PointF = mid23PointF;
    }

    public PointF getMid34PointF() {
        return mid34PointF;
    }

    public void setMid34PointF(PointF mid34PointF) {
        this.mid34PointF = mid34PointF;
    }

    public PointF getMid45PointF() {
        return mid45PointF;
    }

    public void setMid45PointF(PointF mid45PointF) {
        this.mid45PointF = mid45PointF;
    }

    public PointF getStartPointF() {
        return startPointF;
    }

    public void setStartPointF(PointF startPointF) {
        this.startPointF = startPointF;
    }

    public PointF getEndPointF() {
        return endPointF;
    }

    public void setEndPointF(PointF endPointF) {
        this.endPointF = endPointF;
    }

    public PointF getBand1PointF() {
        return band1PointF;
    }

    public void setBand1PointF(PointF band1PointF) {
        this.band1PointF = band1PointF;
    }

    public PointF getBand2PointF() {
        return band2PointF;
    }

    public void setBand2PointF(PointF band2PointF) {
        this.band2PointF = band2PointF;
    }

    public PointF getBand3PointF() {
        return band3PointF;
    }

    public void setBand3PointF(PointF band3PointF) {
        this.band3PointF = band3PointF;
    }

    public PointF getBand4PointF() {
        return band4PointF;
    }

    public void setBand4PointF(PointF band4PointF) {
        this.band4PointF = band4PointF;
    }

    public PointF getBand5PointF() {
        return band5PointF;
    }

    public void setBand5PointF(PointF band5PointF) {
        this.band5PointF = band5PointF;
    }

    int radius = 40;

    public void calculatorPoint() {
        startPointF.x = 0;
        startPointF.y = heightView / 2f;
        endPointF.x = widthView;
        endPointF.y = heightView / 2f;

        band1PointF.x = widthView / 10f;
        band2PointF.x = widthView / 5f + widthView / 10f;
        band3PointF.x = 2 * widthView / 5f + widthView / 10f;
        band4PointF.x = 3 * widthView / 5f + widthView / 10f;
        band5PointF.x = 4 * widthView / 5f + widthView / 10f;
        Log.e("band1Value: ", " " + band1Value + " " + band2Value + " " + band3Value + " " + band4Value + " " + band5Value);
        band1PointF.y = convertBandToDraw(this.band1Value);
        band2PointF.y = convertBandToDraw(this.band2Value);
        band3PointF.y = convertBandToDraw(this.band3Value);
        band4PointF.y = convertBandToDraw(this.band4Value);
        band5PointF.y = convertBandToDraw(this.band5Value);

        //calcul midpoint
        mid12PointF.x = widthView / 5f;
        mid23PointF.x = 2 * widthView / 5f;
        mid34PointF.x = 3 * widthView / 5f;
        mid45PointF.x = 4 * widthView / 5f;
        mid12PointF.y = heightView / 2f;
        mid23PointF.y = heightView / 2f;
        mid34PointF.y = heightView / 2f;
        mid45PointF.y = heightView / 2f;
        pointFArrayList.clear();
        pointFArrayList.add(startPointF);
        pointFArrayList.add(band1PointF);
        pointFArrayList.add(mid12PointF);
        pointFArrayList.add(band2PointF);
        pointFArrayList.add(mid23PointF);
        pointFArrayList.add(band3PointF);
        pointFArrayList.add(mid34PointF);
        pointFArrayList.add(band4PointF);
        pointFArrayList.add(mid45PointF);
        pointFArrayList.add(band5PointF);
        pointFArrayList.add(endPointF);


        rectBand1 = new RectF(band1PointF.x - radius, band1PointF.y - radius, band1PointF.x + radius, band1PointF.y + radius);
        rectBand2 = new RectF(band2PointF.x - radius, band2PointF.y - radius, band2PointF.x + radius, band2PointF.y + radius);
        rectBand3 = new RectF(band3PointF.x - radius, band3PointF.y - radius, band3PointF.x + radius, band3PointF.y + radius);
        rectBand4 = new RectF(band4PointF.x - radius, band4PointF.y - radius, band4PointF.x + radius, band4PointF.y + radius);
        rectBand5 = new RectF(band5PointF.x - radius, band5PointF.y - radius, band5PointF.x + radius, band5PointF.y + radius);
        rectFArrayList.clear();
        rectFArrayList.add(new RectF());
        rectFArrayList.add(rectBand1);
        rectFArrayList.add(new RectF());
        rectFArrayList.add(rectBand2);
        rectFArrayList.add(new RectF());
        rectFArrayList.add(rectBand3);
        rectFArrayList.add(new RectF());
        rectFArrayList.add(rectBand4);
        rectFArrayList.add(new RectF());
        rectFArrayList.add(rectBand5);
        rectFArrayList.add(new RectF());
    }

    public float convertBandToDraw(float bandValue) {
        //float bandDraw = 31 - bandValue;
        float result = ((31 - bandValue) * 4 * heightView / 6f) / 31f + heightView / 6f;
        return result;
    }

    public int convertDrawToBand(float drawValue) {
        float value = 31 - ((drawValue - heightView / 6f) * 31f) / (4 * heightView / 6f);
        return Math.round(value);
    }

    public ArrayList<RectF> getRectFArrayList() {
        return rectFArrayList;
    }

    public void setRectFArrayList(ArrayList<RectF> rectFArrayList) {
        this.rectFArrayList = rectFArrayList;
    }

    public ArrayList<PointF> getPointFArrayList() {
        return pointFArrayList;
    }

    public void setPointFArrayList(ArrayList<PointF> pointFArrayList) {
        this.pointFArrayList = pointFArrayList;
    }
    public Path genPath() {
        finalPath.reset();
        boolean first = true;
        for (int i = 0; i < pointFArrayList.size(); i += 1) {
            PointF point = pointFArrayList.get(i);
            if (first) {
                first = false;
                finalPath.moveTo(point.x, point.y);
                PointF next = pointFArrayList.get(i + 1);
                finalPath.quadTo(point.x, point.y, next.x, next.y);
            } else if (i < pointFArrayList.size() - 1) {
                PointF next = pointFArrayList.get(i + 1);
                finalPath.quadTo(point.x, point.y, next.x, next.y);
            } else {
                finalPath.lineTo(point.x, point.y);
            }
        }
        return finalPath;
    }

    public class PointF {
        public float x, y;
        float dx, dy;

        @Override
        public String toString() {
            return x + ", " + y;
        }
    }

    public void importAnotherEqualizer(EqualizerInfo equalizerInfo) {
        this.band1Value = equalizerInfo.band1Value;
        Log.e("importAnotherEqualizer ban1:", " " + this.band1Value);

        this.band2Value = equalizerInfo.band2Value;
        this.band3Value = equalizerInfo.band3Value;
        this.band4Value = equalizerInfo.band4Value;
        this.band5Value = equalizerInfo.band5Value;
        this.bassValue = equalizerInfo.bassValue;
        this.virtualValue = equalizerInfo.virtualValue;
        calculatorPoint();
    }

    public void updateBandValue(int index) {
        switch (index) {
            case 1:
                setBand1Value(Math.round(convertDrawToBand(getPointFArrayList().get(index).y)));
                Log.e("setBand1Value: ", " " + getBand1Value());
                break;
            case 3:
                setBand2Value(Math.round(convertDrawToBand(getPointFArrayList().get(index).y)));
                break;
            case 5:
                setBand3Value(Math.round(convertDrawToBand(getPointFArrayList().get(index).y)));
                break;
            case 7:
                setBand4Value(Math.round(convertDrawToBand(getPointFArrayList().get(index).y)));
                break;
            case 9:
                setBand5Value(Math.round(convertDrawToBand(getPointFArrayList().get(index).y)));
                break;
        }

    }
}
