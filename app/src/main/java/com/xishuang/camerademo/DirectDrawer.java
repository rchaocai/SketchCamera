package com.xishuang.camerademo;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Author:xishuang
 * Date:2017.10.26
 * Des:绘制类
 */
public class DirectDrawer {

    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mTextureCoordHandle;

    /**
     * 顶点坐标的绘制顺序
     */
    private short drawOrder[] = {0, 1, 2, 0, 2, 3};

    // 每个顶点坐标需要两个数表示
    private static final int COORDS_PER_VERTEX = 2;

    /**
     * 获取坐标值的跨度，其中每个数占用4个字节（float）
     */
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    /**
     * 顶点坐标
     */
    private static float squareCoords[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f,
    };

    /**
     * 纹理坐标
     */
    private static float textureVertices[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };

    private int texture;

    public DirectDrawer(Context context, int texture) {
        this.texture = texture;
        //初始化需要传入着色器中的顶点坐标缓存数据
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);
        //初始化需要传入着色器中的顶点绘制顺序缓存数据
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        //初始化需要传入着色器中的纹理坐标缓存数据
        ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        textureVerticesBuffer = bb2.asFloatBuffer();
        textureVerticesBuffer.put(textureVertices);
        textureVerticesBuffer.position(0);
        //从文件中加载和编译顶点着色器
        String vertex = LoadShaderUtil.readShaderFromRawResource(context, R.raw.vertexshader);
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertex);
        //从文件中加载和编译片元着色器
        //String fra = LoadShaderUtil.readShaderFromRawResource(context, R.raw.fragmentsketchshader);
        String fra = LoadShaderUtil.readShaderFromRawResource(context, R.raw.fragmentcameoshader);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fra);

        mProgram = GLES20.glCreateProgram();             // 创建program
        GLES20.glAttachShader(mProgram, vertexShader);   // 绑定顶点着色器shader到program
        GLES20.glAttachShader(mProgram, fragmentShader); // 绑定片元着色器shader到program
        GLES20.glLinkProgram(mProgram);                  // 链接program
    }

    public void draw() {
        // 使用program
        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);

        // 获取顶点着色器中的顶点引用对象
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // 启用顶点引用对象
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        // 顶点缓存数据绑定到顶点引用对象
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // 获取顶点着色器中的纹理坐标引用对象
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        // 纹理坐标引用对象
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        // 纹理坐标的缓存数据绑定到纹理坐标引用对象
        GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureVerticesBuffer);

        // 正式渲染
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        // 禁用
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
    }

    private int loadShader(int type, String shaderCode) {

        // 根据着色器得类型创建着色器对象
        // GLES20.GL_VERTEX_SHADER 顶点着色器
        // GLES20.GL_FRAGMENT_SHADER 片元着色器
        int shader = GLES20.glCreateShader(type);
        // 着色器源码加载和编译
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
