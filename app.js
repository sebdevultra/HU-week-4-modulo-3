const titulo = document.getElementById("titulo");
const parrafo = document.getElementById("parrafo");
const inputNombreP = document.getElementById("input-nombreP");
const inputPrecio = document.getElementById("input-precio");
const inputDescripcion = document.getElementById("input-descripcion");
const listaNotas = document.getElementById("listaNotas");
const btnAgregar = document.getElementById("btn-agregar");
const formulario = document.getElementById("formulario");

formulario.addEventListener("submit", (event)=> {
    event.preventDefault(); // Evita que el formulario se envíe de forma predeterminada
    const nombreP = inputNombreP.value.trim();
    const precio = inputPrecio.value.trim();
    const descripcion = inputDescripcion.value.trim();
    if(nombreP==="" || precio === "" || descripcion === "") {
        console.error("Los campos no pueden estar vacíos.");
        return;
    }
    if(isNaN(Number(precio))|| Number(precio) <= 0) {
        console.error("El precio debe ser un número válido y positivo.");
        return;
    }
    if (nombreP && precio && descripcion) {
        const liNuevo = document.createElement("li");
        liNuevo.textContent = `Producto: ${nombreP}, Precio: ${precio}, Descripción: ${descripcion}`;
        listaNotas.appendChild(liNuevo);
        console.log("%c✔ Éxito: El producto se guardó correctamente.", "color: #2ecc71; font-weight: bold;");
        }
    inputNombreP.value = "";
    inputPrecio.value = "";
    inputDescripcion.value = "";
});