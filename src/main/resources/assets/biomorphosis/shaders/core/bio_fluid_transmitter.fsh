#version 430

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float GameTime;
uniform int TransportCount;

layout(std430, binding = 3) readonly buffer TransportBuffer
{
    vec4 Transports[];
};

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

const float FLUID_INFLUENCE = 0.045;

void main()
{
    vec4 textureColor = texture(Sampler0, texCoord0);
    float pulse = 0.94 + sin((texCoord0.x + texCoord0.y + GameTime) * 6.2831853) * 0.06;
    vec3 finalRgb = vertexColor.rgb;
    float finalAlpha = vertexColor.a;

    if (TransportCount > 0)
    {
        float strongestInfluence = 0.0;
        vec3 transportColor = vec3(1.0);
        for (int q = 0; q < TransportCount; q++)
        {
            vec4 transport = Transports[q];
            float distance = abs(texCoord0.x - transport.x);
            if (distance > FLUID_INFLUENCE)
                continue;

            float influence = 1.0 - distance / FLUID_INFLUENCE;
            influence = influence * influence * (3.0 - 2.0 * influence);
            if (influence > strongestInfluence)
            {
                strongestInfluence = influence;
                transportColor = transport.yzw;
            }
        }

        if (strongestInfluence <= 0.01)
            discard;

        finalRgb = transportColor;
        finalAlpha *= strongestInfluence;
    }

    vec4 color = vec4(textureColor.rgb * finalRgb * pulse, textureColor.a * finalAlpha) * ColorModulator;

    if (color.a < 0.01)
        discard;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
