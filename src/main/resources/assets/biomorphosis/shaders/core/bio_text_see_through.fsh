#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

uniform float GameTime;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

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

void main()
{
    float t = GameTime;

    vec2 uv = texCoord0;

    // --- base waves ---
    uv.x += sin(uv.y * 18.0 + t * 1.6) * 0.006;
    uv.y += sin(uv.x * 12.0 - t * 1.2) * 0.004;

    // --- noise ---
    float n = noise(uv * 10.0 + t * 0.7);
    uv += (n - 0.5) * 0.01;

    // --- stream ---
    float n1 = noise(uv * 6.0 + t);
    float n2 = noise(uv * 6.0 - t);
    uv += vec2(n1 - 0.5, n2 - 0.5) * 0.008;

    // --- pulsing ---
    vec2 center = vec2(0.5, 0.5);
    vec2 d = uv - center;
    float dist = length(d);

    float pulse = sin(dist * 20.0 - t * 3.0);
    uv += normalize(d) * pulse * 0.003;

    // --- final ---
    vec4 color = texture(Sampler0, uv) * vertexColor;
    if (color.a < 0.1) {
        discard;
    }
    fragColor = color * ColorModulator;
}
