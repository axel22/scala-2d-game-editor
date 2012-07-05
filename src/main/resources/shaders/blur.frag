

uniform vec3 lpos;
uniform sampler2D texunit;

varying vec4 projShadow;
varying vec3 normal;
varying vec3 lightvec;


void main() {
  // project shadow
  vec4 texCoord0 = ((gl_TexCoord[0] / gl_TexCoord[0].w) + 1.0) * 0.5;
  vec4 shadow = texture2D(texunit, texCoord0.xy);
  float shade = 1.0;
  if ((shadow.z + 0.005) < texCoord0.z) shade = 0.7;
  
  // blur
  
  
  // set final color
  gl_FragColor = vec4(0.2, 0.3, 0.5, 1.0) * shade;
}

