const a = `glsl:
    precision mediump float;

    attribute vec4 vPosition;
    uniform mat4 cameraMatrix;
    uniform mat4 projMatrix;
    uniform mat4 modelMatrix;

    void main() {
        gl_Position = <caret>projMatrix * cameraMatrix * modelMatrix * vPosition;
    }
`