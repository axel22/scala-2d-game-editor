

uniform sampler2D shadowtex;
uniform vec3 campos;


void main() {
  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
  gl_TexCoord[0] = gl_TextureMatrix[0] * gl_Vertex;
}
