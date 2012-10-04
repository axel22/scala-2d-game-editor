

uniform sampler2D shadowtex;
uniform vec3 light_color;
uniform float fogstrength;
uniform float fogheight;
varying float height;


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

  // compute colors and alpha
  // light influence
  vec3 color = light_color * shadowed;
  float alpha = (1.0 - shadowed) * 0.6;
  // fog influence
  float fogamount = (1.0 - clamp(height, 0.0, fogheight) / fogheight) * fogstrength;
  vec3 fogcolor = light_color * (shadowed / 2.0 + 0.7);
  color = mix(color, fogcolor, fogamount);
  alpha = max(alpha, fogamount);

  // set final color
  gl_FragColor = vec4(color, alpha);
}





