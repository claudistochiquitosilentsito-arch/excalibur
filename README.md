# Excalibur — mod de Fabric para Minecraft Java 26.3

Añade **Excalibur**, una espada legendaria (dorada, con gema azul) con:

- **Animación propia en primera persona**: carga sobre el hombro y tajo diagonal, en vez del golpe vanilla.
- **Animación propia en tercera persona**: el jugador levanta la espada sobre la cabeza, gira el torso y corta en diagonal; el otro brazo acompaña como si la agarrase a dos manos. La ven también los demás jugadores en multijugador (si tienen el mod).
- **Efecto «Temblor»**: cada golpe cuerpo a cuerpo aplica 3 s de Temblor a la entidad golpeada. Si es un jugador, su cámara tiembla y se va calmando durante el último segundo.

Estadísticas: material de netherita, +5 de daño extra, velocidad normal de espada, resistente al fuego, rareza épica.
Está en la pestaña **Combate** del creativo, o con `/give @s excalibur:excalibur`.

Receta (mesa de crafteo):

```
 [  ]  [Bloque de diamante]  [  ]
 [  ]  [Lingote de netherita] [  ]
 [Bloque de oro] [Estrella del Nether] [Bloque de oro]
```

## Versiones

| Componente | Versión |
|---|---|
| Minecraft | 26.3 |
| Fabric Loader | 0.19.5 o superior |
| Fabric API | 0.163.0+26.3 |
| Fabric Loom | 1.18-SNAPSHOT (plugin `net.fabricmc.fabric-loom`) |
| Java (JDK) | 25 |

## Cómo compilarlo

1. Instala el **JDK 25** (por ejemplo Temurin de adoptium.net).
2. Abre una terminal en esta carpeta y ejecuta:
   - Windows: `gradlew.bat build`
   - Linux/macOS: `./gradlew build`
3. El mod queda en `build/libs/excalibur-1.0.0.jar`.

Para probarlo directamente con un Minecraft de desarrollo: `./gradlew runClient`.

También puedes abrir la carpeta en IntelliJ IDEA (con el plugin «Minecraft Development») e importarla como proyecto Gradle.

## Cómo instalarlo

1. Instala **Fabric Loader** para Minecraft 26.3 (fabricmc.net/use).
2. Copia en tu carpeta `.minecraft/mods`:
   - `excalibur-1.0.0.jar`
   - **Fabric API** para 26.3 (desde Modrinth o CurseForge).
3. Abre el launcher con el perfil de Fabric 26.3.

En un servidor hay que poner el mod también en `mods/` del servidor (el efecto se aplica allí). Las animaciones y el temblor son del lado cliente.

## Estructura

```
src/main/java/com/excalibur/
  ExcaliburMod.java          registro de la espada, el efecto y el evento de golpe
  effect/TremorEffect.java   efecto «Temblor»
src/client/java/com/excalibur/client/
  ExcaliburClient.java       arranque del cliente
  CameraShake.java           temblor de cámara
  anim/ExcaliburAnimations.java   curvas de la animación (1ª y 3ª persona)
  mixin/FirstPersonSwingMixin.java  engancha la vista en primera persona
  mixin/PlayerModelMixin.java       engancha el modelo del jugador (3ª persona)
  mixin/AvatarRendererMixin.java    detecta si el jugador lleva Excalibur
src/main/resources/assets/excalibur/  texturas, modelo, idiomas (es/en)
src/main/resources/data/excalibur/    receta
```

## Ajustar la animación y el temblor

- **Animación**: en `ExcaliburAnimations.java`, las tablas `FP_KEYS` (primera persona) y `TP_KEYS` (tercera persona) son fotogramas clave `{tiempo, ...}`. Cambia ángulos o desplazamientos y vuelve a lanzar `runClient` para ver el resultado.
- **Fuerza del temblor**: `BASE_STRENGTH` en `CameraShake.java` (grados).
- **Duración del temblor**: `TREMOR_DURATION_TICKS` en `ExcaliburMod.java` (20 ticks = 1 segundo).
- **Daño**: la línea `.sword(ToolMaterial.NETHERITE, 5.0F, -2.4F)` en `ExcaliburMod.java`.

## Si algo falla al compilar o arrancar

Minecraft 26.3 renombró varias clases de renderizado (por ejemplo `ItemInHandRenderer` → `FirstPersonHandsAndItemsRenderer`). El código usa los nombres actuales de 26.3, pero si Mojang publica un 26.3.x con cambios:

- Un error de compilación indicará la clase o el método que cambió.
- Si el juego arranca pero no hay animación en primera persona, el punto de enganche `pushPose()` de `FirstPersonSwingMixin` no se encontró (está marcado como opcional para que no cuelgue el juego).
