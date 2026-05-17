#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform float GameTime; // 0.0 ... 1.0
uniform float MsdfRange;
uniform float DeformationStrength;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

const float TAU = 6.28318530718;

float hash(vec2 p)
{
    return fract(sin(dot(p, vec2(127.1,311.7))) * 43758.5453123);
}

float noise(vec2 p)
{
    vec2 i = floor(p);
    vec2 f = fract(p);

    float a = hash(i);
    float b = hash(i + vec2(1.0,0.0));
    float c = hash(i + vec2(0.0,1.0));
    float d = hash(i + vec2(1.0,1.0));

    vec2 u = f*f*(3.0 - 2.0*f);

    return mix(a, b, u.x) +
    (c - a)*u.y*(1.0 - u.x) +
    (d - b)*u.x*u.y;
}

float median(float r, float g, float b)
{
    return max(min(r, g), min(max(r, g), b));
}

float screenPxRange(vec2 uv)
{
    vec2 textureSizePx = vec2(textureSize(Sampler0, 0));
    vec2 unitRange = vec2(MsdfRange) / textureSizePx;
    vec2 screenTexSize = vec2(1.0) / fwidth(uv);

    return max(0.5 * dot(unitRange, screenTexSize), 1.0);
}

float msdfAlpha(vec2 uv)
{
    vec3 msd = texture(Sampler0, uv).rgb;

    float signedDistance = median(msd.r, msd.g, msd.b) - 0.5;
    float px = screenPxRange(uv);

    return clamp(signedDistance * px + 0.5, 0.0, 1.0);
}

void main()
{
    // GameTime — нормализованная фаза цикла: 0.0 ... 1.0
    float phase = fract(GameTime);

    // Ускорение внутри одного цикла.
    // 1.0 = один полный оборот за весь GameTime 0..1
    // 3.5 = 3.5 внутренних оборота за один цикл GameTime
    float animSpeed = 20;

    float t = phase * TAU * animSpeed;

    vec2 loopTime = vec2(cos(t), sin(t));

    vec2 uv = texCoord0;
    vec2 texel = 1.0 / vec2(textureSize(Sampler0, 0));

    float strength = max(DeformationStrength, 0.0);
    vec2 deformationPx = vec2(0.0);

    // =========================================
    // 1. БАЗОВАЯ ЖИВАЯ ВОЛНА
    // =========================================

    float n1 = noise(vec2(uv.y * 7.0, 0.0) + loopTime * 1.4);
    float n2 = noise(vec2(uv.x * 7.0, 4.0) + loopTime * 1.1);

    deformationPx.x += sin(uv.y * 12.0 + t * 2.4) * 2.4;
    deformationPx.y += cos(uv.x * 10.0 - t * 2.0) * 1.7;

    // =========================================
    // 2. ВЕНОЗНЫЙ ДРЕЙФ
    // =========================================

    vec2 drift = vec2(
    n1 - 0.5,
    n2 - 0.5
    );

    drift.y *= 1.8;

    deformationPx += drift * 4.2;

    // =========================================
    // 3. БЫСТРЫЙ ОРГАНИЧЕСКИЙ ПУЛЬС
    // =========================================

    float pulse = sin(t * 4.8 + uv.y * 18.0);

    deformationPx.x += pulse * 1.7;
    deformationPx.y += pulse * 0.9;

    // Вторичный нервный спазм
    float twitch = sin(t * 13.0 + uv.x * 35.0 + uv.y * 20.0);

    deformationPx.x += twitch * 0.45;
    deformationPx.y += twitch * 0.25;

    // =========================================
    // 4. СЛИЗЬ / ПОВЕРХНОСТНОЕ ТЕЧЕНИЕ
    // =========================================

    float edgeBias = 1.0 - abs(texCoord0.x - 0.5) * 2.0;
    edgeBias = clamp(edgeBias, 0.0, 1.0);

    float slimeA = noise(uv * 13.0 + loopTime * 1.2);
    float slimeB = noise(uv * 21.0 - loopTime * 1.7);

    float slime = mix(slimeA, slimeB, 0.45);

    deformationPx += (slime - 0.5) * 2.4 * edgeBias;

    // =========================================
    // 5. ПРИМЕНЕНИЕ ДЕФОРМАЦИИ В ПИКСЕЛЯХ АТЛАСА
    // =========================================

    uv += deformationPx * texel * strength;

    // =========================================
    // 6. MSDF
    // =========================================

    float alpha = msdfAlpha(uv);

    // =========================================
    // 7. ЖИВАЯ ПЛОТЬ / ЦВЕТ
    // =========================================

    float flesh = noise(texCoord0 * 9.0 + loopTime * 0.7);

    vec3 color = vec3(
    0.78,
    0.18 + flesh * 0.07,
    0.22
    );

    color *= 0.92 + pulse * 0.06;

    // =========================================
    // 8. ВЕНЫ / НЕРВНЫЕ ИМПУЛЬСЫ
    // =========================================

    float veinNoise = noise(texCoord0 * 22.0 + loopTime * 0.6);

    float veins = sin(
    texCoord0.y * 70.0 +
    t * 10.0 +
    veinNoise * 4.0
    );

    color += veins * 0.055;

    vec4 outColor = vec4(
    color * vertexColor.rgb * ColorModulator.rgb,
    alpha * vertexColor.a * ColorModulator.a
    );

    if (outColor.a < 0.01)
        discard;

    fragColor = linear_fog(outColor, vertexDistance, FogStart, FogEnd, FogColor);
}