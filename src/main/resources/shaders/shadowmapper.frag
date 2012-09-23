

uniform sampler2D shadowtex;
uniform vec3 light_color;


varying float polydirection;


float centerDistance(vec4 texcenter) {
  return texture2D(shadowtex, texcenter.xy).z;
}


float directShadow(float diff) {
  float shadowed = diff > 0.0001 ? 0.0 : 1.0;
  return shadowed;
}


void main() {
  // project shadow
  vec4 texcoord = (gl_TexCoord[0] / gl_TexCoord[0].w) * 0.5 + 0.5;
  float distance = centerDistance(texcoord);
  float diff = texcoord.z - distance;
  float shadowed = directShadow(diff);
  
  // set final color
  gl_FragColor = vec4(light_color * shadowed, 1.0);
}





