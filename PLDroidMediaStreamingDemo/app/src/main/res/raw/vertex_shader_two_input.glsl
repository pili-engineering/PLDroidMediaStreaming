uniform mat4 uMVPMatrix;

attribute vec4 aPosition;

attribute vec4 aTextureCoord;
attribute vec4 aExtraTextureCoord;

varying vec2 vTextureCoord;
varying vec2 vExtraTextureCoord;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vExtraTextureCoord = vec2(aExtraTextureCoord.x, 1.0 - aExtraTextureCoord.y);
    vTextureCoord = aTextureCoord.xy;
}