

uniform sampler2D shadowtex;

varying float polydirection;


void main() {
  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
  gl_TexCoord[0] = gl_TextureMatrix[0] * gl_Vertex;
}
