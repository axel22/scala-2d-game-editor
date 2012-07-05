

uniform vec3 lpos;
uniform sampler2D texunit;

varying vec4 projShadow;
varying vec3 normal;
varying vec3 lightvec;


void main() {
  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
  gl_TexCoord[0] = gl_TextureMatrix[0] * gl_Vertex;
}
