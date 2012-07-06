

uniform vec3 lpos;
uniform sampler2D shadowtex;

varying vec4 projShadow;
varying vec3 normal;
varying vec3 lightvec;


void main() {
  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
  
  mat4 camview =
    mat4(
	 vec4(-0.707, +0.707, +0.000, -2.622),
	 vec4(-0.353, -0.353, +0.866, +6.278),
	 vec4(+0.612, +0.612, +0.500, -174.1),
	 vec4(+0.000, +0.000, +0.000, +1.000)
	 );
  mat4 lightview = camview;
  mat4 camproj = 
    mat4(
	 vec4(-0.106, +0.000, +0.000, +0.000),
	 vec4(+0.000, +0.150, +0.000, +0.000),
	 vec4(+0.000, +0.000, -0.001, -0.500),
	 vec4(+0.000, +0.000, +0.000, +1.000)
	 );
  mat4 lightproj = 
    mat4(
	 vec4(-0.071, +0.000, +0.000, +0.000),
	 vec4(+0.000, +1.000, +0.000, +0.000),
	 vec4(+0.000, +0.000, -0.002, +0.000),
	 vec4(+0.000, +0.000, +0.000, +1.000)
	 );
  mat4 invcamview = 
    mat4(
	 vec4( -0.707,  -0.354,   0.613,  107.071),
	 vec4(0.707,  -0.354,   0.613, 110.780),
	 vec4(0.000,   0.866,   0.500,  81.551),
	 vec4(0.000,   0.000,   0.000,   1.000)
	 );
  mat4 texmat = lightproj * lightview;
  
  //gl_TexCoord[0] = texmat * gl_Vertex;
  gl_TexCoord[0] = gl_TextureMatrix[0] * gl_Vertex;
}
