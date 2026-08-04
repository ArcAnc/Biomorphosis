#version 430

#moj_import <fog.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

const float PI = 3.14159265;

void main()
{
    float horizontal = texCoord0.x * 2.0 - 1.0;
    float vertical = texCoord0.y * 2.0 - 1.0;
    float radialDistance = sqrt(horizontal * horizontal +
            (1.0 - horizontal * horizontal) * vertical * vertical);
    float body = 1.0 - smoothstep(0.72, 1.0, radialDistance);
    float rim = smoothstep(0.76, 0.98, radialDistance);

    float flow = sin(texCoord0.x * 9.0 - GameTime * 3.0 + texCoord0.y * 4.0);
    float filaments = pow(max(flow * 0.5 + 0.5, 0.0), 5.0) * body;
    float shimmer = sin((texCoord0.y * 7.0 - texCoord0.x * 5.0 + GameTime * 2.5) * PI) * 0.5 + 0.5;
    vec3 deepViolet = vec3(0.075, 0.008, 0.22);
    vec3 violet = vec3(0.30, 0.03, 0.56);
    vec3 fuchsia = vec3(0.55, 0.08, 0.53);
    vec3 lavender = vec3(0.36, 0.28, 0.62);
    vec3 color = mix(deepViolet, violet, body);
    color = mix(color, fuchsia, filaments * 0.20);
    color = mix(color, lavender, shimmer * filaments * 0.12);

    color += lavender * rim * 0.28;
    float alpha = body * (0.42 + filaments * 0.08) + rim * 0.18;
    vec4 result = vec4(color, alpha) * vertexColor * ColorModulator;

    if (result.a < 0.01)
        discard;

    fragColor = linear_fog(result, vertexDistance, FogStart, FogEnd, FogColor);
}
