uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying vec2 vTextureCoord;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord = aTextureCoord.xy;
}