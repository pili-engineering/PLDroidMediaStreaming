package com.pili.pldroid.streaming.camera.demo.filter;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLES20;

import com.pili.pldroid.streaming.camera.demo.R;
import com.pili.pldroid.streaming.camera.demo.gles.GlUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by jerikc on 16/2/23.
 */
public class CameraFilterToneCurve extends CameraFilter {

    private final int mToneCurveTextureId;
    protected int muToneCurveTextureLoc;

    private PointF[] mRgbCompositeControlPoints;
    private PointF[] mRedControlPoints;
    private PointF[] mGreenControlPoints;
    private PointF[] mBlueControlPoints;

    private ArrayList<Float> mRgbCompositeCurve;
    private ArrayList<Float> mRedCurve;
    private ArrayList<Float> mGreenCurve;
    private ArrayList<Float> mBlueCurve;

    public CameraFilterToneCurve(Context context, InputStream inputStream) {
        super(context);
        setFromCurveFileInputStream(inputStream);
        setRgbCompositeControlPoints(mRgbCompositeControlPoints);
        setRedControlPoints(mRedControlPoints);
        setGreenControlPoints(mGreenControlPoints);
        setBlueControlPoints(mBlueControlPoints);

        mToneCurveTextureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D);
    }

    @Override protected int createProgram(Context applicationContext) {
        return GlUtil.createProgram(applicationContext, R.raw.vertex_shader,
                R.raw.fragment_shader_ext_tone_curve);
    }

    @Override protected void getGLSLValues() {
        super.getGLSLValues();

        muToneCurveTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "toneCurveTexture");
    }

    @Override protected void bindTexture(int textureId) {

        super.bindTexture(textureId);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mToneCurveTextureId);
        GLES20.glUniform1i(muToneCurveTextureLoc, 1);

        if ((mRedCurve.size() >= 256)
                && (mGreenCurve.size() >= 256)
                && (mBlueCurve.size() >= 256)
                && (mRgbCompositeCurve.size() >= 256)) {
            byte[] toneCurveByteArray = new byte[256 * 4];
            for (int currentCurveIndex = 0; currentCurveIndex < 256; currentCurveIndex++) {
                // BGRA for upload to texture
                toneCurveByteArray[currentCurveIndex * 4 + 2] = (byte) ((int) Math.min(Math.max(
                        currentCurveIndex
                                + mBlueCurve.get(currentCurveIndex)
                                + mRgbCompositeCurve.get(currentCurveIndex), 0), 255) & 0xff);
                toneCurveByteArray[currentCurveIndex * 4 + 1] = (byte) ((int) Math.min(Math.max(
                        currentCurveIndex
                                + mGreenCurve.get(currentCurveIndex)
                                + mRgbCompositeCurve.get(currentCurveIndex), 0), 255) & 0xff);
                toneCurveByteArray[currentCurveIndex * 4] = (byte) ((int) Math.min(Math.max(
                        currentCurveIndex
                                + mRedCurve.get(currentCurveIndex)
                                + mRgbCompositeCurve.get(currentCurveIndex), 0), 255) & 0xff);
                toneCurveByteArray[currentCurveIndex * 4 + 3] = (byte) (255 & 0xff);
            }

            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, 256 /*width*/,
                    1 /*height*/, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                    ByteBuffer.wrap(toneCurveByteArray));
        }
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex,
                                  int vertexStride, FloatBuffer texBuffer, int texStride) {
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride,
                texBuffer, texStride);
        //GLES20.glEnableVertexAttribArray(maExtraTextureCoordLoc);
        //GLES20.glVertexAttribPointer(maExtraTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride,
        //        texBuffer);
    }

    @Override protected void unbindGLSLValues() {
        super.unbindGLSLValues();
        //GLES20.glDisableVertexAttribArray(maExtraTextureCoordLoc);
    }

    @Override protected void unbindTexture() {
        super.unbindTexture();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    ///////////////////////

    private short readShort(InputStream input) throws IOException {
        return (short) (input.read() << 8 | input.read());
    }

    private void setFromCurveFileInputStream(InputStream input) {
        try {
            int version = readShort(input);
            int totalCurves = readShort(input);

            ArrayList<PointF[]> curves = new ArrayList<PointF[]>(totalCurves);
            float pointRate = 1.0f / 255;

            for (int i = 0; i < totalCurves; i++) {
                // 2 bytes, Count of points in the curve (short integer from 2...19)
                short pointCount = readShort(input);
                PointF[] points = new PointF[pointCount];
                // point count * 4
                // Curve points. Each curve point is a pair of short integers where
                // the first number is the output value (vertical coordinate on the
                // Curves dialog graph) and the second is the input value. All coordinates have range 0 to 255.
                for (int j = 0; j < pointCount; j++) {
                    short y = readShort(input);
                    short x = readShort(input);

                    points[j] = new PointF(x * pointRate, y * pointRate);
                }

                curves.add(points);
            }
            input.close();

            mRgbCompositeControlPoints = curves.get(0);
            mRedControlPoints = curves.get(1);
            mGreenControlPoints = curves.get(2);
            mBlueControlPoints = curves.get(3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRgbCompositeControlPoints(PointF[] points) {
        mRgbCompositeControlPoints = points;
        mRgbCompositeCurve = createSplineCurve(mRgbCompositeControlPoints);
        //updateToneCurveTexture();
    }

    public void setRedControlPoints(PointF[] points) {
        mRedControlPoints = points;
        mRedCurve = createSplineCurve(mRedControlPoints);
        //updateToneCurveTexture();
    }

    public void setGreenControlPoints(PointF[] points) {
        mGreenControlPoints = points;
        mGreenCurve = createSplineCurve(mGreenControlPoints);
        //updateToneCurveTexture();
    }

    public void setBlueControlPoints(PointF[] points) {
        mBlueControlPoints = points;
        mBlueCurve = createSplineCurve(mBlueControlPoints);
        //updateToneCurveTexture();
    }

    private ArrayList<Float> createSplineCurve(PointF[] points) {
        if (points == null || points.length <= 0) {
            return null;
        }

        // Sort the array
        PointF[] pointsSorted = points.clone();
        Arrays.sort(pointsSorted, new Comparator<PointF>() {
            @Override
            public int compare(PointF point1, PointF point2) {
                if (point1.x < point2.x) {
                    return -1;
                } else if (point1.x > point2.x) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        // Convert from (0, 1) to (0, 255).
        Point[] convertedPoints = new Point[pointsSorted.length];
        for (int i = 0; i < points.length; i++) {
            PointF point = pointsSorted[i];
            convertedPoints[i] = new Point((int) (point.x * 255), (int) (point.y * 255));
        }

        ArrayList<Point> splinePoints = createSplineCurve2(convertedPoints);

        // If we have a first point like (0.3, 0) we'll be missing some points at the beginning
        // that should be 0.
        Point firstSplinePoint = splinePoints.get(0);
        if (firstSplinePoint.x > 0) {
            for (int i = firstSplinePoint.x; i >= 0; i--) {
                splinePoints.add(0, new Point(i, 0));
            }
        }

        // Insert points similarly at the end, if necessary.
        Point lastSplinePoint = splinePoints.get(splinePoints.size() - 1);
        if (lastSplinePoint.x < 255) {
            for (int i = lastSplinePoint.x + 1; i <= 255; i++) {
                splinePoints.add(new Point(i, 255));
            }
        }

        // Prepare the spline points.
        ArrayList<Float> preparedSplinePoints = new ArrayList<Float>(splinePoints.size());
        for (Point newPoint : splinePoints) {
            Point origPoint = new Point(newPoint.x, newPoint.x);

            float distance = (float) Math.sqrt(
                    Math.pow((origPoint.x - newPoint.x), 2.0) + Math.pow((origPoint.y - newPoint.y),
                            2.0));

            if (origPoint.y > newPoint.y) {
                distance = -distance;
            }

            preparedSplinePoints.add(distance);
        }

        return preparedSplinePoints;
    }

    private ArrayList<Point> createSplineCurve2(Point[] points) {
        ArrayList<Double> sdA = createSecondDerivative(points);

        // Is [points count] equal to [sdA count]?
        //    int n = [points count];
        int n = sdA.size();
        if (n < 1) {
            return null;
        }
        double sd[] = new double[n];

        // From NSMutableArray to sd[n];
        for (int i = 0; i < n; i++) {
            sd[i] = sdA.get(i);
        }

        ArrayList<Point> output = new ArrayList<Point>(n + 1);

        for (int i = 0; i < n - 1; i++) {
            Point cur = points[i];
            Point next = points[i + 1];

            for (int x = cur.x; x < next.x; x++) {
                double t = (double) (x - cur.x) / (next.x - cur.x);

                double a = 1 - t;
                double b = t;
                double h = next.x - cur.x;

                double y = a * cur.y + b * next.y + (h * h / 6) * ((a * a * a - a) * sd[i]
                        + (b * b * b - b) * sd[i + 1]);

                if (y > 255.0) {
                    y = 255.0;
                } else if (y < 0.0) {
                    y = 0.0;
                }

                output.add(new Point(x, (int) Math.round(y)));
            }
        }

        // If the last point is (255, 255) it doesn't get added.
        if (output.size() == 255) {
            output.add(points[points.length - 1]);
        }
        return output;
    }

    private ArrayList<Double> createSecondDerivative(Point[] points) {
        int n = points.length;
        if (n <= 1) {
            return null;
        }

        double matrix[][] = new double[n][3];
        double result[] = new double[n];
        matrix[0][1] = 1;
        // What about matrix[0][1] and matrix[0][0]? Assuming 0 for now (Brad L.)
        matrix[0][0] = 0;
        matrix[0][2] = 0;

        for (int i = 1; i < n - 1; i++) {
            Point P1 = points[i - 1];
            Point P2 = points[i];
            Point P3 = points[i + 1];

            matrix[i][0] = (double) (P2.x - P1.x) / 6;
            matrix[i][1] = (double) (P3.x - P1.x) / 3;
            matrix[i][2] = (double) (P3.x - P2.x) / 6;
            result[i] =
                    (double) (P3.y - P2.y) / (P3.x - P2.x) - (double) (P2.y - P1.y) / (P2.x - P1.x);
        }

        // What about result[0] and result[n-1]? Assuming 0 for now (Brad L.)
        result[0] = 0;
        result[n - 1] = 0;

        matrix[n - 1][1] = 1;
        // What about matrix[n-1][0] and matrix[n-1][2]? For now, assuming they are 0 (Brad L.)
        matrix[n - 1][0] = 0;
        matrix[n - 1][2] = 0;

        // solving pass1 (up->down)
        for (int i = 1; i < n; i++) {
            double k = matrix[i][0] / matrix[i - 1][1];
            matrix[i][1] -= k * matrix[i - 1][2];
            matrix[i][0] = 0;
            result[i] -= k * result[i - 1];
        }
        // solving pass2 (down->up)
        for (int i = n - 2; i >= 0; i--) {
            double k = matrix[i][2] / matrix[i + 1][1];
            matrix[i][1] -= k * matrix[i + 1][0];
            matrix[i][2] = 0;
            result[i] -= k * result[i + 1];
        }

        ArrayList<Double> output = new ArrayList<Double>(n);
        for (int i = 0; i < n; i++) output.add(result[i] / matrix[i][1]);

        return output;
    }
}