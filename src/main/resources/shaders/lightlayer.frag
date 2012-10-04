

uniform sampler2D litetex;


const float blursize = 1.0 / 1024.0;


vec4 exact(vec4 texcenter) {
  return texture2D(litetex, texcenter.xy);
}


vec4 blurred(vec4 texcenter) {
  vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
  
  color += texture2D(litetex, vec2(texcenter.x - 10.0 * blursize, texcenter.y)) * 0.03;
  color += texture2D(litetex, vec2(texcenter.x - 8.0 * blursize, texcenter.y)) * 0.05;
  color += texture2D(litetex, vec2(texcenter.x - 6.0 * blursize, texcenter.y)) * 0.08;
  color += texture2D(litetex, vec2(texcenter.x - 4.0 * blursize, texcenter.y)) * 0.11;
  color += texture2D(litetex, vec2(texcenter.x - 2.0 * blursize, texcenter.y)) * 0.14;
  color += texture2D(litetex, vec2(texcenter.x + 0.0 * blursize, texcenter.y)) * 0.16;
  color += texture2D(litetex, vec2(texcenter.x + 2.0 * blursize, texcenter.y)) * 0.14;
  color += texture2D(litetex, vec2(texcenter.x + 4.0 * blursize, texcenter.y)) * 0.11;
  color += texture2D(litetex, vec2(texcenter.x + 6.0 * blursize, texcenter.y)) * 0.08;
  color += texture2D(litetex, vec2(texcenter.x + 8.0 * blursize, texcenter.y)) * 0.05;
  color += texture2D(litetex, vec2(texcenter.x + 10.0 * blursize, texcenter.y)) * 0.03;
  
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y - 10.0 * blursize)) * 0.03;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y - 8.0 * blursize)) * 0.05;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y - 6.0 * blursize)) * 0.08;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y - 4.0 * blursize)) * 0.11;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y - 2.0 * blursize)) * 0.14;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 0.0 * blursize)) * 0.16;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 2.0 * blursize)) * 0.14;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 4.0 * blursize)) * 0.11;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 6.0 * blursize)) * 0.08;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 8.0 * blursize)) * 0.05;
  color += texture2D(litetex, vec2(texcenter.x, texcenter.y + 10.0 * blursize)) * 0.03;
  
  return color / 2.0;
}


void main() {
  vec4 texcoord = gl_TexCoord[0] / gl_TexCoord[0].w * 0.5 + 0.5;
  vec4 color = blurred(texcoord);
  
  gl_FragColor = vec4(color.rgb, color.a);
}
