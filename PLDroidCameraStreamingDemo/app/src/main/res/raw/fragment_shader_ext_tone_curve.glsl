#extension GL_OES_EGL_image_external : require
precision mediump float;

varying vec2 vTextureCoord;
uniform samplerExternalOES uTexture;
uniform sampler2D toneCurveTexture;

void main() {
    vec4 textureColor = texture2D(uTexture, vTextureCoord);

    float redCurveValue = texture2D(toneCurveTexture, vec2(textureColor.r, 0.0)).r;
    float greenCurveValue = texture2D(toneCurveTexture, vec2(textureColor.g, 0.0)).g;
    float blueCurveValue = texture2D(toneCurveTexture, vec2(textureColor.b, 0.0)).b;

    gl_FragColor = vec4(redCurveValue, greenCurveValue, blueCurveValue, textureColor.a);
}