

uniform sampler2D shadowtex;
uniform vec3 light_color;
uniform float fogstrength;
uniform float fogheight;
uniform int frame;
varying float height;
const float blursize = 1.0 / 2048.0;


float centerDistance(vec3 texcenter) {
  return texture2D(shadowtex, texcenter.xy).z;
}


float isShadow(float diff) {
  float shadowed = diff > 0.0001 ? 0.0 : 1.0;
  return shadowed;
}


float exactShadow(vec3 texcoord) {
  float distance = centerDistance(texcoord);
  float diff = texcoord.z - distance;
  return isShadow(diff);
}


float blurredShadow(vec3 texcenter) {
  float shadow = 0.0;

  shadow += exactShadow(vec3(texcenter.x - 4.0 * blursize, texcenter.y, texcenter.z)) * 0.05;
  shadow += exactShadow(vec3(texcenter.x - 3.0 * blursize, texcenter.y, texcenter.z)) * 0.09;
  shadow += exactShadow(vec3(texcenter.x - 2.0 * blursize, texcenter.y, texcenter.z)) * 0.12;
  shadow += exactShadow(vec3(texcenter.x - 1.0 * blursize, texcenter.y, texcenter.z)) * 0.15;
  shadow += exactShadow(vec3(texcenter.x + 0.0 * blursize, texcenter.y, texcenter.z)) * 0.16;
  shadow += exactShadow(vec3(texcenter.x + 1.0 * blursize, texcenter.y, texcenter.z)) * 0.15;
  shadow += exactShadow(vec3(texcenter.x + 2.0 * blursize, texcenter.y, texcenter.z)) * 0.12;
  shadow += exactShadow(vec3(texcenter.x + 3.0 * blursize, texcenter.y, texcenter.z)) * 0.09;
  shadow += exactShadow(vec3(texcenter.x + 4.0 * blursize, texcenter.y, texcenter.z)) * 0.05;

  shadow += exactShadow(vec3(texcenter.x, texcenter.y - 4.0 * blursize, texcenter.z)) * 0.05;
  shadow += exactShadow(vec3(texcenter.x, texcenter.y - 3.0 * blursize, texcenter.z)) * 0.09;
  shadow += exactShadow(vec3(texcenter.x, texcenter.y - 2.0 * blursize, texcenter.z)) * 0.12;
  shadow += exactShadow(vec3(texcenter.x, texcenter.y - 1.0 * blursize, texcenter.z)) * 0.15;
  shadow += exactShadow(vec3(texcenter.x, texcenter.y + 0.0 * blursize, texcenter.z)) * 0.16;
  shadow += exactShadow(vec3(texcenter.x, texcenter.y + 1.0 * blursize, texcenter.z)) * 0.15;
  shadow += exactShadow(vec3(texcenter.x, texcenter.y + 2.0 * blursize, texcenter.z)) * 0.12;
  shadow += exactShadow(vec3(texcenter.x, texcenter.y + 3.0 * blursize, texcenter.z)) * 0.09;
  shadow += exactShadow(vec3(texcenter.x, texcenter.y + 4.0 * blursize, texcenter.z)) * 0.05;

  return shadow / 2.0;
}


void main() {
  // project shadow
  vec4 texcoord = (gl_TexCoord[0] / gl_TexCoord[0].w) * 0.5 + 0.5;
  float shadowed = blurredShadow(texcoord.xyz);

  // compute colors and alpha
  // light influence
  vec3 color = light_color * shadowed;
  float alpha = (1.0 - shadowed) * 0.7;

  // fog influence
  float fogamount = (1.0 - clamp(height, 0.0, fogheight) / fogheight) * fogstrength;
  float fogshadowinteraction = (max(shadowed, fogamount * 6.0 - 5.0) / 1.5 + 0.6) * (0.9 + shadowed * 0.2);
  vec3 fogcolor = light_color * fogshadowinteraction;
  color = mix(color, fogcolor, fogamount);
  alpha = max(alpha, fogamount);

  // set final color
  gl_FragColor = vec4(color, alpha);
}





