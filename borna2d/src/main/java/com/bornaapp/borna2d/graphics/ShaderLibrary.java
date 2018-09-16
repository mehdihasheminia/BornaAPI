package com.bornaapp.borna2d.graphics;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Mehdi on 11/29/2016 .
 */
public class ShaderLibrary {

    // region pass-through vertex shader
    static String vsPassThrough =
            "attribute vec4 a_position;\n" +
                    "attribute vec4 a_color;\n" +
                    "attribute vec2 a_texCoord0;\n" +
                    "\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "\n" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_texCoords;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    v_color = a_color;\n" +
                    "    v_texCoords = a_texCoord0;\n" +
                    "    gl_Position = u_projTrans * a_position;\n" +
                    "}";
    //endregion

    //region GrayScale shader
    //openGL fragment shader
    static String fsGrayScale =
            "#ifdef GL_ES\n" +
                    "    precision mediump float;\n" +
                    "#endif\n" +
                    "\n" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_texCoords;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "\n" +
                    "void main() {\n" +
                    "  vec4 c = v_color * texture2D(u_texture, v_texCoords);\n" +
                    "  float grey = (c.r + c.g + c.b) / 3.0;\n" +
                    "  gl_FragColor = vec4(grey, grey, grey, c.a);\n" +
                    "}";

    public static ShaderProgram GrayScale = new ShaderProgram(vsPassThrough, fsGrayScale);
    //endregion

    //region InvertColor shader
    //openGL fragment shader
    static String fsInvert =
            "#ifdef GL_ES\n" +
                    "    precision mediump float;\n" +
                    "#endif\n" +
                    "\n" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_texCoords;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "\n" +
                    "void main() {\n" +
                    "  vec4 c = texture2D(u_texture, v_texCoords);\n" +
                    "  c.rgb = 1.0 - c.rgb;\n" +
                    "  gl_FragColor = v_color * c;\n" +
                    "}";

    public static ShaderProgram InvertColor = new ShaderProgram(vsPassThrough, fsInvert);
    //endregion

    //region Blur shader
    //openGL fragment shader
    //https://gist.github.com/mattdesl/4372018
    //https://github.com/mattdesl/lwjgl-basics/wiki/ShaderLesson5
    static String fsBlur =
            "#ifdef GL_ES\n" +
                    "    precision mediump float;\n" +
                    "#endif\n" +
                    "\n" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_texCoords;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "\n" +
                    "void main() {\n" +
                    "  vec4 sum = vec4(0.0);\n" +
                    "  vec2 tc = v_texCoords;\n" +
                    "  sum += texture2D(u_texture, vec2(tc.x-0.004 , tc.y-0.004 )) * 0.05;\n" +
                    "  sum += texture2D(u_texture, vec2(tc.x-0.003 , tc.y-0.003 )) * 0.09;\n" +
                    "  sum += texture2D(u_texture, vec2(tc.x-0.002 , tc.y-0.002 )) * 0.12;\n" +
                    "  sum += texture2D(u_texture, vec2(tc.x-0.001 , tc.y-0.001 )) * 0.15;\n" +
                    "  sum += texture2D(u_texture, tc)* 0.16;\n" +
                    "  sum += texture2D(u_texture, vec2(tc.x+0.001 , tc.y+0.001 )) * 0.15;\n" +
                    "  sum += texture2D(u_texture, vec2(tc.x+0.002 , tc.y+0.002 )) * 0.12;\n" +
                    "  sum += texture2D(u_texture, vec2(tc.x+0.003 , tc.y+0.003 )) * 0.09;\n" +
                    "  sum += texture2D(u_texture, vec2(tc.x+0.004 , tc.y+0.004 )) * 0.05;\n" +
                    "  sum.a = texture2D(u_texture, tc).a;\n" +
                    "\n" +
                    "  gl_FragColor = v_color * sum;\n" +
                    "}";

    public static ShaderProgram Blur = new ShaderProgram(vsPassThrough, fsBlur);
    //endregion

}
