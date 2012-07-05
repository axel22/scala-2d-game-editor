


varying vec4 projShadow;

void main() {
  // project shadow
  vec3 color = vec3(0.2, 0.3, 0.5);
  color *= shadow2DProj(shadowMap, projShadow).r;
  
  // blur
  
  
  // set final color
  gl_FragColor = vec4(color, 1.0);
}
