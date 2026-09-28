#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 DirectionMat;

out vec3 cubeDirection;
out vec4 vertexColor;

void main()
{
    // Position is a camera-local cube vector.  Remove ModelViewMat translation
    // so player movement can never change either geometry or cube sampling.
    mat4 rotationOnlyView = ModelViewMat;
    rotationOnlyView[3] = vec4(0.0, 0.0, 0.0, 1.0);
    cubeDirection = mat3(DirectionMat) * Position;
    vertexColor = Color;
    gl_Position = ProjMat * rotationOnlyView * vec4(Position, 1.0);
}
