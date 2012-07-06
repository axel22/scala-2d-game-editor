

uniform vec3 lpos;
uniform sampler2D shadowtex;

varying vec4 projShadow;
varying vec3 normal;
varying vec3 lightvec;


void main() {
  // project shadow
  vec4 texcoord = (gl_TexCoord[0] / gl_TexCoord[0].w) * 0.5 + 0.5;
  float distance = texture2D(shadowtex, texcoord.xy).z;
  float shade = distance < texcoord.z ? 1.0 : 0.0;
  
  // set final color
  gl_FragColor = vec4(0.2, 0.3, 0.5, 1.0) * shade;
}

