

varying vec4 projShadow;


void main() {
  projShadow = gl_TextureMatrix[1] * gl_Vertex;
  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}


