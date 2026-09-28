#version 150

uniform samplerCube Skybox;

in vec3 cubeDirection;
in vec4 vertexColor;

out vec4 fragColor;

void main()
{
    fragColor = texture(Skybox, normalize(cubeDirection)) * vertexColor;
}
