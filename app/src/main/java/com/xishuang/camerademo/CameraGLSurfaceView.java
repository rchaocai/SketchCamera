package com.xishuang.camerademo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Author:xishuang
 * Date:2017.10.26
 * Des:自定义用于渲染摄像头数据
 */
public class CameraGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    public static final String TAG = "CameraGLSurfaceView";
    private Context mContext;
    private int mTextureID;
    private SurfaceTexture mSurface;
    private DirectDrawer mDirectDrawer;

    public CameraGLSurfaceView(Context context) {
        super(context);
        mContext = context;
        // 创建一个2.0的context
        setEGLContextClientVersion(2);
        // 设置渲染器来在GLSurfaceView中进行绘制
        setRenderer(this);
        // 只有在用户需要进行绘制时，才会进行真正的重新渲染，配合GLSurfaceView.requestRender()使用
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    /**
     * 纹理配置
     */
    private int createTextureID() {
        int[] texture = new int[1];
        int[] fbo = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        GLES20.glGenBuffers(1, fbo, 0);
        GLES20.glBindBuffer(GLES20.GL_FRAMEBUFFER, fbo[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture[0], 0);

        return texture[0];
    }

    /**
     * CameraGLSurfaceView创建只调用一次，用来创建环境
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mTextureID = createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        mDirectDrawer = new DirectDrawer(mContext, mTextureID);
    }

    /**
     * 当物理环境发生改变的时候会进行调用，比如屏幕方向发生改变
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        openCamera();
    }

    /**
     * 每次重绘都会进行调用
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurface.updateTexImage();
        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);
        mDirectDrawer.draw();
    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.i(TAG, "onFrameAvailable...");
        //进行重绘
        this.requestRender();
    }

    @Override
    public void onPause() {
        super.onPause();
        closeCamera();
    }

    private Camera mCamera;

    /**
     * 打开摄像头
     */
    private void openCamera() {
        try {
            mCamera = getCameraInstance();
            mCamera.setPreviewTexture(mSurface);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭摄像头
     */
    private void closeCamera() {
        try {
            mCamera.stopPreview();
            mCamera.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void readPixe(ImageView imageView) {
//        IntBuffer RGBABuffer = IntBuffer.allocate(512 * 512);
//        RGBABuffer.position(0);
//        GLES20.glReadPixels(0, 0, 512, 512, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, RGBABuffer);
//
//        int[] modelData = RGBABuffer.array();
//        int[] ArData = new int[modelData.length];
//        int offset1, offset2;
//        for (int i = 0; i < 512; i++) {
//            offset1 = i * 512;
//            offset2 = (512 - i - 1) * 512;
//            for (int j = 0; j < 512; j++) {
//                int texturePixel = modelData[offset1 + j];
//                int blue = (texturePixel >> 16) & 0xff;
//                int red = (texturePixel << 16) & 0x00ff0000;
//                int pixel = (texturePixel & 0xff00ff00) | red | blue;
//                ArData[offset2 + j] = pixel;
//            }
//        }
//        Bitmap modelBitmap = Bitmap.createBitmap(ArData, 512, 512, Bitmap.Config.ARGB_8888);
//        saveBitmap(modelBitmap);

        int width = getWidth();
        int height = getHeight();
        IntBuffer PixelBuffer = IntBuffer.allocate(width * height);
        PixelBuffer.position(0);
        GLES20.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, PixelBuffer);


        PixelBuffer.position(0);//这里要把读写位置重置下
        int pix[] = new int[width * height];
        PixelBuffer.get(pix);//这是将intbuffer中的数据赋值到pix数组中

        Bitmap bmp = Bitmap.createBitmap(pix, width, height, Bitmap.Config.ARGB_8888);//pix是上面读到的像素
        imageView.setImageBitmap(bmp);

    }
}
