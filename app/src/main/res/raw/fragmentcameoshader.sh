//浮雕图像处理的渲染器
#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES s_texture;
const vec2 texSize = vec2(1920,1080);

void main() {
     vec2 tex = textureCoordinate;
     vec2 upLeftUV = vec2(tex.x - 1.0/texSize.x, tex.y - 1.0/texSize.y);
     vec4 curColor = texture2D(s_texture,textureCoordinate);
     vec4 upLeftColor = texture2D(s_texture,upLeftUV);
     vec4 delColor = curColor - upLeftColor;
     float h = 0.3*delColor.x + 0.59*delColor.y + 0.11*delColor.z;
     vec4 bkColor = vec4(0.5, 0.5, 0.5, 1.0);
     gl_FragColor = vec4(h,h,h,0.0) +bkColor;
  }