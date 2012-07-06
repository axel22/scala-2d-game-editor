

uniform sampler2D shadowtex;

varying float polydirection;

const float maxd = 0.0002;
const float mind = 0.00001;


void main() {
  // project shadow
  vec4 texcoord = (gl_TexCoord[0] / gl_TexCoord[0].w) * 0.5 + 0.5;
  float distance = texture2D(shadowtex, texcoord.xy).z;
  float shadowed = 1.0;
  float diff = texcoord.z - distance;
  if (diff > maxd) shadowed = 0.0;
  if (diff < maxd && diff > mind) shadowed = 1.0 - (diff - mind) / (maxd - mind);
  
  // set final color
  gl_FragColor = vec4(vec3(0.6, 0.6, 0.6) * shadowed, 0.7);
}

