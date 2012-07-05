

uniform vec3 lpos;
uniform sampler2D shadowtex;

varying vec4 projShadow;
varying vec3 normal;
varying vec3 lightvec;


void main() {
  // project shadow
  vec4 texcoord = ((gl_TexCoord[0] / gl_TexCoord[0].w) + 1.0) * 0.5;
  vec4 tex = texture2D(shadowtex, texcoord.xy);
  float shade = tex.z;
  
  // set final color
  gl_FragColor = vec4(0.2, 0.3, 0.5, 1.0) * shade;
}

