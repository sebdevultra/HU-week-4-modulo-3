# 🛒 CRUD de Productos con Sincronización Híbrida

Este proyecto es una aplicación web moderna que permite gestionar productos conectándose a una API local y manteniendo un respaldo en tiempo real en el almacenamiento del navegador.

## 🚀 Tecnologías usadas
* HTML5 / CSS3
* JavaScript (Async/Await, Fetch API, Eventos del DOM)
* JSON Server (Base de datos ficticia)
* Local Storage (Modo espejo para persistencia offline)

## 🔧 Cómo ejecutar el proyecto localmente
1. Clona este repositorio.
2. Abre una terminal en la carpeta donde se encuentra el archivo `db.json` y enciende la API con:
   `npx json-server --watch db.json --port 3000`
3. Abre el archivo `index.html` con la extensión Live Server de VS Code.
4. ¡Listo! Ahora puedes agregar, editar y eliminar productos, y ver cómo se sincronizan con el almacenamiento local.
## ⚠️ Lecciones Aprendidas & Desafíos Técnicos

### El Bucle de Recarga de Live Server vs JSON Server
Durante el desarrollo, nos enfrentamos a un bug donde **la página web se recargaba por completo por sí sola** cada vez que agregábamos o eliminábamos un producto, lo que limpiaba el historial de la consola del navegador e interrumpía la experiencia de usuario.

* **La Causa:** La extensión *Live Server* de VS Code estaba vigilando toda la carpeta del proyecto. Al ejecutar un método `POST` o `DELETE`, el servidor local (*JSON Server*) modificaba el archivo `db.json`. Live Server detectaba este cambio físico, asumía erróneamente que el programador había modificado el código de la app y forzaba un refresco de pantalla completo.
* **La Solución:** Para solucionar este problema de raíz, **se extrajo el archivo `db.json` de la carpeta vigilada por Live Server** y se colocó en un directorio externo e independiente de la computadora. Al correr el comando de JSON Server desde esa ubicación externa, la API siguió estando disponible para JavaScript a través de `http://localhost:3000/productos`, pero quedó completamente fuera del radar de Live Server, eliminando las recargas fantasmas por completo.


