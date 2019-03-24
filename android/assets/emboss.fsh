#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float resolution;

void main() {
	vec2 pos = gl_FragCoord.xy / resolution.xy;
    vec4 color = v_color * texture2D(u_texture, v_texCoords);
    color.r *= 1.0/dot(pos,pos);
    color.b *= 1.0/dot(pos,pos);
    color.g *= 1.0/dot(pos,pos);
    gl_FragColor = color;
}