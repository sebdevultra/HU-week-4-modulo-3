const titulo = document.querySelector("#titulo");
const parrafo = document.querySelector("#parrafo");
const inputNombreP = document.querySelector("#input-nombreP");
const inputPrecio = document.querySelector("#input-precio");
const inputDescripcion = document.querySelector("#input-descripcion");
const listaDatos = document.querySelector("#lista-datos");
const btnAgregar = document.querySelector("#btn-agregar");
const formulario = document.querySelector("#formulario");
let datos = JSON.parse(localStorage.getItem("datos")) || [];
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
    datos.push({nombreP,precio,descripcion});
    localStorage.setItem("datos",JSON.stringify(datos));
    listarDatos();
    console.log("%c✔ Éxito: El producto se guardó correctamente.", "color: #2ecc71; font-weight: bold;");
    formulario.reset();
});
function eliminarDato(event){
    if(event.target.tagName==='BUTTON'){
        const datoABorrar = event.target.parentElement;
        const index = Array.from(listaDatos.children).indexOf(datoABorrar);
        datos.splice(index, 1);
        localStorage.setItem("datos",JSON.stringify(datos));
        listaDatos.removeChild(datoABorrar);
        console.log("Dato eliminado correctamente");
    }
}

listaDatos.addEventListener("click",eliminarDato);

function listarDatos(){
    listaDatos.innerHTML = "";
    datos.forEach(dato => {
        const nuevoLi =document.createElement("li");
        const btnX = document.createElement("button");
        btnX.textContent="X";
        btnX.style.margin="20px";
        nuevoLi.textContent = `Producto: ${dato.nombreP}, Precio: ${dato.precio}, Descripción: ${dato.descripcion}`;
        nuevoLi.appendChild(btnX);
        listaDatos.appendChild(nuevoLi);
        
    });
    
}
listarDatos();
console.log(`Se han cargado correctamente ${datos.length} datos`);