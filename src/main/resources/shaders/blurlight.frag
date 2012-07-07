

uniform sampler2D litetex;


const float blursize = 1.0 / 1024.0;


vec4 blurred(vec4 texcenter) {
  vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
  
  color += texture2D(litetex, vec2(texcenter.x - 4.0 * blursize, texcenter.y)) * 0.05;
  color += texture2D(litetex, vec2(texcenter.x - 3.0 * blursize, texcenter.y)) * 0.09;
  color += texture2D(litetex, vec2(texcenter.x - 2.0 * blursize, texcenter.y)) * 0.12;
  color += texture2D(litetex, vec2(texcenter.x - 1.0 * blursize, texcenter.y)) * 0.15;
  color += texture2D(litetex, vec2(texcenter.x + 0.0 * blursize, texcenter.y)) * 0.16;
  color += texture2D(litetex, vec2(texcenter.x + 1.0 * blursize, texcenter.y)) * 0.15;
  color += texture2D(litetex, vec2(texcenter.x + 2.0 * blursize, texcenter.y)) * 0.12;
  color += texture2D(litetex, vec2(texcenter.x + 3.0 * blursize, texcenter.y)) * 0.09;
  color += texture2D(litetex, vec2(texcenter.x + 4.0 * blursize, texcenter.y)) * 0.05;
  
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y - 4.0 * blursize)) * 0.05;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y - 3.0 * blursize)) * 0.09;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y - 2.0 * blursize)) * 0.12;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y - 1.0 * blursize)) * 0.15;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 0.0 * blursize)) * 0.16;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 1.0 * blursize)) * 0.15;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 2.0 * blursize)) * 0.12;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 3.0 * blursize)) * 0.09;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 4.0 * blursize)) * 0.05;
  
  return color / 2.0;
}


void main() {
  vec4 texcoord = gl_TexCoord[0] / gl_TexCoord[0].w * 0.5 + 0.5;
  vec4 color = blurred(texcoord);
  float intensity = (1.0 - (color.x + color.y + color.z) / 3.0);
  
  gl_FragColor = vec4(color.xyz, 0.4 * intensity);
}
