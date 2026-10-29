#version 150

uniform sampler2D DepthSampler;
uniform mat4 InvProj;
uniform mat4 Proj;
uniform vec3 LightDir;     // direccion (espacio de camara) hacia el agujero negro
uniform vec4 LightColor;   // rgb = color, a = intensidad

in vec2 texCoord;
out vec4 fragColor;

vec3 viewPos(vec2 uv, float d) {
    vec4 ndc = vec4(uv * 2.0 - 1.0, d * 2.0 - 1.0, 1.0);
    vec4 v = InvProj * ndc;
    return v.xyz / v.w;
}

void main() {
    float d = texture(DepthSampler, texCoord).r;
    if (d >= 0.99999 || LightColor.a <= 0.001) {
        discard;                                   // cielo: no se ilumina
    }

    vec3 p = viewPos(texCoord, d);

    // Normal de la superficie a partir de la profundidad
    vec3 n = normalize(cross(dFdx(p), dFdy(p)));
    if (dot(n, p) > 0.0) {
        n = -n;
    }

    float ndl = max(dot(n, LightDir), 0.0);

    // Sombras en espacio de pantalla: avanza hacia el agujero negro y mira si algo tapa la luz
    float shadow = 1.0;
    vec3 q = p + n * 0.08;
    for (int i = 1; i <= 14; i++) {
        vec3 s = q + LightDir * (0.7 * float(i));
        vec4 clip = Proj * vec4(s, 1.0);
        if (clip.w <= 0.0) {
            break;
        }
        vec2 suv = (clip.xy / clip.w) * 0.5 + 0.5;
        if (suv.x < 0.0 || suv.x > 1.0 || suv.y < 0.0 || suv.y > 1.0) {
            break;
        }
        float sd = texture(DepthSampler, suv).r;
        vec3 sp = viewPos(suv, sd);
        float occ = sp.z - s.z;                    // >0: hay algo mas cerca de la camara que el rayo
        if (occ > 0.15 && occ < 3.0) {
            shadow = 0.0;
            break;
        }
    }

    vec3 light = LightColor.rgb * LightColor.a * (0.10 + 0.90 * ndl) * shadow;
    fragColor = vec4(light, 1.0);
}
