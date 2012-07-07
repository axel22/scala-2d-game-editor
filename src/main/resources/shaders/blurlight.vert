

uniform sampler2D litetex;


void main() {
  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
  
  vec4 pos = gl_ModelViewProjectionMatrix * gl_Vertex;
  gl_TexCoord[0] = pos;
}
