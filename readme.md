# 🛒 CRUD de Productos con Sincronización Híbrida

Este proyecto es una aplicación web moderna que permite gestionar productos conectándose a una API local y manteniendo un respaldo en tiempo real en el almacenamiento del navegador.

## 🚀 Tecnologías usadas
* HTML5 / CSS3
* JavaScript (Async/Await, Fetch API, Eventos del DOM)
* JSON Server (Base de datos ficticia)
* Local Storage (Modo espejo para persistencia offline)

## 🔧 Cómo ejecutar el proyecto localmente
1. Clona este repositorio.
2. Abre una terminal en la carpeta `db` y enciende la API con:
   `npx json-server --watch db/db.json --port 3000`
3. Abre el archivo `index.html` con la extensión Live Server de VS Code.
4. ¡Listo! Ahora puedes agregar, editar y eliminar productos, y ver cómo se sincronizan con el almacenamiento local.