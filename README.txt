BLACK HOLE SKY - Forge 1.20.1 (47.3.0)

Compilar:
1. Descarga el MDK oficial de Forge 1.20.1 (files.minecraftforge.net) y copia su carpeta gradle/ y gradlew(.bat) a esta carpeta.
2. Ejecuta: ./gradlew build   (el .jar queda en build/libs/)  o  ./gradlew runClient para probar.

Activar el cielo (comando de cliente):
/shader cielo on
/shader cielo off
Funciona en el Overworld y en la dimension. Por defecto esta APAGADO.

Ir a la dimension (modo creativo):
/execute in blackholesky:event_horizon run tp @s 0 150 0

Agujero negro: BH en EventHorizonEffects.java (cambia la direccion del vector).
El tsunami se dibuja siempre en el lado opuesto.

Control del tiempo (necesita OP / cheats activados):
/tiempocielo acelerar <2-200>   tiempo X veces mas rapido
/tiempocielo normal             velocidad normal
/tiempocielo dia                fija el dia
/tiempocielo noche              fija la noche
(Usa la regla doDaylightCycle internamente; "normal" la deja en true.)

Luz del agujero negro sobre el suelo (de noche):
Al activar /shader cielo on, el terreno recibe luz naranja desde la direccion del agujero negro
(mas fuerte en superficies que lo miran, con sombras de arboles/bloques). No se ve en cuevas ni bajo techo.
Si usas shaders de Optifine/Oculus, este efecto puede no funcionar (usa el shader vanilla).
