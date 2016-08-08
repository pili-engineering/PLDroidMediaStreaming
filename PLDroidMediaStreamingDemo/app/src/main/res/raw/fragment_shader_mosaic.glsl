#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 vTextureCoord;
uniform samplerExternalOES uTexture;
uniform vec2 TexSize;
vec2 mosaicSize = vec2(2,2);

  void main()
  {
  vec2 intXY = vec2(vTextureCoord.x*TexSize.x, vTextureCoord.y*TexSize.y);
  vec2 XYMosaic = vec2(floor(intXY.x/mosaicSize.x)*mosaicSize.x,floor(intXY.y/mosaicSize.y)*mosaicSize.y);
  vec2 UVMosaic = vec2(XYMosaic.x/TexSize.x,XYMosaic.y/TexSize.y);
  vec4 baseMap = texture2D(uTexture,UVMosaic);
  gl_FragColor = baseMap;
  }