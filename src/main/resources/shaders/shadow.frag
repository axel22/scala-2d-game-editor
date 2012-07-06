

uniform sampler2D shadowtex;

varying float polydirection;

const float maxd = 0.0005;
const float mind = 0.00001;
const float blursize = 2.0 / 1024.0;


float averageDistance(vec4 texcenter) {
  float sum = 0.0;
  
  sum += texture2D(shadowtex, texcenter.xy + vec2(-4.0 * blursize, 0.0)).z * 0.06;
  sum += texture2D(shadowtex, texcenter.xy + vec2(-3.0 * blursize, 0.0)).z * 0.09;
  sum += texture2D(shadowtex, texcenter.xy + vec2(-2.0 * blursize, 0.0)).z * 0.12;
  sum += texture2D(shadowtex, texcenter.xy + vec2(-1.0 * blursize, 0.0)).z * 0.15;
  sum += texture2D(shadowtex, texcenter.xy + vec2(+0.0 * blursize, 0.0)).z * 0.16;
  sum += texture2D(shadowtex, texcenter.xy + vec2(+1.0 * blursize, 0.0)).z * 0.15;
  sum += texture2D(shadowtex, texcenter.xy + vec2(+2.0 * blursize, 0.0)).z * 0.12;
  sum += texture2D(shadowtex, texcenter.xy + vec2(+3.0 * blursize, 0.0)).z * 0.09;
  sum += texture2D(shadowtex, texcenter.xy + vec2(+4.0 * blursize, 0.0)).z * 0.06;
  sum += texture2D(shadowtex, texcenter.xy + vec2(0.0, -4.0 * blursize)).z * 0.06;
  sum += texture2D(shadowtex, texcenter.xy + vec2(0.0, -3.0 * blursize)).z * 0.09;
  sum += texture2D(shadowtex, texcenter.xy + vec2(0.0, -2.0 * blursize)).z * 0.12;
  sum += texture2D(shadowtex, texcenter.xy + vec2(0.0, -1.0 * blursize)).z * 0.15;
  sum += texture2D(shadowtex, texcenter.xy + vec2(0.0, +0.0 * blursize)).z * 0.16;
  sum += texture2D(shadowtex, texcenter.xy + vec2(0.0, +1.0 * blursize)).z * 0.15;
  sum += texture2D(shadowtex, texcenter.xy + vec2(0.0, +2.0 * blursize)).z * 0.12;
  sum += texture2D(shadowtex, texcenter.xy + vec2(0.0, +3.0 * blursize)).z * 0.09;
  sum += texture2D(shadowtex, texcenter.xy + vec2(0.0, +4.0 * blursize)).z * 0.06;
  
  return sum / 2.0;
}


float centerDistance(vec4 texcenter) {
  return texture2D(shadowtex, texcenter.xy).z;
}


float interpolateShadow(float diff) {
  float shadowed = 1.0;
  if (diff > maxd) shadowed = 0.0;
  if (diff < maxd && diff > mind) shadowed = 1.0 - (diff - mind) / (maxd - mind);
  return shadowed;
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
  gl_FragColor = vec4(vec3(0.6, 0.6, 0.6) * shadowed, 1.0);
}





