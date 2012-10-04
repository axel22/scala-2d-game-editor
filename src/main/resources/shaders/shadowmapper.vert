

uniform sampler2D shadowtex;
uniform vec3 campos;
varying float height;


void main() {
  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
  height = gl_Vertex.z;
  gl_TexCoord[0] = gl_TextureMatrix[0] * gl_Vertex;
}
